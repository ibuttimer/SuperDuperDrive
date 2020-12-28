package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Mapper for notes
 */
@Mapper
public interface NoteMapper extends BaseMapper<Note> {

    @Override
    @Select("SELECT * FROM NOTES WHERE noteid = #{id}")
    Note getById(Integer id);

    @Select("SELECT * FROM NOTES WHERE userid = #{id}")
    List<Note> getByUserId(Integer id);

    @Select("SELECT * FROM NOTES WHERE notetitle = #{notetitle}")
    Note getByTitle(String notetitle);

    @Override
    @Select("SELECT * FROM NOTES")
    List<Note> getAll();

    @Select("SELECT COUNT(*) AS cnt FROM NOTES WHERE userid = #{id}")
    int countByUserId(Integer id);

    @Override
    @Insert("INSERT INTO NOTES (notetitle, notedescription, userid) " +
            "VALUES(#{notetitle}, #{notedescription}, #{userid})")
    @Options(useGeneratedKeys = true, keyProperty = "noteid")
    int insert(Note entry);

    @Override
    @Update("UPDATE NOTES SET notetitle = #{notetitle}, notedescription = #{notedescription} " +
            "WHERE userid = #{userid} AND noteid = #{noteid}")
    int update(Note entry);

    @Override
    @Delete("DELETE FROM NOTES WHERE noteid = #{id}")
    int deleteById(Integer id);

    @Delete("DELETE FROM NOTES WHERE userid = #{id}")
    int deleteByUserId(Integer id);

    @Override
    @Delete("DELETE FROM NOTES")
    int deleteAll();
}
