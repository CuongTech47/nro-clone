package com.ngocrong.backend.effect;

public class AmbientEffect {
    public int id;
    public int param;
    public long maintain;
    public long time;

    public AmbientEffect(int id, int param, long maintain) {
        this.id = id;
        this.param = param;
        this.maintain = maintain;
        this.time = System.currentTimeMillis();
    }

    public int[] getItemOption() {
        int oId = this.id;
        int oParam = this.param;
        switch (this.id) {
            case 24:
                oId = 16;
                break;

            case 111:
                oParam = 80;
                break;

            case 117:
                oId = 50;
                break;
        }
        return new int[]{oId, oParam};
    }
}
