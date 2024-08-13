package com.ngocrong.backend.model;

import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.map.tzone.Zone;
import lombok.Data;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
public class History {

    private static final Logger logger = Logger.getLogger(History.class);

    public static final byte COMMON = 0;
    public static final byte LOGIN = 1;
    public static final byte LOGOUT = 2;
    public static final byte TRADE_SEND = 3;
    public static final byte TRADE_RECEIVE = 4;
    public static final byte BUY_ITEM = 5;
    public static final byte SELL_ITEM = 6;
    public static final byte THROW_ITEM = 7;
    public static final byte PICK_ITEM = 8;

    private int playerID;
    private byte type;
    private List<Item> items;
    private JSONObject befores;
    private JSONObject afters;
    private JSONObject maps;
    private String extras;
    private long time;

    // Constructor khởi tạo lịch sử với ID người chơi và loại hành động
    public History(int playerID, byte type) {
        this.playerID = playerID;
        this.type = type;
        this.extras = "";
        this.befores = new JSONObject();
        this.afters = new JSONObject();
        this.maps = new JSONObject();
        this.items = new ArrayList<>();
        this.time = System.currentTimeMillis();
    }

    // Thiết lập giá trị trước khi thay đổi
    public void setBefores(long gold, int gem, int gemLock) {
        addToJson(befores, "gold", gold);
        addToJson(befores, "gem", gem);
        addToJson(befores, "gem_lock", gemLock);
    }

    // Thiết lập giá trị sau khi thay đổi
    public void setAfters(long gold, int gem, int gemLock) {
        addToJson(afters, "gold", gold);
        addToJson(afters, "gem", gem);
        addToJson(afters, "gem_lock", gemLock);
    }

    // Thiết lập thông tin khu vực (Zone)
    public void setZone(Zone zone) {
        addToJson(maps, "mapID", zone.map.mapID);
        addToJson(maps, "zoneID", zone.zoneID);
    }

    // Thiết lập thông tin người bán
    public void setSeller(int id, int receive) {
        this.extras = createJson("seller", id, "receive", receive);
    }

    // Thiết lập thông tin người mua
    public void setBuyer(int id, int price) {
        this.extras = createJson("buyer", id, "price", price);
    }

    // Thêm một vật phẩm vào lịch sử
    public void addItem(Item item) {
        items.add(item);
    }

    // Thiết lập thông tin đối tác giao dịch
    public void setPartner(int id, String name) {
        this.extras = createJson("partner_id", id, "partner_name", name);
    }

    // Helper method for adding data to JSONObject
    private void addToJson(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            logger.error("Failed to add to JSON: " + key + " - " + value, e);
        }
    }

    // Helper method for creating a JSON string from key-value pairs
    private String createJson(String key1, Object value1, String key2, Object value2) {
        JSONObject json = new JSONObject();
        addToJson(json, key1, value1);
        addToJson(json, key2, value2);
        return json.toString();
    }

    // Phương thức lưu lịch sử, hiện chưa được triển khai
    public void save() {

    }
}