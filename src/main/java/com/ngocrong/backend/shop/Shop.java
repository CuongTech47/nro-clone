package com.ngocrong.backend.shop;

import com.ngocrong.backend.model.Npc;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Shop implements Cloneable{
    private static Logger logger = Logger.getLogger(Shop.class);
    private static ArrayList<Shop> shops = new ArrayList<>();

    private ArrayList<Tab> tabs = new ArrayList<>();
    private int typeShop;
    private String tableName;
    private int npcId;
    private Npc npc;


    public static void addShop(Shop shop) {
        shops.add(shop);
    }
}
