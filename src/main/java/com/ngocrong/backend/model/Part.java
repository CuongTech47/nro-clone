package com.ngocrong.backend.model;

public class Part {
    public int type;
    public PartImage[] pi;

    public Part(int type) {
        this.type = type;
        if (type == 0) {
            this.pi = new PartImage[3];
        }
        if (type == 1) {
            this.pi = new PartImage[17];
        }
        if (type == 2) {
            this.pi = new PartImage[14];
        }
        if (type == 3) {
            this.pi = new PartImage[2];
        }
    }
}
