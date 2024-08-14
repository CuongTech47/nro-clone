package com.ngocrong.backend.item;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
public class ItemTemplate {

    public int mountID;
    private short id;


    private byte type;
    private byte gender;
    private String name;
    private String description;
    private byte level;
    private int require;
    private short iconID;
    private short part;
    private boolean isUpToUp;



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

}
