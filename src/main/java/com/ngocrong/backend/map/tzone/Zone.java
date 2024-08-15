package com.ngocrong.backend.map.tzone;

import com.ngocrong.backend.bot.Escort;
import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.disciple.MiniDisciple;
import com.ngocrong.backend.item.ItemMap;
import com.ngocrong.backend.map.MapService;
import com.ngocrong.backend.mob.Mob;
import com.ngocrong.backend.model.Npc;
import com.ngocrong.backend.task.Task;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Zone extends Thread{
    private static final Logger logger = Logger.getLogger(Zone.class);

    public Zone zone;

    public static final byte PTS_GREEN = 0;
    public static final byte PTS_YELLOW = 1;
    public static final byte PTS_RED = 2;
    public static final byte TYPE_ALL = 0;
    public static final byte TYPE_HUMAN = 1;
    public static final byte TYPE_PET = 2;
    public static final byte TYPE_BOSS = 3;
    public static final byte TYPE_MINIPET = 4;
    public static final byte TYPE_ESCORT = 5;

    public short autoIncrease;

    public ReadWriteLock lockChar = new ReentrantReadWriteLock();
    public ReadWriteLock lockMob = new ReentrantReadWriteLock();
    public ReadWriteLock lockItemMap = new ReentrantReadWriteLock();
    public ReadWriteLock lockNpc = new ReentrantReadWriteLock();
    public ReadWriteLock lockSatellite = new ReentrantReadWriteLock();
    public ReadWriteLock lockRespawn = new ReentrantReadWriteLock();

    public TMap map;
    public MapService mapService;
    public int zoneID;
    public boolean running;
    public ArrayList<Char> chars;

    protected ArrayList<Mob> mobs;
    protected ArrayList<Npc> npcs;
    protected int numPlayer, maxPlayer;
    protected ArrayList<ItemMap> satellites;
    protected ArrayList<ItemMap> items;

    public Zone(TMap map, int zoneId) {
        this.map = map;
        this.zoneID = zoneId;
        this.running = true;
        initial();
        start();
    }

    private void initial() {
        long now = System.currentTimeMillis();

    }


    public List<ItemMap> getListSatellite() {
        ArrayList<ItemMap> items = new ArrayList<>();
        lockSatellite.readLock().lock();
        try {
            for (ItemMap item : this.satellites) {
                items.add(item);
            }
        }finally {
            lockSatellite.readLock().unlock();
        }

        return items;
    }

    public void enter(Char character) {
        try {
            handleDiscipleEntry(character);  // Xử lý việc đệ tử của nhân vật có nên theo vào zone hay không.
            handleColdMapEffect(character);  // Áp dụng hiệu ứng của map lạnh (Cold Map) nếu có.
            setOwnerForSatellite(character); // Thiết lập chủ sở hữu cho vệ tinh, nếu có.
            character.service.setMapInfo();  // Cập nhật thông tin bản đồ cho nhân vật.
        } finally {
            finalizeCharacterEntry(character);  // Hoàn tất các bước sau khi nhân vật đã vào zone.
        }
        handleBabidiBaseEffect(character);  // Xử lý hiệu ứng đặc biệt nếu bản đồ không phải Base Babidi.
        synchronizeDiscipleFlag(character); // Đồng bộ hóa cờ trạng thái của đệ tử với chủ nhân.
        reviveIfDead(character);            // Hồi sinh nhân vật nếu nhân vật đang chết.
        postEntryUpdates(character);        // Thực hiện các cập nhật cần thiết sau khi nhân vật vào zone.
    }

    private void postEntryUpdates(Char character) {
        mapService.playerAdd(character);
        mapService.updateBag(character);
        character.setFreeze(false);
        character.setFreezSeconds(0);

        List<Char> charactersInZone = getListChar(Zone.TYPE_ALL);
        for (Char otherCharacter : charactersInZone) {
            if (otherCharacter != character) {
//                character.loadEffectSkillPlayer(otherCharacter);
                handlePetFollow(character, otherCharacter);
                handleMobMeUpdate(character, otherCharacter);
            }
        }
    }

    private void handleMobMeUpdate(Char character, Char otherCharacter) {
        if (character.getMobMe() != null) {
            otherCharacter.service.mobMeUpdate(character, null, -1, (byte) -1, (byte) 0);
        }
    }

    private void handlePetFollow(Char character, Char otherCharacter) {
        if (character.getPetFollow() != null) {
            otherCharacter.service.petFollow(character, (byte) 1);
        }
    }

    private void reviveIfDead(Char character) {
        if (character.isDead()) {
//            character.revival(100);
        }
    }

    private void synchronizeDiscipleFlag(Char character) {
        if (character.myDisciple != null && character.myDisciple.getFlag() != character.getFlag()) {
            character.myDisciple.setFlag();
        }
    }

    private void handleBabidiBaseEffect(Char character) {
        if (!map.isBaseBabidi() && (character.getFlag() == 9 || character.getFlag() == 10)) {
            character.setAccumulatedPoint(null);
            character.setFlag((byte) 0);
        }
    }

    private void finalizeCharacterEntry(Char aChar) {
    }

    private void setOwnerForSatellite(Char aChar) {
    }

    private void handleColdMapEffects(Char _char) {
        if ((_char.isHuman() || _char.isDisciple()) && map.isCold() != _char.isCold()) {
            _char.getCharacterInfo().setCharacterInfo();
            _char.service.loadPoint();
            mapService.playerLoadBody(_char);
            _char.setCold(map.isCold());
            notifyColdEffects(_char);
        }
    }

    private void notifyColdEffects(Char _char) {
        if (map.isCold()) {
            _char.service.serverMessage("Bạn đã đến hành tinh Cold");
            _char.service.serverMessage("Sức tấn công và HP của bạn bị giảm 50% vì lạnh");
        } else {
            _char.service.serverMessage("Sức tấn công và HP của bạn đã trở lại bình thường");
        }
    }
    private void handleColdMapEffect(Char character) {
        if (shouldApplyColdEffect(character)) {
            character.characterInfo.setCharacterInfo();
            character.service.loadPoint();
            mapService.playerLoadBody(character);
            character.setCold(map.isCold());

            if (map.isCold()) {
                character.service.serverMessage("Bạn đã đến hành tinh Cold");
                character.service.serverMessage("Sức tấn công và HP của bạn bị giảm 50% vì lạnh");
            } else {
                character.service.serverMessage("Sức tấn công và HP của bạn đã trở lại bình thường");
            }
        }
    }

    private boolean shouldApplyColdEffect(Char character) {
        return (character.isHuman() || character.isDisciple()) && map.isCold() != character.isCold();
    }

    private void handleDiscipleEntry(Char character) {
        character.zone = this;
        if (character.myDisciple != null && shouldDiscipleFollow(character)) {
            if (!map.isMapSingle() && !map.isDauTruong()) {
                character.myDisciple.followMaster();
                enter(character.myDisciple);
            } else {
                clearDiscipleEffects(character.myDisciple);
            }
        }
    }

    private boolean shouldDiscipleFollow(Char character) {
        return !character.myDisciple.isDead()
                && character.myDisciple.discipleStatus != 3
                && !character.isNhapThe();
    }

    private void clearDiscipleEffects(Char disciple) {
        disciple.clearEffect();
        if (disciple.isMonkey()) {
            disciple.timeOutIsMonkey();
        }
    }

    private void setCharacterZone(Char _Char) {
        _Char.zone = this;
    }


    public List<Mob> getListMob() {
        List<Mob> list = new ArrayList<>();
        lockMob.readLock().lock();
        try {
            for (Mob mob : mobs) {
                list.add(mob);
            }
        } finally {
            lockMob.readLock().unlock();
        }
        return list;
    }

    public List<Npc> getListNpc(Char _c) {
        List<Npc> list = new ArrayList<>();
        lockNpc.readLock().lock();
        try {
            for (Npc npc : npcs) {
                if (npc.templateId == 42) {
                    if (_c.getCharacterInfo().getPower() < 17999000000L) {
                        continue;
                    }
                }
                if ((map.mapID == 27 || map.mapID == 28 || map.mapID == 29) && npc.templateId == 38) {
                    if (Utils.nextInt(2) == 0) {
                        continue;
                    }
                    if (_c.getTaskMain() != null && _c.checkCanEnter(102)) {
                        npc.x = (short) Utils.nextInt(100, map.width - 100);
                        npc.y = map.collisionLand(npc.x, (short) 24);
                    }
                }
                list.add(npc);
            }
        } finally {
            lockNpc.readLock().unlock();
        }
        return list;
    }


    public List<Char> getListChar(int... arrs) {
        List<Char> list = new ArrayList<>();
        lockChar.readLock().lock();
        try {
            for (Char _c : chars) {
                if (!_c.isLoggedOut()) {
                    boolean flag = false;
                    if (arrs.length > 0) {
                        for (int t : arrs) {
                            if (t == TYPE_ALL || (t == TYPE_HUMAN && _c.isHuman()) || (t == TYPE_PET && _c.isDisciple())
                                    || (t == TYPE_BOSS && _c.isBoss()) || (t == TYPE_MINIPET && _c.isMiniDisciple())
                                    || (t == TYPE_ESCORT && _c.isEscort())) {
                                flag = true;
                                break;
                            }
                        }
                    } else {
                        flag = true;
                    }
                    if (flag) {
                        list.add(_c);
                    }
                }
            }
        } finally {
            lockChar.readLock().unlock();
        }
        return list;
    }

    public void leave(Char _char) {
        try {
            if (_char.myDisciple != null && !_char.myDisciple.isDead() && _char.myDisciple.discipleStatus != 3 && !_char.isNhapThe()) {
                Zone z = _char.myDisciple.zone;
                if (z != null) {
                    z.leave(_char.myDisciple);
                }
            }
            if (_char.getHold() != null) {
                _char.getHold().close();
            }
            if (_char.getMobMe() != null) {
                mapService.mobMeUpdate(_char, null, -1, (byte) -1, (byte) 7);
            }
        } finally {
            removeChar(_char);
            MiniDisciple mini = _char.getMiniDisciple();
            if (mini != null) {
                leave(mini);
            }
            Escort escort = _char.getEscortedPerson();
            if (escort != null) {
                leave(escort);
            }
        }
        _char.clearMap();
        mapService.playerRemove(_char);
    }

    private void removeChar(Char aChar) {
        lockChar.writeLock().lock();
        try {
            chars.remove(aChar);
            if (aChar.isHuman()) {
                this.numPlayer = getListChar(TYPE_HUMAN).size();
            }
        }finally {
            lockChar.writeLock().unlock();
        }
    }


    public int getPts() {
        if (this.numPlayer < 8) {
            return PTS_GREEN;
        } else if (this.numPlayer < 10) {
            return PTS_YELLOW;
        }
        return PTS_RED;
    }

    public List<ItemMap> getListItemMap(Task... tasks) {
        ArrayList<ItemMap> items = new ArrayList<>();
        lockItemMap.readLock().lock();
        try {
            for (ItemMap item : this.items) {
                items.add(item);
            }
        } finally {
            lockItemMap.readLock().unlock();
        }
        lockSatellite.readLock().lock();
        try {
            for (ItemMap item : this.satellites) {
                items.add(item);
            }
        } finally {
            lockSatellite.readLock().unlock();
        }
        return items;
    }

    public Char findCharByID(int id) {
        lockChar.readLock().lock();
        try {
            for (Char p : chars) {
                if (p.getId() == id) {
                    return p;
                }
            }
            return null;
        } finally {
            lockChar.readLock().unlock();
        }
    }

    public void addItemMap(ItemMap itemMap) {
        if (itemMap.item.template.getType() == 22) {
            lockSatellite.writeLock().lock();
            try {
                satellites.add(itemMap);
            } finally {
                lockSatellite.writeLock().unlock();
            }
        } else {
            lockItemMap.writeLock().lock();
            try {
                items.add(itemMap);
            } finally {
                lockItemMap.writeLock().unlock();
            }
        }
    }


//    public void sendMessage(Message ms, Char _char) {
//        zone.lockChar.readLock().lock();
//        try {
//            for (Char _c : zone.chars) {
//                if (_c != _char) {
//                    _c.service.sendMessage(ms);
//                }
//            }
//        } finally {
//            zone.lockChar.readLock().unlock();
//        }
//    }
}
