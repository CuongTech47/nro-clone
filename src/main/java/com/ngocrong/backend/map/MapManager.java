package com.ngocrong.backend.map;

import com.ngocrong.backend.map.tzone.TMap;
import org.apache.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MapManager implements Runnable{
    private static Logger logger = Logger.getLogger(MapManager.class);
    private static MapManager instance;

    public HashMap<Integer, TMap> maps;
    public HashMap<Integer, IMap> list;
    public BaseBabidi baseBabidi;
//    public MBlackDragonBall blackDragonBall;
//    public MartialArtsFestival martialArtsFestival;
    public boolean running;

    public MapManager() {
        this.running = true;
        maps = new HashMap<>();
        list = new HashMap<>();
    }

    public static MapManager getInstance() {
        if (instance == null) {
            synchronized (MapManager.class) {
                if (instance == null) {
                    instance = new MapManager();
                }
            }
        }
        return instance;
    }
    @Override
    public void run() {

    }
    public void openBaseBabidi() {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext5 = zonedNow.withHour(12).withMinute(0).withSecond(0);//
        if (zonedNow.compareTo(zonedNext5) > 0) {
            zonedNext5 = zonedNext5.plusDays(1);
        }
        Duration duration = Duration.between(zonedNow, zonedNext5);
        long initalDelay = duration.getSeconds();
        Runnable runnable = new Runnable() {
            public void run() {
                baseBabidi = new BaseBabidi();
                addObj(baseBabidi);
            }
        };
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable, initalDelay, 1 * 24 * 60 * 60, TimeUnit.SECONDS);
    }

    private void addObj(IMap iMap) {
        list.put(iMap.getId(),iMap);
    }

    public TMap getMap(int id) {
        return maps.get(id);
    }

}
