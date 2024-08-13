package com.ngocrong.backend.mob;

import com.ngocrong.backend.effect.Frame;
import com.ngocrong.backend.effect.ImageInfo;
import org.apache.log4j.Logger;

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
}
