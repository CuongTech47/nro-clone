package com.ngocrong.backend.model;

import com.ngocrong.backend.util.Utils;

import java.util.ArrayList;

public class ImgByName {
    public static ArrayList<ImgByName> images;

    public static void addImage(ImgByName mount) {
        images.add(mount);
    }

    public int id;
    public int nFrame;
    public byte[][] imageData = new byte[4][];
    public String filename;

    public static ImgByName getMount(String name) {
        for (ImgByName img : images) {
            String filename = Utils.cutPng(img.filename);
            if (filename.equals(name)) {
                return img;
            }
        }
        return null;
    }

    public void init() {
        for (int i = 0; i < this.imageData.length; i++) {
            imageData[i] = Utils.getFile("resources/image/" + (i + 1) + "/imgbyname/" + this.filename);
        }
    }
}
