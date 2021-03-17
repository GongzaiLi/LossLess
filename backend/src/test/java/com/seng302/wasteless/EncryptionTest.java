package com.seng302.wasteless;

import com.seng302.wasteless.User.Encryption;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class EncryptionTest {

    Encryption encryption;

    @Test
    public void verifyIncorrectPassword() {
        String correctPassword = "CorrectPassword";
        String salt = encryption.getNextSalt().toString();
        String hashedCorrectPassword = encryption.generateHashedPassword(correctPassword, salt);

        String incorrectPassword = "WrongPassword";

        Assert.assertFalse(encryption.verifyUserPassword(incorrectPassword, hashedCorrectPassword, salt));
    }

    @Test
    public void verifyCorrectPassword() {
        String correctPassword = "CorrectPassword";
        String salt = encryption.getNextSalt().toString();
        String hashedCorrectPassword = encryption.generateHashedPassword(correctPassword, salt);

        Assert.assertTrue(encryption.verifyUserPassword(correctPassword, hashedCorrectPassword, salt));
    }
}
