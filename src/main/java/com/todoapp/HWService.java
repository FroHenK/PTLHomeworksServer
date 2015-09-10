package com.todoapp;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by User on 05.09.2015.
 */
public class HWService {
    private static final String TABLE_HOMEWORKS = "homeworks";
    public static final String HOMEWORK_ID="id",
            HOMEWORK_BODY="body",
            HOMEWORK_SUBJECT="subject",
            HOMEWORK_CREATED_ON="createdOn",
            HOMEWORK_DATE_FOR="dateFor";


    private Connection connection;
    private final SimpleDateFormat dateFormat;

    public HWService(Connection connection) {
        this.connection = connection;
        Calendar cal = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);

        }
    private static Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }
    public List<Homework> findAll() throws SQLException {
        List<Homework> homeworks = new ArrayList<>();
        /*DBCursor dbObjects = homeworksCollection.find();
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();
            homeworks.add(new Homework((BasicDBObject) dbObject));
        }*/
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TABLE_HOMEWORKS);
            while (resultSet.next()){

                homeworks.add(parseHomework(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if(statement!=null)
                statement.close();
        }
        return homeworks;
    }
    public void insert(String subject,String body,
                       Calendar date
                       ) throws SQLException {
        PreparedStatement preparedStatement=null;
        try {

            preparedStatement = connection.prepareStatement("INSERT INTO homeworks(body, subject, \"createdOn\", \"dateFor\") VALUES (?, ?, ?, ?);");
            preparedStatement.setString(1, body);
            preparedStatement.setString(2,subject);
            preparedStatement.setString(3, formatCalendar(Calendar.getInstance()));
            preparedStatement.setString(4, formatCalendar(date));
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(preparedStatement!=null)
                preparedStatement.close();
        }
    }

    private String formatCalendar(Calendar date) {
        return dateFormat.format(Date.from(date.toInstant()));
    }

    private Homework parseHomework(ResultSet resultSet) throws SQLException, ParseException {
        Integer id= resultSet.getInt(HOMEWORK_ID);
        String subject=resultSet.getString(HOMEWORK_SUBJECT);
        String dateFor=resultSet.getString(HOMEWORK_DATE_FOR);
        String createdOn=resultSet.getString(HOMEWORK_CREATED_ON);
        String body=resultSet.getString(HOMEWORK_BODY);
        return new Homework(id,subject,dateToCalendar(dateFormat.parse(dateFor)),body,dateToCalendar(dateFormat.parse(createdOn)));
    }
    /*
    public List<Homework> find(Calendar calendar) {
        List<Homework> homeworks = new ArrayList<>();
        DBCursor dbObjects = homeworksCollection.find();
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();

            Homework homework = new Homework((BasicDBObject) dbObject);
            if (homework.getDate().getMonth() == date.getMonth() &&
                    homework.getDate().getDay() == date.getDay() &&
                    homework.getDate().getYear() == date.getYear())
                homeworks.add(homework);
        }

        return homeworks;
    }*/



}
