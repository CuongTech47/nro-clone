package com.ngocrong.backend.model;

import com.google.gson.annotations.SerializedName;
import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.consts.Cmd;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.lib.KeyValue;
import com.ngocrong.backend.network.Message;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;

public class MagicTree {
    private static final Logger logger = Logger.getLogger(MagicTree.class);

    public static final long[] TIME_UPGRADE = new long[]{0L, 600000L, 6000000L, 58920000L, 597600000L, 1202400000L, 2592000000L, 4752000000L, 5961600000L, 8640000000L};
    public static final int[] GOLD_UPGRADE = new int[]{0, 5000, 10000, 100000, 1000000, 10000000, 20000000, 50000000, 100000000, 300000000};
    public static final int[] PEAN_ID = new int[]{0, 13, 60, 61, 62, 63, 64, 65, 352, 523, 595};
    public static final int[][] POSITION = {{378, 336}, {200, 336}, {300, 336}};
    public static final String[] TREE_NAME = {"Đậu thần cấp 1", "Đậu thần cấp 2", "Đậu thần cấp 3", "Đậu thần cấp 4", "Đậu thần cấp 5", "Đậu thần cấp 6", "Đậu thần cấp 7", "Đậu thần cấp 8", "Đậu thần cấp 9", "Đậu thần cấp 10"};
    public static final int[][] ICON = {{84, 85, 86, 87, 88, 89, 90, 90, 90, 90}, {371, 372, 373, 374, 375, 376, 377, 377, 377, 377}, {378, 379, 380, 381, 382, 383, 384, 384, 384, 384}};


    public transient int id;
    public transient int x;
    public transient int y;
    public transient int currPeas;
    public transient int remainPeas;
    public transient int maxPeas;
    public transient String strInfo;
    public transient String name;
    public transient int seconds;
    public transient int planet;


    @SerializedName("level")
    public int level;
    @SerializedName("upgrade")
    public boolean isUpgrade;
    @SerializedName("upgrade_time")
    public long upgradeTime;
    @SerializedName("last_harvest")
    public long lastHarvestTime;


    public void init() {
        int[] pos = POSITION[this.planet];
        int[] icon = ICON[this.planet];
        this.x = pos[0];
        this.y = pos[1];
        int index = this.level - 1;
        this.id = icon[index];
        this.name = TREE_NAME[index];
        this.maxPeas = 5 + index * 2;
        update();
    }


    private void update() {
        long now = System.currentTimeMillis();
        if (isUpgrade) {
            this.seconds = (int) ((this.upgradeTime - now) / 1000);
            if (this.seconds <= 0) {
                this.isUpgrade = false;
                this.upgradeTime = 0;
                this.level++;
                init();
            }
        } else {
            int fruitingTime = 30 * this.level;
            int waited = (int) ((now - this.lastHarvestTime) / 1000);
            this.currPeas = waited / fruitingTime;
            if (this.currPeas >= this.maxPeas) {
                this.currPeas = this.maxPeas;
                this.seconds = 0;
            } else {
                this.seconds = (this.level * 30) - (waited - (this.currPeas * fruitingTime));
            }
        }
    }

    public void openMenu(Char _char) {
        try {
            _char.menus.clear();

            if (shouldSkipMenu(_char)) {
                return;
            }

            if (this.isUpgrade) {
                addUpgradeOptions(_char);
            } else {
                addHarvestAndUpgradeOptions(_char);
            }

            sendMenuMessage(_char);
        } catch (IOException ex) {
            logger.error("Failed to open menu!", ex);
        }
    }

    private void sendMenuMessage(Char _char)throws IOException {
        Message ms = new Message(Cmd.MAGIC_TREE);
        DataOutputStream ds = ms.getWriter();
        ds.writeByte(1);
        for (KeyValue<Integer, String> keyValue : _char.menus) {
            ds.writeUTF(keyValue.getValue());
        }
        ds.flush();
        _char.service.sendMessage(ms);
        ms.cleanup();
    }

    private void addHarvestAndUpgradeOptions(Char _char) {
        _char.menus.add(new KeyValue<>(3500, "Thu hoạch"));
        if (this.level < 10) {
            String upgradeTime = Utils.getTime((int) (TIME_UPGRADE[this.level] / 1000));
            _char.menus.add(new KeyValue<>(3501, "Nâng cấp\n" + upgradeTime + "\n" + Utils.formatNumber(GOLD_UPGRADE[this.level]) + " vàng"));
        }
        if (this.currPeas < this.maxPeas) {
            int gem = calculatePeaGem();
            _char.menus.add(new KeyValue<>(3502, "Kết hạt nhanh " + gem + " ngọc"));
        }
    }

    private int calculatePeaGem() {
        int gem = ((this.level - 1) * 5 + 1);
        return Math.max(gem, 1);
    }

    private void addUpgradeOptions(Char _char) {
        long now = System.currentTimeMillis();
        int gem = calculateUpgradeGem(now);
        _char.menus.add(new KeyValue<>(3504, "Nâng cấp nhanh " + Utils.formatNumber(gem) + " ngọc"));
        _char.menus.add(new KeyValue<>(3505, "Hủy nâng cấp hồi " + Utils.formatNumber(GOLD_UPGRADE[this.level] / 2) + " vàng"));
    }

    private int calculateUpgradeGem(long now) {
        int gem = (int) ((this.upgradeTime - now) / 1000 / 60 / 10);
        return Math.max(gem, 1);
    }

