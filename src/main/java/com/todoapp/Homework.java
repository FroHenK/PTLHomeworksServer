package com.todoapp;

import java.util.Calendar;

/**
 * Created by User on 05.09.2015.
 */
public class Homework {
    private Integer id;
    private String  subject;
    private Calendar date;
    private String body;
    private Calendar createdOn;

    public Homework(Integer id,
                    String subject,
                    Calendar date,
                    String body,
                    Calendar createdOn) {
        this.id = id;
        this.subject = subject;
        this.date = date;
        this.body = body;
        this.createdOn = createdOn;
    }

    public Calendar getCreatedOn() {
        return createdOn;
    }

    public String getSubject() {
        return subject;
    }

    public Calendar getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return date + " " + subject + ": " + body;
    }

    public Integer getId() {
        return id;
    }
}
