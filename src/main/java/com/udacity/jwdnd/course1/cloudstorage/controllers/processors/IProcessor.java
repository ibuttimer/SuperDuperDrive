package com.udacity.jwdnd.course1.cloudstorage.controllers.processors;

import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.Action;
import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.ActionResult;
import com.udacity.jwdnd.course1.cloudstorage.model.IForm;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface to be implemented by controller processors
 * @param <F> - class of data form
 */
public interface IProcessor<F extends IForm> {

    /**
     * Process a request
     * @param userId - user id
     * @param action - action to perform
     * @param id - id of item
     * @param dataForm - data form from which to retrieve data
     * @return
     */
    ActionResult process(Integer userId, Action action, String id, F dataForm);
}
