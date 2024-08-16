package com.ngocrong.backend.bot.boss;

import com.ngocrong.backend.bot.Boss;
import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.item.ItemMap;
import com.ngocrong.backend.item.ItemOption;
import com.ngocrong.backend.map.tzone.Zone;
import com.ngocrong.backend.model.RandomItem;
import com.ngocrong.backend.server.SessionManager;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.skill.Skills;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Cooler  extends Boss{
    private static Logger logger = Logger.getLogger(Cooler.class);
    private byte level;



    public Cooler(byte level) {
        super();
        this.level = level;
        this.distanceToAddToList = 500;
        this.limit = 500;
        if (this.level == 0) {

            setName("Cooler");
            setInfo(80000000, 1000000, 15000, 1000, 50);
        } else {
            setName("Cooler 2");
            setInfo(100000000, 1000000, 20000, 1000, 50);
        }
        this.waitingTimeToLeave = 0;
        setTypePK((byte) 5);
    }
    @Override
    public void initSkill() {
        try {
            setSkills(new ArrayList<>());
            getSkills().add(Skills.getSkill((byte) 0, (byte) 7).clone());
            getSkills().add(Skills.getSkill((byte) 1, (byte) 7).clone());
            getSkills().add(Skills.getSkill((byte) 2, (byte) 7).clone());
            getSkills().add(Skills.getSkill((byte) 3, (byte) 7).clone());
            getSkills().add(Skills.getSkill((byte) 4, (byte) 7).clone());
            getSkills().add(Skills.getSkill((byte) 5, (byte) 7).clone());

        } catch (CloneNotSupportedException ex) {
            logger.error("init skill");
        }
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.getName(), map));
    }

    @Override
    public void sendNotificationWhenDead(String name) {
        SessionManager.chatVip(String.format("%s: Đã tiêu diệt được %s mọi người đều ngưỡng mộ.", name, this.getName()));
    }

    @Override
    public void setDefaultLeg() {
        if (this.level == 0) {
            setHead((short) 317);
        } else {
            setHead((short) 320);
        }
    }

    @Override
    public void setDefaultBody() {
        if (this.level == 0) {
            setBody((short) 318);
        } else {
            setBody((short) 321);
        }
    }

    @Override
    public void setDefaultHead() {
        if (this.level == 0) {
            setLeg((short) 319);
        } else {
            setLeg((short) 322);
        }
    }

    @Override
    public void throwItem(Object obj) {
        if (Utils.nextInt(5) == 0) {
            if (obj != null) {
                Char c = (Char) obj;
                int itemID = RandomItem.COOLER.next();
                Item item = new Item(itemID);
                item.setDefaultOptions();
                for (ItemOption o : item.options) {
                    int p = Utils.nextInt(0, 15);
                    o.param += o.param * p / 100;
                }
                item.addItemOption(new ItemOption(107, Utils.nextInt(2, 6)));
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
    }

    public void startDie() {
        Zone z = zone;
        super.startDie();
        if (level == 0) {
            Cooler cooler = new Cooler((byte) 1);
            cooler.setLocation(z);
        } else {
            Utils.setTimeout(() -> {
                Cooler cooler = new Cooler((byte) 0);
                cooler.setLocation(110, -1);
            }, 1800000);
        }
    }

    @Override
    public void attack(Object obj) {

    }

    @Override
    public Object targetDetect() {
        return null;
    }

    @Override
    public void chat(String chat) {

    }
}
