package com.ngocrong.backend.model;

import lombok.Data;

@Data
public class PowerInfo {
    private String info;
    private int point, maxPoint;
    private int seconds;

    public PowerInfo(String info, int p, int maxP, int seconds) {
        this.info = info;
        this.point = p;
        this.maxPoint = maxP;
        this.seconds = seconds;
    }

    public void addPoint(int point) {
        this.point += point;
        if (this.point < 0) {
            this.point = 0;
        }
    }

    public boolean isMaxPoint() {
        return this.point >= this.maxPoint;
    }
}
