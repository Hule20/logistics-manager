package com.karlohusak.logisticsmanager.files;

import javafx.scene.control.Alert;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;

public class LoginHelper {

    private static final String USER_INFO_FILE = "dat/user-info.txt";

    public static String user;

    public static boolean checkCredentials(String enteredUsername, String enteredPassword) throws NoSuchAlgorithmException, FileNotFoundException {
        boolean isApproved = false;

        String lineFound = "";
            try (BufferedReader in = new BufferedReader(new FileReader(USER_INFO_FILE))) {
                String datLine;
                while ((datLine = in.readLine()) != null) {
                    datLine = datLine.replaceAll("\\s", "");
                    boolean doesContain = datLine.contains(enteredUsername);
                    if (doesContain) {
                        lineFound = datLine;
                        break;
                    } else {
                        isApproved = false;
                        System.out.println("Not found!");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(enteredPassword.getBytes());
        byte[] digest = md.digest();

        String hashedEntry = String
                .format("%032X", new BigInteger(1, digest))
                .toLowerCase();

        if(lineFound.contains(hashedEntry)){
            isApproved = true;
        }

        user = enteredUsername;
        return isApproved;
    }
}
