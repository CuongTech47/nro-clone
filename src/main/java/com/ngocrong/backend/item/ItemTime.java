package com.ngocrong.backend.item;

public class ItemTime {
    public int id;
    public int icon;
    public int seconds;
    public boolean isSave;

    public ItemTime(int id, int icon, int seconds, boolean isSave) {
        this.id = id;
        this.icon = icon;
        this.seconds = seconds;
        this.isSave = isSave;
    }

    public void update() {
        this.seconds--;
    }
}
