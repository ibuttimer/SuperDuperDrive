package com.udacity.jwdnd.course1.cloudstorage.model;


import java.util.Objects;

/**
 * Base class for model objects
 */
public abstract class BaseModel {

    private Integer id;
    private Integer userid;

    public BaseModel() {
        clear();
    }

    public BaseModel(Integer id, Integer userid) {
        this.id = id;
        this.userid = userid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public void clear() {
        this.id = null;
        this.userid = null;
    }

    public <T extends BaseModel> void setFrom(T item) {
        this.id = item.getId();
        this.userid = item.getUserid();
    }

    public abstract <T extends BaseModel> T getInstance();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseModel baseModel = (BaseModel) o;
        return id.equals(baseModel.id) && userid.equals(baseModel.userid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userid);
    }

    /**
     * Base interface to be implemented by all database objects
     */
    public interface IBaseModel {
        Integer getId();
        Integer getUserid();
    }
}
