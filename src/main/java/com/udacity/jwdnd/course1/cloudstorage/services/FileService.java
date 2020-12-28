package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * File service
 */
@Service
public class FileService extends BaseService<File> {

    private final FileMapper fileMapper;
    private final UserService userService;

    public FileService(FileMapper fileMapper, UserService userService) {
        super(fileMapper);
        this.fileMapper = fileMapper;
        this.userService = userService;
    }

    public boolean isFilenameAvailable(String filename) {
        return getByFilename(filename) == null;
    }

    @Nullable
    public File getByFilename(String filename) {
        return fileMapper.getByFilename(filename);
    }

    public List<File> getAllForUser(Integer id) {
        return fileMapper.getByUserId(id);
    }

    public int countByUserId(Integer id) {
        return fileMapper.countByUserId(id);
    }


    @Override
    protected UserService getUserService() {
        return userService;
    }
}