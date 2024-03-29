/**
 * Copyright 2017 Sean Kavanagh - sean.p.kavanagh6@gmail.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.bastillion.manage.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class EncryptionUtil {
    public static final String ENCRYPTION_KEY_NM;
    public static final String KEYBOX_ENCRYPTION_KEY = "KEYBOX-ENCRYPTION_KEY";
    public static final String EC2BOX_ENCRYPTION_KEY = "EC2BOX-ENCRYPTION_KEY";
    private static final byte[] keybox = KeyStoreUtil.getSecretBytes(KEYBOX_ENCRYPTION_KEY);
    private static final byte[] ec2box = KeyStoreUtil.getSecretBytes(EC2BOX_ENCRYPTION_KEY);
    private static final byte[] key;

    static {
       if(keybox != null && keybox.length > 0) {
           key = keybox;
           ENCRYPTION_KEY_NM = KEYBOX_ENCRYPTION_KEY;
       } else {
           key = ec2box;
           ENCRYPTION_KEY_NM = EC2BOX_ENCRYPTION_KEY;
       }
    }
    private EncryptionUtil() {
    }

    public static String generateSalt() {
        byte[] salt = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        return new String(Base64.encodeBase64(salt));
    }

    public static String hash(String str, String salt) {
        String hash = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            if (StringUtils.isNotEmpty(salt)) {
                md.update(Base64.decodeBase64(salt.getBytes()));
            }

            md.update(str.getBytes("UTF-8"));
            hash = new String(Base64.encodeBase64(md.digest()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return hash;
    }

    public static String hash(String str) {
        return hash(str, (String)null);
    }

    public static String encrypt(String str) {
        String retVal = null;
        if (str != null && str.length() > 0) {
            try {
                Cipher c = Cipher.getInstance("AES");
                c.init(1, new SecretKeySpec(key, "AES"));
                byte[] encVal = c.doFinal(str.getBytes());
                retVal = new String(Base64.encodeBase64(encVal));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return retVal;
    }

    public static String decrypt(String str) {
        String retVal = null;
        if (str != null && str.length() > 0) {
            try {
                Cipher c = Cipher.getInstance("AES");
                c.init(2, new SecretKeySpec(key, "AES"));
                byte[] decodedVal = Base64.decodeBase64(str.getBytes());
                retVal = new String(c.doFinal(decodedVal));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return retVal;
    }
}
