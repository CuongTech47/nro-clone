package com.ngocrong.backend.model;

public class Friend {
    private int id;
    private String name;
    private short head;
    private short body;
    private short bag;
    private short leg;
    private long power;
    private transient boolean isOnline;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getHead() {
        return head;
    }

    public void setHead(short head) {
        this.head = head;
    }

    public short getBody() {
        return body;
    }

    public void setBody(short body) {
        this.body = body;
    }

    public short getBag() {
        return bag;
    }

    public void setBag(short bag) {
        this.bag = bag;
    }

    public short getLeg() {
        return leg;
    }

    public void setLeg(short leg) {
        this.leg = leg;
    }

    public long getPower() {
        return power;
    }

    public void setPower(long power) {
        this.power = power;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
