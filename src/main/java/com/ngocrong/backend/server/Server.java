package com.ngocrong.backend.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngocrong.backend.clan.ClanImage;
import com.ngocrong.backend.clan.ClanManager;
import com.ngocrong.backend.crackball.CrackBall;
import com.ngocrong.backend.effect.Effect;
import com.ngocrong.backend.effect.EffectCharPaint;
import com.ngocrong.backend.effect.EffectData;
import com.ngocrong.backend.effect.EffectInfoPaint;
import com.ngocrong.backend.effect.Frame;
import com.ngocrong.backend.effect.ImageInfo;
import com.ngocrong.backend.item.ItemOption;
import com.ngocrong.backend.item.ItemOptionTemplate;
import com.ngocrong.backend.item.ItemTemplate;
import com.ngocrong.backend.lib.KeyValue;
import com.ngocrong.backend.map.MapManager;
import com.ngocrong.backend.map.tzone.TMap;
import com.ngocrong.backend.mob.Mob;
import com.ngocrong.backend.mob.MobCoordinate;
import com.ngocrong.backend.mob.MobTemplate;
import com.ngocrong.backend.model.AchievementTemplate;
import com.ngocrong.backend.model.Arrowpaint;
import com.ngocrong.backend.model.BgItem;
import com.ngocrong.backend.model.Caption;
import com.ngocrong.backend.model.DartInfo;
import com.ngocrong.backend.model.GameInfo;
import com.ngocrong.backend.model.ImgByName;
import com.ngocrong.backend.model.NClass;
import com.ngocrong.backend.model.Notification;
import com.ngocrong.backend.model.Npc;
import com.ngocrong.backend.model.NpcTemplate;
import com.ngocrong.backend.model.Part;
import com.ngocrong.backend.model.PartImage;
import com.ngocrong.backend.model.PowerLimitMark;
import com.ngocrong.backend.model.RandomItem;
import com.ngocrong.backend.model.Waypoint;
import com.ngocrong.backend.network.Session;
import com.ngocrong.backend.server.mysql.MySQLConnect;
import com.ngocrong.backend.shop.Shop;
import com.ngocrong.backend.shop.Tab;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.skill.SkillInfoPaint;
import com.ngocrong.backend.skill.SkillOptionTemplate;
import com.ngocrong.backend.skill.SkillPaint;
import com.ngocrong.backend.skill.SkillPet;
import com.ngocrong.backend.skill.SkillTemplate;
import com.ngocrong.backend.skill.Skills;
import com.ngocrong.backend.skill.SpecialSkill;
import com.ngocrong.backend.skill.SpecialSkillTemplate;
import com.ngocrong.backend.task.TaskTemplate;
import com.ngocrong.backend.top.Top;
import com.ngocrong.backend.util.Utils;

import lombok.Getter;

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
    public ArrayList<Arrowpaint> arrs;
    public ArrayList<Long> powers;
    public int[] idHead;
    public int[] idAvatar;
    public ArrayList<GameInfo> gameInfos;
    protected boolean start;
    public ArrayList<NClass> nClasss;
    protected ServerSocket server;
    public ArrayList<DartInfo> darts;
    protected int id;
    public boolean isMaintained;
    public int[] resVersion = new int[4];
    public static final int COUNT_SESSION_ON_IP = 3;
    public ArrayList<ItemOptionTemplate> iOptionTemplates;
    public HashMap<Integer, ItemTemplate> iTemplates;
    public static ConcurrentHashMap<String, Integer> ips = new ConcurrentHashMap<>();
    public ArrayList<SkillOptionTemplate> sOptionTemplates;
    public ArrayList<EffectCharPaint> efs;
    public ArrayList<int[]> smallImg;
    public ArrayList<Part> parts;
    public ArrayList<SkillPaint> sks;
    public ArrayList<ItemTemplate> flags;
    public ArrayList<ClanImage> clanImages;

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
        MySQLConnect.createConnection(config.getDbHost(),config.getDbPort(),config.getDbName(),config.getDbUser(),config.getDbPassword());
        initBGSmallVersion();
        initSmallVersion();
        initResVersion();
        initItemTemplate();
        setCacheItem(0);
        setCacheItem(1);
        setCacheItem(2);
        initCaption();
        initPower();
        initEffectData();
        initNpc();
        initMob();
        initMap();
        setCacheMap();
        initSkillTemplate();
        setCacheSkillTemplate();
        initDart();
        setCacheDart();
        initArrow();
        setCacheArrow();
        initEffect();
        setCacheEffect();
        initImage();
        setCacheImage();
        initPart();
        setCachePart();
        initSkill();
        setCacheSkill();
        initBGItem();
        initOthers();
        Skills.init();
        initFlags();
        initTaskTemplate();
        initImgByName();
        initClanImage();
        initGameInfo();
        initSkillDisciple();
        initAchievement();
        CrackBall.loadItem();
        ClanManager.getInstance().init();
        RandomItem.init();
        Top.initialize();
//        initLucky();
        initializeSpecialSkill();
