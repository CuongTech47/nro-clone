package com.ngocrong.backend.shop;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.item.ItemTemplate;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;

import java.util.ArrayList;

public class Tab {
    private String tabName;
    private ArrayList<ItemTemplate> TRAI_DAT = new ArrayList<>();
    private ArrayList<ItemTemplate> NAMEC = new ArrayList<>();
    private ArrayList<ItemTemplate> XAYDA = new ArrayList<>();
    private ArrayList<ItemTemplate> ALL = new ArrayList<>();
    private int type;


    public void addItem(ItemTemplate item) {
        switch (item.getGender()) {
            case 0:
                TRAI_DAT.add(item);
                break;

            case 1:
                NAMEC.add(item);
                break;

            case 2:
                XAYDA.add(item);
                break;

            default:
                ALL.add(item);
                break;
        }
    }

    public ArrayList<ItemTemplate> getListItem(Char _c) {
        ArrayList<ItemTemplate> list = new ArrayList<>();
        if (this.type == 0) {
            switch (_c.getGender()) {
                case 0:
                    list.addAll(TRAI_DAT);
                    break;

                case 1:
                    list.addAll(NAMEC);
                    break;

                case 2:
                    list.addAll(XAYDA);
                    break;
            }
        } else if (this.type == 1) {
            list.addAll(TRAI_DAT);
            list.addAll(NAMEC);
            list.addAll(XAYDA);
        }
        Server server = DragonBall.getInstance().getServer();
        for (ItemTemplate item : ALL) {
            if (item.id == 293) {
                if (_c.magicTree == null || _c.magicTree.level <= 1) {
                    continue;
                }
                int mLevel = _c.magicTree.level;
                if (mLevel > 2) {
                    int[] IDS = {294, 295, 296, 297, 298, 299, 596, 597};
                    int id = IDS[mLevel - 3];
                    boolean isNew = item.isNew;
                    int buyGem = item.buyGem;
                    long buyGold = item.buyGold;
                    item = server.iTemplates.get(id);
                    item.isNew = isNew;
                    item.buyGem = buyGem;
                    item.buyGold = buyGold;
                    if (mLevel > 6) {
                        item.buyGem *= 3;
                        item.buyGold *= 3;
                    }
                }
            }
            list.add(item);
        }
        return list;
    }
}
