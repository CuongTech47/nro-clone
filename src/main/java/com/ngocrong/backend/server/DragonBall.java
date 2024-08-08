package com.ngocrong.backend.server;

import lombok.Getter;
import org.apache.log4j.Logger;

public class DragonBall {
    private static final Logger logger = Logger.getLogger(DragonBall.class);

    @Getter
    private static final DragonBall instance = new DragonBall();

    @Getter
    private Server server;

    public void start() {
        server = new Server();
        try {
            logger.debug("Start server!");
            addShutdownHook();
            initializeServer();
            server.start();
        } catch (Exception ex) {
            logger.error("Failed to start the server", ex);
        }
    }


    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.debug("Shutdown Server!");
            server.stop();
        }));
    }

    private void initializeServer() throws Exception {
        server.init();
    }
}