    private boolean shouldSkipMenu(Char _char) {
        return _char.getTaskMain() != null && _char.getTaskMain().id == 0 && _char.getTaskMain().index < 4;
    }


    public void harvest(Char _char) {
        // Cập nhật thông tin cây trước khi thu hoạch
        update();

        // Kiểm tra nếu không có đậu để thu hoạch thì thoát
        if (this.currPeas == 0) {
            return;
        }

        // Xử lý thu hoạch vào túi đồ của nhân vật
//        int received = processHarvestForBag(_char);
//
//        // Nếu còn đậu chưa thu hoạch hết, xử lý thu hoạch vào rương đồ
//        if (received < this.currPeas) {
//            processHarvestForBox(_char);
//        }

        // Nếu vẫn còn đậu chưa thu hoạch và túi/rương đã đầy, thông báo cho người chơi
        if (this.currPeas > 0) {
            _char.service.serverMessage(
                    String.format("Rương đồ đã chứa đầy %d viên đậu, không thể thu hoạch thêm.", 20)
            );
        }

        // Kiểm tra và tiến tới nhiệm vụ tiếp theo nếu điều kiện được thỏa mãn
//        checkAndAdvanceTask(_char);

        // Cập nhật thời gian thu hoạch cuối cùng
        updateLastHarvestTime();

        // Cập nhật thông tin cây sau khi thu hoạch
        update();

        // Thông báo trạng thái cây phép thuật nếu số đậu hiện tại nhỏ hơn số đậu tối đa
//        notifyMagicTreeStatus(_char);
    }

    // Kiểm tra số lượng đậu trong túi và tiến hành thu hoạch
//    private int processHarvestForBag(Char _char) {
//        int numberInBag = getItemCountInBag(_char, Item.TYPE_DAUTHAN); // Đếm số lượng đậu trong túi
//        int maxInBag = Server.getMaxQuantityItem(); // Lấy số lượng đậu tối đa mà túi có thể chứa
//        int received = Math.min(maxInBag - numberInBag, this.currPeas); // Tính số lượng đậu có thể thu hoạch
//
//        if (received > 0) {
//            addItemToBag(_char, received); // Thêm đậu vào túi
//            this.currPeas -= received; // Giảm số lượng đậu hiện tại của cây sau khi thu hoạch
//        }
//
//        return received; // Trả về số lượng đậu đã thu hoạch
//    }

    // Kiểm tra số lượng đậu trong rương và tiến hành thu hoạch
//    private int processHarvestForBox(Char _char) {
//        int numberInBox = getItemCountInBox(_char, Item.TYPE_DAUTHAN); // Đếm số lượng đậu trong rương
//        int maxInBox = 20; // Số lượng đậu tối đa mà rương có thể chứa
//        int received = Math.min(maxInBox - numberInBox, this.currPeas); // Tính số lượng đậu có thể thu hoạch
//
//        if (received > 0) {
//            addItemToBox(_char, received); // Thêm đậu vào rương
//            this.currPeas -= received; // Giảm số lượng đậu hiện tại của cây sau khi thu hoạch
//        }
//
//        return received; // Trả về số lượng đậu đã thu hoạch
//    }

    // Đếm số lượng đậu trong túi của nhân vật
    private int getItemCountInBag(Char _char, int itemType) {
        int count = 0;
        for (Item item : _char.itemBag) {
            if (item != null && item.template.getType() == itemType) {
                count += item.quantity; // Cộng dồn số lượng đậu
            }
        }
        return count;
    }

    // Đếm số lượng đậu trong rương của nhân vật
//    private int getItemCountInBox(Char _char, int itemType) {
//        int count = 0;
//        for (Item item : _char.itemBox) {
//            if (item != null && item.template.type == itemType) {
//                count += item.quantity; // Cộng dồn số lượng đậu
//            }
//        }
//        return count;
//    }

    // Thêm đậu vào túi của nhân vật
//    private void addItemToBag(Char _char, int quantity) {
//        Item item = new Item(PEAN_ID[this.level]);
//        item.setDefaultOptions(); // Đặt các thuộc tính mặc định cho item
//        item.quantity = quantity; // Thiết lập số lượng đậu
//        if (_char.addItem(item)) {
//            // Nếu thêm đậu thành công
//        }
//    }
//
//    // Thêm đậu vào rương của nhân vật
//    private void addItemToBox(Char _char, int quantity) {
//        Item item = new Item(PEAN_ID[this.level]);
//        item.setDefaultOptions(); // Đặt các thuộc tính mặc định cho item
//        item.quantity = quantity; // Thiết lập số lượng đậu
//        if (_char.addItemToBox(item)) {
//            // Nếu thêm đậu vào rương thành công
//        }
//    }

    // Kiểm tra và tiến tới nhiệm vụ tiếp theo nếu điều kiện được thỏa mãn
//    private void checkAndAdvanceTask(Char _char) {
//        if (_char.taskMain != null && _char.taskMain.id == 0 && _char.taskMain.index == 4) {
//            _char.taskNext(); // Chuyển tới nhiệm vụ tiếp theo
//        }
//    }

    // Cập nhật thời gian thu hoạch cuối cùng
    private void updateLastHarvestTime() {
        this.lastHarvestTime = System.currentTimeMillis() - (this.level * 30000L * this.currPeas);
    }

    // Thông báo trạng thái cây phép thuật nếu cần
//    private void notifyMagicTreeStatus(Char _char) {
//        if (this.currPeas < maxPeas) {
//            _char.service.magicTree((byte) 2, this); // Gửi trạng thái cây phép thuật
//        }
//    }


}
