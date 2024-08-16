package com.ngocrong.backend.clan;

import com.google.gson.Gson;
import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.entity.ClanEntity;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.map.Barrack;
import com.ngocrong.backend.map.MapManager;
import com.ngocrong.backend.repository.GameRepo;
import com.ngocrong.backend.server.SessionManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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
    public Barrack barrack;
    public Barrack treasure;

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

    public void saveData() {
        lockMember.readLock().lock();
        try {
            List<Item> itemList = new ArrayList<>();
            for (Item item : items) {
                if (item != null) {
                    itemList.add(item);
                }
            }
            GameRepo.getInstance().clanRepo.saveData(id, abbreviationName, leaderID, leaderName, slogan, imgID, level, clanPoint, powerPoint, maxMember, new Gson().toJson(itemList));
            for (ClanMember member : members) {
                member.saveData();
            }
        } catch (Exception ex) {
            logger.error("saveData", ex);
        } finally {
            lockMember.readLock().unlock();
        }
    }

    public String getStrCreateTime() {
        Date date = new Date(createTime.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        return formatter.format(date);
    }

    public void sort() {
        try {
            members.sort(new Comparator<ClanMember>() {
                @Override
                public int compare(ClanMember m1, ClanMember m2) {
                    return Integer.compare(m1.role, m2.role);
                }
            });
        } catch (Exception ex) {
            logger.error("sort", ex);
        }
    }

    public List<ClanMember> getMembers() {
        lockMember.writeLock().lock();
        try {
            for (ClanMember clanMember : members) {
                Char _char = SessionManager.findChar(clanMember.playerID);
                if (_char != null && _char.characterInfo != null) {
                    clanMember.head = _char.getHead();
                    clanMember.body = _char.getBody();
                    clanMember.leg = _char.getLeg();
                    clanMember.powerPoint = _char.characterInfo.getPower();
                }
            }
            return members;
        } finally {
            lockMember.writeLock().unlock();
        }
    }

    public int getNumberMember() {
        lockMember.readLock().lock();
        try {
            return members.size();
        } finally {
            lockMember.readLock().unlock();
        }
    }

}
