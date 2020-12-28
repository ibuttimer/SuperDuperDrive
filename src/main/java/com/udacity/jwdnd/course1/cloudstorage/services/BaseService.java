package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.BaseMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.BaseModel;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Base service for all service objects
 */
public abstract class BaseService<T extends BaseModel.IBaseModel> {

    private final BaseMapper<T> baseMapper;

    public BaseService(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Nullable
    public T getById(Integer id) {
        return postRetrieve(
                    baseMapper.getById(id));
    }

    public int create(T entry) {
        int affected = 0;
        if (entry != null) {
            affected = baseMapper.insert(
                    prePersist(entry));
        }
        return affected;
    }

    public int update(T entry) {
        int affected = 0;
        if (entry != null) {
            affected = baseMapper.update(
                    prePersist(entry));
        }
        return affected;
    }

    public List<T> getAll() {
        return postRetrieve(
                    baseMapper.getAll());
    }

    public int deleteById(Integer id) {
        return baseMapper.deleteById(id);
    }

    public int deleteAll() {
        return baseMapper.deleteAll();
    }

    protected T prePersist(T entry) {
        // no-op
        return entry;
    }

    protected T postRetrieve(T entry) {
        // no-op
        return entry;
    }

    protected List<T> prePersist(List<T> entries) {
        return entries.stream()
                .map(this::prePersist)
                .collect(Collectors.toList());
    }

    protected List<T> postRetrieve(List<T> entries) {
        return entries.stream()
                .map(this::postRetrieve)
                .collect(Collectors.toList());
    }

    /**
     * Verify that the specified user id corresponds to the user id of the item with the specified id
     * @param userId - user id
     * @param id - item id
     */
    public Pair<Boolean, T> verifyAuthentication(Integer userId, String id) {
        T item = getById(Integer.parseInt(id));
        return Pair.of((item != null && item.getUserid().equals(userId)), item);
    }

    /**
     * Verify that the specified authentication corresponds to the user id of the item with the specified id
     * @param authentication - authentication
     * @param id - item id
     */
    public Pair<Boolean, T> verifyAuthentication(Authentication authentication, String id) {
        AtomicReference<Pair<Boolean, T>> result = new AtomicReference<>(Pair.of(false, null));
        getUserService().getUserId(authentication).ifPresent(uId -> {
            if (authentication.isAuthenticated()) {
                result.set(verifyAuthentication(uId, id));
            }
        });
        return result.get();
    }

    // requirement to avoid cyclical dependencies that occur if user service is injected into base service
    protected abstract UserService getUserService();
}