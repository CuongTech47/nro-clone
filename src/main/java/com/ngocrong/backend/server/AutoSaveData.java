package com.ngocrong.backend.server;

import org.apache.log4j.Logger;

public class AutoSaveData implements Runnable {
    private static final Logger logger = Logger.getLogger(AutoSaveData.class);
    private final Server server;
    private final Config config;

    public AutoSaveData() {
        this.server = DragonBall.getInstance().getServer();
        this.config = server.getConfig();
    }

    @Override
    public void run() {
        while (server.start) {
            try {
                performAutoSave();
            } catch (InterruptedException ex) {
                logger.error("Auto-save interrupted!", ex);
                Thread.currentThread().interrupt(); // Khôi phục trạng thái ngắt của luồng
                break; // Thoát khỏi vòng lặp nếu bị ngắt
            }
        }
    }

    private void performAutoSave() throws InterruptedException {
        Thread.sleep(config.getDelayAutoSave());
        server.saveData();
        logger.debug("Data saved successfully.");
    }
}
