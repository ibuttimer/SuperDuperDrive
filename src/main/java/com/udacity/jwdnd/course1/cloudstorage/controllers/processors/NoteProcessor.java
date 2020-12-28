package com.udacity.jwdnd.course1.cloudstorage.controllers.processors;

import com.udacity.jwdnd.course1.cloudstorage.model.INoteForm;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.BaseService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import org.springframework.stereotype.Component;

/**
 * Processor for notes
 */
@Component
public class NoteProcessor extends BaseProcessor<Note, INoteForm> implements IProcessor<INoteForm> {

    private final NoteService noteService;

    public NoteProcessor(NoteService noteService) {
        this.noteService = noteService;
    }

    @Override
    protected Note of() {
        return new Note();
    }

    @Override
    protected Note of(INoteForm from) {
        return Note.of(from);
    }

    @Override
    protected Note ofExId(INoteForm from, Integer userId) {
        Note note = Note.of(from);
        note.setUserid(userId);
        return note;
    }

    @Override
    protected BaseService<Note> getService() {
        return noteService;
    }
}
