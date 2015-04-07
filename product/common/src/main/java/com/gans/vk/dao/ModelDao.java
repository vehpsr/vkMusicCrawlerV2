package com.gans.vk.dao;

import java.util.Collection;
import java.util.List;

import com.gans.vk.model.Model;

public interface ModelDao<T extends Model> {

    T get(long id);
    List<T> get(List<Long> ids);
    List<T> getAll();
    void refresh(T object);
    void save(T object);
    void save(Collection<T> objects);
    void delete(T object);
    void delete(Collection<T> objects);
    void flush();
}
