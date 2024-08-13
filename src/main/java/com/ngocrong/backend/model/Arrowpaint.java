package com.ngocrong.backend.model;

public class Arrowpaint {
    public short id;
    public short[] imgId;

    public short[] getImgId() {
        return imgId;
    }

    public void setImgId(short[] imgId) {
        this.imgId = imgId;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }
}
