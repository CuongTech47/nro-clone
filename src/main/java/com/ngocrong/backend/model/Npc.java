package com.ngocrong.backend.model;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Npc {
    private static Logger logger = Logger.getLogger(Npc.class);
    public NpcTemplate template;
    public int id;
    public int templateId;
    public short x;
    public short y;
    public byte status;
    public short avatar;
    public static ArrayList<NpcTemplate> vNpcTemplate = new ArrayList<>();
    public static byte[] data;

    public Npc() {
    }
    public Npc(int id, byte status, int templateId, short x, short y, short avatar) {
        this.id = id;
        this.status = status;
        this.templateId = templateId;
        this.x = x;
        this.y = y;
        this.avatar = avatar;
        this.template = vNpcTemplate.get(templateId);
    }

    public Npc clone() {
        Npc npc = new Npc();
        npc.id = this.id;
        npc.status = this.status;
        npc.x = this.x;
        npc.y = this.y;
        npc.avatar = this.avatar;
        npc.templateId = this.templateId;
        npc.template = this.template;
        return npc;
    }

    public static void addNpcTemplate(NpcTemplate npc) {
        vNpcTemplate.add(npc);
    }

    public static void createData() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(vNpcTemplate.size());
            for (NpcTemplate npc : vNpcTemplate) {
                dos.writeUTF(npc.name);
                dos.writeShort(npc.headId);
                dos.writeShort(npc.bodyId);
                dos.writeShort(npc.legId);
                dos.writeByte(npc.menu.length);
                for (String[] m : npc.menu) {
                    dos.writeByte(m.length);
                    for (String str : m) {
                        dos.writeUTF(str);
                    }
                }
            }
            data = bos.toByteArray();
            dos.close();
            bos.close();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }
}
