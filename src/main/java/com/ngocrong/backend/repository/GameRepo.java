package com.ngocrong.backend.repository;

public class GameRepo {
    private static GameRepo instance;
    public UserRepo userRepo;
    public PlayerRepo playerRepo;
    public ClanMemberRepo clanMemberRepo;
    public ClanRepo clanRepo;
    public DiscipleRepo discipleRepo;
    public ConsignmentItemRepo consignmentItemRepo;


    public static GameRepo getInstance() {
        if (instance == null) {
            instance = new GameRepo();
        }
        return instance;
    }
}




