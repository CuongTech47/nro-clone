package com.ngocrong.backend.mob;

import com.ngocrong.backend.item.ItemMap;
import com.ngocrong.backend.item.ItemTime;
import com.ngocrong.backend.map.tzone.Zone;
import com.ngocrong.backend.model.Hold;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Mob {
    public int mobId;
    public int seconds;
    public boolean isDisable;
    public boolean isDontMove;
    public boolean isFire;
    public boolean isIce;
    public boolean isWind, isBoss;
    public byte templateId;

    public short x;
    public short y;
    public byte status;
    public byte levelBoss;
    public byte sys;
    public byte level;
    public byte type;
    public long hp, maxHp, hpDefault;
    protected long lastTimeAttack = 0L;
    protected long attackDelay = 2000L;
    public long damage1;
    public long damage2;
    public Zone zone;
    public ArrayList<ItemMap> items = new ArrayList<>();
    public long deadTime;
    public boolean isFreeze, isSleep, isBlind;

    public final ArrayList<ItemTime> vItemTime = new ArrayList<>();
    public Lock lock = new ReentrantLock();
    public boolean isHeld;
    public Hold hold;
    public int timeLive;
    public int percentDamage;
    public boolean isChangeBody;
    public short body;
    public int dameDown;
    public enum ItemType {
        NONE, GOLD, ITEM, EQUIP, GEM, CARD, EVENT
    }

    private static Logger logger = Logger.getLogger(Mob.class);


    public static final int[] LEVEL = {-1, -1, 1, 2, 3, 4, 5, 6, 9, 9, 9, 9, 10, 10, 10, 11, 11, 11, 11, 12, 12, 12, 12, -1, -1, -1};
    public static final int[][][] OPTIONS = {{{127, 139}, {128, 140}, {129, 141}}, {{130, 142}, {131, 143}, {132, 144}}, {{133, 136}, {134, 137}, {135, 138}}};


    public static ArrayList<MobTemplate> vMobTemplate = new ArrayList<>();

}
