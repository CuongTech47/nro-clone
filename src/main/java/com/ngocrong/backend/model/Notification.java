package com.ngocrong.backend.model;

import lombok.Data;

@Data
public class Notification {
    private static Notification instance;

    public static Notification getInstance() {
        if (instance == null) {
            instance = new Notification();
        }
        return instance;
    }

    private short avatar;
    private String text;


}
