package com.udacity.jwdnd.course1.cloudstorage.controllers.processors;

import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.Action;
import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.ActionResult;
import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.ErrorCode;
import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import com.udacity.jwdnd.course1.cloudstorage.model.BaseModel;
import com.udacity.jwdnd.course1.cloudstorage.model.IForm;
import com.udacity.jwdnd.course1.cloudstorage.services.BaseService;
import org.apache.commons.lang3.tuple.Pair;
import org.thymeleaf.util.StringUtils;

import java.util.OptionalInt;

/**
 * Base controller processor class
 * @param <T> - class of spring model object
 * @param <F> - class of data form
 */
public abstract class BaseProcessor<T extends BaseModel.IBaseModel, F extends IForm> implements IProcessor<F> {

    @Override
    public ActionResult process(Integer userId, Action action, String id, F dataForm) {
        int affected = 0;
        ErrorCode error = ErrorCode.none;

        if (isSupported(action)) {
            switch (action) {
                case update:
                case read:
                case delete:
                    Pair<Boolean, T> verification = getService().verifyAuthentication(userId, id);
                    if (!verification.getLeft()) {
                        if (verification.getRight() != null) {
                            error = ErrorCode.unauthorised;
                        } else {
                            error = ErrorCode.badrequest;
                        }
                        dataForm.clear();
                    }
                    break;
                default:
                    break;
            }
        } else {
            error = ErrorCode.unsupported;
        }

        if (error == ErrorCode.none) {
            switch (action) {
                case create:
                    affected = getService().create(
                            ofExId(dataForm, userId)
                    );
                    dataForm.clear();
                    break;
                case update:
                    affected = getService().update(of(dataForm));
                    dataForm.clear();
                    break;
                case read:
                    dataForm.clear();
                    T item = getService().getById(Integer.valueOf(id));
                    if (item != null) {
                        dataForm.setFrom((IForm) item);
                        affected = 1;
                    }
                    break;
                case delete:
                    affected = getService().deleteById(Integer.valueOf(id));
                    dataForm.clear();
                    break;
                default:
                    error = ErrorCode.badrequest;
                    break;
            }
        }

        return getResult(userId, affected, error);
    }

    /**
     * Generate a result object
     * @param userId - user id
     * @param affected - number of database rows affected
     * @param error - error code
     * @return
     */
    protected ActionResult getResult(Integer userId, int affected, ErrorCode error) {
        ActionResult result;
        if (affected > 0) {
            result = ActionResult.ofSuccess(affected, OptionalInt.of(userId));
        } else {
            if (error == ErrorCode.none) {
                result = ActionResult.ofNotSaved(affected, OptionalInt.of(userId));
            } else {
                result = ActionResult.ofError(affected, OptionalInt.of(userId), error);
            }
        }
        return result;
    }

    /**
     * Check if the specified action is supported.
     * This method should be overridden by subclasses as required
     * @param action - action to check
     * @return
     */
    protected boolean isSupported(Action action) {
        boolean supported;
        switch (action) {
            case create:
            case update:
            case read:
            case delete:
                supported = true;
                break;
            default:
                supported = false;
                break;
        }
        return supported;
    }

    /**
     * Empty model object
     * @return
     */
    protected abstract T of();

    /**
     * Model object from specified data form
     * @param from - data form
     * @return
     */
    protected abstract T of(F from);

    /**
     * Model object from specified data form and specified user id
     * @param from - data form
     * @param userId - user id
     * @return
     */
    protected abstract T ofExId(F from, Integer userId);

    /**
     * Get the service for this processor
     * @return
     */
    protected abstract BaseService<T> getService();
}
