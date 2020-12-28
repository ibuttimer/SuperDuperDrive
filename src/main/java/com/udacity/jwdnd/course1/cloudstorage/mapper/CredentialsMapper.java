package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Credentials;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Mapper for credentials
 */
@Mapper
public interface CredentialsMapper extends BaseMapper<Credentials> {

    @Override
    @Select("SELECT * FROM CREDENTIALS WHERE credentialid = #{id}")
    Credentials getById(Integer id);

    @Select("SELECT * FROM CREDENTIALS WHERE userid = #{id}")
    List<Credentials> getByUserId(Integer id);

    @Select("SELECT * FROM CREDENTIALS WHERE url = #{url}")
    Credentials getByUrl(String url);

    @Override
    @Select("SELECT * FROM CREDENTIALS")
    List<Credentials> getAll();

    @Select("SELECT COUNT(*) AS cnt FROM CREDENTIALS WHERE userid = #{id}")
    int countByUserId(Integer id);

    @Override
    @Insert("INSERT INTO CREDENTIALS (url, username, key, password, userid) " +
            "VALUES(#{url}, #{username}, #{key}, #{password}, #{userid})")
    @Options(useGeneratedKeys = true, keyProperty = "credentialid")
    int insert(Credentials entry);

    @Override
    @Update("UPDATE CREDENTIALS SET url = #{url}, username = #{username}, key = #{key}, password = #{password} " +
            "WHERE userid = #{userid} AND credentialid = #{credentialid}")
    int update(Credentials entry);

    @Override
    @Delete("DELETE FROM CREDENTIALS WHERE credentialid = #{id}")
    int deleteById(Integer id);

    @Delete("DELETE FROM CREDENTIALS WHERE userid = #{id}")
    int deleteByUserId(Integer id);

    @Override
    @Delete("DELETE FROM CREDENTIALS")
    int deleteAll();
}
