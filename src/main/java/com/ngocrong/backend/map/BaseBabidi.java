package com.ngocrong.backend.map;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.consts.MapName;
import com.ngocrong.backend.map.tzone.GravityRoom;
import com.ngocrong.backend.map.tzone.SpaceshipRoom;
import com.ngocrong.backend.map.tzone.TMap;
import org.apache.log4j.Logger;

public class BaseBabidi extends IMap<SpaceshipRoom>{
    private static Logger logger = Logger.getLogger(BaseBabidi.class);
    public static final int[] MAPS = {
            MapName.CONG_PHI_THUYEN, MapName.PHONG_CHO,
            MapName.CUA_AI_1, MapName.CUA_AI_3, MapName.PHONG_CHI_HUY
    };
    private long lastUpdateHypnosis;
    public BaseBabidi() {
        super(3600);
        this.lastUpdateHypnosis = System.currentTimeMillis();
        initializeZones();
    }

    private void initializeZones() {
        int zoneNumber = 5;
        for (int mapId : MAPS) {
            TMap map = MapManager.getInstance().getMap(mapId);

            for (int i = 0; i < zoneNumber; i++) {
                SpaceshipRoom zone = createZone(map, i, mapId);
                zones.add(zone);
                map.addZone(zone);
                initializeBosses(mapId, zone);
            }
        }
    }

    private SpaceshipRoom createZone(TMap map, int index, int mapId) {

        if (mapId == MapName.CUA_AI_1) {
            return new GravityRoom(map, index);
        }
//        else if (mapId == MapName.PHONG_CHI_HUY) {
////            return new CommandRoom(map, index);
//        }
        else {
            return new SpaceshipRoom(map, index);
        }
    }

    public void nextFloor(Char character) {
        int nextFloor = getNextFloor(character.getCurrentNumberFloorInBaseBabidi());
        TMap nextMap = MapManager.getInstance().getMap(nextFloor);
        int nextZoneId = nextMap.getZoneID();
        character.zone.leave(character);
        character.setY((short) 100);
        nextMap.enterZone(character, nextZoneId);
    }

    private int getNextFloor(int currentFloor) {
        int nextFloor = currentFloor + 1;
        if (nextFloor >= MAPS.length) {
            nextFloor = 0;
        }
        return MAPS[nextFloor];
    }

    private void initializeBosses(int mapId, SpaceshipRoom zone) {
//        if (mapId == MapName.CONG_PHI_THUYEN || mapId == MapName.PHONG_CHI_HUY) {
//            new Drabura().setLocation(zone);
//        }
//        if (mapId == MapName.PHONG_CHO || mapId == MapName.CUA_AI_1) {
//            new BuiBui().setLocation(zone);
//        }
//        if (mapId == MapName.CUA_AI_3) {
//            new Yacon().setLocation(zone);
//        }
    }

    @Override
    public void close() {
        MapManager.getInstance().baseBabidi =null;
        for (SpaceshipRoom zone: zones) {

        }
    }
}
