package com.ngocrong.backend.bot;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.character.CharService;
import com.ngocrong.backend.character.CharacterInfo;
import com.ngocrong.backend.network.Service;
import com.ngocrong.backend.util.Utils;

import java.util.ArrayList;
public class TrongTai extends Char {
    public TrongTai() {
        super();
        this.setId(-(((Utils.nextInt(100) * 1000) + Utils.nextInt(100) * 100)) + Utils.nextInt(100));
        this.setName("Trọng tài");
        characterInfo = new CharacterInfo(this);
        characterInfo.setCharacterInfo();
        characterInfo.recovery(CharacterInfo.ALL, 100, false);
        service = (CharService) new Service(this);
        setEffects(new ArrayList<>());

        itemTimes = new ArrayList<>();
        setIdMount(-1);
        setDefaultPart();
    }

//    @Override
//    public boolean isBoss() {
//        return false;
//    }
//
//    @Override
//    public boolean isDisciple() {
//        return false;
//    }
//
//    @Override
//    public boolean isHuman() {
//        return false;
//    }
//
//    @Override
//    public boolean isMiniDisciple() {
//        return false;
//    }
//
//    @Override
//    public boolean isEscort() {
//        return false;
//    }
//
//    @Override
//    public void setDefaultLeg() {
//        setLeg((short) 116);
//    }
//
//    @Override
//    public void setDefaultBody() {
//        setBody((short) 115);
//    }
//
//    @Override
//    public void setDefaultHead() {
//        setHead((short) 114);
//    }
//
//    public void chat(String chat) {
//        if (zone != null) {
//            zone.service.chat(this, chat);
//        }
//    }
}
