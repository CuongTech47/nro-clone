package com.ngocrong.backend.model;

public class Waypoint {

    public short minX;
    public short minY;
    public short maxX;
    public short maxY;
    public boolean isEnter;
    public boolean isOffline;
    public String name;
    public int next;
    public short x, y;


    public Waypoint() {

    }

    public Waypoint(short minX, short minY, short maxX, short maxY, boolean isEnter, boolean isOffline, String name) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.name = name;
        this.isEnter = isEnter;
        this.isOffline = isOffline;
    }
}
