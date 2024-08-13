package com.ngocrong.backend.skill;

import java.util.ArrayList;

public class SkillTemplate {
    public byte id;
    public int classId;
    public String name;
    public int maxPoint;
    public int manaUseType;
    public int type;
    public int icon;
    public String description;
    public ArrayList<Skill> skills;
    public String damInfo;

    public boolean isBuffToPlayer() {
        return this.type == 2;
    }

    public boolean isUseAlone() {
        return this.type == 3;
    }

    public boolean isAttackSkill() {
        return this.type == 1;
    }
}
