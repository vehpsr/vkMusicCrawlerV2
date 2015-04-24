package com.gans.vk.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.gans.vk.dao.AbstractModelDao;
import com.gans.vk.dao.RatingDao;
import com.gans.vk.model.impl.Rating;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public class RatingDaoImpl extends AbstractModelDao<Rating> implements RatingDao {

    private static final Log LOG = LogFactory.getLog(RatingDaoImpl.class);

    public RatingDaoImpl() {
        super(Rating.class);
    }

    @Override
    public Rating getBy(User user, Song song) {
        DetachedCriteria criteria = createCriteria();
        criteria.add(Restrictions.eq("user", user));
        criteria.add(Restrictions.eq("song", song));
        return findUnique(criteria);
    }

    @Override
    public void importUserAudioLib(final User user, final List<Entry<String, String>> audioLib) {
        LOG.info(MessageFormat.format("Start imort audio lib for {0}. Total size: {1}", user.getName(), audioLib.size()));
        if (audioLib.isEmpty()) {
            return;
        }

        final int batchSize = 50;
        final String sqlInsertSongBatch;
        final String sqlInsertRatingBatch;

        sqlInsertSongBatch =
                "INSERT INTO Song " +
                "	(artist, title) " +
                "SELECT " +
                "	* " +
                "FROM " +
                "	(SELECT " +
                "		?, ? " +
                "	) as tmp " +
                "WHERE " +
                "	NOT EXISTS " +
                "		(SELECT " +
                "			artist, title " +
                "		FROM " +
                "			Song " +
                "		WHERE " +
                "			artist = ? " +
                "			AND title = ? " +
                "		LIMIT 1) ";



        sqlInsertRatingBatch =
                "INSERT INTO Rating " +
                "	(value, date, song_id, user_id) " +
                "SELECT " +
                "	5, ?, id, ? " +
                "FROM " +
                "	(SELECT " +
                "		id FROM Song WHERE artist = ? AND title = ? LIMIT 1 " +
                "	) as tmp " +
                "WHERE " +
                "	NOT EXISTS " +
                "		(SELECT " +
                "			1 " +
                "		FROM " +
                "			Rating " +
                "		WHERE " +
                "			user_id = ? " +
                "			AND song_id = (SELECT id FROM Song WHERE artist = ? AND title = ? LIMIT 1) " +
                "		LIMIT 1) ";


        long start = System.currentTimeMillis();

        getHibernateTemplate().execute(new HibernateCallback<Void>() {
            @Override
            public Void doInHibernate(Session session) throws HibernateException, SQLException {
                session.doWork(new Work() {
                    @Override
                    public void execute(Connection connection) throws SQLException {
                        PreparedStatement insertSongStatement = connection.prepareStatement(sqlInsertSongBatch);
                        PreparedStatement insertRatingStatement = connection.prepareStatement(sqlInsertRatingBatch);

                        Date now = new Date(System.currentTimeMillis());

                        int traceInsertSongCount = 0;
                        int traceInsertRatingCount = 0;

                        for (int i = 0; i < audioLib.size(); i++) {
                            Entry<String, String> song = audioLib.get(i);
                            String artist = song.getKey();
                            String title = song.getValue();

                            insertSongStatement.setString(1, artist);
                            insertSongStatement.setString(2, title);
                            insertSongStatement.setString(3, artist);
                            insertSongStatement.setString(4, title);
                            insertSongStatement.addBatch();

                            insertRatingStatement.setDate(1, now);
                            insertRatingStatement.setLong(2, user.getId());
                            insertRatingStatement.setString(3, artist);
                            insertRatingStatement.setString(4, title);
                            insertRatingStatement.setLong(5, user.getId());
                            insertRatingStatement.setString(6, artist);
                            insertRatingStatement.setString(7, title);
                            insertRatingStatement.addBatch();

                            if (i % batchSize == 0 && i != 0) {
                                int[] songCount = insertSongStatement.executeBatch();
                                int[] ratingCount = insertRatingStatement.executeBatch();
                                traceInsertSongCount += sum(songCount);
                                traceInsertRatingCount += sum(ratingCount);
                            }

                            if (i % 1000 == 0 && i != 0) {
                                LOG.info(MessageFormat.format("Inserted {0} new songs from total of {1}", traceInsertSongCount, i));
                                LOG.info(MessageFormat.format("Rated {0} new songs from total of {1}", traceInsertRatingCount, i));
                            }
                        }

                        insertSongStatement.executeBatch();
                        insertRatingStatement.executeBatch();
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
    public Map<java.util.Date, Entry<Integer, Float>> rating(final User user, final User target, final long from, final long to, final int step) {
        final String dateColumn;
        if (MYSQL_VENDOR.equals(_dbVendor)) {
            dateColumn = MessageFormat.format("FROM_UNIXTIME(CEIL(UNIX_TIMESTAMP(ratedByMe.date) / {0}) * {0}) ", String.valueOf(step));
        } else if (POSTGRES_VENDOR.equals(_dbVendor)) {
            dateColumn = MessageFormat.format("TO_TIMESTAMP(CEIL(CAST(EXTRACT(EPOCH FROM ratedByMe.date) AS INTEGER) / {0}) * {0}) ", String.valueOf(step));
        } else {
            throw new IllegalStateException(MessageFormat.format("Unknown database vendor {0}", _dbVendor));
        }

        final String sql =
                "SELECT " +
                    dateColumn + " as period, COUNT(ratedByMe.value), AVG(ratedByMe.value) " +
                "FROM " +
                "	Song song" +
                "	JOIN Rating ratedByUser ON song.id = ratedByUser.song_id " +
                "	JOIN Rating ratedByMe ON song.id = ratedByMe.song_id " +
                "WHERE " +
                "	ratedByUser.user_id = :targetId " +
                "	AND ratedByMe.user_id = :userId " +
                "	AND (ratedByMe.date BETWEEN:from AND :to) " +
                "GROUP BY " +
                "	period " +
                "ORDER BY " +
                "	period ";

        Collection<Object[]> rows = getHibernateTemplate().execute(new HibernateCallback<Collection<Object[]>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Collection<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql);
                query.setLong("userId", user.getId());
                query.setLong("targetId", target.getId());
                query.setDate("from", new Date(from));
                query.setDate("to", new Date(to));
                return query.list();
            }
        });

        Map<java.util.Date, Entry<Integer, Float>> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Date date = new Date(((Timestamp)row[0]).getTime());
            int count = ((Number)row[1]).intValue();
            float avg = ((Number)row[2]).floatValue();
            result.put(date, new AbstractMap.SimpleEntry<Integer, Float>(count, avg));
        }
        return result;
    }

}
