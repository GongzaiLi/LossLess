package org.seng302.example.Logins;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class Encryption {

    private static final Random RANDOM = new SecureRandom();
    /**
     * Generates random data of size 16 bytes.
     *
     * @return a random salt of size 16 bytes
     */
    public static byte[] getNextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * Generates a hash out of the combined salt and given password using the SHA-512 algorithm.
     *
     * @param password the password the user has given
     * @param salt a randomly generated string of data
     * @return a hashed salt/password combination
     * @throws NoSuchAlgorithmException if a SHA-512 algorithm is not available in the environment
     */
    public static String generateHashedPassword(String password, String salt) {

        String hashedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder passwordHash = new StringBuilder();
            for(int i=0; i < bytes.length; i++){
                passwordHash.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashedPassword = passwordHash.toString();
        } catch (NoSuchAlgorithmException e) {

            //Need to decide what to do here
            e.printStackTrace();
        }

        return hashedPassword;
    }

    /**
     * Verifies that a password used by a user is the same as the one they originally created.
     *
     * @param givenPassword a password to be tested for validity
     * @param savedPassword the original password
     * @param salt the original salt
     * @return a boolean, true if the password is correct, false otherwise
     * @throws NoSuchAlgorithmException if a SHA-512 algorithm is not available in the environment
     */
    public static boolean verifyUserPassword(String givenPassword, String savedPassword, String salt) {
        boolean verified = false;

        // Generate a new hash with the salt saved in the database
        String newSecurePassword = generateHashedPassword(givenPassword, salt);

        // Check if the hashed password in the database is the same as the one just created
        verified = newSecurePassword.equalsIgnoreCase(savedPassword);

        return verified;
    }

}