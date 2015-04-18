package com.gans.vk.dao.impl;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.gans.vk.dao.AbstractModelDao;
import com.gans.vk.dao.SongDao;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public class SongDaoImpl extends AbstractModelDao<Song> implements SongDao {

    private static final Log LOG = LogFactory.getLog(SongDaoImpl.class);

    private String _dbVendor;

    public SongDaoImpl() {
        super(Song.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Song> getAllUnratedSongs(final User target, final User user, final int limit) {
        final StringBuilder sql = new StringBuilder(
                "SELECT " +
                "	targetSongs.* " +
                "FROM " +
                "	(SELECT " +
                "		song.* " +
                "	FROM " +
                "		Song song " +
                "		JOIN Rating rating ON song.id = rating.song_id " +
                "	WHERE " +
                "		rating.user_id = :targetId " +
                "	) as targetSongs " +
                "WHERE " +
                "	targetSongs.id NOT IN " +
                "		(SELECT " +
                "			song.id " +
                "		FROM " +
                "			Song song " +
                "			JOIN Rating rating ON song.id = rating.song_id " +
                "		WHERE " +
                "			rating.user_id = :userId " +
                "		) ");
        if (_dbVendor.equals(MYSQL_VENDOR)) {
            sql.append("ORDER BY RAND() ");
        } else if (_dbVendor.equals(POSTGRES_VENDOR)) {
            sql.append("ORDER BY RANDOM() ");
        }

        long start = System.currentTimeMillis();
        Collection<Song> songs = getHibernateTemplate().execute(new HibernateCallback<Collection<Song>>() {
            @Override
            public Collection<Song> doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql.toString());
                query.setLong("userId", user.getId());
                query.setLong("targetId", target.getId());
                query.addEntity("song", Song.class);
                query.setMaxResults(limit);
                return query.list();
            }
        });
        LOG.info(MessageFormat.format("Select all unrated songs take: {0}", System.currentTimeMillis() - start));
        return new ArrayList<Song>(songs);
    }

    @Override
    public Map<String, Entry<Integer, Float>> getArtistData(final Collection<String> artists, final User user) {
        final String sql =
                "SELECT " +
                "	artist, COUNT(rating.value), AVG(rating.value) " +
                "FROM " +
                "	Song song " +
                "	JOIN Rating rating ON song.id = rating.song_id " +
                "WHERE " +
                "	rating.user_id = :userId " +
                "	AND artist IN (:artists) " +
                "GROUP BY " +
                "	artist ";

        Collection<Object[]> rows = getHibernateTemplate().execute(new HibernateCallback<Collection<Object[]>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Collection<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql.toString());
                query.setLong("userId", user.getId());
                query.setParameterList("artists", artists);
                return query.list();
            }
        });

        // Map<Artist, Entry<Count, AvgRating>>
        Map<String, Entry<Integer, Float>> result = new HashMap<>();
        for (Object[] row : rows) {
            result.put((String)row[0], new AbstractMap.SimpleEntry<Integer, Float>(((Number)row[1]).intValue(), ((Number)row[2]).floatValue()));
        }
        return result;
    }

    public void setDbVendor(String dbVendor) {
        _dbVendor = dbVendor;
    }

}
