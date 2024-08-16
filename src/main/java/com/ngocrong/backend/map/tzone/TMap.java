package com.ngocrong.backend.map.tzone;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.consts.MapName;
import com.ngocrong.backend.lib.KeyValue;
import com.ngocrong.backend.mob.MobCoordinate;
import com.ngocrong.backend.model.BgItem;
import com.ngocrong.backend.model.Npc;
import com.ngocrong.backend.model.Waypoint;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TMap {
    private static Logger logger = Logger.getLogger(TMap.class);

    public static final byte UPDATE_ONE_SECONDS = 0;
    public static final byte UPDATE_THIRTY_SECONDS = 1;
    public static final byte UPDATE_ONE_MINUTES = 2;
    public static final byte UPDATE_FIVE_SECONDS = 3;

    public static int T_EMPTY = 0;
    public static int T_TOP = 2;
    public static int T_LEFT = 4;
    public static int T_RIGHT = 8;
    public static int T_TREE = 16;
    public static int T_WATERFALL = 32;
    public static int T_WATERFLOW = 64;
    public static int T_TOPFALL = 128;
    public static int T_OUTSIDE = 256;
    public static int T_DOWN1PIXEL = 512;
    public static int T_BRIDGE = 1024;
    public static int T_UNDERWATER = 2048;
    public static int T_SOLIDGROUND = 4096;
    public static int T_BOTTOM = 8192;
    public static int T_DIE = 16384;
    public static int T_HEBI = 32768;
    public static int T_BANG = 65536;
    public static int T_JUM8 = 131072;
    public static int T_NT0 = 262144;
    public static int T_NT1 = 524288;


    public int autoIncrease = 0;
    public static byte[] data;
    public static ArrayList<String> mapNames = new ArrayList<>();
    public static int[][] tileType;
    public static int[][][] tileIndex;


    public int mapID;

    public String name;
    public int planet;
    public int tileID;
    public int bgID;
    public int typeMap;
    public byte bgType;
    public Waypoint[] waypoints;
    public Npc[] npcs;
    public MobCoordinate[] mobs;
    public BgItem[] positionBgItems;
    public KeyValue[] effects;
    public ArrayList<Zone> zones;
    public int tmw, tmh, width, height;
    public int[] maps, types;
    public boolean[] blocks;
    public int zoneNumber;
    public byte[] mapData;
    public ArrayList<Floor> floors = new ArrayList<>();
    public ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void createData() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeShort(mapNames.size());
            for (String name : mapNames) {
                dos.writeUTF(name);
            }
            data = bos.toByteArray();
            dos.close();
            bos.close();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }


    public boolean isMapSingle() {
        return mapID == 21 || mapID == 22 || mapID == 23 || mapID == 39 || mapID == 40 || mapID == 41 || mapID == 45 || mapID == 46 || mapID == 47 || mapID == 48 || mapID == 49 || mapID == 50 || mapID == 111;
    }

    public boolean isDauTruong() {
        return mapID == MapName.DAU_TRUONG;
    }

    public boolean isCold() {
        return mapID == 105 || mapID == 106 || mapID == 107 || mapID == 108 || mapID == 109 || mapID == 110;
    }

    public boolean isMapSpecial() {
        return isBarrack() || isBaseBabidi() || isTreasure() || isClanTerritory() || isBlackDragonBall() || isNguHanhSon() || isDauTruong();
    }

    public boolean isNguHanhSon() {
        return mapID == MapName.NGU_HANH_SON || mapID == MapName.NGU_HANH_SON_2 || mapID == MapName.NGU_HANH_SON_3;
    }

    public boolean isBlackDragonBall() {
        return mapID == MapName.HANH_TINH_M_2 || mapID == MapName.HANH_TINH_POLARIS || mapID == MapName.HANH_TINH_CRETACEOUS || mapID == MapName.HANH_TINH_MONMAASU || mapID == MapName.HANH_TINH_RUDEEZE || mapID == MapName.HANH_TINH_GELBO || mapID == MapName.HANH_TINH_TIGERE;
    }

    public boolean isClanTerritory() {
        return mapID == MapName.LANH_DIA_BANG_HOI;
    }

    public boolean isTreasure() {
        return mapID == MapName.DONG_HAI_TAC || mapID == MapName.CANG_HAI_TAC || mapID == MapName.HANG_BACH_TUOC || mapID == MapName.DONG_KHO_BAU;
    }

    public boolean isBarrack() {
        return mapID == 53 || mapID == 54 || mapID == 55 || mapID == 56 || mapID == 57 || mapID == 58 || mapID == 59 || mapID == 60 || mapID == 61 || mapID == 62;
    }

    public boolean isBaseBabidi() {
        return mapID == 114 || mapID == 115 || mapID == 117 || mapID == 119 || mapID == 120 || mapID == 127;
    }

    public void enterZone(Char character, int zoneID) {
        Zone z = getZoneByID(zoneID);
        if (z != null) {
            z.enter(character);
        }
    }

    public Zone getZoneByID(int id) {
        lock.readLock().lock();
        try {
            for (Zone z : zones) {
                if (z.zoneID == id) {
                    return z;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getZoneID() {
        ArrayList<Integer> list = new ArrayList<>();
        lock.readLock().lock();
        try {
            for (Zone z : this.zones) {
                int pts = z.getPts();
                if (pts == Zone.PTS_YELLOW || pts == Zone.PTS_GREEN) {
                    return z.zoneID;
                }
                list.add(z.zoneID);
            }
        } finally {
            lock.readLock().unlock();
        }
        int zoneId = -1;
        if (list.size() > 0) {
            zoneId = list.get(Utils.nextInt(list.size()));
        }
        return zoneId;
    }

    public short collisionLand(short x, short y) {
        int type = -1;
        y = (short) (y / 24 * 24);
        while (!((((type = tileTypeAtPixel(x, y)) & T_TOP) == T_TOP || (type & T_BRIDGE) == T_BRIDGE) && !checkBlock(x, y))) {
            y += 24;
            if (y >= this.width) {
                return 24;
            }
        }
        return y;
    }

    public boolean checkBlock(short px, short py) {
        return blocks[py / 24 * tmw + px / 24];
    }

    public int tileTypeAtPixel(short px, short py) {
        int result;
        try {
            result = this.types[py / 24 * this.tmw + px / 24];
        } catch (Exception ex) {
            result = 1000;
        }
        return result;
    }

    public void addZone(Zone zone) {
        lock.writeLock().lock();
        try {
            zones.add(zone);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isDoubleMap() {
        return false;
    }

    public boolean isCantOffline() {
        return isBarrack() || isBaseBabidi() || isTreasure() || isClanTerritory() || isBlackDragonBall() || isNguHanhSon() || isDauTruong();
    }

    public void removeZone(Zone z) {
        z.running = false;
        lock.writeLock().lock();
        try {
            zones.remove(z);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void init() {
        if (tileID != 0) {
            loadMapFromResource();
            this.zones = new ArrayList<>();
            for (autoIncrease = 0; autoIncrease < this.zoneNumber; autoIncrease++) {
                Zone z = null;
//                if (mapID == MapName.VACH_NUI_ARU_2 || mapID == MapName.VACH_NUI_MOORI_2 || mapID == MapName.VACH_NUI_KAKAROT) {
//                    z = new Cliff(this, autoIncrease);
//                } else if (mapID == MapName.NGU_HANH_SON || mapID == MapName.NGU_HANH_SON_2 || mapID == MapName.NGU_HANH_SON_3) {
//                    z = new NguHanhSon(this, autoIncrease);
//                } else if (mapID == MapName.DAI_HOI_VO_THUAT) {
//                    z = new WaitingArea(this, autoIncrease);
//                } else if (mapID == MapName.DAI_HOI_VO_THUAT_3) {
//                    z = new Arena23(this, autoIncrease);
//                } else if (isMapSingle()) {
//                    z = new MapSingle(this, autoIncrease);
//                } else {
//                    z = new Zone(this, autoIncrease);
//                }
                addZone(z);
            }
        }
    }

    public void loadMapFromResource() {
        logger.debug("loadMapFromResource map: " + this.name);
        this.mapData = Utils.getFile("resources/map/" + this.mapID);

        if (this.mapData == null) {
            logger.error("Map data is null for mapID: " + this.mapID);
            return; // Thoát khỏi phương thức nếu không có dữ liệu
        }

        try (ByteArrayInputStream bis = new ByteArrayInputStream(this.mapData);
             DataInputStream dis = new DataInputStream(bis)) {

            // Đọc kích thước bản đồ
            this.tmw = dis.read();
            this.tmh = dis.read();

            // Đọc dữ liệu bản đồ
            int lent = dis.available();
            this.maps = new int[lent];
            for (int i = 0; i < lent; i++) {
                this.maps[i] = dis.read();
            }

            // Khởi tạo các mảng `types` và `blocks`
            this.types = new int[this.maps.length];
            this.blocks = new boolean[this.maps.length];

            // Tải các khối trên bản đồ
            loadBlock();

        } catch (IOException ex) {
            logger.error("I/O error occurred while loading map!", ex);
        } catch (Exception ex) {
            logger.error("Unexpected error occurred while loading map!", ex);
        }
    }



    private void loadBlock() {
        File file = new File("resources/map/block/" + this.mapID);
        if (file.exists()) {
            try {
                byte[] ab = Files.readAllBytes(file.toPath());

                for (int i = 0; i < ab.length; i++) {
                    blocks[i] = ab[i] == 1;
                }
            } catch (IOException ex) {
                logger.error("load block err");
            }
        }
    }

    public void loadMap() {
        this.height = this.tmh * 24;
        this.width = this.tmw * 24;
        int index = this.tileID - 1;
        try {
            int lent = this.tmw * this.tmh;
            for (int i = 0; i < lent; i++) {
                for (int j = 0; j < TMap.tileType[index].length; j++) {
                    setTile(i, TMap.tileIndex[index][j], TMap.tileType[index][j]);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        setListFloor();
    }

    private void setListFloor() {
        for (int i = 0; i < tmw; i++) {
            for (int j = 0; j < tmh; j++) {
                int t = this.types[j * this.tmw + i];
                if ((t & T_TOP) == T_TOP || (t & T_BRIDGE) == T_BRIDGE) {
                    Floor floor = new Floor();
                    floor.x = i;
                    floor.y = j;
                    floors.add(floor);
                }
            }
        }
    }

    private void setTile(int index, int[] mapsArr, int type) {
        for (int i = 0; i < mapsArr.length; i++) {
            if (this.maps[index] == mapsArr[i]) {
                this.types[index] |= type;
                break;
            }
        }
    }

    public void close() {
        lock.readLock().lock();
        try {
            for (Zone z : this.zones) {
                z.running = false;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isFuture() {
        return mapID == 102 || mapID == 92 || mapID == 93 || mapID == 94 || mapID == 96 || mapID == 97 || mapID == 98 || mapID == 99 || mapID == 100 || mapID == 103;
    }

    public boolean isNappa() {
        return mapID == 63 || mapID == 64 || mapID == 65 || mapID == 66 || mapID == 67 || mapID == 68 || mapID == 69 || mapID == 70 || mapID == 71 || mapID == 72 || mapID == 73 || mapID == 74 || mapID == 75 || mapID == 76 || mapID == 77 || mapID == 79 || mapID == 80 || mapID == 81 || mapID == 82 || mapID == 83;
    }

    public boolean isCantGoBack() {
        return isBarrack() || isBaseBabidi() || isTreasure() || isClanTerritory() || isBlackDragonBall() || isDauTruong();
    }

    public Object findWaypoint(short x, short y) {
        for (Waypoint way : waypoints) {
            if (x >= way.minX && x <= way.maxX && y >= way.minY && y <= way.maxY) {
                return way;
            }
        }
        return null;
    }

    public Waypoint getWaypointByNextID(int mapID) {
        for (Waypoint way : waypoints) {
            if (way.next == mapID) {
                return way;
            }
        }
        return null;
    }

    public int randomZoneID() {
        return zones.get(Utils.nextInt(zones.size())).zoneID;
    }

    public boolean isHome() {
        return mapID == 21 || mapID == 22 || mapID == 23;
    }
}
