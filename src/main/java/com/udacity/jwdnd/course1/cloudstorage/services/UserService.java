package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User service
 */
@Service
public class UserService extends BaseService<User> {

    private final UserMapper userMapper;
    private final HashService hashService;

    public UserService(UserMapper userMapper, HashService hashService) {
        super(userMapper);
        this.userMapper = userMapper;
        this.hashService = hashService;
    }

    public boolean isUsernameAvailable(String username) {
        return getByUsername(username) == null;
    }

    @Nullable
    public User getByUsername(String username) {
        return userMapper.getByUsername(username);
    }

    public OptionalInt getUserId(Authentication authentication) {
        User user = getByUsername(authentication.getName());
        OptionalInt userId = OptionalInt.empty();
        if (user != null) {
            userId = OptionalInt.of(user.getUserid());
        }
        return userId;
    }

    public int createUser(User user) {
        Pair<String, String> saltHash = hashService.saltAndHash(user.getPassword());
        return userMapper.insert(
            User.of(user.getUsername(), saltHash.getLeft(), saltHash.getRight(), user.getFirstname(), user.getLastname())
        );
    }

    @Override
    protected UserService getUserService() {
        return this;
    }

}