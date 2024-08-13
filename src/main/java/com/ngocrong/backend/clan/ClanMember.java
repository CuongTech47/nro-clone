package com.ngocrong.backend.clan;

import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.lib.KeyValue;

import java.sql.Timestamp;
import java.util.ArrayList;

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
}
