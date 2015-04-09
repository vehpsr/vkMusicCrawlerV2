package com.gans.vk.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.gans.vk.dao.AbstractModelDao;
import com.gans.vk.dao.SongDao;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public class SongDaoImpl extends AbstractModelDao<Song> implements SongDao {

    public SongDaoImpl() {
        super(Song.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Song> getAllUnratedSongs(final User user, final int limit) {
        final String sql =
                "SELECT " +
                "	song.* " +
                "FROM " +
                "	Song song " +
                "WHERE " +
                "	song.id NOT IN (" +
                "		SELECT " +
                "			song.id " +
                "		FROM " +
                "			Song song " +
                "			JOIN Rating rating ON song.id = rating.song_id " +
                "			JOIN Users u ON u.id = rating.user_id " +
                "		WHERE " +
                "			u.url = :userUrl" +
                "	)";

        Collection<Song> songs = getHibernateTemplate().execute(new HibernateCallback<Collection<Song>>() {
            @Override
            public Collection<Song> doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql);
                query.setString("userUrl", user.getUrl());
                query.addEntity("song", Song.class);
                query.setFetchSize(limit);
                return query.list();
            }
        });

        return new ArrayList<Song>(songs);
    }

}
