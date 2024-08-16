package com.ngocrong.backend.bot;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.character.CharService;
import com.ngocrong.backend.character.CharacterInfo;
import com.ngocrong.backend.consts.SkillName;
import com.ngocrong.backend.map.MapManager;
import com.ngocrong.backend.map.tzone.KarinForest;
import com.ngocrong.backend.map.tzone.TMap;
import com.ngocrong.backend.map.tzone.Zone;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class Boss extends Char implements IBoss {
    private static Logger logger = Logger.getLogger(Boss.class);

    public int limit;
    public long waitingTimeToLeave;
    public String sayTheLastWordBeforeDie;
    public ArrayList<Char> listTarget;
    public int distanceToAddToList;
    public boolean willLeaveAtDeath;
    private long lastTimeSkillShoot;

    public Boss() {
        int id = (int) (System.currentTimeMillis() - 1649080000000L) + Utils.nextInt(1000);
        this.setId(-Math.abs(id));
        service = new CharService(this);
        setDefaultPart();
        this.listTarget = new ArrayList<>();
        characterInfo = new CharacterInfo(this);
        characterInfo.setPowerLimited();
        characterInfo.setSatamina();
        characterInfo.setCharacterInfo();
        characterInfo.recovery(CharacterInfo.ALL, 100, false);
        setIdMount(-1);
        setHaveMount(false);
        this.waitingTimeToLeave = 1000;
        effects = new ArrayList();
        itemTimes = new ArrayList();
        distanceToAddToList = 500;
        willLeaveAtDeath = true;
        initSkill();
        sortSkill();
    }

    public void addTarget(Char _c) {
        if (!listTarget.contains(_c)) {
            this.listTarget.add(_c);
        }
    }


    public void setInfo(long hp , long mp , long dame , int def , int crit) {
        characterInfo.setBaseHP(hp);
        characterInfo.setBaseMP(mp);
        characterInfo.setBaseDamage(dame);
        characterInfo.setBaseDefense(def);
        characterInfo.setBaseCritical(crit);
        characterInfo.setCharacterInfo();
        characterInfo.recovery(CharacterInfo.ALL, 100, false);
    }


    public void sortSkill() {
        for (int i = 0; i < getSkills().size() - 1; i++) {
            Skill skill = getSkills().get(i);
            for (int j = i + 1; j < getSkills().size(); j++) {
                Skill skill2 = getSkills().get(j);
                if (skill2.coolDown > skill.coolDown) {
                    Skill skill3 = skill2;
                    skill2 = skill;
                    skill = skill3;
                    getSkills().set(i, skill);
                    getSkills().set(j, skill2);
                }
            }
        }
    }

    public abstract void initSkill();

    @Override
    public void updateSkin() {
        setDefaultPart();
        if (this.isChocolate()) {
            setHead((short) 312);
            setBody((short) 413);
            setLeg((short) 414);
        }
    }

    public abstract void sendNotificationWhenAppear(String map);

    public abstract void sendNotificationWhenDead(String name);

    public abstract void setDefaultLeg();
    public abstract void setDefaultBody();
    public abstract void setDefaultHead();



    public boolean isMeCanAttackOtherPlayer (Char cAtt) {
        return cAtt != null && !cAtt.isMiniDisciple() && (((cAtt.getTypePk() == 3 && this.getTypePk() == 3) || (this.getTypePk()  == 5 || cAtt.getTypePk() == 5 || ((int) this.getTypePk() == 1 && (int) cAtt.getTypePk() == 1)) || ((int) this.getTypePk() == 4 && (int) cAtt.getTypePk() == 4) || (this.getTypePk() >= 0 && this.getTypePk() == cAtt.getId()) || (this.getKillCharId() >= 0 && this.getKillCharId() == cAtt.getId() && !this.isLang()) || (cAtt.getKillCharId() >= 0 && cAtt.getKillCharId() == this.getId() && !this.isLang()) || (this.getFlag() == 8 && cAtt.getFlag() != 0) || (this.getFlag() != 0 && cAtt.getFlag() == 8) || (this.getFlag() != cAtt.getFlag() && this.getFlag() != 0 && cAtt.getFlag() != 0)) && cAtt.getStatusMe() != 14) && cAtt.getStatusMe() != 5;
    }

    public void setLocation(Zone zone) {
        TMap map = zone.map;
        int w = map.width;
        int h = map.height;
        this.setX((short) (w / 2));
        this.setY(map.collisionLand(getX(), (short) 24));
        zone.enter(this);
        sendNotificationWhenDead(map.name);
    }

    public void setLocation(int mapID, int zoneID) {
        TMap map = MapManager.getInstance().getMap(mapID);
        if (zoneID == -1) {
            zoneID = map.randomZoneID();
        }
        Zone zone = map.getZoneByID(zoneID);
        setLocation(zone);
    }

    @Override
    public boolean meCanAttack() {
        return !isDead() && !isFreeze() && !isSleep() && !isHeld();
    }


    @Override
    public boolean meCanMove() {
        return super.meCanMove() && getHold() == null;
    }


    @Override
    public void killed(Object killer) {
        super.killed(killer);
        if (killer instanceof Char) {
            Char _c = (Char) killer;
            sendNotificationWhenDead(_c.getName());
        }
    }


    @Override
    public void startDie() {
        listTarget.clear();
        if (this.sayTheLastWordBeforeDie != null) {
            chat(this.sayTheLastWordBeforeDie);
        }
        this.typePk = 0;
        super.startDie();


        if (willLeaveAtDeath) {
            Utils.setTimeout(() -> {
                if (zone != null) {
                    zone.leave(this);
                }
            }, waitingTimeToLeave);
        }
    }


    @Override
    public boolean isBoss() {
       return true;
    }

    public ArrayList<Char> getEnemiesClosest() {
        ArrayList<Char> list = new ArrayList<>();
        for (Char enemy : listTarget) {
            if (enemy.isDead() || enemy.zone != zone) {
                continue;
            }
            if (enemy.isBoss()) {
                continue;
            }
            if (isMeCanAttackOtherPlayer(enemy)) {
                if (limit == -1) {
                    list.add(enemy);
                } else {
                    int d = Utils.getDistance(getX(), getY(), enemy.getX(), enemy.getY());
                    if (d < limit) {
                        list.add(enemy);
                    }
                }
            }
        }
        return list;
    }



    public void move() {
        if (!meCanMove()) {
            return;
        }
        TMap map = zone.map;
        int w = map.width;
        int h = map.height;
        int x = Utils.nextInt(50, w - 50);
        int y = Utils.nextInt(24, h - 24);
        setX((short) (getX() + Utils.nextInt(-100, 100)));
        if (getX() < 50) {
            setX((short) 50);
        }
        if (getX() > w - 50) {
            setX((short) (w - 50));
        }
        setY(map.collisionLand(getX(), (short) y));
        setY(map.collisionLand(getX(), (short) 0));
        zone.mapService.move(this);
    }

    @Override
    public void attack(Object obj) {
        long now = System.currentTimeMillis();
        if (now - lastTimeSkillShoot < 1000) {
            return;
        }
        Char target = (Char) obj;
        Skill skill = selectSkillAttack();
        if (skill != null) {
            int d = Utils.getDistance(0, 0, skill.dx, skill.dy);
            if (skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_MASENKO || skill.template.id == SkillName.CHIEU_ANTOMIC) {
                lastTimeSkillShoot = now;
            }
            this.select = skill;
            moveTo((short) (target.getX() + Utils.nextInt(-d, d)), target.getY());
            zone.attackPlayer(this, target);

        }
    }

    private Skill selectSkillAttack() {
        List<Skill> list = new ArrayList<>();
        for (Skill skill : getSkills()) {
            if (!skill.isCooldown()) {
                if (skill.template.type == 1) {
                    list.add(skill);
                }
            }
        }
        if (list.size() > 0) {
            int index = Utils.nextInt(list.size());
            return list.get(index);
        } else {
            return null;
        }
    }

    private void moveTo(short x, short y) {
        if (isDead()) {
            return;
        }
        if (isBlind() || isFreeze() || isSleep() || isCharge()) {
            return;
        }
        setX(x);
        setY(y);
        if (zone != null) {
            zone.mapService.move(this);
        }
    }
}
