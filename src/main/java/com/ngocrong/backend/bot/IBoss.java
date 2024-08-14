package com.ngocrong.backend.bot;

public interface IBoss {
    void attack(Object obj);

    Object targetDetect();

    void move();

    void chat(String chat);
}
