package com.ngocrong.backend.repository;


import com.ngocrong.backend.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Transactional
@Repository
public interface PlayerRepo extends JpaRepository<PlayerEntity,Integer> {
    List<PlayerEntity> findByName(String username);

    List<PlayerEntity> findByUserIdAndServerId (Integer userId, Integer serverId);

    @Modifying
    @Query("UPDATE PlayerEntity p SET p.online = :online, p.loginTime = :loginTime WHERE p.id = :id")
    void setOnline(Integer id, Byte online, Timestamp loginTime);

    @Modifying
    @Query("UPDATE PlayerEntity p SET p.name = :name WHERE p.id = :id")
    void setName(Integer id, String name);

    @Modifying
    @Query("UPDATE PlayerEntity p SET p.clan = :clan WHERE p.id = :id")
    void setClanId(Integer id, Integer clan);

//    @Modifying
//    @Query("UPDATE PlayerEntity p SET p.clanReward = :clanReward WHERE p.id = :id")
//    void setRewardNRD(Integer id, String clanReward);

    @Modifying
    @Query("UPDATE PlayerEntity p SET p.online = :online, p.logoutTime = :logoutTime WHERE p.id = :id")
    void setOffline(Integer id, Byte online, Timestamp logoutTime);

    @Modifying
    @Query("UPDATE PlayerEntity p SET p.online = 0 WHERE p.id > 0")
    void setOfflineAll();

//    @Modifying
//    @Query("UPDATE PlayerEntity p SET p.diemDanh = 0 WHERE p.id > 0")
//    void setDiemDanh();

    @Modifying
    @Query("UPDATE PlayerEntity p SET p.gold = :gold WHERE p.id = :id")
    void saveCoin(Integer id, Long gold);

    @Modifying
    @Query("UPDATE PlayerEntity p SET p.itemBag = :bag, p.itemBox = :box WHERE p.id = :id")
    void saveBagAndBox(Integer id, String bag, String box);


    @Modifying
    @Query("UPDATE PlayerEntity p SET p.itemBag = :bag, p.itemBox = :box, p.gold = :gold WHERE p.id = :id")
    void saveBagAndBoxAndCoin(Integer id, String bag, String box, Long gold);


    @Modifying
    @Query("UPDATE PlayerEntity p " +
            "SET " +
            "p.task = :task, " +
            "p.gold = :gold, " +
            "p.diamond = :diamond, " +
            "p.diamondLock = :diamondLock, " +
            "p.itemBag = :itemBag, " +
            "p.itemBody = :itemBody, " +
            "p.itemBox = :itemBox, " +
            "p.map = :map, " +
            "p.skill = :skill, " +
            "p.info = :info, " +
            "p.clan = :clan, " +
            "p.shortcut = :shortcut, " +
            "p.numberCellBag = :numberCellBag, " +
            "p.numberCellBox = :numberCellBox, " +
            "p.friend = :friend, " +
            "p.enemy = :enemy, " +
            "p.head = :head, " +
            "p.ship = :ship, " +
            "p.magicTree = :magicTree, " +
            "p.itemTime = :itemTime, " +
            "p.fusion = :fusion, " +
            "p.amulet = :amulet, " +
            "p.typeTrainning = :typeTrainning, " +
            "p.achievement = :achievement, " +
            "p.timePlayed = :timePlayed, " +
            "p.studying = :studying, " +
            "p.boxCrackBall = :boxCrackBall, " +
            "p.timeAtSplitFusion = :timeAtSplitFusion, " +
            "p.head2 = :head2, " +
            "p.body = :body, " +
            "p.leg = :leg, " +
            "p.porata = :porata, " +
            "p.collectionBook = :collectionBook, " +
            "p.specialSkill = :specialSkill, " +
            "p.countNumberOfSpecialSkillChanges = :countNumberOfSpecialSkillChanges, " +
            "p.resetTime = :resetTime  WHERE p.id = :id")
    void saveData(Integer id, String task, Long gold, Integer diamond, Integer diamondLock,
                  String itemBag, String itemBody, String itemBox,
                  String map, String skill, String info, Integer clan, String shortcut,
                  Integer numberCellBag, Integer numberCellBox, String friend, String enemy,
                  Short head, Byte ship, String magicTree, String itemTime,
                  Integer fusion, String amulet, Byte typeTrainning, String achievement,
                  Integer timePlayed, String studying, String boxCrackBall, Long timeAtSplitFusion,
                  Integer head2, Integer body, Integer leg, Integer porata,
                  String collectionBook, String specialSkill, Short countNumberOfSpecialSkillChanges,
                  Timestamp resetTime);
}
