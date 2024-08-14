package com.ngocrong.backend.item;

import com.google.gson.annotations.SerializedName;

public class Amulet {

    @SerializedName("id")
    public int id;
    @SerializedName("expired_time")
    public long expiredTime;

    public int getTimeRemaing() {
        return (int) ((this.expiredTime - System.currentTimeMillis()) / 1000 / 60);
    }

    public ItemOption getItemOption() {
        ItemOption itemOption = null;
        int minutes = getTimeRemaing();
        int hours = minutes / 60;
        int days = hours / 24;
        if (days > 0) {
            itemOption = new ItemOption(63, days);
        } else if (hours > 0) {
            itemOption = new ItemOption(64, hours);
        } else {
            itemOption = new ItemOption(65, minutes);
        }
        return itemOption;
    }
}
