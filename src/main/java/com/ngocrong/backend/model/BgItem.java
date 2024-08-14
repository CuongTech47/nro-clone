package com.ngocrong.backend.model;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BgItem {

    private static Logger logger = Logger.getLogger(BgItem.class);

    public static ArrayList<BgItem> bgItems;
    public static byte[] data;

    public int id;
    public short image;
    public byte layer;
    public short dx;
    public short dy;
    public int[] tileX;
    public int[] tileY;
    public short x;
    public short y;

    public static void createData() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeShort(bgItems.size());
            for (BgItem bg : bgItems) {
                dos.writeShort(bg.image);
                dos.writeByte(bg.layer);
                dos.writeShort(bg.dx);
                dos.writeShort(bg.dy);
                int num = bg.tileX.length;
                dos.writeByte(num);
                for (int i = 0; i < num; i++) {
                    dos.writeByte(bg.tileX[i]);
                    dos.writeByte(bg.tileY[i]);
                }
            }
            data = bos.toByteArray();
            dos.close();
            bos.close();
            bgItems = null;
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }
}
