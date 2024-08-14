package com.ngocrong.backend.map;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.map.tzone.ZBarrack;
import com.ngocrong.backend.map.tzone.Zone;

import java.util.List;

public class Barrack extends IMap<ZBarrack>{
    public Barrack(String name) {
        super(1800);
    }

    @Override
    public void close() {
        for (Zone zone : zones) {
            List<Char> list = zone.getListChar(Zone.TYPE_HUMAN);
            for (Char _c : list) {
                try {
                    _c.service.serverMessage("Đã hết thời gian, bạn sẽ được đưa về nhà");
                    _c.goHome();
                } catch (Exception e) {

                }
            }
            zone.map.removeZone(zone);
        }
    }
}
