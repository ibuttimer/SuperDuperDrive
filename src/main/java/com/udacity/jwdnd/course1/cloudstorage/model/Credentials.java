package com.udacity.jwdnd.course1.cloudstorage.model;

import java.util.Objects;

/**
 * Credentials model object
 */
public class Credentials extends BaseModel implements ICredentialsForm, BaseModel.IBaseModel {

    private String url;
    private String username;
    private String key;
    private String password;

    public Credentials() {
        clear();
    }

    public Credentials(Integer credentialid, String url, String username, String key, String password, Integer userid) {
        super(credentialid, userid);
        this.url = url;
        this.username = username;
        this.key = key;
        this.password = password;
    }

    public static Credentials of(Integer credentialid, String url, String username, String key, String password, Integer userid) {
        return new Credentials(credentialid, url, username, key, password, userid);
    }

    public static Credentials of(String url, String username, String key, String password, Integer userid) {
        return of(null, url, username, key, password, userid);
    }

    public static Credentials of(Credentials credentials) {
        return of(credentials.getCredentialid(), credentials.getUrl(), credentials.getUsername(), credentials.getKey(), credentials.getPassword(), credentials.getUserid());
    }

    public static Credentials of(ICredentialsForm from) {
        return of(from.getCredentialid(), from.getUrl(), from.getUsername(), null, from.getPassword(), from.getUserid());
    }

    public static Credentials of() {
        return new Credentials();
    }

    @Override
    public Integer getCredentialid() {
        return getId();
    }

    public void setCredentialid(Integer credentialid) {
        setId(credentialid);
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void clear() {
        super.clear();
        this.url = null;
        this.username = null;
        this.key = null;
        this.password = null;
    }

    @Override
    public <T extends IForm> void setFrom(T from) {
        if (from instanceof Credentials) {
            Credentials credentials = (Credentials) from;
            super.setFrom(credentials);
            this.url = credentials.url;
            this.username = credentials.username;
            this.key = credentials.key;
            this.password = credentials.password;
        }
    }

    @Override
    public Credentials getInstance() {
        return new Credentials();
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
        Credentials that = (Credentials) o;
        return Objects.equals(url, that.url) && Objects.equals(username, that.username) && Objects.equals(key, that.key) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), url, username, key, password);
    }
}
