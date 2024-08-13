package com.ngocrong.backend.map.tzone;

import com.ngocrong.backend.character.Char;

public class GravityRoom extends SpaceshipRoom {
    public GravityRoom(TMap map, int zoneId) {
        super(map, zoneId);
    }



    public void enter(Char p) {
        super.enter(p);
        if (p.isHuman()) {
            p.characterInfo.setCharacterInfo();
            p.service.serverMessage("Đây là không gian cao trọng lục, hãy cẩn thận");
        }



    }
    public void leave(Char p) {
        super.leave(p);
        if (p.isHuman()) {
            p.characterInfo.setCharacterInfo();
        }
    }
}
