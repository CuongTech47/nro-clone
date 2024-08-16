package com.ngocrong.backend.server;

import com.ngocrong.backend.crackball.CrackBall;
import com.ngocrong.backend.model.Notification;

public class ExecuteCommand {
    private static final String ALERT = "alert";
    private static final String CHAT_VIP = "chat_vip";
    private static final String EXP = "exp";
    private static final String UPDATE_LUCKY_WHEEL = "update_lucky_wheel";
    private static final String NOTIFICATION = "notification";

    public static void execute(String[] command) {
        Server server = DragonBall.getInstance().getServer();
        Config config = server.getConfig();
        switch (command[0]) {
            case ALERT:
                SessionManager.serverMessage(command[1]);
                break;

            case CHAT_VIP:
                SessionManager.chatVip(command[1]);
                break;

            case EXP:
                config.setExp(Integer.parseInt(command[1]));
                break;

            case UPDATE_LUCKY_WHEEL:
                CrackBall.loadItem();
                break;

            case NOTIFICATION:
                Notification notification = Notification.getInstance();
                notification.setAvatar(Short.parseShort(command[1]));
                notification.setText(command[2]);
                break;

            default:
                SessionManager.chatVip(command[0]);
                break;

        }
    }
}
