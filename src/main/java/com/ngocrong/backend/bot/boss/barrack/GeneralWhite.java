package com.ngocrong.backend.bot.boss.barrack;

import com.ngocrong.backend.bot.Boss;
import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.item.ItemMap;
import com.ngocrong.backend.map.tzone.Zone;
import com.ngocrong.backend.model.RandomItem;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.skill.Skills;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class GeneralWhite extends Boss {

    private static Logger logger = Logger.getLogger(GeneralWhite.class);

    public ArrayList<String> chats = new ArrayList<>();

    public GeneralWhite() {
        super();
        this.limit = 100;
        this.setName("Trung úy Trắng");
        chats.add("Ha Ha Ha");
        chats.add("Xem mi dùng cách nào để hạ được ta");
        chats.add("Bulon đâu tiêu diệt hết bọn chúng cho ta");
        setInfo(500,1000000,400,10,5);
        characterInfo.setEvasionPercent(20);
        setTypePK((byte) 5);


    }


    public void setLocation(Zone zone) {
        setX((short) 927);
        setY((short) 384);
        zone.enter(this);
    }

    @Override
    public void initSkill() {
        try {
            setSkills(new ArrayList());
            Skill skill;
            skill = Skills.getSkill((byte) 0, (byte) 7).clone();
            getSkills().add(skill);
            skill = Skills.getSkill((byte) 1, (byte) 7).clone();
            getSkills().add(skill);
        } catch (CloneNotSupportedException ex) {
            logger.error("init skill err", ex);
        }
    }

    @Override
    public void sendNotificationWhenAppear(String map) {

    }

    @Override
    public void sendNotificationWhenDead(String name) {

    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 143);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 142);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 141);
    }

    @Override
    public void attack(Object obj) {

    }

    @Override
    public Object targetDetect() {
        return null;
    }

    @Override
    public void move() {
        setX((short) Utils.nextInt(755, 1060));
        setY((short) 384);
    }

    @Override
    public void chat(String chat) {

    }

    @Override
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
                    if (enemy.getX() > 755 && enemy.getX() < 1060) {
                        list.add(enemy);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public void throwItem(Object obj) {
        if (obj != null) {
            Char c = (Char) obj;
            int itemID = RandomItem.DRAGONBALL.next();
            Item item = new Item(itemID);
            item.setDefaultOptions();
            item.quantity = 1;
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.item = item;
            itemMap.playerID = Math.abs(c.getId());
            itemMap.x = getX();
            itemMap.y = zone.map.collisionLand(getX(), getY());
            zone.addItemMap(itemMap);
            zone.mapService.addItemMap(itemMap);
        }
    }

    @Override
    public void updateEveryOneSeconds() {
        super.updateEveryOneSeconds();
        if (!isDead()) {
            int index = Utils.nextInt(chats.size());
            chat(chats.get(index));
        }
    }




}
