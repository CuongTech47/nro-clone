package com.ngocrong.backend.bot;

import com.ngocrong.backend.bot.boss.Cooler;
import com.ngocrong.backend.map.MapManager;
import com.ngocrong.backend.map.tzone.TMap;
import com.ngocrong.backend.map.tzone.Zone;
import com.ngocrong.backend.util.Utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BossManager {
    public static void bornBoss() {
        bossShizuka();
        bossCooler();
//        bossBlackGoku();
//        bossGinyu();
//        bossGalaxySoldier();
    }

    private static void bossCooler() {
        Utils.setTimeout(() -> {
            Cooler cooler = new Cooler((byte) 0);
            cooler.setLocation(110, -1);
            System.out.println("boss cooler");
        }, 1800000);
    }

    private static void bossShizuka() {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext5 = zonedNow.withHour(12).withMinute(30).withSecond(0);
        if (zonedNow.compareTo(zonedNext5) > 0) {
            zonedNext5 = zonedNext5.plusDays(1);
        }
        Duration duration = Duration.between(zonedNow, zonedNext5);
        long initalDelay = duration.getSeconds();
        Runnable runnable = new Runnable() {
            public void run() {
                TMap map = MapManager.getInstance().getMap(155);
//                for (Zone z : map.zones) {
////                    Boss boss = new Shizuka();
////                    boss.setLocation(z);
//                }

            }
        };
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable, initalDelay, 1 * 24 * 60 * 60, TimeUnit.SECONDS);
    }
}
