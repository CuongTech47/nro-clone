package com.ngocrong.backend.clan;

import com.ngocrong.backend.entity.ClanEntity;
import com.ngocrong.backend.item.Item;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Clan {
    private static final Logger logger = Logger.getLogger(Clan.class);
    public static final byte LEVEL_MAX = 20;

    public int id;
    public String name;
    public String leaderName;
    public int leaderID;
    public String slogan;
    public byte imgID;
    public byte level;
    public int clanPoint;
    public long powerPoint;
    public byte maxMember;
    public Timestamp createTime;

    public List<ClanMember> members;
    public ArrayList<ClanMessage> messages;

    public int remainingTimesCanEnterTreasure;
    public long lastTimeEnterTreasure = -1;

    public Item[] items;

    public String abbreviationName;
    public long lastTimeRename;

    public ReadWriteLock lockMember = new ReentrantReadWriteLock();
    public ReadWriteLock lockMessage = new ReentrantReadWriteLock();

    public Clan(ClanEntity data) {
        id = data.id;
        name = data.name;
        abbreviationName = data.abbreviation;
        slogan = data.slogan;
        leaderID = data.leaderId;
        leaderName = data.leaderName;
        level = data.level;
        imgID = data.imageId;
        clanPoint = data.clanPoint;
        powerPoint = data.powerPoint;
        maxMember = data.maxMember;
        createTime = data.createTime;
        initBoxItem();
        JSONArray json = new JSONArray(data.itemBox);
        for (int i = 0; i < json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            Item item = new Item();
            item.load(obj);
            items[item.indexUI] = item;
        }
        messages = new ArrayList<>();
        members = new ArrayList<>();
    }

    private void initBoxItem() {
        int size = this.level - 1;
        if (size < 0) {
            size = 0;
        }
        items = new Item[size];
    }

    public ClanMember getMember(int charID) {
        lockMember.readLock().lock();
        try {
            return members.stream().filter(m -> m.playerID == charID).findFirst().orElse(null);
        } finally {
            lockMember.readLock().unlock();
        }
    }
}
