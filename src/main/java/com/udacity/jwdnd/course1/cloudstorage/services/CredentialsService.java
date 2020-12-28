package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialsMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credentials;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Credentials service
 */
@Service
public class CredentialsService extends BaseService<Credentials> {

    private final CredentialsMapper credentialsMapper;
    private final EncryptionService encryptionService;
    private final HashService hashService;
    private final UserService userService;

    public CredentialsService(CredentialsMapper credentialsMapper,
                              EncryptionService encryptionService,
                              HashService hashService,
                              UserService userService) {
        super(credentialsMapper);
        this.credentialsMapper = credentialsMapper;
        this.encryptionService = encryptionService;
        this.hashService = hashService;
        this.userService = userService;
    }

    public Credentials getByUrl(String url) {
        return credentialsMapper.getByUrl(url);
    }

    @Nullable
    public Credentials getCredentialsPlainText(Integer credentialid) {
        Credentials credentials = getById(credentialid);
        return convertCredentialsPlainText(credentials);
    }

    public List<Credentials> getAllForUser(Integer id) {
        return postRetrieve(
                credentialsMapper.getByUserId(id));
    }

    public int countByUserId(Integer id) {
        return credentialsMapper.countByUserId(id);
    }

    @Override
    public Credentials prePersist(Credentials entry) {
        entry.setKey(hashService.getKeyOrSalt());
        entry.setPassword(
                encryptionService.encryptValue(entry.getPassword(), entry.getKey()));
        return entry;
    }

    public Credentials convertCredentialsPlainText(Credentials entry) {
        if (entry != null) {
            entry.setPassword(
                    encryptionService.decryptValue(entry.getPassword(), entry.getKey()));
        }
        return entry;
    }

    @Override
    protected UserService getUserService() {
        return userService;
    }
}