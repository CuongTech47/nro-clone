package com.ngocrong.backend.server;

import com.google.gson.JsonArray;
import com.ngocrong.backend.item.ItemOptionTemplate;
import com.ngocrong.backend.item.ItemTemplate;
import com.ngocrong.backend.model.AchievementTemplate;
import com.ngocrong.backend.model.NClass;
import com.ngocrong.backend.network.Session;
import com.ngocrong.backend.task.TaskTemplate;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
//import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    public byte[][] CACHE_ITEM = new byte[3][];
    public byte[] CACHE_MAP;
    public byte[] CACHE_SKILL;
    public byte[] CACHE_SKILL_TEMPLATE;
    public byte[] CACHE_DART;
    public byte[] CACHE_PART;
    public byte[] CACHE_ARROW;
    public byte[] CACHE_IMAGE;
    public byte[] CACHE_EFFECT;
    private static final Logger logger = Logger.getLogger(Server.class);
    public ArrayList<Long> powers;
    public int[] idHead;
    public int[] idAvatar;
    protected boolean start;
    public ArrayList<NClass> nClasss;
    protected ServerSocket server;
    protected int id;
    private boolean isMaintained;

    public static final int COUNT_SESSION_ON_IP = 3;
    public ArrayList<ItemOptionTemplate> iOptionTemplates;
    public HashMap<Integer, ItemTemplate> iTemplates;
    public static ConcurrentHashMap<String, Integer> ips = new ConcurrentHashMap<>();

    @Getter
    private List<AchievementTemplate> achievements;
    public ArrayList<TaskTemplate> taskTemplates;

    public byte[][] smallVersion, backgroundVersion;

    @Getter
    private final Config config;

    public Server() {
        this.config = new Config();
        this.config.load();
        this.achievements = new ArrayList<>();
    }

    public boolean isMaintained() {
        return isMaintained;
    }

    public void setMaintained(boolean maintained) {
        this.isMaintained = maintained;
    }

    public void saveData() {
        // Implementation for saving data
    }

    public static int getMaxQuantityItem() {
        return DragonBall.getInstance().getServer().getConfig().getMaxQuantity();
    }


    protected void start() {
        logger.debug("Start socket post=" + config.getPort());
        try {
            server = new ServerSocket(config.getPort());
            id = 0;
            start = true;
            Thread auto = new Thread(new AutoSaveData());
            auto.start();
//            BossManager.bornBoss();
//            MapManager mapManager = MapManager.getInstance();
//            mapManager.bornBroly();
//            mapManager.openBaseBabidi();
//            mapManager.openBlackDragonBall();
//            mapManager.openMartialArtsFestival();
//            Thread threadMapManager = new Thread(mapManager);
//            threadMapManager.start();
            logger.debug("Start server Success!");
            while (start) {
                try {
                    Socket client = server.accept();
                    InetSocketAddress socketAddress = (InetSocketAddress) client.getRemoteSocketAddress();
                    String ip = socketAddress.getAddress().getHostAddress();

                    // Ghi log khi có kết nối từ client
                    logger.info("Client connected from IP: " + ip);
                    if (ips.getOrDefault(ip, 0) < COUNT_SESSION_ON_IP) {
                        Session session = new Session(client, ip, ++id);
                        logger.debug("New session created: " + session.user);
                        SessionManager.addSession(session);
                    } else {
                        client.close();
                    }
                } catch (IOException e) {
                    logger.error("failed!", e);
                }
            }
        } catch (Exception e) {
            logger.error("failed!", e);
        }
    }

    protected void stop() {
        if (start) {
            close();
            start = false;
        }
    }

    protected void close() {
        try {
            server.close();
            server = null;
//            Lucky.isRunning = false;
//            MySQLConnect.close();
            System.gc();
            logger.debug("End socket");
        } catch (IOException e) {
            logger.error("failed!", e);
        }
    }

    protected void init() {
        System.out.println("hihi");
    }
}
