package com.ngocrong.backend.map.tzone;

import com.ngocrong.backend.consts.ItemName;
import com.ngocrong.backend.consts.MapName;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.item.ItemMap;
import com.ngocrong.backend.task.Task;

import java.util.List;

public class Home extends MapSingle{
    public ItemMap itemDuiGa;
    public Home(TMap map, int zoneId) {
        super(map, zoneId);
        initDuiGa();
    }

    public void initDuiGa() {
        ItemMap item = new ItemMap(this.autoIncrease++);
        item.isPickedUp = false;
        item.throwTime = System.currentTimeMillis();
        item.item = new Item(ItemName.DUI_GA_NUONG);
        item.playerID = -1;
        item.item.setDefaultOptions();
        item.y = 318;
        if (map.mapID == MapName.NHA_GOHAN || map.mapID == MapName.NHA_BROLY) {
            item.x = 627;
        } else {
            item.x = 49;
        }
        itemDuiGa = item;
    }

    @Override
    public List<ItemMap> getListItemMap(Task... tasks) {
        List<ItemMap> items = super.getListItemMap();
        if (tasks.length > 0) {
            for (Task task : tasks) {
                if (task != null) {
                    if (itemDuiGa != null && task.id > 2) {
                        items.add(itemDuiGa);
                    }
                }
            }
        }
        return items;
    }


    public ItemMap findItemMapByID(int id) {
        if (itemDuiGa != null && itemDuiGa.id == id) {
            return itemDuiGa;
        }
        lockItemMap.readLock().lock();
        try {
            for (ItemMap item : items) {
                if (item.id == id) {
                    return item;
                }
            }
            return null;
        } finally {
            lockItemMap.readLock().unlock();
        }
    }

}
