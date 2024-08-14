package com.ngocrong.backend.clan;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngocrong.backend.entity.ClanMemberEntity;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.lib.KeyValue;
import com.ngocrong.backend.repository.GameRepo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ClanMember {
    public long id;
    public int playerID;
    public String name;
    public short head, leg, body;
    public byte role;
    public long powerPoint;
    public int donate;
    public int receiveDonate;
    public int clanPoint;
    public int currClanPoint;
    public Timestamp joinTime;
    public ArrayList<KeyValue<String, Item>> items;
    public ArrayList<ClanReward> rewards;

    public ClanMember() {
        items = new ArrayList<>();
        rewards = new ArrayList<>();
    }
    public ClanMember(ClanMemberEntity data) {
        id = data.id;
        name = data.name;
        playerID = data.playerId;
        role = data.role;
        head = data.head;
        body = data.body;
        leg = data.leg;
        donate = data.donate;
        receiveDonate = data.receiveDonate;
        clanPoint = data.clanPoint;
        currClanPoint = data.currentPoint;
        powerPoint = data.powerPoint;
        joinTime = data.joinTime;
        rewards = new Gson().fromJson(data.clanReward, new TypeToken<List<ClanReward>>() {
        }.getType());
        if (rewards == null) {
            rewards = new ArrayList<>();
        }
        items = new ArrayList<>();
    }

    public void saveData() {
        try {
            long now = System.currentTimeMillis();
            ArrayList<ClanReward> rewards = new ArrayList<>();
            for (ClanReward r : this.rewards) {
                if (r.getTimeEnd() > now) {
                    rewards.add(r);
                }
            }
            GameRepo.getInstance().clanMemberRepo.saveData(id, role, head, body, leg,
                    powerPoint, donate, receiveDonate, clanPoint,
                    currClanPoint, new Gson().toJson(rewards));
        } finally {
        }
    }
}
