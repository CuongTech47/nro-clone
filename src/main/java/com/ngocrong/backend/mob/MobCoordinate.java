package com.ngocrong.backend.mob;

import lombok.Data;

@Data
public class MobCoordinate {
    private byte templateID;
    private short x;
    private short y;
}
