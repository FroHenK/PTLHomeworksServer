package com.todoapp;

/**
 * Created by User on 10.09.2015.
 */

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

import static spark.Spark.*;

public class HWResource {
    private HWService service;

    public HWResource(HWService service) {
        this.service = service;
        initHW();
    }

    private void initHW() {
        get("/",(request, response) -> {

            HashMap<String, Object> map = new HashMap<String, Object>();
            return new MustacheTemplateEngine().render(new ModelAndView(map, "template/main.mustache"));
        });

        get("/get_homework/",(request, response) -> {

            String requestDate = request.queryParams("date");
            if(requestDate.length()==0){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("errors",new Error("Вы не ввели дату"));
                return new MustacheTemplateEngine().render(new ModelAndView(map, "template/main.mustache"));
            }
            return requestDate;
        });
    }
}
