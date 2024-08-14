package com.ngocrong.backend.model;

import com.ngocrong.backend.skill.SkillTemplate;

import java.util.ArrayList;

public class NClass {
    public int classId;
    public String name;
    public ArrayList<SkillTemplate> skillTemplates = new ArrayList<>();
}
