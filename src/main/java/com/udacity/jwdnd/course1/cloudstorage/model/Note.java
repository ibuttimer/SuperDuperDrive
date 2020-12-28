package com.udacity.jwdnd.course1.cloudstorage.model;

import java.util.Objects;

/**
 * Note model object
 */
public class Note extends BaseModel implements INoteForm, BaseModel.IBaseModel {

    private String notetitle;
    private String notedescription;

    public Note() {
        clear();
    }

    public Note(Integer noteid, String notetitle, String notedescription, Integer userid) {
        super(noteid, userid);
        this.notetitle = notetitle;
        this.notedescription = notedescription;
    }

    public static Note of(Integer noteid, String notetitle, String notedescription, Integer userid) {
        return new Note(noteid, notetitle, notedescription, userid);
    }

    public static Note of(String notetitle, String notedescription, Integer userid) {
        return of(null, notetitle, notedescription, userid);
    }

    public static Note of(Note note) {
        return of((INoteForm)note);
    }

    public static Note of(INoteForm note) {
        return of(note.getNoteid(), note.getNotetitle(), note.getNotedescription(), note.getUserid());
    }

    public static Note of() {
        return new Note();
    }

    @Override
    public Integer getNoteid() {
        return getId();
    }

    public void setNoteid(Integer noteid) {
        setId(noteid);
    }

    @Override
    public String getNotetitle() {
        return notetitle;
    }

    public void setNotetitle(String notetitle) {
        this.notetitle = notetitle;
    }

    @Override
    public String getNotedescription() {
        return notedescription;
    }

    public void setNotedescription(String notedescription) {
        this.notedescription = notedescription;
    }

    @Override
    public void clear() {
        super.clear();
        this.notetitle = null;
        this.notedescription = null;
    }

    @Override
    public <T extends IForm> void setFrom(T from) {
        if (from instanceof Note) {
            Note note = (Note) from;
            super.setFrom(note);
            this.notetitle = note.notetitle;
            this.notedescription = note.notedescription;
        }
    }

    @Override
    public Note getInstance() {
        return new Note();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        return equalsExBase(o);
    }

    public boolean equalsExBase(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(notetitle, note.notetitle) && Objects.equals(notedescription, note.notedescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), notetitle, notedescription);
    }
}