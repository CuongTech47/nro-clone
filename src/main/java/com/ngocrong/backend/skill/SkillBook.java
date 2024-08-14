package com.ngocrong.backend.skill;

public class SkillBook {
    public int id;
    public int level;
    public long studyTime;

    public SkillBook(int id, int level, long time) {
        this.id = id;
        this.level = level;
        this.studyTime = time;
    }
}
