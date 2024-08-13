package com.ngocrong.backend.crackball;

import com.ngocrong.backend.lib.RandomCollection;
import com.ngocrong.backend.server.SQLStatement;
import com.ngocrong.backend.server.mysql.MySQLConnect;
import org.apache.log4j.Logger;

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
}
