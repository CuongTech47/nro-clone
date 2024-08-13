package com.ngocrong.backend.model;

public class DartInfo {

    private short id;
    private short[][] head;
    private short[][] headBorder;
    private short[] tail;
    private short[] tailBorder;
    private short[] xd1;
    private short[] xd2;
    private short xdPercent;
    private short nUpdate;
    private int va;

    // Getters và setters cho các thuộc tính của DartInfo

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short[][] getHead() {
        return head;
    }

    public void setHead(short[][] head) {
        this.head = head;
    }

    public short[][] getHeadBorder() {
        return headBorder;
    }

    public void setHeadBorder(short[][] headBorder) {
        this.headBorder = headBorder;
    }

    public short[] getTail() {
        return tail;
    }

    public void setTail(short[] tail) {
        this.tail = tail;
    }

    public short[] getTailBorder() {
        return tailBorder;
    }

    public void setTailBorder(short[] tailBorder) {
        this.tailBorder = tailBorder;
    }

    public short[] getXd1() {
        return xd1;
    }

    public void setXd1(short[] xd1) {
        this.xd1 = xd1;
    }

    public short[] getXd2() {
        return xd2;
    }

    public void setXd2(short[] xd2) {
        this.xd2 = xd2;
    }

    public short getXdPercent() {
        return xdPercent;
    }

    public void setXdPercent(short xdPercent) {
        this.xdPercent = xdPercent;
    }

    public short getNUpdate() {
        return nUpdate;
    }

    public void setNUpdate(short nUpdate) {
        this.nUpdate = nUpdate;
    }

    public int getVa() {
        return va;
    }

    public void setVa(int va) {
        this.va = va;
    }
}