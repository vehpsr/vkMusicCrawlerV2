package com.gans.vk.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

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
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

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
    public void importUserAudioLib(final User user, final Set<Entry<String, String>> audioLib) {
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
                "		? AS artist, ? AS title " +
                "	) AS tmp " +
                "WHERE " +
                "	NOT EXISTS " +
                "		(SELECT " +
                "			1 " +
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
                "	) as tmp ";


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
                        int traceCounter = 1;

                        for (Entry<String, String> song : audioLib) {
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
                            insertRatingStatement.addBatch();

                            if (traceCounter % batchSize == 0) {
                                int[] songCount = insertSongStatement.executeBatch();
                                int[] ratingCount = insertRatingStatement.executeBatch();
                                traceInsertSongCount += sum(songCount);
                                traceInsertRatingCount += sum(ratingCount);
                            }

                            if (traceCounter % 1000 == 0) {
                                LOG.info(MessageFormat.format("Inserted {0} new songs from total of {1}", traceInsertSongCount, traceCounter));
                                LOG.info(MessageFormat.format("Rated {0} new songs from total of {1}", traceInsertRatingCount, traceCounter));
                            }

                            traceCounter++;
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
    public Multimap<java.util.Date, Entry<Integer, Integer>> rating(final User user, final long from, final long to, final long step) {
        final String dateColumn;
        if (MYSQL_VENDOR.equals(_dbVendor)) {
            dateColumn = MessageFormat.format("FROM_UNIXTIME(FLOOR(UNIX_TIMESTAMP(date) / {0}) * {0}) ", String.valueOf(step));
        } else if (POSTGRES_VENDOR.equals(_dbVendor)) {
            dateColumn = MessageFormat.format("TO_TIMESTAMP(FLOOR(CAST(EXTRACT(EPOCH FROM date) AS INTEGER) / {0}) * {0}) ", String.valueOf(step));
        } else {
            throw new IllegalStateException(MessageFormat.format("Unknown database vendor {0}", _dbVendor));
        }

        final String sql =
                "SELECT " +
                    dateColumn + " as period, value, count(value) " +
                "FROM " +
                "	Rating " +
                "WHERE " +
                "	user_id = :userId " +
                "	AND date >= :from " +
                "GROUP BY " +
                "	period, value " +
                "ORDER BY " +
                "	period ";

        Collection<Object[]> rows = getHibernateTemplate().execute(new HibernateCallback<Collection<Object[]>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Collection<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql);
                query.setLong("userId", user.getId());
                query.setDate("from", new Date(from));
                //query.setDate("to", new Date(to));
                return query.list();
            }
        });

        // Multimap<Date, Entry<RatingValue, Count>>
        Multimap<java.util.Date, Entry<Integer, Integer>> result = LinkedListMultimap.create();
        for (Object[] row : rows) {
            Date date = new Date(((Timestamp)row[0]).getTime());
            int value = ((Number)row[1]).intValue();
            int count = ((Number)row[2]).intValue();
            result.put(date, new AbstractMap.SimpleEntry<Integer, Integer>(value, count));
        }
        return result;
    }

}
