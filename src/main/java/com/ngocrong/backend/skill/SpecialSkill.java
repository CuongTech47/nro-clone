package com.ngocrong.backend.skill;

import java.util.ArrayList;
import java.util.List;

public class SpecialSkill {
    public static ArrayList<SpecialSkillTemplate> specialSkillTemplates;

    public static List<SpecialSkillTemplate> getListSpecialSkill(int planet) {
        List<SpecialSkillTemplate> list = new ArrayList<>();
        for (SpecialSkillTemplate s : specialSkillTemplates) {
            if (s.planet == planet || s.planet == 3) {
                list.add(s);
            }
        }
        return list;
    }

    public int id;
    public int param;
    public transient SpecialSkillTemplate template;

    public SpecialSkill(int id, int param) {
        this.id = id;
        this.param = param;
        setTemplate();
    }

    public void setTemplate() {
        for (SpecialSkillTemplate t : specialSkillTemplates) {
            if (t.id == this.id) {
                this.template = t;
            }
        }
    }

    public int getIcon() {
        return template.icon;
    }

    public String getInfo() {
        String info = template.info.replaceAll("#", this.param + "%");
        return info;
    }

    public String getInfo2() {
        String info = getInfo();
        info += String.format(" [%d đến %d]", template.min, template.max);
        return info;
    }
}
