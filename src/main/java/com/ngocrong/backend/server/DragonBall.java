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
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    logger.debug("Shutdown Server!");
                    server.stop();
                }
            }));
            server.init();
            System.gc();
            server.start();
        } catch (Exception ex) {
            logger.error("START ERR", ex);
        }
    }
}