//        Card.loadTemplate();
//        Consignment.getInstance().init();
    }

    private void initializeSpecialSkill() {
        try {
            SpecialSkill.specialSkillTemplates = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_SPECIAL_SKILL);
            while (res.next()) {
                SpecialSkillTemplate s = new SpecialSkillTemplate();
                s.id = res.getInt("id");
                s.icon = res.getInt("icon");
                s.info = res.getString("info");
                s.planet = res.getByte("planet");
                s.min = res.getInt("min");
                s.max = res.getInt("max");
                SpecialSkill.specialSkillTemplates.add(s);
            }
            res.close();
            stmt.close();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    private void initLucky() {
    }

    private void initAchievement() {
        try {
            achievements = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_ACHIEVEMENT);
            while (res.next()) {
                AchievementTemplate achive = new AchievementTemplate();
                achive.setId(res.getInt("id"));
                achive.setName(res.getString("name"));
                achive.setContent(res.getString("content"));
                achive.setMaxCount(res.getInt("count"));
                achive.setReward( res.getInt("reward"));
                achievements.add(achive);
            }
            res.close();
            stmt.close();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    private void initSkillDisciple() {
        try {
            SkillPet.list = new ArrayList<>();
            Gson g = new Gson();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_SKILL_DISCIPLE);
            while (res.next()) {
                SkillPet skill = new SkillPet();
                skill.id = res.getInt("id");
                skill.name = res.getString("name");
                skill.powerRequire = res.getLong("power_require");
                skill.skills = g.fromJson(res.getString("skills"), byte[].class);
                SkillPet.list.add(skill);
            }
            res.close();
            stmt.close();
        } catch (SQLException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initGameInfo() {
        try {
            gameInfos = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_GAME_INFO);
            while (res.next()) {
                GameInfo gameInfo = new GameInfo();
                gameInfo.setId(res.getShort("id")); 
                gameInfo.setTitle(res.getString("title"));
                gameInfo.setContent(res.getString("content"));
                gameInfos.add(gameInfo);
            }
            res.close();
            stmt.close();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    private void initClanImage() {
        try {
            clanImages = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_CLAN_IMAGE);
            while (res.next()) {
                ClanImage clan = new ClanImage();
                clan.id = res.getByte("id");
                clan.name = res.getString("name");
                clan.gold = res.getInt("gold");
                clan.gem = res.getInt("gem");
                clan.isSale = res.getBoolean("is_sale");
                String str = res.getString("images");
                JSONArray jArr = new JSONArray(str);
                int size = jArr.length();
                clan.idImages = new short[size];
                for (int i = 0; i < size; i++) {
                    clan.idImages[i] = (short) jArr.getInt(i);
                }
                clanImages.add(clan);
            }
            res.close();
            stmt.close();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    private void initImgByName() {
        try {
            ImgByName.images = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_IMAGE_BY_NAME);
            while (res.next()) {
                ImgByName img = new ImgByName();
                img.id = res.getInt("id");
                img.filename = res.getString("filename");
                img.nFrame = res.getInt("n_frame");
                img.init();
                ImgByName.addImage(img);
            }
            res.close();
            stmt.close();
        } catch (SQLException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initTaskTemplate() {
        try {
            taskTemplates = new ArrayList<>();
            Gson g = new Gson();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_TASK_TEMPLATE);
            while (res.next()) {
                TaskTemplate task = new TaskTemplate();
                task.id = res.getInt("id");
                task.name = res.getString("name");
                task.rewardPotential = res.getInt("reward_potential");
                task.rewardPower = res.getInt("reward_power");
                task.rewardGold = res.getInt("reward_gold");
                task.rewardGem = res.getInt("reward_gem");
                task.rewardGemLock = res.getInt("reward_gem_lock");
                task.details = g.fromJson(res.getString("details"), String[].class);
                task.subNames = g.fromJson(res.getString("subnames"), String[][].class);
                task.contents = g.fromJson(res.getString("contents"), String[][].class);
                task.counts = g.fromJson(res.getString("counts"), short[].class);
                task.tasks = g.fromJson(res.getString("npcs"), int[][].class);
                task.mapTasks = g.fromJson(res.getString("maps"), int[][].class);
                taskTemplates.add(task);
            }
            res.close();
            stmt.close();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    private void initFlags() {
        flags = new ArrayList<>();
        for (ItemTemplate itemTemplate : iTemplates.values()) {
            if (itemTemplate.type == 28) {
                flags.add(itemTemplate);
            }
        }
    }

    private void initOthers() {
        try {
            Gson g = new Gson();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_CONFIG);
            while (res.next()) {
                String name = res.getString("key");
                String value = res.getString("value");
                if (name.equals("notification")) {
                    JSONObject obj = new JSONObject(value);
                    Notification notification = Notification.getInstance();
                    notification.setAvatar((short) obj.getInt("avatar"));
                    notification.setText(obj.getString("text"));
                }
                if (name.equals("avatar")) {
                    JSONArray json = new JSONArray(value);
                    int lent = json.length();
                    idHead = new int[lent];
                    idAvatar = new int[lent];
                    for (int i = 0; i < lent; i++) {
                        JSONObject obj = json.getJSONObject(i);
                        idHead[i] = obj.getInt("head");
                        idAvatar[i] = obj.getInt("avatar");
                    }
                }
                if (name.equals("shop")) {
                    JSONArray shops = new JSONArray(value);
                    int lent = shops.length();
                    for (int i = 0; i < lent; i++) {
                        Shop shop = new Shop();
                        JSONObject obj = shops.getJSONObject(i);
                        String table = obj.getString("table");
                        int npc = obj.getInt("npc");
                        int type = obj.getInt("type");
                        shop.setTableName(table);
                        shop.setNpcId(npc);
                        shop.setTypeShop((byte) type);
                        JSONArray tabs = obj.getJSONArray("tabs");
                        int lent2 = tabs.length();
                        for (int a = 0; a < lent2; a++) {
                            JSONObject obj2 = tabs.getJSONObject(a);
                            String tabName = obj2.getString("name");
                            int tabType = obj2.getInt("type");
                            Tab tab = new Tab();
                            tab.setTabName(tabName);
                            tab.setType(tabType);
                            shop.addTab(tab);
                        }
                        shop.init();
                        Shop.addShop(shop);
                    }
                }
                if (name.equals("tile_set")) {
                    JSONObject json = new JSONObject(value);
                    String strTileIndex = json.getJSONArray("tile_index").toString();
                    String strTileType = json.getJSONArray("tile_type").toString();
                    int[][][] tileIndex = g.fromJson(strTileIndex, int[][][].class);
                    int[][] tileType = g.fromJson(strTileType, int[][].class);
                    TMap.tileIndex = tileIndex;
                    TMap.tileType = tileType;
                    MapManager mapManager = MapManager.getInstance();
                    for (TMap map : mapManager.maps.values()) {
                        if (map.tileID != 0) {
                            map.loadMap();
                        }
                    }
                }
                if (name.equals("open_power")) {
                    PowerLimitMark.limitMark = g.fromJson(value, new TypeToken<List<PowerLimitMark>>() {
                    }.getType());
                }
            }
            res.close();
            stmt.close();
        } catch (SQLException | JSONException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initBGItem() {
        try {
            BgItem.bgItems = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_BACKGROUND);
            while (res.next()) {
                BgItem bg = new BgItem();
                bg.id = res.getInt("id");
                bg.image = res.getShort("image");
                bg.layer = res.getByte("layer");
                bg.dx = res.getShort("dx");
                bg.dy = res.getShort("dy");
                JSONArray tileX = new JSONArray(res.getString("tile_x"));
                JSONArray tileY = new JSONArray(res.getString("tile_y"));
                int lent = tileX.length();
                bg.tileX = new int[lent];
                bg.tileY = new int[lent];
                for (int i = 0; i < lent; i++) {
                    bg.tileX[i] = tileX.getInt(i);
                    bg.tileY[i] = tileY.getInt(i);
                }
                BgItem.bgItems.add(bg);
            }
            BgItem.createData();
            res.close();
            stmt.close();
        } catch (SQLException | JSONException ex) {
            logger.error("failed!", ex);
        }
    }

    private void setCacheSkill() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeShort(sks.size());
            for (SkillPaint sp : sks) {
                dos.writeShort(sp.id);
                dos.writeShort(sp.effectHappenOnMob);
                dos.writeByte(sp.numEff);
                dos.writeByte(sp.skillStand.length);
                for (SkillInfoPaint sip : sp.skillStand) {
                    dos.writeByte(sip.status);
                    dos.writeShort(sip.effS0Id);
                    dos.writeShort(sip.e0dx);
                    dos.writeShort(sip.e0dy);
                    dos.writeShort(sip.effS1Id);
                    dos.writeShort(sip.e1dx);
                    dos.writeShort(sip.e1dy);
                    dos.writeShort(sip.effS2Id);
                    dos.writeShort(sip.e2dx);
                    dos.writeShort(sip.e2dy);
                    dos.writeShort(sip.arrowId);
                    dos.writeShort(sip.adx);
                    dos.writeShort(sip.ady);
                }
                dos.writeByte(sp.skillfly.length);
                for (SkillInfoPaint sip : sp.skillfly) {
                    dos.writeByte(sip.status);
                    dos.writeShort(sip.effS0Id);
                    dos.writeShort(sip.e0dx);
                    dos.writeShort(sip.e0dy);
                    dos.writeShort(sip.effS1Id);
                    dos.writeShort(sip.e1dx);
                    dos.writeShort(sip.e1dy);
                    dos.writeShort(sip.effS2Id);
                    dos.writeShort(sip.e2dx);
                    dos.writeShort(sip.e2dy);
                    dos.writeShort(sip.arrowId);
                    dos.writeShort(sip.adx);
                    dos.writeShort(sip.ady);
                }

            }
            CACHE_SKILL = bos.toByteArray();
            dos.close();
            bos.close();
            sks = null;
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initSkill() {
        try {
            sks = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_SKILL_PAINT);
            while (res.next()) {
                SkillPaint skill = new SkillPaint();
                skill.id = res.getShort("skill_id");
                skill.effectHappenOnMob = res.getShort("on_mob");
                skill.numEff = res.getByte("num_eff");
                JSONArray skillStand = new JSONArray(res.getString("skill_stand"));
                skill.skillStand = new SkillInfoPaint[skillStand.length()];
                for (int i = 0; i < skill.skillStand.length; i++) {
                    JSONObject obj = skillStand.getJSONObject(i);
                    skill.skillStand[i] = new SkillInfoPaint();
                    skill.skillStand[i].status = obj.getInt("status");
                    skill.skillStand[i].effS0Id = obj.getInt("effS0Id");
                    skill.skillStand[i].e0dx = obj.getInt("e0dx");
                    skill.skillStand[i].e0dy = obj.getInt("e0dy");
                    skill.skillStand[i].effS1Id = obj.getInt("effS1Id");
                    skill.skillStand[i].e1dx = obj.getInt("e1dx");
                    skill.skillStand[i].e1dy = obj.getInt("e1dy");
                    skill.skillStand[i].effS2Id = obj.getInt("effS2Id");
                    skill.skillStand[i].e2dx = obj.getInt("e2dx");
                    skill.skillStand[i].e2dy = obj.getInt("e2dy");
                    skill.skillStand[i].arrowId = obj.getInt("arrowId");
                    skill.skillStand[i].adx = obj.getInt("adx");
                    skill.skillStand[i].ady = obj.getInt("ady");
                }
                JSONArray skillfly = new JSONArray(res.getString("skill_fly"));
                skill.skillfly = new SkillInfoPaint[skillfly.length()];
                for (int i = 0; i < skill.skillfly.length; i++) {
                    JSONObject obj = skillfly.getJSONObject(i);
                    skill.skillfly[i] = new SkillInfoPaint();
                    skill.skillfly[i].status = obj.getInt("status");
                    skill.skillfly[i].effS0Id = obj.getInt("effS0Id");
                    skill.skillfly[i].e0dx = obj.getInt("e0dx");
                    skill.skillfly[i].e0dy = obj.getInt("e0dy");
                    skill.skillfly[i].effS1Id = obj.getInt("effS1Id");
                    skill.skillfly[i].e1dx = obj.getInt("e1dx");
                    skill.skillfly[i].e1dy = obj.getInt("e1dy");
                    skill.skillfly[i].effS2Id = obj.getInt("effS2Id");
                    skill.skillfly[i].e2dx = obj.getInt("e2dx");
                    skill.skillfly[i].e2dy = obj.getInt("e2dy");
                    skill.skillfly[i].arrowId = obj.getInt("arrowId");
                    skill.skillfly[i].adx = obj.getInt("adx");
                    skill.skillfly[i].ady = obj.getInt("ady");
                }
                sks.add(skill);
            }
            res.close();
            stmt.close();
        } catch (SQLException | JSONException ex) {
            logger.error("failed!", ex);
        }

    }

    private void setCachePart() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeShort(parts.size());
            for (Part part : parts) {
                dos.writeByte(part.type);
                for (PartImage pi : part.pi) {
                    dos.writeShort(pi.id);
                    dos.writeByte(pi.dx);
                    dos.writeByte(pi.dy);
                }
            }
            CACHE_PART = bos.toByteArray();
            dos.close();
            bos.close();
            parts = null;
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initPart() {
        try {
            parts = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_PART);
            while (res.next()) {
                byte type = res.getByte("type");
                JSONArray jA = new JSONArray(res.getString("part"));
                Part part = new Part(type);
                for (int k = 0; k < part.pi.length; k++) {
                    JSONObject o = jA.getJSONObject(k);
                    part.pi[k] = new PartImage();
                    part.pi[k].id = (short) o.getInt("id");
                    part.pi[k].dx = (byte) o.getInt("dx");
                    part.pi[k].dy = (byte) o.getInt("dy");
                }
                parts.add(part);
            }
            res.close();
            stmt.close();
        } catch (SQLException | JSONException ex) {
            logger.error("failed!", ex);
        }
    }

    private void setCacheImage() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeShort(smallImg.size());
            for (int[] small : smallImg) {
                dos.writeByte(small[0]);
                dos.writeShort(small[1]);
                dos.writeShort(small[2]);
                dos.writeShort(small[3]);
                dos.writeShort(small[4]);
            }
            CACHE_IMAGE = bos.toByteArray();
            dos.close();
            bos.close();
            smallImg = null;
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initImage() {
        try {
            smallImg = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_IMAGE);
            while (res.next()) {
                int[] smallImage = new int[5];
                JSONObject small = new JSONObject(res.getString("small_image"));
                smallImage[0] = small.getInt("id");
                smallImage[1] = small.getInt("x");
                smallImage[2] = small.getInt("y");
                smallImage[3] = small.getInt("w");
                smallImage[4] = small.getInt("h");
                smallImg.add(smallImage);
            }
            res.close();
            stmt.close();
        } catch (SQLException | JSONException ex) {
            logger.error("failed!", ex);
        }
    }

    private void setCacheEffect() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeShort(efs.size());
            for (EffectCharPaint eff : efs) {
                dos.writeShort(eff.idEf);
                dos.writeByte(eff.arrEfInfo.length);
                for (EffectInfoPaint ep : eff.arrEfInfo) {
                    dos.writeShort(ep.idImg);
                    dos.writeByte(ep.dx);
                    dos.writeByte(ep.dy);
                }
            }
            CACHE_EFFECT = bos.toByteArray();
            dos.close();
            bos.close();
            efs = null;
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initEffect() {
        try {
            efs = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_EFFECT);
            while (res.next()) {
                EffectCharPaint eff = new EffectCharPaint();
                eff.idEf = res.getInt("id");
                JSONArray info = new JSONArray(res.getString("info"));
                eff.arrEfInfo = new EffectInfoPaint[info.length()];
                for (int i = 0; i < eff.arrEfInfo.length; i++) {
                    JSONObject obj = info.getJSONObject(i);
                    eff.arrEfInfo[i] = new EffectInfoPaint();
                    eff.arrEfInfo[i].idImg = obj.getInt("id");
                    eff.arrEfInfo[i].dx = obj.getInt("dx");
                    eff.arrEfInfo[i].dy = obj.getInt("dy");

                }
                efs.add(eff);
            }
            res.close();
            stmt.close();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    private void setCacheArrow() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeShort(arrs.size());
            for (Arrowpaint arrow : arrs) {
                dos.writeShort(arrow.id);
                dos.writeShort(arrow.imgId[0]);
                dos.writeShort(arrow.imgId[1]);
                dos.writeShort(arrow.imgId[2]);
            }
            CACHE_ARROW = bos.toByteArray();
            dos.close();
            bos.close();
            arrs = null;
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initArrow() {
        try {
            arrs = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_ARROW);
            while (res.next()) {
                Arrowpaint arrow = new Arrowpaint();
                arrow.id = res.getShort("id");
                JSONArray img = new JSONArray(res.getString("img"));
                arrow.imgId = new short[3];
                arrow.imgId[0] = (short) img.getInt(0);
                arrow.imgId[1] = (short) img.getInt(1);
                arrow.imgId[2] = (short) img.getInt(2);
                arrs.add(arrow);
            }
            res.close();
            stmt.close();
        } catch (SQLException | JSONException ex) {
            logger.error("failed!", ex);
        }
    }

    private void setCacheDart() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeShort(darts.size());
            for (DartInfo dart : darts) {
                dos.writeShort(dart.getId());
                dos.writeShort(dart.getNUpdate());
                dos.writeShort(dart.getVa());
                dos.writeShort(dart.getXdPercent());
                dos.writeShort(dart.getTail().length);
                for (short s : dart.getTail()) {
                    dos.writeShort(s);
                }
                dos.writeShort(dart.getTailBorder().length);
                for (short s : dart.getTailBorder()) {
                    dos.writeShort(s);
                }
                dos.writeShort(dart.getXd1().length);
                for (short s : dart.getXd1()) {
                    dos.writeShort(s);
                }
                dos.writeShort(dart.getXd2().length);
                for (short s : dart.getXd2()) {
                    dos.writeShort(s);
                }
                dos.writeShort(dart.getHead().length);
                for (short[] ss : dart.getHead()) {
                    dos.writeShort(ss.length);
                    for (short s : ss) {
                        dos.writeShort(s);
                    }
                }
                dos.writeShort(dart.getHeadBorder().length);
                for (short[] ss : dart.getHeadBorder()) {
                    dos.writeShort(ss.length);
                    for (short s : ss) {
                        dos.writeShort(s);
                    }
                }
            }
            CACHE_DART = bos.toByteArray();
            dos.close();
            bos.close();
            darts = null;
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initDart() {
        try {
            darts = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_DART);
            while (res.next()) {
                DartInfo dart = new DartInfo();
                dart.setId(res.getShort("id"));
                dart.setNUpdate(res.getShort("n_update"));
                dart.setVa(res.getShort("va"));
                dart.setXdPercent(res.getShort("xd_percent"));
                JSONArray tail = new JSONArray(res.getString("tail"));
                dart.setTail(new short[tail.length()]);
                for (int i = 0; i < dart.getTail().length; i++) {
                    dart.getTail()[i] = (short) tail.getInt(i);
                }
                JSONArray tailBorder = new JSONArray(res.getString("tail_border"));
                dart.setTailBorder(new short[tailBorder.length()]);
                for (int i = 0; i < dart.getTailBorder().length; i++) {
                    dart.getTailBorder()[i] = (short) tailBorder.getInt(i);
                }
                JSONArray xd1 = new JSONArray(res.getString("xd1"));
                dart.setXd1(new short[xd1.length()]);
                for (int i = 0; i < dart.getXd1().length; i++) {
                    dart.getXd1()[i] = (short) xd1.getInt(i);
                }
                JSONArray xd2 = new JSONArray(res.getString("xd2"));
                dart.setXd2(new short[xd2.length()]);
                for (int i = 0; i < dart.getXd2().length; i++) {
                    dart.getXd2()[i] = (short) xd2.getInt(i);
                }
                JSONArray head = new JSONArray(res.getString("head"));
                dart.setHead(new short[head.length()][]);
                for (int i = 0; i < dart.getHead().length; i++) {
                    JSONArray tmp = head.getJSONArray(i);
                    dart.getHead()[i] = new short[tmp.length()];
                    for (int a = 0; a < dart.getHead()[i].length; a++) {
                        dart.getHead()[i][a] = (short) tmp.getInt(a);
                    }
                }
                JSONArray headBorder = new JSONArray(res.getString("head_border"));
                dart.setHeadBorder(new short[headBorder.length()][]); 
                for (int i = 0; i < dart.getHeadBorder().length; i++) {
                    JSONArray tmp = headBorder.getJSONArray(i);
                    dart.getHeadBorder()[i] = new short[tmp.length()];
                    for (int a = 0; a < dart.getHeadBorder()[i].length; a++) {
                        dart.getHeadBorder()[i][a] = (short) tmp.getInt(a);
                    }
                }
                darts.add(dart);
            }
            res.close();
            stmt.close();
        } catch (SQLException | JSONException ex) {
            logger.error("failed!", ex);
        }
    }

    private void setCacheSkillTemplate() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(config.getSkillVersion());
            dos.writeByte(sOptionTemplates.size());
            for (SkillOptionTemplate template : sOptionTemplates) {
                dos.writeUTF(template.name);
            }
            dos.writeByte(nClasss.size());
            for (NClass n : nClasss) {
                dos.writeUTF(n.name);
                dos.writeByte(n.skillTemplates.size());
                for (SkillTemplate skillTemplate : n.skillTemplates) {
                    dos.writeByte(skillTemplate.id);
                    dos.writeUTF(skillTemplate.name);
                    dos.writeByte(skillTemplate.maxPoint);
                    dos.writeByte(skillTemplate.manaUseType);
                    dos.writeByte(skillTemplate.type);
                    dos.writeShort(skillTemplate.icon);
                    dos.writeUTF(skillTemplate.damInfo);
                    dos.writeUTF(skillTemplate.description);
                    dos.writeByte(skillTemplate.skills.size());
                    for (Skill skill : skillTemplate.skills) {
                        dos.writeShort(skill.id);
                        dos.writeByte(skill.point);
                        dos.writeLong(skill.powerRequire);
                        dos.writeShort(skill.manaUse);
                        dos.writeInt(skill.coolDown);
                        dos.writeShort(skill.dx);
                        dos.writeShort(skill.dy);
                        dos.writeByte(skill.maxFight);
                        dos.writeShort(skill.damage);
                        dos.writeShort(skill.price);
                        dos.writeUTF(skill.moreInfo);
                    }
                }
            }
            CACHE_SKILL_TEMPLATE = bos.toByteArray();
            dos.close();
            bos.close();
            sOptionTemplates = null;
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initSkillTemplate() {
        try {
            sOptionTemplates = new ArrayList<>();
            nClasss = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_SKILL_OPTION);
            while (res.next()) {
                SkillOptionTemplate sOptionTemplate = new SkillOptionTemplate();
                sOptionTemplate.id = res.getInt("id");
                sOptionTemplate.name = res.getString("name");
                sOptionTemplates.add(sOptionTemplate);
            }
            res.close();
            stmt.close();
            for (int g = 0; g < 7; g++) {
                stmt = conn.createStatement();
                res = stmt.executeQuery(SQLStatement.INIT_SKILL_TEMPLATE + g);
                NClass nClass = new NClass();
                nClass.classId = g;
                switch (nClass.classId) {
                    case 0:
                        nClass.name = "Trái đất";
                        break;

                    case 1:
                        nClass.name = "Namec";
                        break;

                    case 2:
                        nClass.name = "Xayda";
                        break;

                    case 3:
                        nClass.name = "Chưa xác định";
                        break;

                    case 4:
                        nClass.name = "Chưa xác định";
                        break;

                    case 5:
                        nClass.name = "Chưa xác định";
                        break;

                    default:
                        nClass.name = "Chưa xác định";
                        break;
                }
                while (res.next()) {
                    SkillTemplate template = new SkillTemplate();
                    template.id = (byte) res.getInt("skill_id");
                    template.name = res.getString("name");
                    template.maxPoint = res.getByte("max_point");
                    template.type = res.getByte("type");
                    template.icon = res.getShort("icon");
                    template.description = res.getString("description");
                    template.damInfo = res.getString("info");
                    template.manaUseType = res.getByte("mana_use_type");
                    template.skills = new ArrayList<>();
                    JSONArray skills = new JSONArray(res.getString("skills"));
                    for (int a = 0; a < skills.length(); a++) {
                        JSONObject obj = skills.getJSONObject(a);
                        Skill skill = new Skill();
                        skill.id = (short) obj.getInt("id");
                        skill.template = template;
                        skill.point = obj.getInt("point");
                        skill.coolDown = obj.getInt("cool_down");
                        skill.powerRequire = obj.getLong("power_require");
                        skill.maxFight = obj.getInt("max_fight");
                        skill.manaUse = obj.getInt("mana_use");
                        skill.dx = obj.getInt("dx");
                        skill.dy = obj.getInt("dy");
                        skill.damage = (short) obj.getInt("damage");
                        skill.price = (short) obj.getInt("price");
                        skill.moreInfo = obj.getString("more_info");
                        template.skills.add(skill);
                    }
                    nClass.skillTemplates.add(template);
                }
                res.close();
                stmt.close();
                nClasss.add(nClass);
            }
        } catch (SQLException | JSONException ex) {
            logger.error("failed!", ex);
        }
    }

    private void setCacheMap() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(config.getMapVersion());
            dos.write(TMap.data);
            dos.write(Npc.data);
            dos.write(Mob.data);
            CACHE_MAP = bos.toByteArray();
            dos.close();
            bos.close();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initMap() {
        try {
            MapManager mapManager = MapManager.getInstance();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_MAP);
            while (res.next()) {
                String mapName = res.getString("name");
                TMap.mapNames.add(mapName);
                TMap map = new TMap();
                map.mapID = res.getInt("id");
                map.bgID = res.getByte("bg_id");
                map.planet = res.getByte("planet");
                map.tileID = res.getByte("tile_id");
                map.typeMap = res.getByte("type");
                map.name = mapName;
                map.bgType = res.getByte("bg_type");
                map.zoneNumber = res.getInt("zone_number");

                JSONArray waypoints = new JSONArray(res.getString("waypoint"));
                int lent = waypoints.length();
                map.waypoints = new Waypoint[lent];
                for (int i = 0; i < lent; i++) {
                    JSONObject obj = waypoints.getJSONObject(i);
                    Waypoint w = new Waypoint();
                    w.isEnter = obj.getBoolean("enter");
                    w.isOffline = obj.getBoolean("offline");
                    w.name = obj.getString("name");
                    w.minX = (short) obj.getInt("min_x");
                    w.minY = (short) obj.getInt("min_y");
                    w.maxX = (short) obj.getInt("max_x");
                    w.maxY = (short) obj.getInt("max_y");
                    w.next = obj.getInt("next");
                    w.x = (short) obj.getInt("x");
                    w.y = (short) obj.getInt("y");
                    map.waypoints[i] = w;
                }
                JSONArray mobs = new JSONArray(res.getString("mob"));
                lent = mobs.length();
                map.mobs = new MobCoordinate[lent];
                for (int i = 0; i < lent; i++) {
                    JSONObject obj = mobs.getJSONObject(i);
                    byte templateId = (byte) obj.getInt("id");
                    short x = (short) obj.getInt("x");
                    short y = (short) obj.getInt("y");
                    MobCoordinate mob = new MobCoordinate();
                    mob.setTemplateID(templateId);
                    mob.setX(x);
                    mob.setY(y);
                    map.mobs[i] = mob;
                }

                JSONArray npcs = new JSONArray(res.getString("npc"));
                lent = npcs.length();
                map.npcs = new Npc[lent];
                for (int i = 0; i < lent; i++) {
                    JSONObject obj = npcs.getJSONObject(i);
                    byte templateId = (byte) obj.getInt("id");
                    byte status = (byte) obj.getInt("status");
                    short x = (short) obj.getInt("x");
                    short y = (short) obj.getInt("y");
                    short avatar = (short) obj.getInt("avatar");
                    Npc npc = new Npc(i, status, templateId, x, y, avatar);
                    map.npcs[i] = npc;
                }

                JSONArray poss = new JSONArray(res.getString("position_bg_item"));
                lent = poss.length();
                map.positionBgItems = new BgItem[lent];
                for (int i = 0; i < lent; i++) {
                    JSONObject obj = poss.getJSONObject(i);
                    int id = obj.getInt("id");
                    short x = (short) obj.getInt("x");
                    short y = (short) obj.getInt("y");
                    BgItem bg = new BgItem();
                    bg.id = id;
                    bg.x = x;
                    bg.y = y;
                    map.positionBgItems[i] = bg;
                }
                KeyValue[] eff = null;
                KeyValue[] eff2 = null;
                if (res.getObject("effect") != null) {
                    JSONArray effects = new JSONArray(res.getString("effect"));
                    lent = effects.length();
                    eff = new KeyValue[lent];
                    for (int i = 0; i < lent; i++) {
                        JSONObject obj = effects.getJSONObject(i);
                        String key = obj.getString("key");
                        String value = obj.getString("value");
                        KeyValue keyValue = new KeyValue(key, value);
                        eff[i] = keyValue;
                    }
                }
                if (res.getObject("effect_event") != null) {
                    JSONArray effects2 = new JSONArray(res.getString("effect_event"));
                    lent = effects2.length();
                    eff2 = new KeyValue[lent];
                    for (int i = 0; i < lent; i++) {
                        JSONObject obj = effects2.getJSONObject(i);
                        String key = obj.getString("key");
                        String value = obj.getString("value");
                        KeyValue keyValue = new KeyValue(key, value);
                        eff2[i] = keyValue;
                    }
                }
                int length1 = 0;
                int length2 = 0;
                if (eff != null) {
                    length1 += eff.length;
                }
                if (eff2 != null) {
                    length2 += eff2.length;
                }
                map.effects = new KeyValue[length1 + length2];
                for (int i = 0; i < length1; i++) {
                    map.effects[i] = eff[i];
                }
                for (int i = 0; i < length2; i++) {
                    map.effects[i + length1] = eff2[i];
                }
                map.init();
                mapManager.maps.put(map.mapID, map);
            }
            TMap.createData();
            res.close();
            stmt.close();
        } catch (SQLException | JSONException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initMob() {
        try {
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_MOB_TEMPLATE);
            while (res.next()) {
                int id = res.getInt("id");
                String name = res.getString("name");
                int type = res.getInt("type");
                int hp = res.getInt("hp");
                int new1 = res.getInt("new");
                int rangeMove = res.getInt("range_move");
                int speed = res.getInt("speed");
                int dart_type = res.getInt("dart_type");
                byte level = res.getByte("level");
                String json1 = res.getString("image");
                String json2 = res.getString("frame");
                String json3 = res.getString("run");
                String json4 = res.getString("frame_boss");
                MobTemplate mob = new MobTemplate();
                mob.mobTemplateId = id;
                mob.name = name;
                mob.level = level;
                mob.hp = hp;
                mob.rangeMove = (byte) rangeMove;
                mob.type = (byte) type;
                mob.speed = (byte) speed;
                mob.dartType = (byte) dart_type;
                mob.new1 = (byte) new1;
                if (json1 != null && !json1.equals("")) {
                    JSONArray images = new JSONArray(json1);
                    int lent = images.length();
                    mob.images = new ArrayList<>();
                    for (int i = 0; i < lent; i++) {
                        JSONObject obj = images.getJSONObject(i);
                        ImageInfo img = new ImageInfo();
                        img.ID = obj.getInt("id");
                        img.x = obj.getInt("x");
                        img.y = obj.getInt("y");
                        img.w = obj.getInt("w");
                        img.h = obj.getInt("h");
                        mob.images.add(img);
                    }
                    JSONArray frames = new JSONArray(json2);
                    lent = frames.length();
                    mob.frames = new ArrayList<>();
                    for (int i = 0; i < lent; i++) {
                        JSONArray frame = frames.getJSONArray(i);
                        int lent2 = frame.length();
                        Frame f = new Frame();
                        f.dx = new short[lent2];
                        f.dy = new short[lent2];
                        f.idImg = new byte[lent2];
                        for (int j = 0; j < lent2; j++) {
                            JSONObject obj = frame.getJSONObject(j);
                            f.dx[j] = (short) obj.getInt("dx");
                            f.dy[j] = (short) obj.getInt("dy");
                            f.idImg[j] = (byte) obj.getInt("image_id");
                        }
                        mob.frames.add(f);
                    }
                    JSONArray run = new JSONArray(json3);
                    lent = run.length();
                    mob.run = new short[lent];
                    for (int i = 0; i < lent; i++) {
                        mob.run[i] = (short) run.getInt(i);
                    }
                    if (json4 != null && !json4.equals("")) {
                        JSONArray frameBoss = new JSONArray(json4);
                        int lent2 = frameBoss.length();
                        mob.frameBoss = new byte[lent2][];
                        for (int i = 0; i < lent2; i++) {
                            JSONArray frame = frameBoss.getJSONArray(i);
                            int lent3 = frame.length();
                            mob.frameBoss[i] = new byte[lent3];
                            for (int j = 0; j < lent3; j++) {
                                mob.frameBoss[i][j] = (byte) frame.getInt(j);
                            }
                        }

                    }
                    mob.createData();
                }
                Mob.addMobTemplate(mob);
            }
            Mob.createData();
            res.close();
            stmt.close();
        } catch (SQLException | JSONException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initNpc() {
        try {
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_NPC_TEMPLATE);
            while (res.next()) {
                int id = res.getInt("id");
                String name = res.getString("name");
                short head = res.getShort("head");
                short body = res.getShort("body");
                short leg = res.getShort("leg");
                String json = res.getString("menu");
                JSONArray menus = new JSONArray(json);
                NpcTemplate npc = new NpcTemplate();
                npc.npcTemplateId = id;
                npc.name = name;
                npc.headId = head;
                npc.bodyId = body;
                npc.legId = leg;
                npc.menu = new String[menus.length()][];
                for (int i = 0; i < npc.menu.length; i++) {
                    JSONArray menu = menus.getJSONArray(i);
                    npc.menu[i] = new String[menu.length()];
                    for (int a = 0; a < npc.menu[i].length; a++) {
                        npc.menu[i][a] = menu.getString(a);
                    }
                }
                Npc.addNpcTemplate(npc);
            }
            Npc.createData();
            res.close();
            stmt.close();
        } catch (SQLException | JSONException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initEffectData() {
        try {
            Effect.effects = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_EFFECT_DATA);
            while (res.next()) {
                int id = res.getInt("id");
                String json1 = res.getString("image");
                String json2 = res.getString("frame");
                String json3 = res.getString("run");
                EffectData eff = new EffectData();
                eff.ID = id;
                JSONArray images = new JSONArray(json1);
                int lent = images.length();
                eff.imgInfo = new ImageInfo[lent];
                for (int i = 0; i < lent; i++) {
                    JSONObject img = images.getJSONObject(i);
                    eff.imgInfo[i] = new ImageInfo();
                    eff.imgInfo[i].ID = img.getInt("id");
                    eff.imgInfo[i].x = img.getInt("x");
                    eff.imgInfo[i].y = img.getInt("y");
                    eff.imgInfo[i].w = img.getInt("w");
                    eff.imgInfo[i].h = img.getInt("h");
                }
                JSONArray frames = new JSONArray(json2);
                lent = frames.length();
                eff.frame = new Frame[lent];
                for (int i = 0; i < lent; i++) {
                    JSONArray frame = frames.getJSONArray(i);
                    int lent2 = frame.length();
                    eff.frame[i] = new Frame();
                    eff.frame[i].dx = new short[lent2];
                    eff.frame[i].dy = new short[lent2];
                    eff.frame[i].idImg = new byte[lent2];
                    for (int a = 0; a < lent2; a++) {
                        JSONObject obj = frame.getJSONObject(a);
                        eff.frame[i].dx[a] = (short) obj.getInt("dx");
                        eff.frame[i].dy[a] = (short) obj.getInt("dy");
                        eff.frame[i].idImg[a] = (byte) obj.getInt("image_id");
                    }
                }
                JSONArray run = new JSONArray(json3);
                lent = run.length();
                eff.arrFrame = new short[lent];
                for (int i = 0; i < lent; i++) {
                    eff.arrFrame[i] = (short) run.getInt(i);
                }
                eff.createData();
                Effect.addEffData(eff);
            }
            res.close();
            stmt.close();
        } catch (SQLException | JSONException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initPower() {
        try {
            powers = new ArrayList<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_POWER);
            while (res.next()) {
                long power = res.getLong("power");
                powers.add(power);
            }
            res.close();
            stmt.close();
        } catch (SQLException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initCaption() {
        try {
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_CAPTION);
            while (res.next()) {
                int planet = res.getInt("planet");
                String name = res.getString("name");
                Caption.addCaption((byte) planet, name);
            }
            res.close();
            stmt.close();
        } catch (SQLException ex) {
            logger.error("failed!", ex);
        }
    }

    private void setCacheItem(int type) {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            int split = 800;
            int size = iTemplates.size();
            ByteArrayOutputStream dos = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(dos);
            ds.writeByte(config.getItemVersion());
            ds.writeByte(type);
            if (type == 0) {
                ds.writeShort(iOptionTemplates.size());
                for (ItemOptionTemplate iOptionTemplate : iOptionTemplates) {
                    ds.writeShort(iOptionTemplate.id);
                    ds.writeUTF(iOptionTemplate.name);
                    ds.writeByte(iOptionTemplate.type);
                }
            } else if (type == 1) {
                ds.writeShort(split);
                List<ItemTemplate> iTemplates = new ArrayList<>(this.iTemplates.values());
                for (int i = 0; i < split; i++) {
                    ItemTemplate iTemplate = iTemplates.get(i);
                    ds.writeShort(iTemplate.id);
                    ds.writeByte(iTemplate.type);
                    ds.writeByte(iTemplate.gender);
                    ds.writeUTF(iTemplate.name);
                    ds.writeUTF(iTemplate.description);
                    ds.writeByte(iTemplate.level);
                    ds.writeInt(iTemplate.require);
                    ds.writeShort(iTemplate.iconID);
                    ds.writeShort(iTemplate.part);
                    ds.writeBoolean(iTemplate.isUpToUp);
                }
            } else if (type == 2) {
                ds.writeShort(split);
                ds.writeShort(size);
                List<ItemTemplate> iTemplates = new ArrayList<>(this.iTemplates.values());
                for (int i = split; i < size; i++) {
                    ItemTemplate iTemplate = iTemplates.get(i);
                    ds.writeShort(iTemplate.id);
                    ds.writeByte(iTemplate.type);
                    ds.writeByte(iTemplate.gender);
                    ds.writeUTF(iTemplate.name);
                    ds.writeUTF(iTemplate.description);
                    ds.writeByte(iTemplate.level);
                    ds.writeInt(iTemplate.require);
                    ds.writeShort(iTemplate.iconID);
                    ds.writeShort(iTemplate.part);
                    ds.writeBoolean(iTemplate.isUpToUp);
                }
            }
            CACHE_ITEM[type] = dos.toByteArray();
            ds.close();
            dos.close();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void initItemTemplate() {
        try {
            iOptionTemplates = new ArrayList<>();
            iTemplates = new HashMap<>();
            Connection conn = MySQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(SQLStatement.INIT_ITEM_OPTION);
            while (res.next()) {
                ItemOptionTemplate iOptionTemplate = new ItemOptionTemplate();
                iOptionTemplate.id = res.getInt("id");
                iOptionTemplate.name = res.getString("name");
                iOptionTemplate.type = res.getByte("type");
                iOptionTemplates.add(iOptionTemplate);
            }
            res.close();
            stmt.close();
            Statement stmt2 = conn.createStatement();
            ResultSet res2 = stmt2.executeQuery(SQLStatement.INIT_ITEM_TEMPLATE);
            while (res2.next()) {
                ItemTemplate iTemplate = new ItemTemplate();
                iTemplate.id = res2.getShort("id");
                iTemplate.gender = res2.getByte("gender");
                iTemplate.type = res2.getByte("type");
                iTemplate.level = res2.getByte("level");
                iTemplate.name = res2.getString("name");
                iTemplate.mountID = res2.getInt("mount_id");
                iTemplate.description = res2.getString("description");
                iTemplate.require = res2.getInt("require");
                iTemplate.resalePrice = res2.getInt("resale_price");
                iTemplate.iconID = res2.getShort("icon");
                iTemplate.part = res2.getShort("part");
                iTemplate.isUpToUp = res2.getBoolean("is_up_to_up");
                iTemplate.head = res2.getShort("head");
                iTemplate.body = res2.getShort("body");
                iTemplate.leg = res2.getShort("leg");
                iTemplate.isLock = res2.getBoolean("lock");
                iTemplate.options = new ArrayList<>();
                if (res2.getObject("options") != null) {
                    JSONArray json = new JSONArray(res2.getString("options"));
                    int lent = json.length();
                    for (int i = 0; i < lent; i++) {
                        JSONObject obj = json.getJSONObject(i);
                        int id = obj.getInt("id");
                        int param = obj.getInt("param");
                        iTemplate.options.add(new ItemOption(id, param));
                    }
                }
                iTemplates.put((int) iTemplate.id, iTemplate);
            }
            res2.close();
            stmt2.close();
        } catch (SQLException ex) {
            logger.error("failed!", ex);
        } catch (JSONException ex) {
            logger.error("init item template err", ex);
        }
    }

    private void initResVersion() {
        for (int i = 0; i < 4; i++) {
            File folder = new File("resources/data/" + (i + 1));
            int ver = (int) Utils.getFolderSize(folder);

            resVersion[i] = ver;
        }
    }

    private void initSmallVersion() {
        try {

            smallVersion = new byte[4][];
            for (int i = 0; i < 4; i++) {
                File file = new File("resources/image/" + (i + 1) + "/small/");
                File[] files = file.listFiles();
                int max = 0;
                for (File f : files) {
                    String name = f.getName();
                    name = name.replaceAll("Small", "");
                    name = name.replace(".png", "");
                    int id = Integer.parseInt(name);
                    if (id > max) {
                        max = id;
                    }
                }
                smallVersion[i] = new byte[max + 1];
                for (File f : files) {
                    String name = f.getName();
                    name = name.replaceAll("Small", "");
                    name = name.replace(".png", "");
                    int id = Integer.parseInt(name);

                    smallVersion[i][id] = (byte) (Files.readAllBytes(f.toPath()).length % 127);
                }
            }
        } catch (Exception ex) {
            logger.error("small version", ex);
        }
    }

    private void initBGSmallVersion() {
        try {
            backgroundVersion = new byte[4][];
            for (int i = 0; i < 4; i++) {
                File file = new File("resources/image/" + (i + 1) + "/background/");
                File[] files = file.listFiles();

                if (files == null) {
                    logger.warn("No files found in directory: " + file.getAbsolutePath());
                    continue; // Bỏ qua thư mục nếu không tìm thấy tệp nào
                }

                int max = 0;
                for (File f : files) {
                    String name = f.getName();
                    if (name.endsWith(".png")) {
                        try {
                            int id = Integer.parseInt(name.replace(".png", ""));
                            if (id > max) {
                                max = id;
                            }
                        } catch (NumberFormatException nfe) {
                            logger.warn("Invalid file name format: " + name);
                        }
                    }
                }

                backgroundVersion[i] = new byte[max + 1];
                for (File f : files) {
                    String name = f.getName();
                    if (name.endsWith(".png")) {
                        try {
                            int id = Integer.parseInt(name.replace(".png", ""));
                            backgroundVersion[i][id] = (byte) (Files.readAllBytes(f.toPath()).length % 127);
                        } catch (NumberFormatException nfe) {
                            logger.warn("Invalid file name format: " + name);
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("I/O error during background version initialization", e);
        } catch (Exception e) {
            logger.error("Unexpected error during background version initialization", e);
        }
    }

}
