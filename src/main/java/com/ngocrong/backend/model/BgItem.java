package com.ngocrong.backend.model;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class BgItem {

    private static final Logger logger = Logger.getLogger(BgItem.class);
    private static List<BgItem> bgItems;
    private static byte[] data;

    private int id;
    private short image;
    private byte layer;
    private short dx;
    private short dy;
    private int[] tileX;
    private int[] tileY;
    private short x;
    private short y;

    /**
     * Creates the data for background items by serializing the list of BgItem objects.
     */
    public static void createData() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {

            dos.writeShort(bgItems.size());
            for (BgItem bg : bgItems) {
                dos.writeShort(bg.image);
                dos.writeByte(bg.layer);
                dos.writeShort(bg.dx);
                dos.writeShort(bg.dy);
                int numTiles = bg.tileX.length;
                dos.writeByte(numTiles);
                for (int i = 0; i < numTiles; i++) {
                    dos.writeByte(bg.tileX[i]);
                    dos.writeByte(bg.tileY[i]);
                }
            }
            data = bos.toByteArray();
            bgItems = null;
        } catch (IOException ex) {
            logger.error("Failed to create data", ex);
        }
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public short getImage() {
        return image;
    }

    public void setImage(short image) {
        this.image = image;
    }

    public byte getLayer() {
        return layer;
    }

    public void setLayer(byte layer) {
        this.layer = layer;
    }

    public short getDx() {
        return dx;
    }

    public void setDx(short dx) {
        this.dx = dx;
    }

    public short getDy() {
        return dy;
    }

    public void setDy(short dy) {
        this.dy = dy;
    }

    public int[] getTileX() {
        return tileX;
    }

    public void setTileX(int[] tileX) {
        this.tileX = tileX;
    }

    public int[] getTileY() {
        return tileY;
    }

    public void setTileY(int[] tileY) {
        this.tileY = tileY;
    }

    public short getX() {
        return x;
    }

    public void setX(short x) {
        this.x = x;
    }

    public short getY() {
        return y;
    }

    public void setY(short y) {
        this.y = y;
    }
}
