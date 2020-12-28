package com.udacity.jwdnd.course1.cloudstorage.model;

import org.springframework.web.multipart.MultipartFile;

public interface IFileForm extends IForm {

    Integer getFileid();

    String getFilename();

    String getContenttype();

    String getFilesize();

    byte[] getFiledata();

    MultipartFile getMultipartFile();
}
