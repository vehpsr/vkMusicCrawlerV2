package com.gans.vk.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
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

    private String _dbVendor;

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
        final int batchSize = 50;
        final String sqlInsertSongBatch;
        final String sqlInsertRatingBatch;

        if (_dbVendor.equals(POSTGRES_VENDOR)) {
            sqlInsertSongBatch =
                "INSERT INTO Song " +
                "	(artist, title) " +
                "SELECT " +
                "	?, ? " +
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
        } else if (_dbVendor.equals(MYSQL_VENDOR)) { // TODO check if this query will pass on postgres
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
        } else {
            throw new IllegalStateException(MessageFormat.format("Unsupported database vendor: {0}", _dbVendor));
        }

        if (_dbVendor.equals(POSTGRES_VENDOR)) {
            sqlInsertRatingBatch =
                "INSERT INTO Song " +
                "	(artist, title) " +
                "SELECT " +
                "	?, ? " +
                "WHERE " +
                "	NOT EXISTS " +
                "		(SELECT " +
                "			artist, title " +
                "		FROM " +
                "			Song " +
                "		WHERE " +
                "			artist = ? " +
                "			AND title = ? " +
                "		) LIMIT 1 ";
        } else if (_dbVendor.equals(MYSQL_VENDOR)) { // TODO check if passes on Postgres
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
        } else {
            throw new IllegalStateException(MessageFormat.format("Unsupported database vendor: {0}", _dbVendor));
        }

        LOG.info(MessageFormat.format("Start imort audio lib for {0}. Total size: {1}", user.getName(), audioLib.size()));
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
                                trace("songs", songCount);
                                trace("ratings", ratingCount);
                            }
                        }

                        insertSongStatement.executeBatch();
                        insertRatingStatement.executeBatch();
                    }

                    private void trace(String entry, int[] batchCount) {
                        int insertCount = 0;
                        for (int count : batchCount) {
                            insertCount += count;
                        }
                        LOG.info(MessageFormat.format("{0}: updated {1} from {2}", entry, insertCount, batchCount.length));
                    }
                });
                return null;
            }
        });

        LOG.info("Import take: " + (System.currentTimeMillis() - start));
    }

    public void setDbVendor(String dbVendor) {
        _dbVendor = dbVendor;
    }

}
