package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Mapper for users
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Override
    @Select("SELECT * FROM USERS WHERE userid = #{userid}")
    User getById(Integer userid);

    @Select("SELECT * FROM USERS WHERE username = #{username}")
    User getByUsername(String username);

    @Override
    @Select("SELECT * FROM USERS")
    List<User> getAll();

    @Override
    @Insert("INSERT INTO USERS (username, salt, password, firstname, lastname) " +
            "VALUES(#{username}, #{salt}, #{password}, #{firstname}, #{lastname})")
    @Options(useGeneratedKeys = true, keyProperty = "userid")
    int insert(User user);

    @Override
    @Update("UPDATE USERS SET notetitle = #{notetitle}, notedescription = #{notedescription} " +
            "WHERE userid = #{userid}")
    int update(User entry);

    @Override
    @Delete("DELETE FROM USERS WHERE userid = #{userid}")
    int deleteById(Integer userid);

    @Override
    @Delete("DELETE FROM USERS")
    int deleteAll();
}
