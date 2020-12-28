package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Note service
 */
@Service
public class NoteService extends BaseService<Note> {

    private final NoteMapper noteMapper;
    private final UserService userService;

    public NoteService(NoteMapper noteMapper, UserService userService) {
        super(noteMapper);
        this.noteMapper = noteMapper;
        this.userService = userService;
    }

    @Nullable
    public Note getNote(String notetitle) {
        return noteMapper.getByTitle(notetitle);
    }

    @Nullable
    public Note getNote(Integer noteid) {
        return getById(noteid);
    }

    public List<Note> getAllForUser(Integer id) {
        return noteMapper.getByUserId(id);
    }

    public int countByUserId(Integer id) {
        return noteMapper.countByUserId(id);
    }

    @Override
    protected UserService getUserService() {
        return userService;
    }
}