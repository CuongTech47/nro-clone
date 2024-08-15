package com.ngocrong.backend.item;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
public class ItemTemplate {

    public int mountID;
    public short id;


    public byte type;
    public byte gender;
    public String name;
    public String description;
    public byte level;
    public int require;
    public short iconID;
    public short part;
    public boolean isUpToUp;

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

    public void setOptions(int expired, ArrayList<ItemOption> options) {
        if (!options.isEmpty()) {
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
