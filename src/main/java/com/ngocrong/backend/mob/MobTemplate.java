package com.ngocrong.backend.mob;

import com.ngocrong.backend.effect.Frame;
import com.ngocrong.backend.effect.ImageInfo;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MobTemplate {
    private static final Logger logger = Logger.getLogger(MobTemplate.class);

    public int mobTemplateId;
    public byte rangeMove;
    public byte speed;
    public byte type;
    public long hp;
    public String name;
    public byte level;
    public byte dartType;
    public byte new1;
    public ArrayList<ImageInfo> images;
    public ArrayList<Frame> frames;
    public short[] run;
    public byte[][] frameBoss;
    public short x;
    public short y;
    public boolean isData = false;

    public byte[][] img = new byte[4][];
    public byte[] data;
    public byte[] dataBoss;

    public void createData() {
        logger.debug("create data mob " + this.name);
        createDataBoss();
        createDataFrameBoss();
        for (int i = 1; i <= img.length; i++) {
            byte[] ab = Utils.getFile("resources/image/" + i + "/mob/" + this.mobTemplateId + ".png");
            this.img[i - 1] = ab;
        }
        this.isData = true;
        frameBoss = null;
        images = null;
        run = null;
    }

    private void createDataFrameBoss() {
        byte[] ab = null;
        try {
            if (frameBoss != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream d = new DataOutputStream(bos);
                d.writeByte(this.frameBoss.length);
                for (byte[] frames : this.frameBoss) {
                    d.writeByte(frames.length);
                    for (byte f : frames) {
                        d.writeByte(f);
                    }
                }
                this.dataBoss = bos.toByteArray();
                d.close();
                bos.close();
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void createDataBoss() {
        byte[] ab = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream d = new DataOutputStream(bos);
            d.writeByte(this.images.size());
            for (ImageInfo imgInfo : this.images) {
                d.writeByte(imgInfo.ID);
                if (this.new1 == 0 || this.new1 == 1) {
                    d.writeByte(imgInfo.x);
                    d.writeByte(imgInfo.y);
                } else {
                    d.writeShort(imgInfo.x);
                    d.writeShort(imgInfo.y);
                }
                d.writeByte(imgInfo.w);
                d.writeByte(imgInfo.h);
            }
            d.writeShort(this.frames.size());
            for (Frame frame : this.frames) {
                int lent = frame.idImg.length;
                d.writeByte(lent);
                for (int i = 0; i < lent; i++) {
                    d.writeShort(frame.dx[i]);
                    d.writeShort(frame.dy[i]);
                    d.writeByte(frame.idImg[i]);
                }
            }
            d.writeShort(this.run.length);
            for (short r : this.run) {
                d.writeShort(r);
            }
            this.data = bos.toByteArray();
            d.close();
            bos.close();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }
}
