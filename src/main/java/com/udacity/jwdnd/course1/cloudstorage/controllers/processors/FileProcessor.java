package com.udacity.jwdnd.course1.cloudstorage.controllers.processors;

import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.Action;
import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.ActionResult;
import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.ErrorCode;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.IFileForm;
import com.udacity.jwdnd.course1.cloudstorage.services.BaseService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Processor for files
 */
@Component
public class FileProcessor extends BaseProcessor<File, IFileForm> implements IProcessor<IFileForm> {

    private final FileService fileService;

    public FileProcessor(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public ActionResult process(Integer userId, Action action, String id, IFileForm dataForm) {
        int affected = 0;
        ActionResult result = null;
        ErrorCode error = ErrorCode.none;

        switch (action) {
            case create:
                affected = getService().create(
                        ofFile(dataForm.getMultipartFile(), userId)
                );
                break;
            case read:
            case delete:
                result = super.process(userId, action, id, dataForm);
                break;
            default:
                error = ErrorCode.unsupported;
                break;
        }

        if (result == null) {
            result = getResult(userId, affected, error);
        }
        return result;
    }

    @Override
    protected boolean isSupported(Action action) {
        boolean supported;
        switch (action) {
            case create:
            case read:
            case delete:
                supported = true;
                break;
            case update:
            default:
                supported = false;
                break;
        }
        return supported;
    }

    @Override
    protected File of() {
        return new File();
    }

    @Override
    protected File of(IFileForm from) {
        return File.of(from);
    }

    @Override
    protected File ofExId(IFileForm from, Integer userId) {
        File file = File.of(from);
        file.setUserid(userId);
        return file;
    }

    protected File ofFile(MultipartFile from, Integer userId) {
        AtomicReference<File> file = new AtomicReference<>(null) ;
        File.of(from, userId).ifPresent(file::set);
        return file.get();
    }

    @Override
    protected BaseService<File> getService() {
        return fileService;
    }
}
