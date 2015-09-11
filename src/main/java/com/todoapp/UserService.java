package com.todoapp;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by User on 10.09.2015.
 */
public class UserService {
    private final Connection connection;

    public UserService(Connection connection) {
        this.connection = connection;
    }

    public void registerUser(String user,String password) throws SQLException {
        PreparedStatement preparedStatement=null;
        try {

            preparedStatement = connection.prepareStatement("INSERT INTO users(username, md5password) VALUES (?, ?);");
            preparedStatement.setString(1, user);
            preparedStatement.setString(2,toMD5(password));

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(preparedStatement!=null)
                preparedStatement.close();
        }

    }
    public boolean login(String user,String password) throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String.format("SELECT id, username, md5password FROM users WHERE \"username\"='%s' AND \"md5password\"='%s';", user, toMD5(password)));
        return resultSet.next();
    }
    public static String toMD5(String plaintext){
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(plaintext.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
// Now we need to zero pad it if you actually want the full 32 chars.
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext;
        }
    return hashtext;
    }
}
