package com.ngocrong.backend.shop;

import com.google.gson.Gson;
import com.ngocrong.backend.entity.ConsignmentItemEntity;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.item.ItemOption;
import com.ngocrong.backend.repository.GameRepo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConsignmentItem {
    public static final long EXPIRY_TIME = 259200000L;
    public static int autoIncrease;
    public long databaseId;
    public int id;
    public Item item;
    public int price;
    public Timestamp createTime;
    public int sellerId;
    public int buyerId;
    public Timestamp buyTime;
    public Timestamp receiveTime;
    public ConsignmentItemStatus status;
    public Lock lock = new ReentrantLock();
    public boolean isCharge;


    public ConsignmentItem(ConsignmentItemEntity data) {
        id = autoIncrease++;
        databaseId = data.id;
        sellerId = data.sellerId;
        buyerId = data.buyerId;
        status = data.status;
        createTime = data.createTime;
        buyTime = data.buyTime;
        receiveTime = data.receiveTime;
        price = data.price;
        item = new Item(data.itemId);
        JSONArray json = new JSONArray(data.options);
        for (int i = 0; i < json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            item.addItemOption(new ItemOption(obj.getInt("id"), obj.getInt("param")));
        }
        item.quantity = data.quantity;
    }

    public void setStatus(ConsignmentItemStatus status) {
        this.status = status;
        isCharge = true;
    }

    public void saveData() {
        lock.lock();
        try {
            if (!isCharge) {
                return;
            }
            isCharge = false;
            GameRepo.getInstance().consignmentItemRepo.saveData(databaseId, buyerId, status, new Gson().toJson(item.options), buyTime, receiveTime);
        } finally {
            lock.unlock();
        }
    }
}
