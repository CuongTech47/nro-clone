package com.ngocrong.backend.map;

import com.ngocrong.backend.map.tzone.TMap;
import org.apache.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void close() {
        for (TMap t : maps.values()) {
            try {
                t.close();
            } catch (Exception e) {
                logger.error("close", e);
            }
        }
    }

    public void update() {
        List<IMap> removes = new ArrayList<>();
        for (IMap iMap : list.values()) {
            try {
                if (iMap.running) {
                    iMap.update();
                } else {
                    removes.add(iMap);
                }
            } catch (Exception e) {
                logger.error("update error", e);
            }
        }
        for (IMap iMap : removes) {
            removeObj(iMap);
        }
    }

    private void removeObj(IMap iMap) {
        list.remove(iMap.getId(), iMap);
    }

    @Override
    public void run() {
        while (running) {
            long delay = 1000;
            try {
                long l1 = System.currentTimeMillis();
                update();
                long l2 = System.currentTimeMillis();
                long l3 = l2 - l1;
                if (l3 > delay) {
                    continue;
                }
                Thread.sleep(delay - l3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
