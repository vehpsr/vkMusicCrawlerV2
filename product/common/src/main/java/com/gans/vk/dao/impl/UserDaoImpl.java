package com.gans.vk.dao.impl;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.gans.vk.dao.AbstractModelDao;
import com.gans.vk.dao.UserDao;
import com.gans.vk.model.impl.User;

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
    public List<UserLibData> getRecomendedAudioLibsFor(final User user) {
        final String sql =
            "SELECT " +
            "	u.id, u.name, u.url, u.vkId, avgRatingQuery.avgRating, totalCountQuery.total, ratedCountQuery.rated " +
            "FROM " +
            "	(SELECT " +
            "		id, AVG(rating) AS avgRating " +
            "	FROM " +
            "		(SELECT " +
            "			rating.user_id AS id, CASE " +
            "							WHEN ratedSongs.rating IS NOT NULL THEN ratedSongs.rating " +
            "							WHEN artistRating.rating IS NOT NULL THEN artistRating.rating " +
            "							ELSE 3.1 END AS rating " +
            "		FROM " +
            "			Rating rating " +
            "			JOIN Song song ON rating.song_id = song.id " +
            "			LEFT JOIN " +
            "			(SELECT " +
            "				artist, AVG(rating.value) AS rating " +
            "			FROM " +
            "				Song song " +
            "				JOIN Rating rating ON song.id = rating.song_id " +
            "			WHERE " +
            "				rating.user_id = :userId " +
            "			GROUP BY " +
            "				artist " +
            "			) AS artistRating ON song.artist = artistRating.artist " +
            "			LEFT JOIN " +
            "			(SELECT " +
            "				song.id, value as rating " +
            "			FROM " +
            "				Song song " +
            "				JOIN Rating rating ON song.id = rating.song_id " +
            "			WHERE " +
            "				rating.user_id = :userId " +
            "			) AS ratedSongs ON ratedSongs.id = song.id " +
            "		WHERE " +
            "			rating.user_id <> :userId " +
            "		) AS aggRatingTable " +
            "	GROUP BY " +
            "		id " +
            "	ORDER BY " +
            "		avgRating DESC " +
            "	) AS avgRatingQuery " +
            "	JOIN " +
            "	(SELECT " +
            "		rating.user_id, COUNT(rating.song_id) AS total " +
            "	FROM " +
            "		Rating rating " +
            "	WHERE " +
            "		rating.user_id <> :userId " +
            "	GROUP BY " +
            "		rating.user_id " +
            "	) AS totalCountQuery ON totalCountQuery.user_id = avgRatingQuery.id " +
            "	LEFT JOIN " +
            "	(SELECT " +
            "		rating.user_id, COUNT(*) AS rated " +
            "	FROM " +
            "		(SELECT song_id FROM Rating WHERE user_id = :userId) AS ratedByUser " +
            "		JOIN Rating rating ON rating.song_id = ratedByUser.song_id " +
            "	WHERE " +
            "		rating.user_id <> :userId " +
            "	GROUP BY " +
            "		rating.user_id " +
            "	) AS ratedCountQuery ON totalCountQuery.user_id = ratedCountQuery.user_id " +
            "	RIGHT JOIN " +
            "	Users u ON u.id = totalCountQuery.user_id " +
            "WHERE " +
            "	u.id <> :userId " +
            "	AND (totalCountQuery.total IS NOT NULL OR totalCountQuery.total > 0) " +
            "ORDER BY " +
            "	avgRatingQuery.avgRating DESC ";

        long start = System.currentTimeMillis();
        Collection<Object[]> resultSet = getHibernateTemplate().execute(new HibernateCallback<Collection<Object[]>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Collection<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql);
                query.setLong("userId", user.getId());
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

}
