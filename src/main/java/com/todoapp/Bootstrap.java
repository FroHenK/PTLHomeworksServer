package com.todoapp;

import com.mongodb.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;

import static spark.Spark.*;

/**
 * Created by shekhargulati on 09/06/14.
 */
public class Bootstrap {
    private static final String IP_ADDRESS = System.getenv("OPENSHIFT_DIY_IP") != null ? System.getenv("OPENSHIFT_DIY_IP") : "localhost";
    private static final int PORT = System.getenv("OPENSHIFT_DIY_PORT") != null ? Integer.parseInt(System.getenv("OPENSHIFT_DIY_PORT")) : 8080;
    private static final String POSTGRESQL_HOST = System.getenv("OPENSHIFT_POSTGRESQL_DB_HOST") != null ? System.getenv("OPENSHIFT_POSTGRESQL_DB_HOST") : "localhost";
    private static final int POSTGRESQL_PORT = System.getenv("OPENSHIFT_POSTGRESQL_DB_PORT") != null ? Integer.parseInt(System.getenv("OPENSHIFT_POSTGRESQL_DB_PORT")) : 5432;;
    private static final String POSTGRESQL_DATABASE  = "roguelike";
    private static final String POSTGRESQL_USER  = System.getenv("OPENSHIFT_POSTGRESQL_DB_USERNAME") != null ? System.getenv("OPENSHIFT_POSTGRESQL_DB_USERNAME") : "postgres";
    private static final String POSTGRESQL_ADMIN = System.getenv("OPENSHIFT_POSTGRESQL_DB_PASSWORD") != null ? System.getenv("OPENSHIFT_POSTGRESQL_DB_PASSWORD") : "1d8327deb882cf99";

    public static void main(String[] args) throws Exception {
        setIpAddress(IP_ADDRESS);
        setPort(PORT);
        /*staticFileLocation("/public");
        new TodoResource(new TodoService(mongo()));*/
        Class.forName("org.postgresql.Driver");

        Connection connection= DriverManager.getConnection("jdbc:postgresql://" + POSTGRESQL_HOST + ":" + POSTGRESQL_PORT + "/" + POSTGRESQL_DATABASE, POSTGRESQL_USER, POSTGRESQL_ADMIN);
        HWService hwService = new HWService(connection);
        UserService userService = new UserService(connection);
        SubjectService subjectService = new SubjectService(connection);
        HWResource hwResource = new HWResource(hwService, userService, subjectService);
        /*Calendar instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_MONTH,11);
        hwService.insert("Russian", "123 123 q", instance);*/
        try {


        }catch (Exception e){
            get("/",(request, response) -> e.toString());


        }
    }

    private static DB mongo() throws Exception {
        String host = System.getenv("OPENSHIFT_MONGODB_DB_HOST");
        if (host == null) {
            MongoClient mongoClient = new MongoClient("localhost");
            return mongoClient.getDB("todoapp");
        }
        int port = Integer.parseInt(System.getenv("OPENSHIFT_MONGODB_DB_PORT"));
        String dbname = System.getenv("OPENSHIFT_APP_NAME");
        String username = System.getenv("OPENSHIFT_MONGODB_DB_USERNAME");
        String password = System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD");
        MongoClientOptions mongoClientOptions = MongoClientOptions.builder().connectionsPerHost(20).build();
        MongoClient mongoClient = new MongoClient(new ServerAddress(host, port), mongoClientOptions);
        mongoClient.setWriteConcern(WriteConcern.SAFE);
        DB db = mongoClient.getDB(dbname);
        if (db.authenticate(username, password.toCharArray())) {
            return db;
        } else {
            throw new RuntimeException("Not able to authenticate with MongoDB");
        }
    }
}
