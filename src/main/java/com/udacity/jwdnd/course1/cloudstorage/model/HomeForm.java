package com.udacity.jwdnd.course1.cloudstorage.model;

import org.springframework.web.multipart.MultipartFile;

/**
 * Class to transfer data from webpage to controllers
 */
public class HomeForm implements ICredentialsForm, INoteForm, IFileForm {

    private Integer noteid;
    private String notetitle;
    private String notedescription;

    private Integer credentialid;
    private String url;
    private String username;
    private String password;

    private MultipartFile multipartFile;

    private Integer userid;

    private String action;

    public HomeForm() {
        clear();
    }

    private void init(Integer noteid, String notetitle, String notedescription,
                      Integer credentialid, String url, String username, String password,
                      MultipartFile multipartFile,
                      Integer userid, String action) {
        this.noteid = noteid;
        this.notetitle = notetitle;
        this.notedescription = notedescription;

        this.credentialid = credentialid;
        this.url = url;
        this.username = username;
        this.password = password;

        this.multipartFile = multipartFile;

        this.userid = userid;

        this.action = action;
    }

    // note related

    @Override
    public Integer getNoteid() {
        return noteid;
    }

    public void setNoteid(Integer noteid) {
        this.noteid = noteid;
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

    // credential related

    @Override
    public Integer getCredentialid() {
        return credentialid;
    }

    public void setCredentialid(Integer credentialid) {
        this.credentialid = credentialid;
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

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // file related

    @Override
    public Integer getFileid() {
        return null;
    }

    @Override
    public String getFilename() {
        return null;
    }

    @Override
    public String getContenttype() {
        return null;
    }

    @Override
    public String getFilesize() {
        return null;
    }

    @Override
    public byte[] getFiledata() {
        return new byte[0];
    }

    @Override
    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    // general

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public void clear() {
        init(null, null, null,
                null, null, null, null,
                null,
                null, null);
    }

    @Override
    public <T extends IForm> void setFrom(T from) {
        if (from instanceof Credentials) {
            Credentials credentials = (Credentials) from;
            this.credentialid = credentials.getCredentialid();
            this.url = credentials.getUrl();
            this.username = credentials.getUsername();
            this.password = credentials.getPassword();
        } else if (from instanceof Note) {
            Note note = (Note) from;
            this.noteid = note.getNoteid();
            this.notetitle = note.getNotetitle();
            this.notedescription = note.getNotedescription();
        } else if (from instanceof File) {
            File file = (File) from;
            this.multipartFile = file.getMultipartFile();
        }
        this.userid = from.getUserid();
    }

}
