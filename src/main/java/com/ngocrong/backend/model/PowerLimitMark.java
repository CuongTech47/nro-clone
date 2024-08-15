package com.ngocrong.backend.model;

import java.util.ArrayList;
import java.util.List;

public class PowerLimitMark {
    // Static list to hold all PowerLimitMark instances
    public static ArrayList<PowerLimitMark> limitMark;


    // Fields representing power limits and attributes
    private long power;
    private int hp;
    private int mp;
    private int damage;
    private short defense;
    private byte critical;

    // Constructor
    public PowerLimitMark(long power, int hp, int mp, int damage, short defense, byte critical) {
        this.power = power;
        this.hp = hp;
        this.mp = mp;
        this.damage = damage;
        this.defense = defense;
        this.critical = critical;
    }

    // Getters and Setters for encapsulation
    public long getPower() {
        return power;
    }

    public void setPower(long power) {
        this.power = power;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public short getDefense() {
        return defense;
    }

    public void setDefense(short defense) {
        this.defense = defense;
    }

    public byte getCritical() {
        return critical;
    }

    public void setCritical(byte critical) {
        this.critical = critical;
    }
}
