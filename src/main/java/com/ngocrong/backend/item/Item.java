package com.ngocrong.backend.item;

import com.google.gson.annotations.SerializedName;
import com.ngocrong.backend.server.Config;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Item {
    private static Logger logger = Logger.getLogger(Item.class);

    public static final int TYPE_BODY_MIN = 0;
    public static final int TYPE_BODY_MAX = 6;
    public static final int TYPE_AO = 0;
    public static final int TYPE_QUAN = 1;
    public static final int TYPE_GANGTAY = 2;
    public static final int TYPE_GIAY = 3;
    public static final int TYPE_RADA = 4;
    public static final int TYPE_HAIR = 5;
    public static final int TYPE_DAUTHAN = 6;
    public static final int TYPE_NGOCRONG = 12;
    public static final int TYPE_SACH = 7;
    public static final int TYPE_NHIEMVU = 8;
    public static final int TYPE_GOLD = 9;
    public static final int TYPE_DIAMOND = 10;
    public static final int TYPE_BALO = 11;
    public static final int TYPE_AMULET = 13;
    public static final int TYPE_DIAMOND_LOCK = 34;

    public static final byte BOX_BAG = 0;
    public static final byte BAG_BOX = 1;
    public static final byte BOX_BODY = 2;
    public static final byte BODY_BOX = 3;
    public static final byte BAG_BODY = 4;
    public static final byte BODY_BAG = 5;
    public static final byte BAG_PET = 6;
    public static final byte PET_BAG = 7;

    @SerializedName("index")
    public int indexUI;
    public int id;
    public int quantity;
    public ArrayList<ItemOption> options;

    public transient String info;
    public transient String content;
    public transient boolean isLock;
    public transient boolean isCantSaleForPlay, isCantSale, isNhapThe;
    public transient byte typeThrow;
    public transient ItemTemplate template;
    public transient Lock lock = new ReentrantLock();
    public transient long require;
    public transient int upgrade;
    public transient int levelMosaicStone;
    public transient boolean isSet;


    public Item() {
    }

    public Item(int id) {
        this.id = id;
    }

    public ArrayList<ItemOption> getItemOptions() {
        ArrayList<ItemOption> options = new ArrayList<>();
        int damage = 0;
        int hp = 0;
        int mp = 0;

        for (ItemOption o : this.options) {
            int optionID = o.optionTemplate.id;
            if (optionID == 50) {
                damage += o.param;
            } else if (optionID == 77) {
                hp += o.param;
            } else if (optionID == 103) {
                mp += o.param;
            } else {
                options.add(o);
            }
        }
        if (damage > 0) {
            options.add(new ItemOption(50, damage));
        }
        if (hp > 0) {
            options.add(new ItemOption(77, hp));
        }
        if (mp > 0) {
            options.add(new ItemOption(103, mp));
        }
        return options;
    }

    public void load(JSONObject obj) {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            this.id = obj.getInt("id");
            this.indexUI = obj.getInt("index");
            init();
        }catch (JSONException ex) {

        }
    }



    private void init() {
        Server server = DragonBall.getInstance().getServer();
        this.template = server.iTemplates.get(id);

        setTypeThrow(template.getType());
        setCantSale(template.getType());

        this.info = "";
        this.content = "";
        this.options = new ArrayList<>();
        this.require = template.getRequire();
    }

    private void setCantSale(byte type) {
    }

    private void setTypeThrow(byte type) {
    }

    public void setDefaultOptions() {
        this.options.clear();
        for (ItemOption option : this.template.options) {
            addItemOption(new ItemOption(option.optionTemplate.id, option.param));
        }
    }

    private void addItemOption(ItemOption itemOption) {
        setAttribute(itemOption);
        this.options.add(itemOption);
    }

    private void setAttribute(ItemOption itemOption) {
        if (itemOption.optionTemplate.id == 30) {
            this.isLock = true;
        }
        if (itemOption.optionTemplate.id >= 34 && itemOption.optionTemplate.id <= 36) {
            this.levelMosaicStone = itemOption.param;
        }
        if (itemOption.optionTemplate.id >= 127 && itemOption.optionTemplate.id <= 135) {
            this.isSet = true;
        }
        if (itemOption.optionTemplate.id == 72) {
            this.upgrade = itemOption.param;
        }
        if (itemOption.optionTemplate.id == 38) {
            this.isNhapThe = true;
        }
        if (itemOption.optionTemplate.id == 107 || itemOption.optionTemplate.type == 9) {
            this.typeThrow = 2;
        }
        if (itemOption.optionTemplate.id == 154) {
            this.isCantSaleForPlay = true;
        }
        if (itemOption.optionTemplate.id == 21) {
            this.require = (long) itemOption.param * 1000000000L;
        }
    }
}
