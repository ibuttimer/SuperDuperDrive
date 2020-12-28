package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.ByteObjectArrayTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * Mapper for files
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {

    @Override
    @Select("SELECT * FROM FILES WHERE fileid = #{id}")
    File getById(Integer id);

    @Select("SELECT * FROM FILES WHERE userid = #{id}")
    List<File> getByUserId(Integer id);

    @Select("SELECT * FROM FILES WHERE filename = #{filename}")
    File getByFilename(String filename);

    @Override
    @Select("SELECT * FROM FILES")
    List<File> getAll();

    @Select("SELECT COUNT(*) AS cnt FROM FILES WHERE userid = #{id}")
    int countByUserId(Integer id);

    @Override
    @Insert("INSERT INTO FILES (filename, contenttype, filesize, filedata, userid) " +
            "VALUES(#{filename}, #{contenttype}, #{filesize}, #{filedata}, #{userid})")
    @Options(useGeneratedKeys = true, keyProperty = "fileid")
    int insert(File entry);

    @Override
    @Update("UPDATE FILES SET filename = #{filename}, contenttype = #{contenttype}, filesize = #{filesize}, filedata = #{filedata} " +
            "WHERE userid = #{userid} AND fileid = #{fileid}")
    int update(File entry);

    @Override
    @Delete("DELETE FROM FILES WHERE fileid = #{id}")
    int deleteById(Integer id);

    @Delete("DELETE FROM FILES WHERE userid = #{id}")
    int deleteByUserId(Integer id);

    @Override
    @Delete("DELETE FROM FILES")
    int deleteAll();
}
