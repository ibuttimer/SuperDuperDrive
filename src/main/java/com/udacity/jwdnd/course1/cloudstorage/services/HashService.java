package com.udacity.jwdnd.course1.cloudstorage.services;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Hash service
 */
@Component
public class HashService {

    private final Logger logger = LoggerFactory.getLogger(HashService.class);

    private final SecureRandom random = new SecureRandom();


    public String getHashedValue(String data, String salt) {
        byte[] hashedValue = null;

        KeySpec spec = new PBEKeySpec(data.toCharArray(), salt.getBytes(), 5000, 128);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hashedValue = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }

        return Base64.getEncoder().encodeToString(hashedValue);
    }

    /**
     * Get the salt and hash for the specified password
     * @param password - password to hash
     * @return Pair with salt as left/key, and hash as right/value
     */
    public Pair<String, String> saltAndHash(String password) {
        String encodedSalt = getKeyOrSalt();
        String hashedPassword = getHashedValue(password, encodedSalt);
        return Pair.of(encodedSalt, hashedPassword);
    }

    public String getKeyOrSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
