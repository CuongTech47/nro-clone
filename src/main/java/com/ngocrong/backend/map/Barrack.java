package com.ngocrong.backend.map;

import com.ngocrong.backend.bot.Boss;
import com.ngocrong.backend.bot.boss.barrack.GeneralWhite;
import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.map.tzone.TMap;
import com.ngocrong.backend.map.tzone.ZBarrack;
import com.ngocrong.backend.map.tzone.Zone;
import org.apache.log4j.Logger;

import java.util.List;

public class Barrack extends IMap<ZBarrack> {

    private static Logger logger = Logger.getLogger(Barrack.class);
    public static final int[] MAPS = {53, 58, 59, 60, 61, 62, 55, 56, 54, 57};

    private String openMemberName;
    private long openedAt;
    public boolean isWinner;
    public boolean rewarded;

    public Barrack(String name) {
        super(1800);
        this.openMemberName = name;
        this.openedAt = System.currentTimeMillis();
        for (int mapID : MAPS) {
            TMap map = MapManager.getInstance().getMap(mapID);
            ZBarrack z = new ZBarrack(this, map, map.autoIncrease++);
            map.addZone(z);
            zones.add(z);
            if (mapID == 59 || mapID == 62 || mapID == 55 || mapID == 54 || mapID == 57) {
                //set boss
                Boss boss = null;
                if (mapID == 59) {
                    // trung úy trắng
                    boss = new GeneralWhite();
                }
                if (mapID == 62) {
                    // trung úy xanh lơ
//                    boss = new GeneralBlue();
                }
                if (mapID == 55) {
                    // trung úy thép
//                    boss = new MajorMetallitron();
                }
                if (mapID == 54) {
                    // Ninja Áo tím
//                    boss = new NinjaMurasaki(-1000000000, false);
                }
                if (mapID == 57) {
                    // Robot vệ sĩ
//                    Boss boss1 = new Robot(-10000000, "Robot vệ sĩ 1");
//                    boss1.setLocation(z);
//                    Boss boss2 = new Robot(-10000001, "Robot vệ sĩ 2");
//                    boss2.setLocation(z);
//                    Boss boss3 = new Robot(-10000002, "Robot vệ sĩ 3");
//                    boss3.setLocation(z);
//                    Boss boss4 = new Robot(-10000003, "Robot vệ sĩ 4");
//                    boss4.setLocation(z);
                }
                if (boss != null) {
                    boss.setLocation(z);
                }
            }
        }
    }

    public void enterMap(int map, Char _c) {
        for (ZBarrack zone : zones) {
            if (zone.map.mapID == map) {
                zone.setBarrack(_c.clan);
                zone.enter(_c);
                return;
            }
        }
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



