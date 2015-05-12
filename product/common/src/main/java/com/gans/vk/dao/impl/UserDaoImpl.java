package com.gans.vk.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.gans.vk.dao.AbstractModelDao;
import com.gans.vk.dao.UserDao;
import com.gans.vk.model.impl.User;
import com.gans.vk.model.impl.User.UserStatus;

public class UserDaoImpl extends AbstractModelDao<User> implements UserDao {

    private static final Log LOG = LogFactory.getLog(UserDaoImpl.class);

    public UserDaoImpl() {
        super(User.class);
    }

    @Override
    public User getUserByUrl(String url) {
        DetachedCriteria criteria = createCriteria();
        criteria.add(Restrictions.eq("url", url));
        return findUnique(criteria);
    }

    @Override
    public User getUserByVkId(String vkId) {
        DetachedCriteria criteria = createCriteria();
        criteria.add(Restrictions.eq("vkId", vkId));
        return findUnique(criteria);
    }

    @Override
    public List<UserLibData> getRecomendedUserLibData(final User user, final User target) {
        final String filterByUser;
        if (target == null) { // for all
            filterByUser = "<> :userId ";
        } else { // get data for specific user
            filterByUser = "= :targetId ";
        }
        final String sql =
            "SELECT " +
            "	u.id, u.name, u.url, u.vkId, avgRatingQuery.avgRating, totalCountQuery.total, ratedCountQuery.rated " +
            "FROM " +
            "	(SELECT " +
            "		user_id, AVG(aggSongRatingTable.rating) AS avgRating " +
            "	FROM " +
            "		(SELECT " +
            "			song.id, CASE " +
            "							WHEN ratedSongs.rating IS NOT NULL THEN ratedSongs.rating " +
            "							WHEN artistRating.rating IS NOT NULL THEN artistRating.rating " +
            "							ELSE 3.1 END AS rating " +
            "		FROM " +
            "			Song song " +
            "			LEFT JOIN " +
            "			(SELECT " +
            "				artist, AVG(value) AS rating " +
            "			FROM " +
            "				Song song " +
            "				JOIN Rating rating ON song.id = rating.song_id " +
            "			WHERE " +
            "				user_id = :userId " +
            "			GROUP BY " +
            "				artist " +
            "			) AS artistRating ON song.artist = artistRating.artist " +
            "			LEFT JOIN " +
            "			(SELECT " +
            "				song_id, value as rating " +
            "			FROM " +
            "				Rating " +
            "			WHERE " +
            "				user_id = :userId " +
            "			) AS ratedSongs ON ratedSongs.song_id = song.id " +
            "		) AS aggSongRatingTable " +
            "		JOIN Rating rating ON aggSongRatingTable.id = rating.song_id " +
            "	WHERE " +
            "		user_id " + filterByUser +
            "	GROUP BY " +
            "		user_id " +
            "	ORDER BY " +
            "		avgRating DESC " +
            "	) AS avgRatingQuery " +
            "	JOIN " +
            "	(SELECT " +
            "		rating.user_id, COUNT(rating.song_id) AS total " +
            "	FROM " +
            "		Rating rating " +
            "	WHERE " +
            "		rating.user_id " + filterByUser +
            "	GROUP BY " +
            "		rating.user_id " +
            "	) AS totalCountQuery ON totalCountQuery.user_id = avgRatingQuery.user_id " +
            "	LEFT JOIN " +
            "	(SELECT " +
            "		rating.user_id, COUNT(*) AS rated " +
            "	FROM " +
            "		(SELECT song_id FROM Rating WHERE user_id = :userId) AS ratedByUser " +
            "		JOIN Rating rating ON rating.song_id = ratedByUser.song_id " +
            "	WHERE " +
            "		rating.user_id " + filterByUser +
            "	GROUP BY " +
            "		rating.user_id " +
            "	) AS ratedCountQuery ON totalCountQuery.user_id = ratedCountQuery.user_id " +
            "	RIGHT JOIN " +
            "	Users u ON u.id = totalCountQuery.user_id " +
            "WHERE " +
            "	(u.id " + filterByUser + "AND u.vkId IS NOT NULL AND u.vkId NOT IN (:userStatus)) " +
            "ORDER BY " +
            "	avgRatingQuery.avgRating DESC ";

        long start = System.currentTimeMillis();
        Collection<Object[]> resultSet = getHibernateTemplate().execute(new HibernateCallback<Collection<Object[]>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Collection<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql);
                query.setLong("userId", user.getId());
                if (target != null) {
                    query.setLong("targetId", target.getId());
                }
                query.setParameterList("userStatus", UserStatus.names());
                query.setMaxResults(20);
                return query.list();
            }
        });

        LOG.info(MessageFormat.format("Recommended audio libs query took: {0}ms", System.currentTimeMillis() - start));

        List<UserLibData> userData = new ArrayList<>();
        for (Object[] row : resultSet) {
            UserLibData data = new UserLibData();
            data.setId(((Number)row[0]).longValue());
            data.setName((String)row[1]);
            data.setUrl((String)row[2]);
            data.setVkId((String)row[3]);
            data.setRating(row[4] == null ? 0 : ((Number)row[4]).floatValue());
            data.setTotalAudioCount(row[5] == null ? 0 : ((Number)row[5]).intValue());
            data.setRatedAudioCount(row[6] == null ? 0 : ((Number)row[6]).intValue());
            userData.add(data);
        }
        return userData;
    }

    @Override
    public void importUnique(final List<Entry<String, String>> users) {
        LOG.info(MessageFormat.format("Start group members imort. Total size: {0}", users.size()));
        if (users.isEmpty()) {
            return;
        }

        final int batchSize = 50;
        final String sqlInsertUsersBatch;

        sqlInsertUsersBatch =
                "INSERT INTO Users " +
                "	(name, url) " +
                "SELECT " +
                "	* " +
                "FROM " +
                "	(SELECT " +
                "		?, ? " +
                "	) as tmp " +
                "WHERE " +
                "	NOT EXISTS " +
                "		(SELECT " +
                "			name, url " +
                "		FROM " +
                "			Users " +
                "		WHERE " +
                "			url = ? " +
                "		LIMIT 1) ";

        long start = System.currentTimeMillis();

        getHibernateTemplate().execute(new HibernateCallback<Void>() {
            @Override
            public Void doInHibernate(Session session) throws HibernateException, SQLException {
                session.doWork(new Work() {
                    @Override
                    public void execute(Connection connection) throws SQLException {
                        PreparedStatement insertUsersStatement = connection.prepareStatement(sqlInsertUsersBatch);
                        int traceInsertUsersCount = 0;

                        for (int i = 0; i < users.size(); i++) {
                            Entry<String, String> song = users.get(i);
                            String url = song.getKey();
                            String name = song.getValue();

                            insertUsersStatement.setString(1, name);
                            insertUsersStatement.setString(2, url);
                            insertUsersStatement.setString(3, url);
                            insertUsersStatement.addBatch();

                            if (i % batchSize == 0 && i != 0) {
                                int[] usersCount = insertUsersStatement.executeBatch();
                                traceInsertUsersCount += sum(usersCount);
                            }

                            if (i % 1000 == 0 && i != 0) {
                                LOG.info(MessageFormat.format("Inserted {0} new users from total of {1}", traceInsertUsersCount, i));
                            }
                        }

                        int[] usersCount = insertUsersStatement.executeBatch();
                        traceInsertUsersCount += sum(usersCount);
                        LOG.info(MessageFormat.format("Inserted {0} new users from total of {1}", traceInsertUsersCount, users.size()));
                    }

                    private int sum(int[] batchCount) {
                        int sum = 0;
                        for (int count : batchCount) {
                            sum += count;
                        }
                        return sum;
                    }
                });
                return null;
            }
        });

        LOG.info(MessageFormat.format("Import take: {0}ms", System.currentTimeMillis() - start));
    }

    @Override
    public int getUndiscoveredUsersCount() {
        DetachedCriteria criteria = createCriteria();
        criteria.add(Restrictions.isNull("vkId"));
        criteria.setProjection(Projections.rowCount());
        List<?> resultSet = getHibernateTemplate().findByCriteria(criteria);
        return ((Number)resultSet.get(0)).intValue();
    }

    @Override
    public List<User> getUndiscoveredUsers(int limit) {
        final String sql =
                "SELECT " +
                    "u.*" +
                "FROM " +
                    "Users u " +
                "WHERE " +
                    "u.vkId IS NULL " +
                "ORDER BY " +
                    random() +
                "LIMIT " + limit;

        return getHibernateTemplate().execute(new HibernateCallback<List<User>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<User> doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql);
                query.addEntity(User.class);
                return query.list();
            }
        });
    }

    @Override
    public User getRandomUser() {
        final String sql =
                "SELECT " +
                    "* " +
                "FROM " +
                    "Users " +
                "WHERE " +
                    "vkId IS NOT NULL " +
                    "AND vkId NOT IN (:userStatus) " +
                    "AND id >= FLOOR(" + random() + " * (SELECT MAX(id) FROM Users)) " +
                "ORDER BY " +
                    "id" +
                "LIMIT 1 ";

        return getHibernateTemplate().execute(new HibernateCallback<User>() {
            @Override
            public User doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql);
                query.setParameterList("userStatus", UserStatus.names());
                query.addEntity(User.class);
                return (User) query.uniqueResult();
            }
        });
    }

    @Override
    public List<Entry<String, Integer>> userStatusStatistics() {
        final String sql =
                "SELECT " +
                    "vkId, COUNT(vkId) " +
                "FROM " +
                    "Users " +
                "WHERE " +
                    "vkId IN (:userStatus) " +
                "GROUP BY " +
                    "vkId ";

        List<Object[]> rows = getHibernateTemplate().execute(new HibernateCallback<List<Object[]>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql);
                query.setParameterList("userStatus", UserStatus.names());
                return query.list();
            }
        });

        List<Entry<String, Integer>> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(new AbstractMap.SimpleEntry<String, Integer>(row[0].toString(), ((Number)row[1]).intValue()));
        }
        return result;
    }

}
