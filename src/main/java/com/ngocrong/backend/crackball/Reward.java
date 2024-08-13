package com.ngocrong.backend.crackball;

import lombok.Data;

@Data
public class Reward {
    private int id;
    private String name;
    private int itemId;
    private int quantity;
    private double rate;
    private int expire;
}
