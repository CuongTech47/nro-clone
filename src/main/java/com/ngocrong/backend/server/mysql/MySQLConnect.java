package com.ngocrong.backend.server.mysql;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MySQLConnect {
    private static Logger logger = Logger.getLogger(MySQLConnect.class);

    private static Connection conn;

    public static Connection getConnection() {
        return conn;
    }

    public static synchronized void createConnection(String host, int port, String database, String user, String pass) {
        loadDriver();
        String url = buildUrl(host, port, database);
        connect(url,user,pass);
    }

    private static String buildUrl(String host, int port, String database) {
        System.out.println("host: " + host);
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        logger.debug("MySQL connect: " + url);
        return url;
    }

    private static void loadDriver() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.debug("MySQL driver loaded successfully.");
        }catch (ClassNotFoundException e) {
            logger.error("MySQL driver not found!", e);
            System.exit(0);
        }
    }


    private static void connect(String url, String user, String pass) {
        try {
            conn = DriverManager.getConnection(url,user,pass);
            logger.debug("Successful connection to MySQL.");
        } catch (SQLException e) {
            logger.error("Failed to connect to MySQL!", e);
            System.exit(0);
        }
    }


    public static synchronized boolean close() {
        logger.debug("Close connection to database");
        if (conn != null) {
            try {
                conn.close();
                logger.debug("Connection closed successfully.");
                return true;
            } catch (SQLException e) {
                logger.error("Failed to close connection!", e);
                return false;
            }
        }
        return false;
    }
}
