package com.udacity.jwdnd.course1.cloudstorage.controllers.processors;

import com.udacity.jwdnd.course1.cloudstorage.model.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.model.ICredentialsForm;
import com.udacity.jwdnd.course1.cloudstorage.services.BaseService;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialsService;
import org.springframework.stereotype.Component;

/**
 * Processor for credentials
 */
@Component
public class CredentialsProcessor extends BaseProcessor<Credentials, ICredentialsForm> implements IProcessor<ICredentialsForm> {

    private final CredentialsService credentialsService;

    public CredentialsProcessor(CredentialsService credentialsService) {
        this.credentialsService = credentialsService;
    }

    @Override
    protected Credentials of() {
        return Credentials.of();
    }

    @Override
    protected Credentials of(ICredentialsForm from) {
        return Credentials.of(from);
    }

    @Override
    protected Credentials ofExId(ICredentialsForm from, Integer userId) {
        Credentials credentials = Credentials.of(from);
        credentials.setUserid(userId);
        return credentials;
    }

    @Override
    protected BaseService<Credentials> getService() {
        return credentialsService;
    }

}
