package com.ngocrong.backend.bot.boss.karin;

import com.ngocrong.backend.bot.Boss;
import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.skill.Skills;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class TaoPaiPai extends Boss {



    private static final Logger logger = Logger.getLogger(TaoPaiPai.class);

    public TaoPaiPai() {
        super();
        this.limit = -1;
        this.setName("Tàu Pảy Pảy");
        this.sayTheLastWordBeforeDie = "Ngươi hãy chờ đấy";
        Utils.setTimeout(() -> {
            setTypePK((byte) 5);
        }, 5000);
    }

    public void initSkill() {
        try {
            setSkills(new ArrayList<>());
            Skill skill = Skills.getSkill((byte) 0, (byte) 7).clone();
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
        setLeg((short) 94);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 93);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 92);
    }

    public void killed(Object obj) {
        Char killer = (Char) obj;
        if (killer.getTaskMain().id == 10 && killer.getTaskMain().index == 1) {
            killer.taskNext();
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
    public void move() {

    }

    @Override
    public void chat(String chat) {

    }


}
