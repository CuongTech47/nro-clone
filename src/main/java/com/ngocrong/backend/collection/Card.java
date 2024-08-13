package com.ngocrong.backend.collection;

import com.google.gson.annotations.SerializedName;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Card {
    private static Logger logger = Logger.getLogger(Card.class);
    public static ArrayList<CardTemplate> templates;

    @SerializedName("id")
    public int id;
    @SerializedName("amount")
    public int amount;
    @SerializedName("level")
    public int level;
    @SerializedName("use")
    public boolean isUse;
    public transient CardTemplate template;



}
