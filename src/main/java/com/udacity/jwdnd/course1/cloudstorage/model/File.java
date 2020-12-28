package com.udacity.jwdnd.course1.cloudstorage.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * File model object
 */
public class File extends BaseModel implements IFileForm, BaseModel.IBaseModel {

    private String filename;
    private String contenttype;
    private String filesize;
    private byte[] filedata;

    private MultipartFile multipartFile;

    public File() {
        clear();
    }

    public static File of(Integer fileid, String filename, String contenttype, String filesize, byte[] filedata, Integer userid) {
        // multi-argument constructor doesn't seem to work with MyBatis
        File file =  new File();
        file.setFileid(fileid);
        file.filename = filename;
        file.contenttype = contenttype;
        file.filesize = filesize;
        file.filedata = filedata;
        file.setUserid(userid);
        return file;
    }

    public static File of(String filename, String contenttype, String filesize, byte[] filedata, Integer userid) {
        return of(null, filename, contenttype, filesize, filedata, userid);
    }

    public static File of(File file) {
        return of((IFileForm) file);
    }

    public static File of(IFileForm file) {
        return of(file.getFileid(), file.getFilename(), file.getContenttype(), file.getFilesize(), file.getFiledata(), file.getUserid());
    }

    public static File of() {
        return new File();
    }

    public static Optional<File> of(MultipartFile uploadFile, Integer userid) {
        Optional<File> file = Optional.empty();
        try {
            file = Optional.of(
                    of(uploadFile.getOriginalFilename(), uploadFile.getContentType(), Long.toString(uploadFile.getSize()),
                            uploadFile.getBytes(), userid)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    @Override
    public Integer getFileid() {
        return getId();
    }

    public void setFileid(Integer fileid) {
        setId(fileid);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public byte[] getFiledata() {
        return filedata;
    }

    public void setFiledata(byte[] filedata) {
        this.filedata = filedata;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    @Override
    public void clear() {
        super.clear();
        this.filename = null;
        this.contenttype = null;
        this.filesize = null;
        this.filedata = null;
        this.multipartFile = null;
    }

    @Override
    public <T extends IForm> void setFrom(T from) {
        if (from instanceof File) {
            File file = (File) from;
            super.setFrom(file);
            this.filename = file.filename;
            this.contenttype = file.contenttype;
            this.filesize = file.filesize;
            this.filedata = file.filedata;
            this.multipartFile = file.multipartFile;
        }
    }

    @Override
    public File getInstance() {
        return new File();
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
        File file = (File) o;
        return Objects.equals(filename, file.filename) && Objects.equals(contenttype, file.contenttype)
                && Objects.equals(filesize, file.filesize) && Arrays.equals(filedata, file.filedata)
                && Objects.equals(multipartFile, file.multipartFile);
    }


    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), filename, contenttype, filesize, multipartFile);
        result = 31 * result + Arrays.hashCode(filedata);
        return result;
    }
}