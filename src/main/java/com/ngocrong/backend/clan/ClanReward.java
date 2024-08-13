package com.ngocrong.backend.clan;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ClanReward {

    @SerializedName("star")
    private int star;
    @SerializedName("time_start")
    private long timeStart;
    @SerializedName("time_end")
    private long timeEnd;
    @SerializedName("delay")
    private long timeDelay;
    @SerializedName("receive_time")
    private long receiveTime;
    @SerializedName("number_of_times_received")
    private int numberOfTimesReceived;
    @SerializedName("can_be_received_directly")
    private boolean canBeReceivedDirectly;

    public boolean isExpired() {
        long now = System.currentTimeMillis();
        return now >= timeEnd;
    }
}
