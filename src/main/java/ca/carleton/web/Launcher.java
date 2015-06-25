package ca.carleton.web;

import spark.Request;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static spark.Spark.*;

/**
 * Test launcher for messing around with spark.
 * Created by Mike on 23/06/2015.
 */
public class Launcher {

    public static void main(final String[] args) throws ClassNotFoundException, SQLException {

        Class.forName("org.sqlite.JDBC");
        final Connection connection = DriverManager.getConnection("jdbc:sqlite:D:/IdeaProjects/Spark Test/src/main/resources/testdb");

        get("/users/:username", (req, res) ->
                        "Hello " + req.params(":username") + ", your database identifier is: " + getIdFor(connection, req.params(":username"))
        );


        get("/login/:username", (req, res) -> {
                    if (getIdFor(connection, req.params(":username")) != -1) {
                        req.session().attribute("auth", true);
                    }
                    res.redirect("/protected/success", 301);
                    return "Welcome!";
                }
        );

        before("/protected/*", (req, res) -> {
            if (!authenticated(req)) {
                halt(401, "Not authenticated. Use /login");
            }
        });

        get("/protected/success", (req, res) ->
                        "Hello! Welcome!"
        );

    }

    public static int getIdFor(final Connection connection, final String userName) throws SQLException {
        return connection.createStatement().executeQuery(String.format("SELECT ID FROM USERS WHERE USERNAME = '%s';", userName)).getInt("id");
    }

    public static boolean authenticated(final Request request) {
        return request.session().attribute("auth") != null && request.session().attribute("auth").equals(true);
    }
}
