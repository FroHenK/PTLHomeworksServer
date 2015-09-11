package com.todoapp;

/**
 * Created by User on 10.09.2015.
 */
public class Error {
    private String name;
    private String title;
    public Error(String name,String title) {
        this.name = name;
        this.title=title;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }
}
