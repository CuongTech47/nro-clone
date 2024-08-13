package com.ngocrong.backend.collection;

import com.ngocrong.backend.item.ItemOption;

import java.util.ArrayList;

public class CardTemplate {
    public int id;
    public byte rank;
    public byte max_amount;
    public byte type;
    public int icon;
    public String name;
    public String info;
    public short templateID;
    public short head;
    public short body;
    public short leg;
    public short bag;
    public short aura;
    public ArrayList<ItemOption> options;
}
