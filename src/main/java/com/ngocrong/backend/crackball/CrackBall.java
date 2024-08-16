package com.ngocrong.backend.crackball;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.consts.Cmd;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.item.ItemOption;
import com.ngocrong.backend.item.ItemTemplate;
import com.ngocrong.backend.lib.RandomCollection;
import com.ngocrong.backend.network.Message;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.SQLStatement;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.server.mysql.MySQLConnect;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class CrackBall {
    private static Logger logger = Logger.getLogger(CrackBall.class);
    public static HashMap<Byte, RandomCollection> randoms;
    public static final byte VONG_QUAY_THUONG = 0;
    public static final byte VONG_QUAY_DAC_BIET = 1;
    public static final byte CAPSULE_KI_BI = 2;

    public static void loadItem() {
        try {
            randoms = new HashMap<>();
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(SQLStatement.INIT_LUCKY_WHEEL);
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int itemId = rs.getInt("item_id");
                    int quantity = rs.getInt("quantity");
                    String name = rs.getString("name");
                    double rate = rs.getDouble("rate");
                    int expire = rs.getInt("expire");
                    byte type = rs.getByte("type");
                    Reward rw = new Reward();
                    rw.setId(id);
                    rw.setName(name);
                    rw.setItemId(itemId);
                    rw.setQuantity(quantity);
                    rw.setRate(rate);
                    rw.setExpire(expire);
                    RandomCollection<Reward> rd = randoms.get(type);
                    if (rd == null) {
                        rd = new RandomCollection<>();
                        randoms.put(type, rd);
                    }
                    rd.add(rate, rw);
                }
            } finally {
                rs.close();
                ps.close();
            }
        } catch (SQLException ex) {
            logger.error("failed!", ex);
        }
    }

    private int[] imgs;
    private byte typePrice;
    private int price;
    private int idTicket;
    private byte type;
    private Char player;
    private byte quantity;
    private volatile RandomCollection<Reward> rd;

    public void setRandom() {
        this.rd = randoms.get(this.type);
    }

    public void show() {
        try {
            Message ms = new Message(Cmd.LUCKY_ROUND);
            DataOutputStream ds = ms.getWriter();
            ds.writeByte(0);
            ds.writeByte(imgs.length);
            for (int idImage : this.imgs) {
                ds.writeShort(idImage);
            }
            ds.writeByte(this.typePrice);
            ds.writeInt(this.price);
            ds.writeShort(this.idTicket);
            ds.flush();
            player.service.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void result() {
        try {
            Item itm = player.getItemInBag(idTicket);
            int numTicket = 0;
            if (itm != null) {
                numTicket = itm.quantity;
            }
            int q = this.quantity;
            if (numTicket > q) {
                numTicket = q;
            }
            q -= numTicket;
            int price = q * this.price;
            if (typePrice == 0) {
                if (player.getGold() < price) {
                    return;
                }
                player.addGold(-price);
            }
            if (typePrice == 1) {
                if (player.getTotalGem() < price) {
                    return;
                }
                player.subDiamond(price);
            }
            if (itm != null) {
                player.removeItem(itm.indexUI, numTicket);
            }
            Server server = DragonBall.getInstance().getServer();
            Message ms = new Message(Cmd.LUCKY_ROUND);
            DataOutputStream ds = ms.getWriter();
            ds.writeByte(1);
            ds.writeByte(quantity);
            for (int i = 0; i < quantity; i++) {
                Reward rw = rd.next();
                ItemTemplate itemTemplate = server.iTemplates.get(rw.getItemId());
                Item item = new Item(itemTemplate.id);
                item.setDefaultOptions();
                item.quantity = rw.getQuantity();
                int expire = rw.getExpire();
                if (expire != -1) {
                    item.addItemOption(new ItemOption(93, expire));
                }
                player.boxCrackBall.add(item);
                ds.writeShort(itemTemplate.iconID);
            }
            ds.flush();
            player.service.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }
}
