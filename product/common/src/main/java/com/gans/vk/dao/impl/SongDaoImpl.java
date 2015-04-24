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
import org.springframework.orm.hibernate3.HibernateCallback;

import com.gans.vk.dao.AbstractModelDao;
import com.gans.vk.dao.SongDao;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public class SongDaoImpl extends AbstractModelDao<Song> implements SongDao {

    private static final Log LOG = LogFactory.getLog(SongDaoImpl.class);

    public SongDaoImpl() {
        super(Song.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SongData> getAllUnratedSongs(final User target, final User user, final int limit) {
        final String sql =
                "SELECT " +
                "	targetSongs.id, targetSongs.artist, targetSongs.title, songData.artistCount, songData.avgArtistRating " +
                "FROM " +
                "	(SELECT " +
                "		song.id, artist, title " +
                "	FROM " +
                "		Song song " +
                "		JOIN Rating rating ON song.id = rating.song_id " +
                "	WHERE " +
                "		rating.user_id = :targetId " +
                "		AND song.id NOT IN " +
                "			(SELECT " +
                "				song.id " +
                "			FROM " +
                "				Song song " +
                "				JOIN Rating rating ON song.id = rating.song_id " +
                "			WHERE " +
                "				rating.user_id = :userId " +
                "			) " +
                "	LIMIT :limit " +
                "	) as targetSongs " +
                "   LEFT JOIN " +
                "	(SELECT " +
                "		artist, COUNT(rating.value) AS artistCount, AVG(rating.value) AS avgArtistRating " +
                "	FROM " +
                "		Song song " +
                "		JOIN Rating rating ON song.id = rating.song_id " +
                "	WHERE " +
                "		rating.user_id = :userId " +
                "	GROUP BY " +
                "		artist " +
                "	) AS songData ON songData.artist = targetSongs.artist " +
                "	ORDER BY " +
                        random();

        long start = System.currentTimeMillis();
        Collection<Object[]> rows = getHibernateTemplate().execute(new HibernateCallback<Collection<Object[]>>() {
            @Override
            public Collection<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql);
                query.setLong("userId", user.getId());
                query.setLong("targetId", target.getId());
                query.setInteger("limit", limit);
                return query.list();
            }
        });
        LOG.info(MessageFormat.format("Select all unrated songs take: {0}", System.currentTimeMillis() - start));

        List<SongData> songs = new ArrayList<>();
        for (Object[] row : rows) {
            SongData song = new SongData();
            song.setId(((Number) row[0]).longValue());
            song.setArtist((String) row[1]);
            song.setTitle((String) row[2]);
            song.setArtistRateCount(row[3] == null ? 0 : ((Number) row[3]).intValue());
            song.setArtistAvgRating(row[4] == null ? 0.0f : ((Number) row[4]).floatValue());
            songs.add(song);
        }
        return songs;
    }

}
