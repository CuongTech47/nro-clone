package com.ngocrong.backend.network;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.clan.ClanImage;
import com.ngocrong.backend.consts.Cmd;
import com.ngocrong.backend.consts.Info;
import com.ngocrong.backend.effect.Effect;
import com.ngocrong.backend.effect.EffectData;
import com.ngocrong.backend.item.ItemMap;
import com.ngocrong.backend.item.ItemTime;
import com.ngocrong.backend.lib.KeyValue;
import com.ngocrong.backend.mob.Mob;
import com.ngocrong.backend.mob.MobTemplate;
import com.ngocrong.backend.model.BgItem;
import com.ngocrong.backend.model.Caption;
import com.ngocrong.backend.model.Npc;
import com.ngocrong.backend.model.Waypoint;
import com.ngocrong.backend.server.Config;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.map.tzone.TMap;
import com.ngocrong.backend.map.tzone.Zone;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Service implements IService{
    private static final Logger logger = Logger.getLogger(Service.class);
    public static int[][] PET = {{281, 361, 351}, {512, 513, 536}, {514, 515, 537}};

    private Session session;
    protected Char player;
    private byte[] small, bg;
    public Service(Session session) {
        this.session = session;
    }

    public Service(Char deTu) {
        this.player = deTu;
    }

    @Override
    public void setChar(Char _char) {
        this.player = _char;
    }

    @Override
    public void close() {
        small = null;
        bg = null;
        session = null;
        player = null;
    }

    @Override
    public void setResource() {
        Server server = DragonBall.getInstance().getServer();
        try {
            small = server.smallVersion[session.getZoomLevel() -1];
            bg = server.backgroundVersion[session.getZoomLevel() - 1];
        }catch (NullPointerException ex) {
            logger.error("set resource err: " + ex.getMessage(), ex);
        }
    }





    public void loadHP() {
        try {
            Message message = messageSubCommand(Cmd.ME_LOAD_HP);

            DataOutputStream dos = message.getWriter();

           dos.writeLong(player.getCharacterInfo().getHp());
           dos.flush();
            sendMessage(message);

        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void loadMP() {
        try {
            Message message = messageSubCommand(Cmd.ME_LOAD_MP);
            DataOutputStream dos = message.getWriter();
            dos.writeLong(player.getCharacterInfo().getMp());
            dos.flush();
            sendMessage(message);
        }catch (IOException ex){
            logger.error("failed!", ex);
        }
    }

    public void sendMessage(Message message) {
        if (player != null && !player.isHuman()) {
            return;
        }
        if (small != null) {
            this.session.sendMessage(message);
        }
    }

    public void setItemTime(ItemTime item) {
        try {
            Message mss = new Message(Cmd.ITEM_TIME);
            DataOutputStream ds = mss.getWriter();
            ds.writeShort(item.icon);
            int seconds = item.seconds;
            if (seconds > Short.MAX_VALUE) {
                seconds = Short.MAX_VALUE;
            }
            ds.writeShort(seconds);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void loadPoint() {
        try {
            Message mss = new Message(Cmd.ME_LOAD_POINT);
            DataOutputStream ds = mss.getWriter();
            ds.writeLong(player.getCharacterInfo().getBaseHP());
            ds.writeLong(player.getCharacterInfo().getBaseMP());
            ds.writeLong(player.getCharacterInfo().getBaseDamage());
            ds.writeLong(player.getCharacterInfo().getFullHP());
            ds.writeLong(player.getCharacterInfo().getFullMP());
            ds.writeLong(player.getCharacterInfo().getHp());
            ds.writeLong(player.getCharacterInfo().getMp());
            ds.writeByte(player.getCharacterInfo().getSpeed());
            ds.writeByte(Info.HP_FROM_1000_TIEM_NANG);
            ds.writeByte(Info.MP_FROM_1000_TIEM_NANG);
            ds.writeByte(Info.DAMAGE_FROM_1000_TIEM_NANG);
            ds.writeLong(player.getCharacterInfo().getFullDamage());
            ds.writeInt(player.getCharacterInfo().getFullDefense());
            ds.writeByte(player.getCharacterInfo().getFullCritical());
            ds.writeLong(player.getCharacterInfo().getPotential());
            ds.writeShort(Info.EXP_FOR_ONE_ADD);
            ds.writeShort(player.getCharacterInfo().getBaseDefense());
            ds.writeByte(player.getCharacterInfo().getBaseCritical());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public static Message messageSubCommand(int command) {
        try {
            Message message = new Message(Cmd.SUB_COMMAND);
            message.getWriter().writeByte(command);
            return message;

        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


    public void serverMessage(String s) {
        try {
            Message ms = new Message(Cmd.SERVER_MESSAGE);
            DataOutputStream ds = ms.getWriter();
            ds.writeUTF(s);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    public void setMapInfo() {
        try {
            Zone z = player.getZone();
            if (z == null) {
                return;
            }
            TMap map = z.map;
            if (map == null) {
                return;
            }
            Message message = new Message(Cmd.MAP_INFO);
            DataOutputStream dos = message.getWriter();
            dos.writeByte(map.mapID);
            dos.writeByte(map.planet);
            dos.writeByte(map.tileID);
            dos.writeByte(map.bgID);
            dos.writeByte(map.typeMap);
            dos.writeUTF(map.name);
            dos.writeByte(z.zoneID);
            dos.writeShort(player.getX());
            dos.writeShort(player.getY());
            dos.writeByte(map.waypoints.length);

            for (Waypoint w : map.waypoints) {
                dos.writeShort(w.minX);
                dos.writeShort(w.minY);
                dos.writeShort(w.maxX);
                dos.writeShort(w.maxY);
                dos.writeBoolean(w.isEnter);
                dos.writeBoolean(w.isOffline);
                dos.writeUTF(w.name);
            }

            List<Mob> mobs = z.getListMob();
            dos.writeByte(mobs.size());

            for (Mob mob : mobs) {
                dos.writeInt(mob.mobId);
                dos.writeBoolean(mob.isDisable);
                dos.writeBoolean(mob.isDontMove);
                dos.writeBoolean(mob.isFire);
                dos.writeBoolean(mob.isIce);
                dos.writeBoolean(mob.isWind);
                dos.writeByte(mob.templateId);
                dos.writeByte(mob.sys);
                dos.writeLong(mob.hp);
                dos.writeByte(mob.level);
                dos.writeLong(mob.maxHp);
                dos.writeShort(mob.x);
                dos.writeShort(mob.y);
                dos.writeByte(mob.status);
                dos.writeByte(mob.levelBoss);
                dos.writeBoolean(mob.isBoss);
            }
            dos.writeByte(0);
            List<Npc> npcs = z.getListNpc(player);
            dos.writeByte(npcs.size());
            for (Npc npc : npcs) {
                dos.writeByte(npc.status);
                dos.writeShort(npc.x);
                dos.writeShort(npc.y);
                dos.writeByte(npc.templateId);
                dos.writeShort(npc.avatar);
            }

            List<ItemMap> items = z.getListItemMap(player.getTaskMain());
            dos.writeByte(items.size());
            for (ItemMap item : items) {
                dos.writeShort(item.id);
                dos.writeShort(item.item.id);
                dos.writeShort(item.x);
                dos.writeShort(item.y);
                dos.writeInt(item.playerID);
                if (item.playerID == -2) {
                    dos.writeShort(item.r);
                }
            }
            dos.writeShort(map.positionBgItems.length);
            for (BgItem bg : map.positionBgItems) {
                dos.writeShort(bg.id);
                dos.writeShort(bg.x);
                dos.writeShort(bg.y);
            }
            dos.writeShort(map.effects.length);
            for (KeyValue<String, String> k : map.effects) {
                dos.writeUTF(k.getKey());
                dos.writeUTF(k.getValue());
            }
            dos.writeByte(map.bgType);
            dos.writeByte(player.getTeleport());
            dos.writeBoolean(map.isDoubleMap());
            dos.flush();
            sendMessage(message);
            message.cleanup();

        }catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void openUIConfirm(int npcTemplateID, String say, short avatar, ArrayList<KeyValue> menus) {
        try {
            Message mss = new Message(Cmd.OPEN_UI_CONFIRM);
            DataOutputStream ds = mss.getWriter();
            ds.writeShort(npcTemplateID);
            ds.writeUTF(say);
            ds.writeByte(menus.size());
            for (KeyValue<Integer, String> keyValue : menus) {
                ds.writeUTF(keyValue.getValue());
            }
            ds.writeShort(avatar);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void clearMap() {
        sendMessage(new Message(Cmd.MAP_CLEAR));
    }

    public void dialogMessage(String s) {
        try {
            Message ms = new Message(Cmd.DIALOG_MESSAGE);
            DataOutputStream ds = ms.getWriter();
            ds.writeUTF(s);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    public void updateMap() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Message ms = messageNotMap(Cmd.UPDATE_MAP);
            DataOutputStream ds = ms.getWriter();
            ds.write(server.CACHE_MAP);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    private Message messageNotMap(int command) {
        try {
            Message ms = new Message(Cmd.NOT_MAP);
            ms.getWriter().writeByte(command);
            return ms;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void updateSkill() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Message ms = messageNotMap(Cmd.UPDATE_SKILL);
            DataOutputStream ds = ms.getWriter();
            ds.write(server.CACHE_SKILL_TEMPLATE);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    public void updateItem(byte b) {
        try {
            Server server = DragonBall.getInstance().getServer();
            Message ms = messageNotMap(Cmd.UPDATE_ITEM);
            DataOutputStream ds = ms.getWriter();
            ds.write(server.CACHE_ITEM[b]);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    public void requestMapTemplate(Message mss) {
        try {
            int map = mss.getReader().readUnsignedByte();
            byte[] ab = player.zone.map.mapData;
            Message mssage = messageNotMap(Cmd.REQUEST_MAPTEMPLATE);
            DataOutputStream ds = mssage.getWriter();
            ds.write(ab);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void serverMessage2(String s) {
        try {
            Message ms = messageNotMap(35);
            DataOutputStream ds = ms.getWriter();
            ds.writeUTF(s);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    public void openUISay(int npcId, String say, short avatar) {
        try {
            Message mss = new Message(Cmd.OPEN_UI_SAY);
            DataOutputStream ds = mss.getWriter();
            ds.writeShort(npcId);
            ds.writeUTF(say);
            ds.writeShort(avatar);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void sendSmallVersion() {
        try {
            Message mss = new Message(Cmd.SMALLIMAGE_VERSION);
            DataOutputStream ds = mss.getWriter();
            ds.writeShort(small.length);
            for (byte ver : small) {
                ds.writeByte(ver);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }
    public void sendBGSmallVersion() {
        try {
            Message mss = new Message(Cmd.BGITEM_VERSION);
            DataOutputStream ds = mss.getWriter();
            ds.writeShort(bg.length);
            for (byte ver : bg) {
                ds.writeByte(ver);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }
    public void sendResVersion() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Message mss = new Message(Cmd.GET_IMAGE_SOURCE);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(0);
            ds.writeInt(server.resVersion[session.getZoomLevel() - 1]);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }
    public void download(String path) {
        try {
            String str = path.replace("\\", "/").replace("resources/data/" + session.getZoomLevel(), "");
            str = Utils.cutPng(str);
            Message mss = new Message(Cmd.GET_IMAGE_SOURCE);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(2);
            ds.writeUTF(str);
            byte[] ab = Utils.getFile(path);
            ds.writeInt(ab.length);
            ds.write(ab);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("download error", ex);
        }
    }

    public void sendVersion() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            Message mss = messageNotMap(Cmd.UPDATE_VERSION);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(config.getDataVersion());
            ds.writeByte(config.getMapVersion());
            ds.writeByte(config.getSkillVersion());
            ds.writeByte(config.getItemVersion());
            ds.writeByte(server.powers.size());
            for (long sm : server.powers) {
                ds.writeLong(sm);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void size(int size) {
        try {
            Message mss = new Message(Cmd.GET_IMAGE_SOURCE);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(1);
            ds.writeShort(size);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void downloadOk() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Message mss = new Message(Cmd.GET_IMAGE_SOURCE);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(3);
            ds.writeInt(server.resVersion[session.getZoomLevel() - 1]);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void setLinkListServer() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            Message ms = messageNotLogin(Cmd.CLIENT_INFO);
            DataOutputStream ds = ms.getWriter();
            ds.writeUTF(config.getListServers());
            ds.writeByte(0);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    private Message messageNotLogin(byte clientInfo) {
        try {
            Message ms = new Message(Cmd.NOT_LOGIN);
            ms.getWriter().writeByte(clientInfo);
            return ms;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void updateData() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            Message ms = new Message(Cmd.UPDATE_DATA);
            DataOutputStream ds = ms.getWriter();
            ds.writeByte(config.getDataVersion());
            ds.writeInt(server.CACHE_DART.length);
            ds.write(server.CACHE_DART);
            ds.writeInt(server.CACHE_ARROW.length);
            ds.write(server.CACHE_ARROW);
            ds.writeInt(server.CACHE_EFFECT.length);
            ds.write(server.CACHE_EFFECT);
            ds.writeInt(server.CACHE_IMAGE.length);
            ds.write(server.CACHE_IMAGE);
            ds.writeInt(server.CACHE_PART.length);
            ds.write(server.CACHE_PART);
            ds.writeInt(server.CACHE_SKILL.length);
            ds.write(server.CACHE_SKILL);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    public void createChar() {
        sendMessage(new Message(Cmd.CREATE_PLAYER));
    }

    public void requestIcon(Message ms) {
        try {
            int id = ms.getReader().readInt();
            if (id < 0 || id >= small.length) {
                return;
            }
            byte[] ab = Utils.getFile(String.format("resources/image/%d/small/Small%d.png", session.getZoomLevel(), id));
            if (ab == null) {
                return;
            }
            Message mss = new Message(Cmd.REQUEST_ICON);
            DataOutputStream ds = mss.getWriter();
            ds.writeInt(id);
            ds.writeInt(ab.length);
            ds.write(ab);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("request icon error", ex);
        }
    }

    public void getBag(Message ms) {
        try {
            Server server = DragonBall.getInstance().getServer();
            byte id = ms.getReader().readByte();
            ClanImage clanImage = server.getClanImageByID(id);
            if (clanImage != null) {
                Message mss = new Message(Cmd.GET_BAG);
                DataOutputStream ds = mss.getWriter();
                ds.writeByte(id);
                int lent = clanImage.idImages.length;
                ds.writeByte(lent - 1);
                for (int i = 1; i < lent; i++) {
                    ds.writeShort(clanImage.idImages[i]);
                }
                ds.flush();
                sendMessage(mss);
                mss.cleanup();
            }
        } catch (IOException ex) {
            logger.error("get bag err", ex);
        }
    }

    public void requestMobTemplate(Message ms) {
        try {
            int id = ms.getReader().readByte();
            MobTemplate tm = Mob.getMobTemplate(id);
            if (tm != null && tm.isData && tm.mobTemplateId != 70) {
                byte[] data = tm.data;
                byte[] img = tm.img[session.getZoomLevel() - 1];
                Message mss = new Message(Cmd.REQUEST_NPCTEMPLATE);
                DataOutputStream ds = mss.getWriter();
                ds.writeByte(tm.mobTemplateId);
                ds.writeByte(tm.new1);
                ds.writeInt(data.length);
                ds.write(data);
                ds.writeInt(img.length);
                ds.write(img);
                if (tm.dataBoss != null) {
                    ds.writeByte(1);
                    ds.write(tm.dataBoss);
                } else {
                    ds.writeByte(0);
                }
                ds.flush();
                sendMessage(mss);
                mss.cleanup();
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void updateCaption(Message ms) {
        try {
            byte planet = ms.getReader().readByte();
            ArrayList<String> captions = (ArrayList<String>) Caption.getCaption(planet);
            if (captions != null) {
                Message mss = new Message(Cmd.UPDATE_CAPTION);
                DataOutputStream ds = mss.getWriter();
                ds.writeByte(captions.size());
                for (String cap : captions) {
                    ds.writeUTF(cap);
                }
                ds.flush();
                sendMessage(mss);
                mss.cleanup();
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }

    }

    public void requestEffectData(Message ms) {
        try {
            short id = ms.getReader().readShort();
            EffectData effData = Effect.getEfDataById(id);
            if (effData != null) {
                Message mss = new Message(Cmd.GET_EFFDATA);
                DataOutputStream ds = mss.getWriter();
                ds.writeShort(effData.ID);
                ds.writeInt(effData.data.length);
                ds.write(effData.data);
                byte[] ab = effData.img[session.getZoomLevel() - 1];
                ds.writeInt(ab.length);
                ds.write(ab);
                ds.flush();
                sendMessage(mss);
                mss.cleanup();
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void resetPoint() {
        try {
            Message msg = new Message(Cmd.RESET_POINT);
            DataOutputStream ds = msg.getWriter();
            ds.writeShort(player.getX());
            ds.writeShort(player.getY());
            ds.flush();
            sendMessage(msg);
            msg.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void requestBackgroundItem(Message ms) {
        try {
            int id = ms.getReader().readShort();
            if (id < 0 || id >= bg.length) {
                return;
            }
            byte[] ab = Utils.getFile(String.format("resources/image/%d/background/%d.png", session.getZoomLevel(), id));
            if (ab == null) {
                return;
            }
            Message mss = new Message(Cmd.BACKGROUND_TEMPLATE);
            DataOutputStream ds = mss.getWriter();
            ds.writeShort(id);
            ds.writeInt(ab.length);
            ds.write(ab);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }
}
