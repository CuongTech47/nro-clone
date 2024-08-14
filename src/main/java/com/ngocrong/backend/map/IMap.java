package com.ngocrong.backend.map;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.map.tzone.Zone;
import com.ngocrong.backend.util.Utils;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Data
public abstract class IMap<E>{
    private boolean running;

//    @Getter

    private final int id;
    private int countDown;
    private  int countdownTimes;
    protected final List<E> zones;

    public IMap(int countdown) {
        this.countdownTimes = countdown;
        this.countDown = countdown;
        this.id = generateUniqueId();
        this.zones = new ArrayList<>();
        this.running = true;
    }

    private int generateUniqueId() {
        long nowInSeconds = System.currentTimeMillis() / 1000;
        return (int) (nowInSeconds + Utils.nextInt((int) nowInSeconds));
    }

    public Zone getZone(int mapId) {
        return zones.stream()
                .map(zone -> (Zone) zone)
                .filter(zone -> zone.map.mapID == mapId)
                .findFirst()
                .orElse(null);
    }

    public void sendServerMessage(String message) {
        for (E zoneElement : zones) {
            Zone zone = (Zone) zoneElement;
            List<Char> characters = zone.getListChar(Zone.TYPE_HUMAN);
            for (Char character : characters) {
                try {
                    character.getService().serverMessage(message);
                } catch (Exception ignored) {
                    // Log or handle exception if necessary
                }
            }
        }
    }

    public void update() {
        if (--countDown <= 0) {
            running = false;
            close();
        }
    }

    public abstract void close();
}
