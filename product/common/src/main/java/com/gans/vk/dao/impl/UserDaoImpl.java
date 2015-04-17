package com.gans.vk.dao.impl;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
            "	u.id, u.name, u.url, u.vkId, totalQuery.total, ratedQuery.rated " +
            "FROM " +
            "	(SELECT " +
            "		rating.user_id, count(rating.song_id) as total " +
            "	FROM " +
            "		Users u " +
            "		LEFT JOIN Rating rating ON rating.user_id = u.id " +
            "	WHERE " +
            "		u.id <> :userId " +
            "	GROUP BY " +
            "		u.id " +
            "	) AS totalQuery " +
            "	LEFT JOIN " +
            "	(SELECT " +
            "		rating.user_id, count(*) as rated " +
            "	FROM " +
            "		(SELECT song_id FROM Rating WHERE user_id = :userId) as ratedByUser " +
            "		JOIN Rating rating on rating.song_id = ratedByUser.song_id " +
            "	WHERE " +
            "		rating.user_id <> :userId " +
            "	GROUP BY " +
            "		rating.user_id " +
            "	) AS ratedQuery " +
            "	ON totalQuery.user_id = ratedQuery.user_id " +
            "	RIGHT JOIN Users u on u.id = totalQuery.user_id " +
            "WHERE " +
            "	u.id <> :userId " +
            "ORDER BY " +
            "	ratedQuery.rated DESC";

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

        List<UserLibData> userData = new ArrayList<>();
        for (Object[] row : resultSet) {
            UserLibData data = new UserLibData();
            data.setId(((BigInteger)row[0]).longValue());
            data.setName((String)row[1]);
            data.setUrl((String)row[2]);
            data.setVkId((String)row[3]);
            data.setTotalAudioCount(row[4] == null ? 0 : ((BigInteger)row[4]).intValue());
            data.setRatedAudioCount(row[5] == null ? 0 : ((BigInteger)row[5]).intValue());
            userData.add(data);
        }
        return userData;
    }

}
