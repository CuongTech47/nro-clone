package com.ngocrong.backend.repository;

public class GameRepo {
    private static GameRepo instance;
    public UserRepo user;



    public static GameRepo getInstance() {
        if (instance == null) {
            instance = new GameRepo();
        }
        return instance;
    }
}




