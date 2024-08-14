package com.ngocrong.backend.clan;

import com.ngocrong.backend.entity.ClanEntity;
import com.ngocrong.backend.entity.ClanMemberEntity;
import com.ngocrong.backend.repository.GameRepo;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ClanManager {
    private static final Logger logger = Logger.getLogger(ClanManager.class);
    private static ClanManager instance;
    public HashMap<Integer, Clan> clans;
    public ReadWriteLock lock = new ReentrantReadWriteLock();


    public static ClanManager getInstance() {
        if (instance == null) {
            instance = new ClanManager();
        }
        return instance;
    }

    public void init() {
        clans = new HashMap<>();
        List<ClanEntity> clanDataList = GameRepo.getInstance().clanRepo.findAll();
        for (ClanEntity data : clanDataList) {
            clans.put(data.id, new Clan(data));
        }
        ArrayList<ClanMemberEntity> removes = new ArrayList<>();
        List<ClanMemberEntity> members = GameRepo.getInstance().clanMemberRepo.findAll();
        for (ClanMemberEntity data : members) {
            ClanMember member = new ClanMember(data);
            if (clans.containsKey(data.clanId)) {
                clans.get(data.clanId).members.add(member);
            } else {
                removes.add(data);
            }
        }
        GameRepo.getInstance().clanMemberRepo.deleteAll(removes);
        for (Clan clan : clans.values()) {
            clan.sort();
        }
    }

    public Clan findClanById(int clanId) {
        lock.readLock().lock();
        try {
            return clans.get(clanId);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Clan> search(String text) {
        List<Clan> clanList = new ArrayList<>();
        lock.readLock().lock();
        try {
            for (Clan clan : clans.values()) {
                if (clan.name.startsWith(text)) {
                    clanList.add(clan);
                    if (clanList.size() >= 10) {
                        break;
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return clanList;
    }

    public Clan findClanByName(String name) {
        lock.readLock().lock();
        try {
            return clans.values().stream().filter(clan -> clan.name.equals(name)).findFirst().orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void saveData() {
        lock.readLock().lock();
        try {
            for (Clan clan : clans.values()) {
                clan.saveData();
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addClan(Clan clan) {
        lock.writeLock().lock();
        try {
            clans.put(clan.id, clan);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void delete(Clan clan) {
        lock.writeLock().lock();
        try {
            if (!clans.containsKey(clan.id)) {
                return;
            }



        } finally {
            lock.writeLock().unlock();
        }
    }
}
