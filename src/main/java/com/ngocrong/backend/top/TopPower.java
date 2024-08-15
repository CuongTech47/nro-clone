package com.ngocrong.backend.top;

import com.ngocrong.backend.server.mysql.MySQLConnect;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TopPower extends Top {
    private static Logger logger = Logger.getLogger(TopPower.class);
    public TopPower(int id, byte type, String name, byte limit) {
        super(id, type, name, limit);
    }
    @Override
    public void load() {
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement("SELECT `id`, `name`, `head2`, `body`, `leg`, CAST(JSON_UNQUOTE(JSON_EXTRACT(info,\"$.power\")) AS INT) AS power FROM `players` ORDER BY power DESC LIMIT 100;");
            ResultSet rs = ps.executeQuery();
            try {
                int i = 0;
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    short head = rs.getShort("head2");
                    short body = rs.getShort("body");
                    short leg = rs.getShort("leg");
                    long power = rs.getLong("power");
                    TopInfo info = new TopInfo();
                    info.playerID = id;
                    info.name = name;
                    info.head = head;
                    info.body = body;
                    info.leg = leg;
                    info.info = String.format("Sức mạnh: %s", Utils.formatNumber(power));
                    info.info2 = "";
                    info.score = power;
                    i++;
                    elements.add(info);
                    if (i >= limit) {
                        break;
                    }
                }
            } finally {
                rs.close();
                ps.close();
            }
            update();
            updateLowestScore();
        } catch (Exception ex) {
            logger.debug("failed", ex);
        }
    }
}
