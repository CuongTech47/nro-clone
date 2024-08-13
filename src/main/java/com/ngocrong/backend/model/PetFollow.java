package com.ngocrong.backend.model;

import lombok.Data;

@Data
public class PetFollow {
    private short smallID;
    private byte img;
    private byte[] frame;
    private short w;
    private short h;
}
