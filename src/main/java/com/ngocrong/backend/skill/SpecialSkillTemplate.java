package com.ngocrong.backend.skill;

import lombok.Getter;

public class SpecialSkillTemplate {
    public int id;
    public String info;
    public byte planet;
    public int min, max;
    @Getter
    public int icon;

    public String getInfo() {
        String info = this.info.replaceAll("#", String.format("%d%% đến %d%%", min, max));
        return info;
    }
}
