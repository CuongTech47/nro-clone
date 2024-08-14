package com.ngocrong.backend.map.tzone;

import com.ngocrong.backend.character.Char;

public class MapSingle extends  Zone{
    public MapSingle(TMap map, int zoneId) {
        super(map, zoneId);
    }
    public void leave(Char _c) {
        super.leave(_c);
        if (_c.isHuman()) {
            this.running = false;
            map.removeZone(this);
        }
    }
}
