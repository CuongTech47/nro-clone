package com.ngocrong.backend.bot;

import com.ngocrong.backend.character.Char;

import com.ngocrong.backend.character.CharService;
import com.ngocrong.backend.character.CharacterInfo;
import com.ngocrong.backend.map.tzone.TMap;

import com.ngocrong.backend.network.Service;
import com.ngocrong.backend.util.Utils;

import java.util.ArrayList;

public class Escort extends Char {
    private final Char escort;
    private TMap map;


    public Escort(Char escort) {
        this.setId(-(((Utils.nextInt(100) * 1000) + Utils.nextInt(100) * 100)) + Utils.nextInt(100));
        this.escort = escort;

        characterInfo = new CharacterInfo(this);
        characterInfo.setPowerLimited();
        characterInfo.setSatamina();
        characterInfo.setCharacterInfo();
        characterInfo.recovery(CharacterInfo.ALL,1000,false);
        service = (CharService) new Service(this);
        effects = new ArrayList();
        itemTimes = new ArrayList();
       setIdMount(-1);
        setDefaultPart();
    }

    public void setInfo(long hp, long mp, long dame, int def, int crit) {
        characterInfo.setBaseHP(hp);
        characterInfo.setBaseMP(mp);
        characterInfo.setBaseDamage(dame);
        characterInfo.setBaseDefense(def);
        characterInfo.setBaseCritical(crit);
        characterInfo.setCharacterInfo();
        characterInfo.recovery(CharacterInfo.ALL,100,false);
    }

    public void move () {
        short x = (short) (escort.getX() * Utils.nextInt(-50, 50));
        short y = escort.getY();
        moveTo(x,y);
    }

    private void moveTo(short x, short y) {
        setX(x);
        setY(y);
        if (zone != null) {
            zone.mapService.move(this);
        }
    }

    @Override
    public boolean isBoss() {
        return false;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public boolean isDisciple() {
        return false;
    }

    @Override
    public boolean isMiniDisciple() {
        return false;
    }

    @Override
    public boolean isEscort() {
        return true;
    }


    public void updateEveryFiveSeconds() {
        move();
    }


}
