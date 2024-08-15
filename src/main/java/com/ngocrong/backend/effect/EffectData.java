package com.ngocrong.backend.effect;

import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EffectData {
    private static Logger logger = Logger.getLogger(EffectData.class);

    public byte[][] img = new byte[4][];
    public byte[] data;
    public ImageInfo[] imgInfo;
    public Frame[] frame;
    public short[] arrFrame;
    public int ID;
    public int width;
    public int height;

    public void createData() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(imgInfo.length);
            for (ImageInfo img : imgInfo) {
                dos.writeByte(img.ID);
                dos.writeByte(img.x);
                dos.writeByte(img.y);
                dos.writeByte(img.w);
                dos.writeByte(img.h);
            }
            dos.writeShort(frame.length);
            for (Frame f : frame) {
                int lent = f.idImg.length;
                dos.writeByte(lent);
                for (int i = 0; i < lent; i++) {
                    dos.writeShort(f.dx[i]);
                    dos.writeShort(f.dy[i]);
                    dos.writeByte(f.idImg[i]);
                }
            }
            dos.writeShort(arrFrame.length);
            for (short s : arrFrame) {
                dos.writeShort(s);
            }
            data = bos.toByteArray();
            dos.close();
            bos.close();

            for (int i = 0; i < img.length; i++) {
                img[i] = Utils.getFile("resources/image/" + (i + 1) + "/effect/" + this.ID + ".png");
            }
            imgInfo = null;
            frame = null;
            arrFrame = null;
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }
}
