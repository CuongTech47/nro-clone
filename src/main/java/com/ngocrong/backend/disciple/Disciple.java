package com.ngocrong.backend.disciple;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.consts.SkillName;
import com.ngocrong.backend.lib.KeyValue;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.skill.SkillPet;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;
import com.ngocrong.backend.character.CharacterInfo;

import java.util.ArrayList;

public class Disciple extends Char {
    private static final Logger logger = Logger.getLogger(Disciple.class);

    public byte discipleStatus;
    public Char master;
    public byte skillOpened;
    public ArrayList<Char> listTarget;
    private boolean isAttacking;
    private long deadTime;
    public byte typeDisciple;

    public Disciple() {
        this.effects = new ArrayList<>();
        this.itemTimes = new ArrayList<>();
        this.setIdMount(-1);
        setHaveMount(false);
        this.listTarget = new ArrayList<>();
    }


    public void followMaster() {
        if (isRecoveryEnergy()) {
            stopRecoveryEnery();
        }
        short x = (short) (master.getX() + Utils.nextInt(-50, 50));
        short y = master.getY();
        moveTo(x, y);
    }

    private void moveTo(short x, short y) {
        if (isDead() || master.isDead()) {
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

    public void setFlag() {
        if (getFlag() != master.getFlag()) {
            setFlag(master.getFlag());
            if (zone != null) {
                zone.mapService.flag(this);
            }
        }
    }


    public void setMaster(Char master) {
        this.master = master;
    }

    public ArrayList<KeyValue> getInfoSkill() {
        ArrayList<KeyValue> list = new ArrayList<>();
        for (Skill skill : getSkills()) {
            KeyValue keyValue = new KeyValue((short) skill.id, skill.template.name);
            list.add(keyValue);
        }
        for (int i = list.size(); i < SkillPet.list.size(); i++) {
            SkillPet skillPet = SkillPet.list.get(i);
            KeyValue keyValue = new KeyValue((short) -1, skillPet.getMoreInfo());
            list.add(keyValue);
        }
        return list;
    }

    public void addSkill(Skill skill) {
        if (skill.template.id == SkillName.CHIEU_DAM_DRAGON || skill.template.id == SkillName.CHIEU_DAM_DEMON || skill.template.id == SkillName.CHIEU_DAM_GALICK) {
            skill.coolDown = 700;
        }
        if (skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_MASENKO || skill.template.id == SkillName.CHIEU_ANTOMIC) {
            skill.coolDown = 1300;
        }
        getSkills().add(skill);
    }
}
