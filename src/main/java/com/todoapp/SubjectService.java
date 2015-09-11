package com.todoapp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 11.09.2015.
 */
public class SubjectService {
    private final Connection connection;
    public List<Subject> findAll() throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM subjects");
            while (resultSet.next()){

                subjects.add(new Subject(resultSet.getString("name")));
            }
        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            if(statement!=null)
                statement.close();
        }
        return subjects;
    }
    public SubjectService(Connection connection) {
        this.connection = connection;
    }

}
