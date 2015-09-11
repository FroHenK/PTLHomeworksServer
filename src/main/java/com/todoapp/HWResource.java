package com.todoapp;

/**
 * Created by User on 10.09.2015.
 */

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import spark.ModelAndView;
import spark.Request;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.StringWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static spark.Spark.*;

public class HWResource {
    private static final String SESSION = "session";
    private static final String SESSION_VALUE = "somesessionqwerty";
    private final UserService userService;
    private final SubjectService subjectService;
    private final Logger logger;
    private SimpleDateFormat dateFormat;
    private HWService service;
    private Gson gson;

    public HWResource(HWService service, UserService userService, SubjectService subjectService) {
        this.subjectService = subjectService;
        this.userService = userService;
        this.service = service;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        gson = new Gson();
        logger = Logger.getLogger("HWRes");
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                System.out.println(record.getMessage());
            }

            @Override
            public void flush() {
                //We don't need flush here
            }

            @Override
            public void close() throws SecurityException {

            }
        });
        initHW();
        initAdmin();
    }

    private void initAdmin() {

        get("/login", (request, response) -> {
            HashMap<String, Object> map = new HashMap<String, Object>();
            return new MustacheTemplateEngine().render(new ModelAndView(map, "template/login.mustache"));
        });
        post("/login", (request, response) -> {
            HashMap<String, Object> map = new HashMap<String, Object>();
            try {
                if (userService.login(request.queryParams("username"), request.queryParams("password"))) {
                    //TODO Make better session system
                    response.cookie(SESSION, SESSION_VALUE);
                    return new MustacheTemplateEngine().render(new ModelAndView(map, "template/main.mustache"));
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Some error", e);
                response.status(500);
            }

            map.put("errors", new Error("Wrong password or login", "Error"));
            return new MustacheTemplateEngine().render(new ModelAndView(map, "template/main.mustache"));
        });
    }

    private void initHW() {
        get("/", (request, response) -> {

            HashMap<String, Object> map = new HashMap<String, Object>();
            return new MustacheTemplateEngine().render(new ModelAndView(map, "template/main.mustache"));
        });

        //TODO get recent homeworks
        get("/recentHomeworks", (request, response) -> {

            HashMap<String, Object> map = new HashMap<String, Object>();
            try {
                List<Homework> homeworkList = service.findAll();
                Collections.sort(homeworkList, (o1, o2) -> o1.getDate().get(Calendar.MILLISECOND) - o2.getDate().get(Calendar.MILLISECOND));

                map.put("homeworks", homeworkList);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Some error", e);
                response.status(500);
                map.put("errors", new Error(e.toString(), "Error"));
            }

            return new MustacheTemplateEngine().render(new ModelAndView(map, "template/recentHomeworks.mustache"));
        });

        get("/get_homework/", (request, response) -> {

            String requestDate = request.queryParams("date");
            if (requestDate.length() == 0) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("errors", new Error("You didn't enter date", "Error"));
                return new MustacheTemplateEngine().render(new ModelAndView(map, "template/main.mustache"));
            }
            Calendar calendar;
            try {
                calendar = HWService.dateToCalendar(dateFormat.parse(requestDate));
                List<Homework> homeworks = service.find(calendar);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("homeworks", homeworks);

                amIAdmin(request, map);
                map.put("requestDate", formatString(calendar));
                calendar.add(Calendar.DATE, -1);
                map.put("previousDate", "/get_homework/?date=" + formatString(calendar));
                calendar.add(Calendar.DATE, 2);
                map.put("nextDate", "/get_homework/?date=" + formatString(calendar));
                map.put("subjects", subjectService.findAll());
                return new MustacheTemplateEngine().render(new ModelAndView(map, "template/homeworks.mustache"));
            } catch (Exception e) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                logger.log(Level.WARNING, "Some error", e);
                response.status(500);
                map.put("errors", new Error(e.toString(), "Error"));
                return new MustacheTemplateEngine().render(new ModelAndView(map, "template/main.mustache"));
            }


            //return requestDate;
        });

        //MOBILE

        get("/get_homeworks_json/", (request, response) -> {


            Calendar calendar;
            try {

                List<Homework> homeworks = service.findAll();
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("homeworks", homeworks);


                //json writing
                return gson.toJson(map);
            } catch (Exception e) {
                HashMap<String, Object> map = new HashMap<String, Object>();

                response.status(500);
                logger.log(Level.WARNING, "Some error", e);
                map.put("errors", new Error(e.toString(), "Error"));
                return gson.toJson(map);
            }


            //return requestDate;
        });
        //END MOBILE

        post("/create/:date/", (request, response) -> {
            if (!amIAdmin(request)) {
                return "Nope you are not admin";

            }
            String requestDate = request.params("date");
            if (requestDate.length() == 0) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("errors", new Error("You didn't enter date", "Error"));
                return new MustacheTemplateEngine().render(new ModelAndView(map, "template/main.mustache"));
            }
            Calendar calendar;
            try {
                calendar = HWService.dateToCalendar(dateFormat.parse(requestDate));
                service.insert(request.queryParams("subject"), request.queryParams("body"), calendar);

                List<Homework> homeworks = service.find(calendar);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("homeworks", homeworks);

                amIAdmin(request, map);
                map.put("requestDate", formatString(calendar));
                calendar.add(Calendar.DATE, -1);
                map.put("previousDate", "/get_homework/?date=" + formatString(calendar));
                calendar.add(Calendar.DATE, 2);
                map.put("nextDate", "/get_homework/?date=" + formatString(calendar));
                map.put("subjects", subjectService.findAll());
                response.redirect("/get_homework/?date=" + requestDate);
                return new MustacheTemplateEngine().render(new ModelAndView(map, "template/homeworks.mustache"));
            } catch (Exception e) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("errors", new Error(e.toString(), "Error"));
                logger.log(Level.WARNING, "Some error", e);
                response.status(500);
                return new MustacheTemplateEngine().render(new ModelAndView(map, "template/main.mustache"));
            }


            //return requestDate;
        });
        get("/delete_homework/:date/:id/", (request, response) -> {
            if (!amIAdmin(request)) {
                return "Nope you are not admin";

            }
            String requestDate = request.params("date");
            if (requestDate.length() == 0) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("errors", new Error("You didn't enter date", "Error"));
                return new MustacheTemplateEngine().render(new ModelAndView(map, "template/main.mustache"));
            }
            Calendar calendar;
            try {
                calendar = HWService.dateToCalendar(dateFormat.parse(requestDate));
                service.delete(Integer.valueOf(request.params("id")));

                List<Homework> homeworks = service.find(calendar);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("homeworks", homeworks);

                amIAdmin(request, map);
                map.put("requestDate", formatString(calendar));
                calendar.add(Calendar.DATE, -1);
                map.put("previousDate", "/get_homework/?date=" + formatString(calendar));
                calendar.add(Calendar.DATE, 2);
                map.put("nextDate", "/get_homework/?date=" + formatString(calendar));
                map.put("subjects", subjectService.findAll());
                response.redirect("/get_homework/?date=" + requestDate);
                return new MustacheTemplateEngine().render(new ModelAndView(map, "template/homeworks.mustache"));
            } catch (Exception e) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                logger.log(Level.WARNING, "Some error", e);
                response.status(500);
                map.put("errors", new Error(e.toString(), "Error"));
                return new MustacheTemplateEngine().render(new ModelAndView(map, "template/main.mustache"));
            }


            //return requestDate;
        });
    }

    private boolean amIAdmin(Request request, HashMap<String, Object> map) {
        if (request.cookie(SESSION) != null && request.cookie(SESSION).equals(SESSION_VALUE)) {
            map.put("isAdmin", true);
            return true;
        }
        return false;
    }

    private boolean amIAdmin(Request request) {
        if (request.cookie(SESSION).equals(SESSION_VALUE)) {
            return true;
        }
        return false;
    }

    private String formatString(Calendar calendar) {
        return dateFormat.format(Date.from(calendar.toInstant()));
    }
}
