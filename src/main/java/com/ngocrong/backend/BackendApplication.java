package com.ngocrong.backend;

import com.ngocrong.backend.repository.*;
import com.ngocrong.backend.server.DragonBall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {



    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        GameRepo.getInstance().userRepo = userDataRepository;
        GameRepo.getInstance().playerRepo = playerRepository;
        GameRepo.getInstance().clanRepo = clanRepository;
        GameRepo.getInstance().clanMemberRepo = clanMemberRepository;
        GameRepo.getInstance().discipleRepo = discipleRepository;
        GameRepo.getInstance().consignmentItemRepo = consignmentItemRepository;
        DragonBall.getInstance().start();
    }


    @Autowired
    UserRepo userDataRepository;

    @Autowired
    PlayerRepo playerRepository;

    @Autowired
    ClanRepo clanRepository;


    @Autowired
    ClanMemberRepo clanMemberRepository;

    @Autowired
    DiscipleRepo discipleRepository;

    @Autowired
    ConsignmentItemRepo consignmentItemRepository;


}
