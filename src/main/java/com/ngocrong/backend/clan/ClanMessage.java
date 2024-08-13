package com.ngocrong.backend.clan;

public class ClanMessage {

    public static int autoIncrease = 0;

    public int id;
    public int type;
    public int playerId;
    public String playerName;
    public int time;
    public String chat;
    public byte color;
    public byte role;
    public int receive;
    public int maxCap;
    public boolean isNewMessage;
    public short head, body, leg;
    public long power;

    public ClanMessage() {
        this.id = autoIncrease++;
        this.time = (int) (System.currentTimeMillis() / 1000 - 1000000000);
    }

    public void setFashion(short head, short body, short leg) {
        this.head = head;
        this.body = body;
        this.leg = leg;
    }
}
