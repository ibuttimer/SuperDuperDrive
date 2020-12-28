package com.udacity.jwdnd.course1.cloudstorage.mapper;

import java.util.List;

/**
 * Base mapper interface
 * @param <T> - class of spring model object
 */
public interface BaseMapper<T> {

    T getById(Integer id);

    List<T> getAll();

    int insert(T entry);

    int update(T entry);

    int deleteById(Integer id);

    int deleteAll();
}
