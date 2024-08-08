package com.ngocrong.backend.server;

import lombok.Getter;

public class Server {

    protected boolean start;
    public boolean isMaintained;

    public static int COUNT_SESSION_ON_IP = 3;

    @Getter
    private final Config config;

    public Server() {
        config = new Config();
        config.load();
    }

    public void saveData() {

    }
}
