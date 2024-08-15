package com.ngocrong.backend.shop;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.consts.ItemName;
import com.ngocrong.backend.item.ItemOption;
import com.ngocrong.backend.item.ItemTemplate;
import com.ngocrong.backend.model.Npc;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.server.mysql.MySQLConnect;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.skill.SkillBook;
import com.ngocrong.backend.skill.Skills;
import lombok.Data;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

@Data
public class Shop implements Cloneable{
    private static Logger logger = Logger.getLogger(Shop.class);
    private static ArrayList<Shop> shops = new ArrayList<>();

    private ArrayList<Tab> tabs = new ArrayList<>();
    private int typeShop;
    private String tableName;
    private int npcId;
    private Npc npc;


    public static void addShop(Shop shop) {
        shops.add(shop);
    }

    public static Shop getShopSkill(Char _c) {
        Shop shop = getShop(16);
        if (shop == null || shop.tabs == null) {
            return null;  // Kiểm tra null để tránh lỗi NullPointerException
        }

        Shop shopSkill = new Shop();
        shopSkill.setTypeShop(1);

        int numOfTabs = Math.min(3, shop.tabs.size());  // Đảm bảo chỉ lặp qua 3 tab hoặc ít hơn nếu shop.tabs có ít hơn 3 tab

        for (int i = 0; i < numOfTabs; i++) {
            Tab tab1 = shop.tabs.get(i);
            Tab tab2 = new Tab();

            tab2.setTabName(tab1.getTabName());
            tab2.setType(tab1.getType());

            ArrayList<ItemTemplate> list = tab1.getListItem(_c);
            if (list != null) {
                for (ItemTemplate item : list) {
                    SkillBook book = Skills.getSkillBook(item.id);
                    if (book != null) {
                        int skillID = book.id;
                        int level = book.level;
                        Skill skill = _c.getSkillByID(skillID);

                        if (skill == null || skill.point < level) {
                            if (item.powerRequire == 0) {
                                skill = Skills.getSkill((byte) skillID, (byte) level);
                                if (skill != null) {
                                    item.powerRequire = skill.powerRequire;
                                }
                            }
                            tab2.addItem(item);
                        }
                    }
                }
            }

            shopSkill.addTab(tab2);
        }

        return shopSkill;
    }


    public void addTab(Tab tab) {
        this.tabs.add(tab);
    }

    private static Shop getShop(int npcId) {
        if (npcId == 7 || npcId == 8 || npcId == 9) {
            npcId = -1;
        }
        for (Shop shop : shops) {
            if (shop.npcId == npcId) {
                return shop;
            }
        }
        return null;
    }

    public ArrayList<ItemTemplate> getListItem(Char _c) {
        ArrayList<ItemTemplate> list = new ArrayList<>();
        for (Tab tab : this.tabs) {
            list.addAll(tab.getListItem(_c));
        }
        return list;
    }

    public void init() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Statement stmt = MySQLConnect.getConnection().createStatement();
            ResultSet res = stmt.executeQuery("SELECT * FROM " + this.tableName);
            while (res.next()) {
                int itemId = res.getInt("item_id");
                long gold = 0;
                int gem = 0;
                short iconSpec = 0;
                int buySpec = 0;
                if (typeShop == 0) {
                    gold = res.getLong("buy_gold");
                    gem = res.getInt("buy_gem");
                }
                if (typeShop == 3) {
                    iconSpec = res.getShort("icon_special");
                    buySpec = res.getInt("buy_special");
                }
                boolean isNew = res.getBoolean("new");
                boolean isPreview = res.getBoolean("preview");
                int tab = res.getInt("tab");
                int expired = res.getInt("expired");
                ArrayList<ItemOption> options = new ArrayList<>();
                JSONArray jArr = new JSONArray(res.getString("options"));
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject obj = jArr.getJSONObject(i);
                    int id = obj.getInt("id");
                    int param = obj.getInt("param");
                    options.add(new ItemOption(id, param));
                }
                ItemTemplate template = server.iTemplates.get(itemId);
                if (template != null) {
                    if (template.id == ItemName.THOI_VANG) {
                        template.resalePrice = gold;
                    }
                    template.buyGold = gold;
                    template.buyGem = gem;
                    template.iconSpec = iconSpec;
                    template.buySpec = buySpec;
                    template.isNew = isNew;
                    template.isPreview = isPreview;
                    template = template.clone();
                    template.setOptions(expired, options);
                    addItem(template, tab);
                }
            }
            res.close();
            stmt.close();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    private void addItem(ItemTemplate item, int tab) {
        this.tabs.get(tab).addItem(item);
    }
}
