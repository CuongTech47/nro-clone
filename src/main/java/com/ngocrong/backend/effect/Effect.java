package com.ngocrong.backend.effect;

import java.util.ArrayList;

public class Effect {
    public static final int NEAR_PLAYER = 0;

    public static final int LOOP_NORMAL = 1;

    public static final int LOOP_TRANS = 2;

    public static final int BACKGROUND = 3;

    public static final int CHAR = 4;

    public static final int FIRE_TD = 0;

    public static final int BIRD = 1;

    public static final int FIRE_NAMEK = 2;

    public static final int FIRE_SAYAI = 3;

    public static final int FROG = 5;

    public static final int CA = 4;

    public static final int ECH = 6;

    public static final int TACKE = 7;

    public static final int RAN = 8;

    public static final int KHI = 9;

    public static final int GACON = 10;

    public static final int DANONG = 11;

    public static final int DANBUOM = 12;

    public static final int QUA = 13;

    public static final int THIENTHACH = 14;

    public static final int CAVOI = 15;

    public static final int NAM = 16;

    public static final int RONGTHAN = 17;

    public static final int BUOMBAY = 26;

    public static final int KHUCGO = 27;

    public static final int DOIBAY = 28;

    public static final int CONMEO = 29;

    public static final int LUATAT = 30;

    public static final int ONGCONG = 31;

    public static final int KHANGIA1 = 42;

    public static final int KHANGIA2 = 43;

    public static final int KHANGIA3 = 44;

    public static final int KHANGIA4 = 45;

    public static final int KHANGIA5 = 46;

    public static ArrayList<EffectData> effects;

    public static void addEffData(EffectData eff) {
        effects.add(eff);
    }

    public static EffectData getEfDataById(short id) {
        for (EffectData eff : effects) {
            if (eff.ID == id) {
                return eff;
            }
        }
        return null;
    }
}
