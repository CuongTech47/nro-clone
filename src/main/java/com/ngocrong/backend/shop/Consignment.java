package com.ngocrong.backend.shop;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.consts.Cmd;
import com.ngocrong.backend.consts.ItemName;
import com.ngocrong.backend.consts.Language;
import com.ngocrong.backend.entity.ConsignmentItemEntity;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.item.ItemOption;
import com.ngocrong.backend.repository.GameRepo;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;
import com.ngocrong.backend.network.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Consignment {
    private static final Logger logger = Logger.getLogger(Consignment.class);
    private static Consignment instance;
    public HashMap<Integer, ConsignmentItem> items;

    public ReadWriteLock lock = new ReentrantReadWriteLock();

    private static final String[] TAB = {"Trang\nbị", "Sự\nkiện", "Linh\ntinh", "Đang\nbán"};

    public static Consignment getInstance() {
        if (instance == null) {
            instance = new Consignment();
        }
        return instance;
    }

    public void init() {
        items = new HashMap<>();
        ConsignmentItemStatus[] statuses = new ConsignmentItemStatus[]{
                ConsignmentItemStatus.ON_SALE,
                ConsignmentItemStatus.SOLD};
        List<ConsignmentItemEntity> dataList = GameRepo.getInstance().consignmentItemRepo.findByStatusIn(statuses);
        for (ConsignmentItemEntity data : dataList) {
            ConsignmentItem item = new ConsignmentItem(data);
            items.put(item.id, item);
        }
        Utils.setScheduled(this::updateNewDay, 86400, 0, 0);
    }

    public void addItem(Char player, int index, int quantity, int price) {
        lock.writeLock().lock();
        try {
            if (player.isTrading()) {
                return;
            }
            if (quantity < 1) {
                return;
            }
            if (items.values().stream()
                    .filter(i -> i.sellerId == player.getId()
                            && (i.status == ConsignmentItemStatus.ON_SALE || i.status == ConsignmentItemStatus.SOLD))
                    .count() > 10) {
                player.service.serverMessage("Chỉ có thể kí tối đa 10 vật phẩm");
                return;
            }
            long gold = 500000000;
            if (player.getGold() < gold) {
                player.service.serverMessage("Bạn không đủ vàng để kí");
                return;
            }
            if (price < 1) {
                player.service.serverMessage("Giá kí phải từ 1 thỏi vàng trở lên");
                return;
            }
            Item item = player.itemBag[index];
            if (item == null) {
                return;
            }
            if (!item.isCanSaleToConsignment()) {
                player.service.serverMessage("Không thể kí vật phẩm này");
                return;
            }
            if (item.quantity < quantity) {
                player.service.serverMessage("Số lượng không đủ");
                return;
            }
            ConsignmentItemEntity data = new ConsignmentItemEntity(player, item, quantity, price);
            GameRepo.getInstance().consignmentItemRepo.save(data);
            ConsignmentItem itemConsignment = new ConsignmentItem(data);
            items.put(itemConsignment.id, itemConsignment);
            player.addGold(-gold);
            player.removeItem(index, quantity);
            showShop(player);
            player.service.serverMessage("Treo bán vật phẩm thành công");
        } catch (Exception ex) {
            logger.error("failed!", ex);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void getItem(Char player, int id) {
        ConsignmentItem itemConsignment = items.get(id);
        if (itemConsignment == null || itemConsignment.sellerId != player.getId()) {
            return;
        }
        itemConsignment.lock.lock();
        try {
            if (player.isBagFull()) {
                player.service.serverMessage(Language.ME_BAG_FULL);
                return;
            }
            if (itemConsignment.status == ConsignmentItemStatus.ON_SALE) {
                Item item = itemConsignment.item.clone();
                if (player.addItem(item)) {
                    itemConsignment.setStatus(ConsignmentItemStatus.CANCEL_SALE);
                    itemConsignment.receiveTime = new Timestamp(System.currentTimeMillis());
                    player.service.serverMessage("Hủy bán vật phẩm thành công");
                    showShop(player);
                }
            } else if (itemConsignment.status == ConsignmentItemStatus.SOLD) {
                itemConsignment.setStatus(ConsignmentItemStatus.RECEIVED_MONEY);
                Item item = new Item(ItemName.THOI_VANG);
                item.setDefaultOptions();
                item.quantity = itemConsignment.price;
                player.addItem(item);
                player.service.serverMessage(String.format("Bạn nhận được %d thỏi vàng", itemConsignment.price));
                showShop(player);
            }
        } finally {
            itemConsignment.lock.unlock();
        }
    }

    public void buyItem(Char player, int itemId) {
        ConsignmentItem itemConsignment = items.get(itemId);
        if (itemConsignment == null || itemConsignment.sellerId == player.getId()) {
            return;
        }
        itemConsignment.lock.lock();
        try {
            if (player.isBagFull()) {
                player.service.serverMessage(Language.ME_BAG_FULL);
                return;
            }
            if (itemConsignment.status != ConsignmentItemStatus.ON_SALE) {
                player.service.serverMessage("Vật phẩm đã được bán, hủy bán hoặc hết hạn sử dụng");
                return;
            }
            int day = itemConsignment.item.getExpiry();
            if (day != -1 && day <= 0) {
                player.service.serverMessage("Vật phẩm đã hết hạn sử dụng");
                return;
            }
            Item item = player.getItemInBag(ItemName.THOI_VANG);
            if (item == null || item.quantity < itemConsignment.price) {
                player.service.serverMessage("Bạn không đủ thỏi vàng");
                return;
            }
            player.removeItem(item.indexUI, itemConsignment.price);
            player.addItem(itemConsignment.item.clone());
            itemConsignment.setStatus(ConsignmentItemStatus.SOLD);
            itemConsignment.buyerId = player.getId();
            itemConsignment.buyTime = new Timestamp(System.currentTimeMillis());
            if (item.quantity > 1) {
                player.service.serverMessage(String.format("Bạn nhận được x%d %s", itemConsignment.item.quantity, itemConsignment.item.template.name));
            } else {
                player.service.serverMessage(String.format("Bạn nhận được %s", itemConsignment.item.template.name));
            }
            showShop(player);

        } finally {
            itemConsignment.lock.unlock();
        }
    }

    public void showShop(Char player) {
        try {
            HashMap<Integer, HashMap<Integer, ConsignmentItem>> consignmentItemHashMap = new HashMap<>();
            consignmentItemHashMap.put(0, new HashMap<>());
            consignmentItemHashMap.put(1, new HashMap<>());
            consignmentItemHashMap.put(2, new HashMap<>());
            consignmentItemHashMap.put(3, new HashMap<>());
            HashMap<Integer, ConsignmentItem> itemMarketHashMap = getItems();
            for (ConsignmentItem itemMarket : itemMarketHashMap.values()) {
                if (itemMarket.sellerId == player.getId()) {
                    if (itemMarket.status == ConsignmentItemStatus.ON_SALE || itemMarket.status == ConsignmentItemStatus.SOLD) {
                        consignmentItemHashMap.get(3).put(itemMarket.id, itemMarket);
                    }
                } else if (itemMarket.status == ConsignmentItemStatus.ON_SALE) {
                    if (itemMarket.item.template.type < 5) {
                        consignmentItemHashMap.get(0).put(itemMarket.id, itemMarket);
                    } else {
                        consignmentItemHashMap.get(2).put(itemMarket.id, itemMarket);
                    }
                }
            }
            Message mss = new Message(Cmd.SHOP);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(2);
            ds.writeByte(consignmentItemHashMap.size());
            int tab_index = 0;
            for (HashMap<Integer, ConsignmentItem> tab : consignmentItemHashMap.values()) {
                ds.writeUTF(TAB[tab_index++]);
                ds.writeInt(tab.size());
                for (ConsignmentItem consignmentItem : tab.values()) {
                    ds.writeShort(consignmentItem.item.template.id);
                    ds.writeInt(consignmentItem.id);
                    ds.writeInt(consignmentItem.price);
                    ds.writeInt(consignmentItem.item.quantity);
                    ds.writeByte(consignmentItem.status == ConsignmentItemStatus.ON_SALE ? 0 : 1);
                    ds.writeBoolean(consignmentItem.sellerId == player.getId());
                    ds.writeByte(consignmentItem.item.options.size());
                    for (ItemOption option : consignmentItem.item.options) {
                        ds.writeShort(option.optionTemplate.id);
                        ds.writeInt(option.param);
                    }
                    ds.writeBoolean(false);
                    ds.writeBoolean(false);
                }
            }
            ds.flush();
            player.service.sendMessage(mss);
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public HashMap<Integer, ConsignmentItem> getItems() {
        lock.readLock().lock();
        try {
            return items;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void saveData() {
        //lock.readLock().lock();
        try {
            for (ConsignmentItem itemConsignment : items.values()) {
                itemConsignment.saveData();
            }
        } finally {
            //lock.readLock().unlock();
        }
    }

    public void updateNewDay() {
        lock.writeLock().lock();
        try {
            for (ConsignmentItem itemConsignment : items.values()) {
                if (itemConsignment.status == ConsignmentItemStatus.ON_SALE) {
                    itemConsignment.lock.lock();
                    try {
                        if (itemConsignment.status == ConsignmentItemStatus.ON_SALE) {
                            for (ItemOption option : itemConsignment.item.options) {
                                if (option.optionTemplate.id == 50) {
                                    option.param--;
                                    if (option.param <= 0) {
                                        itemConsignment.setStatus(ConsignmentItemStatus.EXPIRED);
                                        break;
                                    }
                                }
                            }
                        }
                    } finally {
                        itemConsignment.lock.unlock();
                    }
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
