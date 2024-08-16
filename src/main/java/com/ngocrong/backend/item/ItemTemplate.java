package com.ngocrong.backend.item;


import com.ngocrong.backend.consts.ItemName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class ItemTemplate {

    public ItemTemplate() {
    }

    public ItemTemplate(short templateID, byte type, byte gender, String name, String description, byte level, int require, short iconID, short part, boolean isUpToUp) {
        this.id = templateID;
        this.type = type;
        this.gender = gender;
        this.name = name;
        this.description = description;
        this.level = level;
        this.require = require;
        this.iconID = iconID;
        this.part = part;
        this.isUpToUp = isUpToUp;
    }

    public boolean isUpToUp() {
        return this.isUpToUp || this.type == 12 || this.type == 14 || this.type == 6 || this.type == 7 || this.type == 8 || this.type == 15 || this.type == 29 || this.type == 30 || this.id == 457 || this.id == 193 || this.id == 211 || this.id == 361 || this.id == 521 || this.id == 400 || this.id == 401 || this.id == 402 || this.id == 403 || this.id == 404 || this.id == 759 || this.id == 342 || this.id == 343 || this.id == 344 || this.id == 345;
    }

    public boolean isDeTu() {
        return this.id == ItemName.CAI_TRANG_BAT_GIOI || this.id == ItemName.CAI_TRANG_TON_NGO_KHONG_DT || this.id == ItemName.CAI_TRANG_UUB;
    }

    public boolean isSuPhu() {
        return this.id == ItemName.CAI_TRANG_TON_NGO_KHONG_TD || this.id == ItemName.CAI_TRANG_TON_NGO_KHONG_NM || this.id == ItemName.CAI_TRANG_TON_NGO_KHONG_XD;
    }

    public boolean isFood() {
        return this.id == ItemName.BANH_PUDDING || this.id == ItemName.XUC_XICH || this.id == ItemName.KEM_DAU || this.id == ItemName.MI_LY || this.id == ItemName.SUSHI;
    }

    public boolean isAoThanLinh() {
        return this.id == ItemName.AO_THAN_LINH || this.id == ItemName.AO_THAN_NAMEC || this.id == ItemName.AO_THAN_XAYDA;
    }

    public boolean isQuanThanLinh() {
        return this.id == ItemName.QUAN_THAN_LINH || this.id == ItemName.QUAN_THAN_NAMEC || this.id == ItemName.QUAN_THAN_XAYDA;
    }

    public boolean isGangThanLinh() {
        return this.id == ItemName.GANG_THAN_LINH || this.id == ItemName.GANG_THAN_NAMEC || this.id == ItemName.GANG_THAN_XAYDA;
    }

    public boolean isGiayThanLinh() {
        return this.id == ItemName.GIAY_THAN_LINH || this.id == ItemName.GIAY_THAN_NAMEC || this.id == ItemName.GIAY_THAN_XAYDA;
    }

    public boolean isNhanThanLinh() {
        return this.id == ItemName.NHAN_THAN_LINH;
    }

    public boolean isAn() {
        return this.type == 35;
    }

    public boolean isThanLinh() {
        return isAoThanLinh() || isQuanThanLinh() || isGangThanLinh() || isGiayThanLinh() || isNhanThanLinh();
    }

    public short id;
    public byte type;
    public byte gender;
    public String name;
    public String description;
    public byte level;
    public short iconID;
    public short part;
    public boolean isUpToUp;
    public int require;
    public int mountID;
    public boolean isLock;

    public long powerRequire;
    public long buyGold;
    public int buyGem;
    public long resalePrice;
    public String reason;
    public short iconSpec;
    public int buySpec;
    public boolean isNew;
    public boolean isPreview;
    public short head;
    public short body;
    public short leg;
    public ArrayList<ItemOption> options;

    public void setOptions(int expired, ArrayList<ItemOption> options) {
        if (options.size() > 0) {
            this.options = options;
        }
        if (expired != -1) {
            this.options.add(new ItemOption(93, expired));
        }
    }

    public ItemTemplate clone() {
        ItemTemplate item = new ItemTemplate();
        item.id = id;
        item.type = type;
        item.gender = gender;
        item.name = name;
        item.description = description;
        item.level = level;
        item.iconID = iconID;
        item.part = part;
        item.isUpToUp = isUpToUp;
        item.require = require;
        item.mountID = mountID;
        item.powerRequire = powerRequire;
        item.buyGold = buyGold;
        item.buyGem = buyGem;
        item.reason = reason;
        item.iconSpec = iconSpec;
        item.buySpec = buySpec;
        item.isNew = isNew;
        item.isPreview = isPreview;
        item.head = head;
        item.body = body;
        item.leg = leg;
        item.resalePrice = resalePrice;
        item.options = new ArrayList<>();
        for (ItemOption o : options) {
            item.options.add(new ItemOption(o.optionTemplate.id, o.param));
        }
        return item;
    }
}
