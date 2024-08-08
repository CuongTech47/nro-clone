package com.ngocrong.backend.server;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


@Getter
@Setter
public class Config {
    private static Logger logger = Logger.getLogger(Config.class);

    private int serverID;
    private String name;
    private int port;
    private String host;
    private boolean redirect;
    private int dbPort;
    private String dbHost;
    private String dbUser;
    private String dbPassword;
    private String dbName;
    private int exp;
    private int maxQuantity;
    private byte dataVersion;
    private byte skillVersion;
    private byte itemVersion;
    private byte mapVersion;
    private long delayAutoSave;
    private String listServers;


    public void load() {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.error("Sorry, unable to find application.properties");
                return;
            }

            Properties props = new Properties();
            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));

            props.forEach((key, value) -> logger.debug(String.format("Config - %s: %s", key, value)));

            this.serverID = Integer.parseInt(props.getProperty("server.id"));
            this.name = props.getProperty("server.name");
            this.port = Integer.parseInt(props.getProperty("server.port_game"));
            this.host = props.getProperty("server.host");
            this.redirect = Boolean.parseBoolean(props.getProperty("server.redirect"));
            this.delayAutoSave = Long.parseLong(props.getProperty("server.autosave.delay"));
            this.dbPort = Integer.parseInt(props.getProperty("database.port"));
            this.dbHost = props.getProperty("database.host");
            this.dbName = props.getProperty("database.name");
            this.dbUser = props.getProperty("database.user");
            this.dbPassword = props.getProperty("database.password");
            this.dataVersion = Byte.parseByte(props.getProperty("game.data.version"));
            this.itemVersion = Byte.parseByte(props.getProperty("game.item.version"));
            this.mapVersion = Byte.parseByte(props.getProperty("game.map.version"));
            this.skillVersion = Byte.parseByte(props.getProperty("game.skill.version"));
            this.exp = Integer.parseInt(props.getProperty("game.exp"));
            this.maxQuantity = Integer.parseInt(props.getProperty("game.item.quantity.max"));
            this.listServers = props.getProperty("game.servers");
        } catch (Exception ex) {
            logger.error("Error loading configuration", ex);
        }
    }
}
