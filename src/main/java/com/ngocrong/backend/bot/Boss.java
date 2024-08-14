package com.ngocrong.backend.bot;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.character.CharService;
import com.ngocrong.backend.character.CharacterInfo;
import com.ngocrong.backend.network.Service;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public abstract class Boss extends Char implements IBoss {
    private static Logger logger = Logger.getLogger(Boss.class);

    public int limit;
    public long waitingTimeToLeave;
    public String sayTheLastWordBeforeDie;
    public ArrayList<Char> listTarget;
    public int distanceToAddToList;
    public boolean willLeaveAtDeath;
    private long lastTimeSkillShoot;

    public Boss() {
        int id = (int) (System.currentTimeMillis() - 1649080000000L) + Utils.nextInt(1000);
        this.setId(-Math.abs(id));
        service = new CharService(this);
        setDefaultPart();
        this.listTarget = new ArrayList<>();
        characterInfo = new CharacterInfo(this);
        characterInfo.setPowerLimited();
        characterInfo.setSatamina();
        characterInfo.setCharacterInfo();
        characterInfo.recovery(CharacterInfo.ALL, 100, false);
        setIdMount(-1);
        setHaveMount(false);
        this.waitingTimeToLeave = 1000;
        effects = new ArrayList();
        itemTimes = new ArrayList();
        distanceToAddToList = 500;
        willLeaveAtDeath = true;
        initSkill();
        sortSkill();
    }

    public void sortSkill() {
        for (int i = 0; i < getSkills().size() - 1; i++) {
            Skill skill = getSkills().get(i);
            for (int j = i + 1; j < getSkills().size(); j++) {
                Skill skill2 = getSkills().get(j);
                if (skill2.coolDown > skill.coolDown) {
                    Skill skill3 = skill2;
                    skill2 = skill;
                    skill = skill3;
                    getSkills().set(i, skill);
                    getSkills().set(j, skill2);
                }
            }
        }
    }

    public abstract void initSkill();
}
