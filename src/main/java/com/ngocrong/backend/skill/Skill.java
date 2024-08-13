package com.ngocrong.backend.skill;

public class Skill implements Cloneable{
    public static final byte ATT_STAND = 0;
    public static final byte ATT_FLY = 1;
    public static final byte SKILL_AUTO_USE = 0;
    public static final byte SKILL_CLICK_USE_ATTACK = 1;
    public static final byte SKILL_CLICK_USE_BUFF = 2;
    public static final byte SKILL_CLICK_NPC = 3;
    public static final byte SKILL_CLICK_LIVE = 4;

    public static final byte ADD_EFFECT = 1;
    public static final byte REMOVE_EFFECT = 0;
    public static final byte REMOVE_ALL_EFFECT = 2;

    public static final byte CHARACTER = 0;
    public static final byte MONSTER = 1;

    public SkillTemplate template;
    public short id;
    public int point;
    public long powerRequire;
    public int coolDown;
    public long lastTimeUseThisSkill;
    public int dx;
    public int dy;
    public int maxFight;
    public int manaUse;
    public SkillOption[] options;
    public boolean paintCanNotUseSkill;
    public short damage;
    public String moreInfo;
    public short price;

    public boolean isCooldown() {
        long currentTimeMillis = System.currentTimeMillis();
        long num = currentTimeMillis - lastTimeUseThisSkill;
        return num < (long) this.coolDown;
    }

    @Override
    public Skill clone() throws CloneNotSupportedException {
        return (Skill) super.clone();
    }
}
