package com.udacity.jwdnd.course1.cloudstorage.model;

public interface IForm {

    void clear();

    Integer getId();

    Integer getUserid();

    <T extends IForm> void setFrom(T from);
}
