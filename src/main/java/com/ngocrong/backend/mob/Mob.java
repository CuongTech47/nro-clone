package com.ngocrong.backend.mob;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.collection.Card;
import com.ngocrong.backend.collection.CardTemplate;
import com.ngocrong.backend.consts.Cmd;
import com.ngocrong.backend.consts.ItemName;
import com.ngocrong.backend.consts.SkillName;
import com.ngocrong.backend.disciple.Disciple;
import com.ngocrong.backend.event.Event;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.item.ItemMap;
import com.ngocrong.backend.item.ItemOption;
import com.ngocrong.backend.item.ItemTime;
import com.ngocrong.backend.map.tzone.Zone;
import com.ngocrong.backend.model.Hold;
import com.ngocrong.backend.model.RandomItem;
import com.ngocrong.backend.network.Message;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.skill.SpecialSkill;
import com.ngocrong.backend.task.TaskText;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Mob {
    public int mobId;
    public int seconds;
    public boolean isDisable;
    public boolean isDontMove;
    public boolean isFire;
    public boolean isIce;
    public boolean isWind, isBoss;
    public byte templateId;

    public short x;
    protected ArrayList<Char> listChar = new ArrayList<>();
    public short y;
    public byte status;
    public byte levelBoss;
    public byte sys;
    public byte level;
    public byte type;
    public long hp, maxHp, hpDefault;
    protected long lastTimeAttack = 0L;
    protected long attackDelay = 2000L;
    public long damage1;
    public long damage2;
    public Zone zone;
    public ArrayList<ItemMap> items = new ArrayList<>();
    public long deadTime;
    public boolean isFreeze, isSleep, isBlind;

    public final ArrayList<ItemTime> vItemTime = new ArrayList<>();
    public Lock lock = new ReentrantLock();
    public boolean isHeld;
    public Hold hold;
    public int timeLive;
    public int percentDamage;
    public boolean isChangeBody;
    public short body;
    public int dameDown;

    public boolean isDead() {
        return this.status == 0;
    }

    public void startDie(long dame, boolean isCrit, Char killer) {
        try {
            try {
                if (this.templateId != MobName.MOC_NHAN) {
                    throwItem(killer);
                }
                this.listChar.clear();
                this.status = 0;
                this.hp = 0;
//                if (levelBoss == 1) {
//                    zone.isHadSuperMob = false;
//                }
//                clearBody();
//                clearEffect();
                this.deadTime = System.currentTimeMillis();
                Message ms = new Message(Cmd.NPC_DIE);
                DataOutputStream ds = ms.getWriter();
                ds.writeInt(this.mobId);
                ds.writeLong(dame);
                ds.writeBoolean(isCrit);
                ds.writeByte(this.items.size());
                for (ItemMap item : this.items) {
                    ds.writeShort(item.id);
                    ds.writeShort(item.item.id);
                    ds.writeShort(item.x);
                    ds.writeShort(item.y);
                    ds.writeInt(item.playerID);
                }
                ds.flush();
                zone.mapService.sendMessage(ms, null);
                ms.cleanup();
                if (killer.isDisciple()) {
                    Disciple disciple = (Disciple) killer;
                    killer = disciple.master;
                }
                if (killer.isBuaThuHut() || killer.isAutoPlay()) {
                    for (ItemMap itemMap : items) {
//                        killer.pickItem(itemMap, 0);
                    }
                }
            } finally {
//                zone.addWaitForRespawn(this);
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }


    private void throwItem (Char _c) {
        if (_c == null) {
            return;
        }
        if (zone == null) {
            return;
        }
        if (_c.getTaskMain() != null) {
            int itemTask = -1;
            if (_c.getTaskMain().id == 2 && _c.getTaskMain().index == 0) {
                int mobTemplateID = (new int[]{MobName.KHUNG_LONG, MobName.LON_LOI, MobName.QUY_DAT})[_c.getGender()];
                if (templateId == mobTemplateID) {
                    itemTask = ItemName.DUI_GA;
                }
            }
            if (_c.getTaskMain().id == 8 && _c.getTaskMain().index == 1) {
                int mobTemplateID = (new int[]{MobName.PHI_LONG_ME, MobName.QUY_BAY_ME, MobName.THAN_LAN_ME})[_c.getGender()];
                if (templateId == mobTemplateID) {
                    if (Utils.nextInt(20) == 0) {
                        itemTask = ItemName.NGOC_RONG_7_SAO;
                        _c.taskNext();
                    } else {
                        _c.service.serverMessage(TaskText.TASK_8_1[_c.getGender()]);
                    }
                }
            }
            if (_c.getTaskMain().id == 14 && _c.getTaskMain().index == 1) {
                int mobTemplateID = (new int[]{MobName.OC_MUON_HON, MobName.OC_SEN, MobName.HEO_XAYDA_ME})[_c.getGender()];
                if (templateId == mobTemplateID) {
                    if (Utils.nextInt(20) == 0) {
                        itemTask = ItemName.TRUYEN_TRANH;
                        _c.service.serverMessage("Bạn đã tìm thấy cuốn truyện rồi, hãy click đôi vào đối tượng để lấy");
                    } else {
                        _c.service.serverMessage(TaskText.TASK_14_1[_c.getGender()]);
                    }
                }
            }
            if (itemTask != -1) {
                ItemMap task = new ItemMap(zone.autoIncrease++);
                Item item = new Item(itemTask);
                item.setDefaultOptions();
                item.quantity = 1;

                task.playerID = Math.abs(_c.getId());
                task.isPickedUp = false;
                task.x = (short) (this.x + Utils.nextInt(-30, 30));
                task.y = zone.map.collisionLand(this.x, this.y);
                task.item = item;
                this.items.add(task);
                zone.addItemMap(task);
            }
        }
        if (this.body == 4132 && isChangeBody) {
            ItemMap chocolate = new ItemMap(zone.autoIncrease++);
            Item item = new Item(ItemName.SOCOLA);
            item.setDefaultOptions();
            item.quantity = 1;
            chocolate.playerID = Math.abs(_c.getId());
            chocolate.isPickedUp = false;
            chocolate.x = (short) (this.x + Utils.nextInt(-30, 30));
            chocolate.y = zone.map.collisionLand(this.x, this.y);
            chocolate.item = item;
            this.items.add(chocolate);
            zone.addItemMap(chocolate);
        }
        if (_c.isMayDo() && zone.map.isFuture()) {
            int rd = Utils.nextInt(20);
            if (rd == 0) {
                ItemMap capsule = new ItemMap(zone.autoIncrease++);
                Item item = new Item(ItemName.VIEN_CAPSULE_KI_BI);
                item.setDefaultOptions();
                item.quantity = 1;
                capsule.playerID = Math.abs(_c.getId());
                capsule.isPickedUp = false;
                capsule.x = (short) (this.x + Utils.nextInt(-30, 30));
                capsule.y = zone.map.collisionLand(this.x, this.y);
                capsule.item = item;
                this.items.add(capsule);
                zone.addItemMap(capsule);
            }
        }
        if (_c.isSetThanLinh() && (zone.map.isFuture() || zone.map.isCold())) {
            int rd = Utils.nextInt(100);
            if (rd == 0) {
                ItemMap food = new ItemMap(zone.autoIncrease++);
                Item item = new Item(RandomItem.FOOD.next());
                item.addItemOption(new ItemOption(30, 0));
                item.quantity = 1;
                food.playerID = Math.abs(_c.getId());
                food.isPickedUp = false;
                food.x = (short) (this.x + Utils.nextInt(-30, 30));
                food.y = zone.map.collisionLand(this.x, this.y);
                food.item = item;
                this.items.add(food);
                zone.addItemMap(food);
            }
        }
        if (_c.isDoSaoPhaLe()) {
            int rd = Utils.nextInt(10000);
            if (rd == 0) {
                ItemMap spl = new ItemMap(zone.autoIncrease++);
                Item item = new Item(RandomItem.SAO_PHA_LE.next());
                item.setDefaultOptions();
                item.quantity = 1;
                spl.playerID = Math.abs(_c.getId());
                spl.isPickedUp = false;
                spl.x = (short) (this.x + Utils.nextInt(-30, 30));
                spl.y = zone.map.collisionLand(this.x, this.y);
                spl.item = item;
                this.items.add(spl);
                zone.addItemMap(spl);
            }
        }
        int itemID = -1;
        Item item = null;
        ItemType type = RandomItem.MOB.next();
        if (type == ItemType.EVENT) {
            if (Math.abs(this.level - _c.characterInfo.getLevel()) < 5) {
                item = new Item(Event.getItems().next());
                item.quantity = 1;
                item.setDefaultOptions();
            }
        } else if (type == ItemType.GOLD) {
            int gold1 = this.level * 100;
            int gold2 = gold1 - (gold1 / Utils.nextInt(1, 3));
            int gold = Utils.nextInt(gold2, gold1);
            if (_c.characterInfo.getOptions()[155] > 0) {
                gold += gold * _c.characterInfo.getOptions()[155] / 100;
            }
            SpecialSkill sp = _c.getSpecialSkill();
            if (sp != null) {
                if (sp.id == 29) {
                    gold += gold * sp.param / 100;
                }
            }
            itemID = Utils.getItemGoldByQuantity(gold);
            item = new Item(itemID);
            item.setDefaultOptions();
            item.quantity = gold;
        } else if (type == ItemType.GEM) {
            item = new Item(861);
            item.setDefaultOptions();
            item.quantity = 1;
        } else if (type == ItemType.ITEM) {
            int rd = Utils.nextInt(10);
            switch (rd) {
                case 0:
                    itemID = 191;
                    break;

                case 1:
                    itemID = 192;
                    break;

                case 2:
                    itemID = 74;
                    break;

                case 3:
                    itemID = 225;
                    break;
            }
            if (itemID != -1) {
                item = new Item(itemID);
                item.setDefaultOptions();
                item.quantity = 1;
            }
        } else if (type == ItemType.EQUIP) {
            int level = LEVEL[this.level];
            if (level != -1) {
                int p2 = level;
                if (p2 < 2) {
                    p2 = 2;
                }
                if (Utils.nextInt(p2) == 0) {
                    Server server = DragonBall.getInstance().getServer();
//                    itemID = server.randomItemTemplate(_c.getGender(), level);
                    if (itemID != -1) {
                        item = new Item(itemID);
                        item.quantity = 1;
                        item.setDefaultOptions();

                        if (Utils.nextInt(5) == 0) {
                            for (ItemOption option : item.options) {
                                int p = Utils.nextInt(1, 10);
                                int add = option.param * p / 100;
                                if (add == 0) {
                                    add = 1;
                                }
                                option.param += (Utils.nextInt(2) == 0 ? 1 : -1) * add;
                                if (option.param <= 0) {
                                    option.param = 1;
                                }
                            }
                        }
                    }
                }
            }
        } else if (type == ItemType.CARD) {
            int id = -1;
            int rank = 0;
            for (CardTemplate c : Card.templates) {
                if (c.templateID != -1 && c.templateID == this.templateId) {
                    id = c.id;
                    rank = c.rank;
                    break;
                }
            }
            if (id != -1) {
                if (Utils.nextInt(1 + rank) == 0) {
                    item = new Item(id);
                    item.setDefaultOptions();
                    item.quantity = 1;
                }
            }
        }
        if (zone.map.isTreasure()) {
            int[] arr = {2, 3, 4, 5, 6, 7};
//            ZTreasure z = (ZTreasure) zone;
//            int level = z.getTreasure().getLevel();
            int gold = 300 * level;
            if (gold > 30000) {
                gold = 30000;
            }
            itemID = Utils.getItemGoldByQuantity(gold);
            int index = level / 20;
            int loop = arr[index];
            for (int i = 0; i < loop; i++) {
                Item itemGold = new Item(itemID);
                itemGold.setDefaultOptions();
                itemGold.quantity = gold;
                ItemMap itemMap = new ItemMap(zone.autoIncrease++);
                itemMap.item = itemGold;
                itemMap.playerID = Math.abs(_c.getId());
//                itemMap.x = (short) (getX() + Utils.nextInt(-loop * 10, loop * 10));
//                itemMap.y = zone.map.collisionLand(getX(), getY());
                zone.addItemMap(itemMap);
                zone.mapService.addItemMap(itemMap);
            }
        }

        if (item != null) {
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.playerID = Math.abs(_c.getId());
            itemMap.isPickedUp = false;
            itemMap.x = (short) (this.x + Utils.nextInt(-30, 30));
            itemMap.y = zone.map.collisionLand(this.x, this.y);
            itemMap.item = item;
            itemMap.isThrowFromMob = true;
            itemMap.killerIsHuman = _c.isHuman();
            itemMap.mobLevel = this.level;
            this.items.add(itemMap);
            zone.addItemMap(itemMap);
        }

    }

    public enum ItemType {
        NONE, GOLD, ITEM, EQUIP, GEM, CARD, EVENT
    }

    private static Logger logger = Logger.getLogger(Mob.class);


    public static final int[] LEVEL = {-1, -1, 1, 2, 3, 4, 5, 6, 9, 9, 9, 9, 10, 10, 10, 11, 11, 11, 11, 12, 12, 12, 12, -1, -1, -1};
    public static final int[][][] OPTIONS = {{{127, 139}, {128, 140}, {129, 141}}, {{130, 142}, {131, 143}, {132, 144}}, {{133, 136}, {134, 137}, {135, 138}}};


    public static ArrayList<MobTemplate> vMobTemplate = new ArrayList<>();
    public static byte[] data;
    public static int baseId = 0;
    public static void addMobTemplate(MobTemplate mob) {
        vMobTemplate.add(mob);
    }

    public static MobTemplate getMobTemplate(int id) {
        return vMobTemplate.get(id);
    }

    public static void createData() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(vMobTemplate.size());
            for (MobTemplate mob : vMobTemplate) {
                dos.writeByte(mob.mobTemplateId);
                dos.writeByte(mob.type);
                dos.writeUTF(mob.name);
                dos.writeLong(mob.hp);
                dos.writeByte(mob.rangeMove);
                dos.writeByte(mob.speed);
                dos.writeByte(mob.dartType);
            }
            data = bos.toByteArray();
            dos.close();
            bos.close();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }


    public void attack(Char _owner, Object obj) {
        int skill_id = _owner.select.template.id;
        if (skill_id != SkillName.CHIEU_DAM_DEMON && skill_id != SkillName.CHIEU_MASENKO && skill_id != SkillName.LIEN_HOAN && skill_id != SkillName.CHIEU_DAM_DRAGON && skill_id != SkillName.CHIEU_KAMEJOKO && skill_id != 9 && skill_id != 4 && skill_id != 5) {
            return;
        }
        long damageFull = _owner.characterInfo.getFullDamage();
        long dame = Utils.percentOf(damageFull, this.percentDamage);
        if (_owner.isSetPikkoroDaimao()) {
            dame *= 2;
        }
        if (obj instanceof Char) {
            Char target = (Char) obj;
            if (target.isDead()) {
                return;
            }
//            if (target instanceof Broly) {
//                long p = ((Broly) target).info.hpFull / 100;
//                if (dame > p) {
//                    dame = p;
//                }
//            }
            target.getLock().lock();
            try {
                if (target.isProtected() && dame >= target.characterInfo.getHp()) {
                    dame = target.characterInfo.getFullHP() - 1;
                }
                //_char.info.hp -= dame;
                target.characterInfo.setHp(target.characterInfo.getHp() - dame);
                if (target.characterInfo.getHp() <= 0) {
                    _owner.kill(target);
                    target.killed(_owner);
                    target.startDie();
                }
                _owner.zone.mapService.mobMeUpdate(_owner, target, dame, (byte) -1, (byte) 2);
            } finally {
                target.getLock().unlock();
            }
        } else {
            Mob target = (Mob) obj;
            if (target.isDead()) {
                return;
            }
            if (target.templateId == MobName.MOC_NHAN) {
                dame = 10;
            }
            if (target.zone.map.isNguHanhSon()) {
                dame = 80000;
            }
            target.lock.lock();
            try {
                target.hp -= dame;
                long hp = target.hp;
                if (target.hp <= 0) {
                    _owner.kill(obj);
                    target.startDie(dame, false, _owner);
                }
                _owner.zone.mapService.mobMeUpdate(_owner, target, dame, (byte) -1, (byte) 3);
            } finally {
                target.lock.unlock();
            }
        }
    }

    public void attack(Char target) {
        try {
            try {
                if (target == null) {
                    List<Char> list = getChars();
                    if (list.size() > 0) {
                        int index = Utils.nextInt(list.size());
                        target = list.get(index);
                    } else if (this.maxHp >= 6000) {
                        int distanceClosest = -1;
                        List<Char> list2 = zone.getListChar(Zone.TYPE_HUMAN, Zone.TYPE_PET, Zone.TYPE_ESCORT);
                        for (Char enemy : list2) {
                            try {
                                if (enemy.isDead() || enemy.isInvisible() || enemy.isVoHinh() || enemy.checkEffectOfSatellite(344) > 0) {
                                    continue;
                                }
                                int distance = Utils.getDistance(this.x, this.y, enemy.getX(), enemy.getY());
                                if (distance > 60) {
                                    continue;
                                }
                                if (distanceClosest == -1 || distance < distanceClosest) {
                                    distanceClosest = distance;
                                    target = enemy;
                                }
                            } catch (Exception e) {
                                logger.error(String.format("attack err - enemy is null: %b", enemy == null), e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("attack err - block 1", e);
                logger.error(String.format("attack err - zone is null: %b", zone == null), e);
            }
            if (target != null && target.zone == this.zone) {
                boolean isMiss = Utils.nextInt(100) < (target.characterInfo.getEvasionPercent() + 5);
                long dameHp = Utils.nextLong(this.damage2, this.damage1);
                if (isChangeBody) {
                    dameHp -= Utils.percentOf(dameHp, this.dameDown);
                }
                dameHp -= target.characterInfo.getFullDefense();
                if (dameHp <= 0) {
                    dameHp = 1;
                }
                if (target.checkEffectOfSatellite(344) > 0) {
                    dameHp -= dameHp / 5;
                }
                if (target.isBuaDaTrau()) {
                    dameHp /= 2;
                }
                if (target.isDisciple()) {
                    Disciple disciple = (Disciple) target;
                    if (disciple.master.isBuaDeTu()) {
                        dameHp /= 2;
                    }
                }
                if (target.isGiapXen()) {
                    dameHp /= 2;
                }
                dameHp -= Utils.percentOf(dameHp, target.characterInfo.getOptions()[94]);
                if (target.characterInfo.getOptions()[157] > 0) {
                    long pM = target.characterInfo.getMp() * 100 / target.characterInfo.getFullMP();
                    if (pM < 20) {
                        dameHp -= Utils.percentOf(dameHp, target.characterInfo.getOptions()[157]);
                    }
                }
                if (target.isDisciple()) {
                    Disciple disciple = (Disciple) target;
                    if (disciple.master.isBuaManhMe()) {
                        dameHp /= 2;
                    }
                }
                if (this.levelBoss != 0) {
                    dameHp = target.characterInfo.getFullHP() / 10;
                }
                if (zone.map.isNguHanhSon()) {
                    dameHp = target.characterInfo.getHp() / 20;
                }
                if (target.isBuaBatTu()) {
                    if (target.characterInfo.getHp() == 1) {
                        isMiss = true;
                    }
                    if (dameHp >= target.characterInfo.getHp()) {
                        dameHp = target.characterInfo.getHp() - 1;
                    }
                }
                if (dameHp > 0) {
                    long reactDame = Utils.percentOf(dameHp, target.characterInfo.getOptions()[97]);
                    if (reactDame >= this.hp) {
                        reactDame = this.hp - 1;
                    }
                    if (reactDame > 0) {
                        if (this.hp > 1) {
                            this.hp -= reactDame;
                            zone.mapService.attackNpc(reactDame, false, this, (byte) 36);
                        } else if (this.hp == 1) {
                            zone.mapService.attackNpc(-1, false, this, (byte) 36);
                        }
                    }
                }
                if (!isMiss && target.isProtected()) {
                    dameHp = 1;
                    if (dameHp >= target.characterInfo.getHp()) {
                        isMiss = true;
                    }
                }
                int dameMp = 0;
                if (!isMiss) {
                    target.getLock().lock();
                    try {
                        if (isDead() || target.isDead()) {
                            return;
                        }
                        target.characterInfo.setHp(target.characterInfo.getHp() - dameHp);
                        target.characterInfo.setMp(target.characterInfo.getMp() - dameMp);
                        attack(target, dameHp, dameMp);
                        if (target.characterInfo.getHp() <= 0) {
//                            kill(target);
                            target.killed(this);
                            target.startDie();
                        }
                    } finally {
                        target.getLock().unlock();
                    }
                } else {
                    zone.mapService.attackNpc(-1, false, this, (byte) -1);
                }
            }
        } catch (Exception e) {
            logger.error("attack err", e.getCause());
        }
    }

    public void attack(Char _char, long dameHp, int dameMp) {
        try {
            Message ms = new Message(Cmd.NPC_ATTACK_ME);
            DataOutputStream ds = ms.getWriter();
            ds.writeInt(this.mobId);
            ds.writeLong(dameHp);
            if (dameMp > 0) {
                ds.writeInt(dameMp);
            }
            ds.flush();
            _char.service.sendMessage(ms);
            ms.cleanup();

            ms = new Message(Cmd.NPC_ATTACK_PLAYER);
            ds = ms.getWriter();
            ds.writeInt(this.mobId);
            ds.writeInt(_char.getId());
            ds.writeLong(_char.characterInfo.getHp());
            if (dameMp > 0) {
                ds.writeLong(_char.characterInfo.getMp());
            }
            ds.flush();
            zone.mapService.sendMessage(ms, _char);
            ms.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public List<Char> getChars() {
        ArrayList<Char> list = new ArrayList<>();
        for (Char _char : this.listChar) {
            if (!_char.isDead() && _char.zone == this.zone) {
                int distance = Utils.getDistance(this.x, this.y, _char.getX(), _char.getY());
                if (distance < 500) {
                    list.add(_char);
                }
            }
        }
        return list;
    }


}
