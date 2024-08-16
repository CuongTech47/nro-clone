package com.ngocrong.backend.server;

import com.ngocrong.backend.map.MapManager;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

public class ServerMaintenance implements Runnable{
    private static final Logger logger = Logger.getLogger(ServerMaintenance.class);

    private final String message;
    private int seconds;


    public ServerMaintenance(String message, int seconds) {
        this.message = message;
        this.seconds = seconds;
    }


    @Override
    public void run() {
        Server server = DragonBall.getInstance().getServer();
        if (!server.isMaintained) {
            logger.debug(String.format("Máy chủ bảo trì sau %s", Utils.getTimeAgo(seconds)));
            server.isMaintained = true;
            String text = "Máy chủ bảo trì sau %s, vui lòng thoát trò chơi để đảm bảo không bị mất dữ liệu.\n%s";
            SessionManager.addBigMessage(String.format(text, Utils.getTimeAgo(seconds), this.message));
            while (seconds > 0) {
                if (seconds == 60) {
                    SessionManager.addBigMessage(String.format(text, Utils.getTimeAgo(seconds), this.message));
                    logger.debug(String.format("Máy chủ bảo trì sau %s", Utils.getTimeAgo(seconds)));
                }
                seconds--;
                try {
                    Thread.sleep(1000L);
                } catch (Exception ex) {
                    logger.error("failed!", ex);
                }
            }
            server.saveData();
            server.setOfflineAll();
            MapManager.getInstance().close();
            server.stop();
            System.exit(1);
        }
    }
}
