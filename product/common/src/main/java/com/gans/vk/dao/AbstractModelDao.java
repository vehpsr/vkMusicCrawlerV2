package com.gans.vk.dao;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.gans.vk.model.AbstractModel;

public abstract class AbstractModelDao<T extends AbstractModel> extends HibernateDaoSupport implements ModelDao<T> {

    protected final Class<T> _entityClass;

    public AbstractModelDao(Class<T> entityClass) {
        _entityClass = entityClass;
    }

    @Override
    protected void initDao() throws Exception {
        if (getSessionFactory().getClassMetadata(_entityClass) == null) {
            throw new IllegalArgumentException("Unable to initialize DAO implementation " +
                                               getClass().getName() + " for non-persistent class " + _entityClass.getName());
        }
    }

    /**
     * Returns objects with specified id.
     */
    @Override
    public T get(long id) {
        return (T) getHibernateTemplate().get(_entityClass, id);
    }

    /**
     * Returns objects with specified ids. Does not preserve the specified order.
     */
    @Override
    @SuppressWarnings(value = {"unchecked"})
    public List<T> get(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return (List<T>) getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(_entityClass).add(Restrictions.in("id", ids)));
    }

    public List<T> getAll() {
        return getHibernateTemplate().loadAll(_entityClass);
    }

    public void refresh(T object) {
        getHibernateTemplate().refresh(object);
    }

    @Override
    public void save(T object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    @Override
    public void save(Collection<T> objects) {
        for (T object : objects) {
            save(object);
        }
    }

    @Override
    public void delete(T object) {
        getHibernateTemplate().delete(object);
    }

    @Override
    public void delete(Collection<T> objects) {
        if (!objects.isEmpty()) {
            getHibernateTemplate().deleteAll(objects);
        }
    }

    @Override
    public void flush() {
        getHibernateTemplate().flush();
    }

    protected DetachedCriteria createCriteria() {
        return DetachedCriteria.forClass(_entityClass);
    }

    @SuppressWarnings(value = {"unchecked"})
    protected List<T> find(DetachedCriteria hibernateCriteria) {
        return (List<T>) getHibernateTemplate().findByCriteria(hibernateCriteria);
    }

    @SuppressWarnings(value = {"unchecked"})
    protected List<T> find(DetachedCriteria hibernateCriteria, int firstRow, int maxRows) {
        return (List<T>) getHibernateTemplate().findByCriteria(hibernateCriteria, firstRow, maxRows);
    }

    @SuppressWarnings(value = {"unchecked"})
    protected T findUnique(DetachedCriteria hibernateCriteria) {
        List<T> objects = (List<T>) getHibernateTemplate().findByCriteria(hibernateCriteria);
        if (objects.size() > 1) {
            throw new IllegalStateException(MessageFormat.format("Multiple objects of type {0} found while single one was expected", _entityClass.getName()));
        }
        if (objects.isEmpty()) {
            return null;
        }
        return objects.get(0);
    }

}
