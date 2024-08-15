package com.ngocrong.backend.character;


import com.google.gson.Gson;
import com.ngocrong.backend.bot.Escort;
import com.ngocrong.backend.clan.Clan;
import com.ngocrong.backend.clan.ClanMember;
import com.ngocrong.backend.collection.Card;
import com.ngocrong.backend.collection.CardTemplate;
import com.ngocrong.backend.consts.*;
import com.ngocrong.backend.crackball.CrackBall;
import com.ngocrong.backend.disciple.Disciple;
import com.ngocrong.backend.disciple.MiniDisciple;
import com.ngocrong.backend.effect.AmbientEffect;
import com.ngocrong.backend.effect.EffectChar;
import com.ngocrong.backend.item.*;
import com.ngocrong.backend.lib.KeyValue;
import com.ngocrong.backend.map.Barrack;
import com.ngocrong.backend.map.BaseBabidi;
import com.ngocrong.backend.map.MapManager;
import com.ngocrong.backend.map.tzone.*;
import com.ngocrong.backend.mob.Mob;
import com.ngocrong.backend.model.*;
import com.ngocrong.backend.network.Message;
import com.ngocrong.backend.network.Session;
import com.ngocrong.backend.repository.GameRepo;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.shop.Shop;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.skill.SkillBook;
import com.ngocrong.backend.skill.SpecialSkill;
import com.ngocrong.backend.task.Task;
import com.ngocrong.backend.util.Utils;
import org.json.JSONArray;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
public class Char {


    private static Logger logger = Logger.getLogger(Char.class);
    public CharacterInfo characterInfo;
    public static final byte UPDATE_ONE_SECONDS = 0;
    public static final byte UPDATE_THIRTY_SECONDS = 1;
    public static final byte UPDATE_ONE_MINUTES = 2;
    public static final byte TAI_TAO = 3;
    public static final byte UPDATE_FIVE_SECONDS = 4;
    public static final byte UPDATE_HALF_SECONDS = 5;
    public static final byte UPDATE_TEN_SECONDS = 6;
    public static final int DISTANCE_EFFECT = 150;


    // int
    private int id;
    private int diamond;
    private int diamondLock;
    private int timeIsMoneky;
    private int hpPercent;
    private int betAmount;
    private int freezSeconds;
    private int seconds;
    private int clanID;
    private int mapEnter;
    private int idMount;
    private int dameDown;
    private int goldTrading;
    private int fusionType;
    private int currZoneId;
    private int capsule;
    private int timePlayed;
    private int numberCellBag, numberCellBox;
    private int deltaTime = 1000;
    private int testCharId = -9999;
    private int killCharId = -9999;
    private int goldBarUnpaid;
    private int tickMove;
    private int percentDamageBonus;
    private int phuX;

    //ARR ITEM
    public Item[] itemBody;
    public Item[] itemBag;
    public Item[] itemBox;

    //ARR LIST
    private ArrayList<Skill> skills;
    private ArrayList<AmbientEffect> ambientEffects;
    private ArrayList<Card> cards;
    public ArrayList<EffectChar> effects;
    public ArrayList<ItemTime> itemTimes;
    private ArrayList<Integer> listAccessMap;
    public ArrayList<KeyValue> menus = new ArrayList<>();
    private ArrayList<Amulet> amulets;
    public ArrayList<Achievement> achievements;
    private ArrayList<MessageTime> messageTimes;
    public ArrayList<Item> boxCrackBall;
    public ArrayList<Friend> friends;
    public Menu menu;
    public ArrayList<Friend> enemies;
    // sort
    private short head, headDefault;
    private short body;
    private short leg;
    private short wp;
    private short x, y, preX, preY;
    private short eff5buffhp, eff5buffmp;
    private short idAuraEff = -1;
    private short idEffSetItem = -1;
    private short countNumberOfSpecialSkillChanges;

    //bool
    private boolean isNhapThe;
    private boolean isMonkey;
    private boolean isHaveMount;
    private boolean isDead;
    private boolean isCharge, isRecoveryEnergy;
    private boolean isCold;
    private boolean setNappa;
    private boolean isLoggedOut;
    private boolean isHeld;
    private boolean isHaveEquipInvisible, isHaveEquipTransformIntoChocolate, isHaveEquipTransformIntoStone,
            isHaveEquipMiNuong, isHaveEquipBulma, isHaveEquipXinbato, isHaveEquipBuiBui, isDoSaoPhaLe;
    private boolean isFreeze, isSleep, isBlind, isProtected, isHuytSao, isCritFirstHit;
    private boolean isStone;
    private boolean isChocolate;
    private boolean isMask;
    private boolean isInvisible;
    private boolean isCuongNo, isBoHuyet, isGiapXen, isBoKhi, isAnDanh, isMayDo, isDuoiKhi, isPudding, isXucXich, isKemDau, isMiLy, isSushi;
    private boolean isTrading;
    private boolean isNewMember;
    private boolean isAutoPlay;
    private boolean isSkillSpecial;
    private boolean isBuaTriTue, isBuaManhMe, isBuaDaTrau, isBuaOaiHung, isBuaBatTu, isBuaDeoDai, isBuaThuHut,
            isBuaDeTu, isBuaTriTue3, isBuaTriTue4;
    private boolean setThienXinHang, setKirin, setSongoku, setPicolo, setOcTieu, setPikkoroDaimao, setKakarot, setCaDic, setThanLinh;
    private boolean isGoBack;
    //obj
    public Zone zone;
    private String name;
    private MiniDisciple miniDisciple;
    public Disciple myDisciple;
    public Clan clan;
    public CharService service;
    public Skill select;
    private Task taskMain;
    private Hold hold;
    private Mob mobMe;
    private Escort escortedPerson;
    private Item itemLoot;
    private PowerInfo accumulatedPoint;
    private PetFollow petFollow;
    private Char trader;
    private Status status;
    private CrackBall crackBall;
    public Shop shop;
    public MagicTree magicTree;
    private SkillBook studying;
    private Timestamp resetTime;
    private SpecialSkill specialSkill;
    private Session session;
    private Invite invite;

    //    private PowerInfo accumulatedPoint;
    // byte
    private byte classId;
    private byte gender;
    private byte pointPk;
    private byte teleport;
    private byte statusMe = 1;
    private byte ship;
    private byte typeTraining;
    private byte bag = -1;
    public byte typePk;
    private byte flag;
    private byte commandTransport;
    private String captcha;
    private int playerReportedID;
    private String playerReportedName;
    private byte numCheck;
    public int typePorata;
    private byte commandPK;

    // long
    private long gold;
    private long lastRequestPean;
    private long lastAttack;
    private long lastUsePotion;
    private long lastTimeChatGlobal;
    private long timeAtSplitFusion;
    private long lastTimeRequestChangeZone;
    private long lastTimeMove;
    private long lastTick, currTick;
    public long lastUseRecoveryEnery;
    private long lastTimeUsePorata;
    private long lastTimeRevenge;
    private long lastTimeTrade;
    private long lastPickup;
    private long lastTimeUseGiftCode;


    // arr
    private long[] lastUpdates = new long[100];
    private byte[] shortcut;


    // lock

    private Lock lock = new ReentrantLock();
    private Lock lockAction = new ReentrantLock();

    public boolean isDisciple() {
        return false;
    }

    public boolean isMiniDisciple() {
        return false;
    }

    public boolean isEscort() {
        return false;
    }

    public boolean isBoss() {
        return false;
    }

    public boolean isHuman() {
        return true;
    }

    public boolean isNhapThe() {
        return isNhapThe;
    }


    public Item getIemInBag(int id) {
        for (Item item : itemBag) {
            if (item != null && item.id == id) {
                return item;
            }
        }
        return null;
    }

    public int checkEffectOfSatellite(int id) {
        int number = 0;
        if (zone != null) {
            List<ItemMap> list = zone.getListSatellite();
            for (ItemMap itemMap : list) {
                if (itemMap.item.id == id) {
                    int d = Utils.getDistance(this.x, this.y, itemMap.x, itemMap.y);

                    if (d < itemMap.r) {
                        if (itemMap.owner.id == this.id || (itemMap.owner.clan == this.clan)) {
                            number++;
                        }
                    }
                }
            }
        }
        return number;
    }

    public Skill getSkill(int id) {
        for (Skill skill : skills) {
            if (skill.template.id == id) {
                return skill;
            }
        }
        return null;
    }

    public void stopRecoveryEnery() {
        this.isRecoveryEnergy = false;
        if (zone != null) {
            zone.mapService.skillNotFocus(this, (byte) 3, null, null);
        }
    }

    public void clearEffect() {
        setTimeForItemtime(0, 0);
        setTimeForItemtime(1, 0);
        setTimeForItemtime(3, 0);
        setTimeForItemtime(4, 0);
        setTimeForItemtime(5, 0);
        setTimeForItemtime(14, 0);

        if (zone != null) {
            zone.mapService.setEffect(null, this.id, Skill.REMOVE_ALL_EFFECT, Skill.CHARACTER, (short) -1);
        }
        updateItemTime();
    }

    private void updateItemTime() {
        if (itemTimes != null) {
            synchronized (itemTimes) {
                List<ItemTime> listRemove = new ArrayList<>();
                for (ItemTime item : itemTimes) {
                    item.update();
                    if (item.seconds <= 0) {
                        handleExpiredItem(item);
                        listRemove.add(item);
                    }
                }
                itemTimes.removeAll(listRemove);
            }
        }
    }

    private void handleExpiredItem(ItemTime item) {

    }


    public void timeOutIsMonkey() {
        setMonkey(false);
        this.timeIsMoneky = 0;
        this.updateSkin();
        this.characterInfo.setCharacterInfo();
        service.loadPoint();
        if (zone != null) {
            zone.mapService.playerLoadBody(this);
            zone.mapService.updateBody((byte) 0, this);
        }
    }

    protected void updateSkin() {
        this.bag = -1;
        if (taskMain != null && taskMain.id == 3 && taskMain.index == 2) {
            this.bag = ClanImageName.DUA_BE_51;
        }
        updateBag();
        setDefaultPart();
        if (itemBody != null) {
            if (itemBody[0] != null) {
                this.body = itemBody[0].template.getPart();
            }
            if (itemBody[1] != null) {
                this.leg = itemBody[1].template.getPart();
            }
        }
        if (this.isStone) {
            this.head = 454;
            this.body = 455;
            this.leg = 456;
        } else if (this.isChocolate) {
            this.head = 412;
            this.body = 413;
            this.leg = 414;
        } else if (isNhapThe && !isMonkey) {
            this.isMask = true;
            if (itemBody != null && itemBody[5] != null && itemBody[5].template.getPart() == -1 && itemBody[5].isNhapThe) {
                ItemTemplate template = itemBody[5].template;
                this.head = template.head;
                this.body = template.body;
                this.leg = template.leg;
            } else {
                if (typePorata == 0) {
                    if (gender == 1) {
                        this.head = 391;
                        this.body = 392;
                        this.leg = 393;
                    } else {
                        if (fusionType == 4) {
                            this.head = 380;
                            this.body = 381;
                            this.leg = 382;
                        } else if (fusionType == 6) {
                            this.head = 383;
                            this.body = 384;
                            this.leg = 385;
                        }
                    }
                } else if (typePorata == 1) {
                    switch (gender) {
                        case 0:
                            this.head = 870;
                            this.body = 871;
                            this.leg = 872;
                            break;

                        case 1:
                            this.head = 873;
                            this.body = 874;
                            this.leg = 875;
                            break;

                        case 2:
                            this.head = 867;
                            this.body = 868;
                            this.leg = 869;
                            break;
                    }
                }
            }
        } else if (!this.isMonkey) {
            if (itemBody != null && itemBody[5] != null && !itemBody[5].isNhapThe) {
                this.head = itemBody[5].template.getPart();
                if (this.head == -1) {
                    this.isMask = true;
                    ItemTemplate template = itemBody[5].template;
                    this.head = template.head;
                    this.body = template.body;
                    this.leg = template.leg;
                }
            } else {
                this.isMask = false;
            }
        } else {
            Skill skill = getSkill(13);
            if (skill != null) {
                this.isMask = true;
                this.body = 193;
                this.leg = 194;
                switch (skill.point) {
                    case 1:
                        this.head = 192;
                        break;
                    case 2:
                        this.head = 195;
                        break;
                    case 3:
                        this.head = 196;
                        break;
                    case 4:
                        this.head = 199;
                        break;
                    case 5:
                        this.head = 197;
                        break;
                    case 6:
                        this.head = 200;
                        break;
                    case 7:
                        this.head = 198;
                        break;
                }
            }
        }
    }

    private void updateBag() {
        if (itemLoot != null) {
            switch (itemLoot.id) {
                case ItemName.NGOC_DEN_1_SAO:
                    this.bag = ClanImageName.NGOC_RONG_1_SAO_DEN_61;
                    break;
                case ItemName.NGOC_DEN_2_SAO:
                    this.bag = ClanImageName.NGOC_RONG_2_SAO_DEN_62;
                    break;
                case ItemName.NGOC_DEN_3_SAO:
                    this.bag = ClanImageName.NGOC_RONG_3_SAO_DEN_63;
                    break;
                case ItemName.NGOC_DEN_4_SAO:
                    this.bag = ClanImageName.NGOC_RONG_4_SAO_DEN_64;
                    break;
                case ItemName.NGOC_DEN_5_SAO:
                    this.bag = ClanImageName.NGOC_RONG_5_SAO_DEN_65;
                    break;
                case ItemName.NGOC_DEN_6_SAO:
                    this.bag = ClanImageName.NGOC_RONG_6_SAO_DEN_66;
                    break;
                case ItemName.NGOC_DEN_7_SAO:
                    this.bag = ClanImageName.NGOC_RONG_7_SAO_DEN_80;
                    ;
                    break;
                case ItemName.NGOC_RONG_NAMEK_1_SAO:
                    this.bag = ClanImageName.NGOC_RONG_NAMEK_1_SAO_52;
                    break;
                case ItemName.NGOC_RONG_NAMEK_2_SAO:
                    this.bag = ClanImageName.NGOC_RONG_NAMEK_2_SAO_53;
                    break;
                case ItemName.NGOC_RONG_NAMEK_3_SAO:
                    this.bag = ClanImageName.NGOC_RONG_NAMEK_3_SAO_54;
                    break;
                case ItemName.NGOC_RONG_NAMEK_4_SAO:
                    this.bag = ClanImageName.NGOC_RONG_NAMEK_4_SAO_55;
                    break;
                case ItemName.NGOC_RONG_NAMEK_5_SAO:
                    this.bag = ClanImageName.NGOC_RONG_NAMEK_5_SAO_56;
                    break;
                case ItemName.NGOC_RONG_NAMEK_6_SAO:
                    this.bag = ClanImageName.NGOC_RONG_NAMEK_6_SAO_57;
                    break;
                case ItemName.NGOC_RONG_NAMEK_7_SAO:
                    this.bag = ClanImageName.NGOC_RONG_NAMEK_7_SAO_58;
                    break;
            }
        } else if (itemBody.length > 8 && itemBody[8] != null) {
            switch (itemBody[8].template.getId()) {
                case ItemName.LUOI_HAI_THAN_CHET:
                    this.bag = ClanImageName.LUOI_HAI_THAN_CHET_72;
                    break;
                case ItemName.CANH_DOI_DRACULA:
                    this.bag = ClanImageName.CANH_DOI_DRACULA_73;
                    break;
                case ItemName.BONG_TUYET:
                    this.bag = ClanImageName.BONG_TUYET_74;
                    break;
                case ItemName.LONG_DEN_CO_VY:
                    this.bag = ClanImageName.LONG_DEN_CO_VY_37;
                    break;
                case ItemName.LONG_DEN_CON_TAU:
                    this.bag = ClanImageName.LONG_DEN_CON_TAU_38;
                    break;
                case ItemName.LONG_DEN_CON_GA:
                    this.bag = ClanImageName.LONG_DEN_CON_GA_39;
                    break;
                case ItemName.LONG_DEN_CON_BUOM:
                    this.bag = ClanImageName.LONG_DEN_CON_BUOM_40;
                    break;
                case ItemName.LONG_DEN_DOREMON:
                    this.bag = ClanImageName.LONG_DEN_DOREMON_41;
                    break;
                case ItemName.NON_THIEN_THAN:
                    this.bag = ClanImageName.NON_THIEN_THAN_42;
                    break;
                case ItemName.MA_TROI:
                    this.bag = ClanImageName.MA_TROI_43;
                    break;
                case ItemName.HON_MA_GOKU:
                    this.bag = ClanImageName.HON_MA_GOKU_44;
                    break;
                case ItemName.HON_MA_CA_DIC:
                    this.bag = ClanImageName.HON_MA_CA_DIC_45;
                    break;
                case ItemName.HON_MA_POCOLO:
                    this.bag = ClanImageName.HON_MA_POCOLO_46;
                    break;
                case ItemName.CAY_THONG:
                    this.bag = ClanImageName.CAY_THONG_47;
                    break;
                case ItemName.TUI_QUA:
                    this.bag = ClanImageName.TUI_QUA_48;
                    break;
                case ItemName.CAY_TRUC:
                    this.bag = ClanImageName.CAY_TRUC_49;
                    break;
                case ItemName.KIEM_Z:
                    this.bag = ClanImageName.KIEM_Z_50;
                    break;
                case ItemName.TRAI_BONG_966:
                    this.bag = ClanImageName.TRAI_BONG_77;
                    break;
                case ItemName.CUP_VANG_982:
                    this.bag = ClanImageName.CUP_VANG_78;
                    break;
                case ItemName.CO_CO_DONG_983:
                    this.bag = ClanImageName.CO_CO_DONG_79;
                    break;
                case ItemName.VO_OC_994:
                    this.bag = ClanImageName.VO_OC_81;
                    break;
                case ItemName.CAY_KEM_995:
                    this.bag = ClanImageName.CAY_KEM_82;
                    break;
                case ItemName.CA_HEO_996:
                    this.bag = ClanImageName.CA_HEO_83;
                    break;
                case ItemName.CON_DIEU_997:
                    this.bag = ClanImageName.CON_DIEU_84;
                    break;
                case ItemName.DIEU_RONG_998:
                    this.bag = ClanImageName.DIEU_RONG_85;
                    break;
                case ItemName.MEO_MUN_999:
                    this.bag = ClanImageName.MEO_MUN_86;
                    break;
                case ItemName.XIEN_CA_1000:
                    this.bag = ClanImageName.XIEN_CA_87;
                    break;
                case ItemName.PHONG_LON_1001:
                    this.bag = ClanImageName.PHONG_LON_88;
                    break;
                case ItemName.VAN_LUOT_SONG_1007:
                    this.bag = ClanImageName.VAN_LUOT_SONG_89;
                    break;
            }
        } else {
            if (clan != null) {
                this.bag = clan.imgID;
            } else {
                this.bag = -1;
            }
        }
        characterInfo.setCharacterInfo();
        service.loadPoint();
        if (zone != null) {
            zone.mapService.playerLoadBody(this);
            zone.mapService.updateBag(this);
        }
    }


    public boolean checkCanEnter(int mapID) {
        if (taskMain.id >= 15) {
            return true;
        }
        for (int map : listAccessMap) {
            if (map == mapID) {
                return true;
            }
        }
        TMap map = MapManager.getInstance().getMap(mapID);
        if (map.isMapSpecial()) {
            return true;
        }
        return false;
    }

//

    private void setTimeForItemtime(int i, int seconds) {
        synchronized (this.itemTimes) {
            for (ItemTime itemTime : this.itemTimes) {
                if (itemTime.id == id) {
                    itemTime.seconds = seconds;
                    service.setItemTime(itemTime);
                }
            }
        }
    }


    public int getCurrentNumberFloorInBaseBabidi() {

        for (int i = 0; i < BaseBabidi.MAPS.length; i++) {
            if (BaseBabidi.MAPS[i] == zone.map.mapID) {
                return i;
            }
        }
        return -1;
    }

    public void addAccumulatedPoint(int point) {
        if (accumulatedPoint != null) {
            accumulatedPoint.addPoint(point);
            service.setPowerInfo(accumulatedPoint);
            int floor = getCurrentNumberFloorInBaseBabidi();
            if (accumulatedPoint.isMaxPoint() && floor + 1 < BaseBabidi.MAPS.length) {
                showMenuDownToNextFloor();
            }
        }
    }

    private void showMenuDownToNextFloor() {
        menus.clear();
        menus.add(new KeyValue(459, "OK"));
        String say = "Mau đi với ta xuống tầng tiếp theo";
        byte npcTemplateID = 5;
        short avatar = (short) ((flag == 9) ? 4390 : 4388);
        service.openUIConfirm(npcTemplateID, say, avatar, menus);
    }


    public void clearMap() {
        if (this.typePk == 3) {
            Char _char = zone.findCharByID(this.testCharId);
            clearPk();
            resultPk((byte) 3);
            _char.clearPk();
            _char.resultPk((byte) 2);
        }
        if (isTrading) {
            trader.service.serverMessage(Language.TRADE_FAIL);
            service.serverMessage(Language.TRADE_FAIL);
            trader.clearTrade();
            clearTrade();
        }
        service.clearMap();
        this.menus.clear();
        this.crackBall = null;
        this.shop = null;
        this.zone = null;
//        this.combine = null;
//        this.lucky = null;
//        if (callDragon != null) {
//            callDragon.close();
//        }
//        clearAmbientEffect();
    }

    private void clearTrade() {
//        if (!(this.status == Status.DONG_Y_GIAO_DICH)) {
//            this.service.giaoDich(null, (byte) 7, -1);
//        }
//        this.isTrading = false;
//        this.goldTrading = 0;
//        this.itemsTrading = null;
//        this.trader = null;
//        this.status = Status.NORMAL;
    }

    private void resultPk(byte b) {
        switch (getCommandPK()) {
            case CMDPk.THACH_DAU: {
//                if (type == 0) {
//                    int gold = this.betAmount * 2;
//                    gold -= gold / 10;
//                    addGold(gold);
//                    service.serverMessage("Đối thủ đã kiệt sức, bạn thắng được " + gold + " vàng");
//                }
//                if (type == 1) {
//                    service.serverMessage("Bạn đã thua vì kiệt sức");
//                }
//                if (type == 2) {
//                    int gold = this.betAmount * 2;
//                    gold -= gold / 10;
//                    addGold(gold);
//                    service.serverMessage("Đối thủ đã bỏ chạy, bạn thắng được " + gold + " vàng");
//                }
//                if (type == 3) {
//                    service.serverMessage("Bạn đã thua vì bỏ chạy");
//                }
            }
            break;

            case CMDPk.TRA_THU: {
//                if (type == 3) {
//                    service.serverMessage("Bạn đã bị xử thua");
//                }
            }
            break;

            case CMDPk.DAI_HOI_VO_THUAT:
//                Arena arena = (Arena) zone;
//                arena.checkResult();
//                break;
        }
        setCommandPK(CMDPk.NORMAL);
//        if (type == 0 || type == 2) {
//            if (achievements != null) {
//                achievements.get(3).addCount(1);// trăm trận trăm tháng
//            }
//        }
    }

    private void clearPk() {
        this.testCharId = -9999;
        setTypePk((byte) 0);
    }

    private void setTypePk(byte typePk) {
        this.typePk = typePk;
        if (zone != null) {
            zone.mapService.playerSetTypePk(this);
        }
    }

    public void setDefaultPart() {
        setDefaultHead();
        setDefaultBody();
        setDefaultLeg();
    }

    private void setDefaultLeg() {
        if (this.gender == 0) {
            this.leg = 58;
        } else if (this.gender == 1) {
            this.leg = 60;
        } else if (this.gender == 2) {
            this.leg = 58;
        }
    }

    private void setDefaultBody() {
        if (this.gender == 0) {
            this.body = 57;
        } else if (this.gender == 1) {
            this.body = 59;
        } else if (this.gender == 2) {
            this.body = 57;
        }
    }

    private void setDefaultHead() {
        this.head = this.headDefault;
    }

    public short getPetAvatar() {
        if (this.gender == 1) {
            return 536;
        }
        if (this.gender == 2) {
            return 537;
        }
        return 351;
    }

    public void saveData() {
        try {
            if (isLoggedOut) {
                return;
            }
            if (myDisciple != null) {
                myDisciple.saveData();
            }
            Gson g = new Gson();
            ArrayList<Item> bags = new ArrayList<>();
            for (Item item : this.itemBag) {
                if (item != null) {
                    bags.add(item);
                }
            }

            ArrayList<Item> bodys = new ArrayList<>();
            for (Item item : this.itemBody) {
                if (item != null) {
                    bodys.add(item);
                }
            }

            ArrayList<Item> boxs = new ArrayList<>();
            for (Item item : this.itemBox) {
                if (item != null) {
                    boxs.add(item);
                }
            }

            ArrayList<Integer> maps = new ArrayList<>();
            int mapId = 0;
            if (zone == null) {
//                mapId = transportToMap;
            } else {
                mapId = zone.map.mapID;
            }

            int x = this.x;
            int y = this.y;
            if (zone != null) {
                TMap map = zone.map;
                if (this.isDead || (map.isCantOffline())) {
                    if (map.isTreasure() || map.isClanTerritory()) {
                        mapId = MapName.DAO_KAME;
                        x = 1000;
                        y = 408;
                    } else if (map.isDauTruong()) {
                        mapId = MapName.DAI_HOI_VO_THUAT;
                    } else {
                        switch (this.gender) {
                            case 0:
                                mapId = MapName.NHA_GOHAN;
                                x = 456;
                                y = 336;
                                break;

                            case 1:
                                mapId = MapName.NHA_MOORI;
                                x = 168;
                                y = 336;
                                break;

                            case 2:
                                mapId = MapName.NHA_BROLY;
                                x = 432;
                                y = 336;
                                break;
                        }
                    }
                    if (isDead) {
                        this.characterInfo.setHp(1);
                        this.characterInfo.setMp(1);
                    }
                }
            }
            maps.add(mapId);
            maps.add(x);
            maps.add(y);
            ArrayList<ItemTime> items = new ArrayList<>();
            for (ItemTime item : itemTimes) {
                if (!item.isSave) {
                    continue;
                }
                items.add(item);
            }
            JSONArray skills = new JSONArray();
            for (Skill skill : this.skills) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("id", skill.template.id);
                    obj.put("level", skill.point);
                    obj.put("last_time_use", skill.lastTimeUseThisSkill);
                    skills.put(obj);
                } catch (JSONException ex) {
                    logger.error("failed!", ex);
                }
            }
            String study = null;
            if (this.studying != null) {
                JSONObject obj = new JSONObject();
                obj.put("id", studying.id);
                obj.put("level", studying.level);
                obj.put("studying_time", studying.studyTime);
                study = obj.toString();
            }
            GameRepo.getInstance().playerRepo.saveData(this.id, g.toJson(this.taskMain), this.gold, this.diamond, this.diamondLock, g.toJson(bags), g.toJson(bodys), g.toJson(boxs),
                    g.toJson(maps), skills.toString(), g.toJson(this.characterInfo), this.clanID, g.toJson(this.shortcut), this.numberCellBag, this.numberCellBox, g.toJson(this.friends), g.toJson(this.enemies),
                    this.headDefault, this.ship, g.toJson(this.magicTree), g.toJson(items), this.fusionType, g.toJson(this.amulets), this.typeTraining, g.toJson(this.achievements), this.timePlayed, study,
                    g.toJson(this.boxCrackBall), this.timeAtSplitFusion, (int) this.head, (int) this.body, (int) this.leg, typePorata, g.toJson(this.cards), g.toJson(this.specialSkill),
                    this.countNumberOfSpecialSkillChanges, resetTime);
        } catch (Exception e) {
            logger.debug("saveData", e);
        }
    }

    public void logout() {
        if (isLoggedOut) {
            return;
        }
        try {
            closeTrade();
            saveData();
            if (zone != null) {
                zone.leave(this);
            }
            History history = new History(id, History.LOGOUT);
            history.setBefores(gold, diamond, diamondLock);
            history.setAfters(gold, diamond, diamondLock);
            for (Item item : itemBag) {
                if (item != null) {
                    history.addItem(item);
                }
            }
            for (Item item : itemBody) {
                if (item != null) {
                    history.addItem(item);
                }
            }
            for (Item item : itemBox) {
                if (item != null) {
                    history.addItem(item);
                }
            }

            history.setExtras(session.getIp());
            history.save();
            GameRepo.getInstance().playerRepo.setOffline(this.id, (byte) 0, new Timestamp(System.currentTimeMillis()));
        } finally {
            isLoggedOut = true;
        }
    }

    private void closeTrade() {
        if (isTrading) {
            trader.service.serverMessage(Language.TRADE_FAIL);
            service.serverMessage(Language.TRADE_FAIL);
            trader.clearTrade();
            clearTrade();
        }
    }

    public void setNewMember(boolean b) {
    }

    public void initializedCollectionBook() {
        if (cards == null) {
            cards = new ArrayList<>();
        }
        if (cards.size() < Card.templates.size()) {
            for (CardTemplate cardT : Card.templates) {
                boolean isExist = false;
                for (Card card : cards) {
                    if (card.id == cardT.id) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    Card cardNew = new Card();
                    cardNew.id = cardT.id;
                    cardNew.level = 0;
                    cardNew.amount = 0;
                    cardNew.isUse = false;
                    cards.add(cardNew);
                }
            }
        }
        for (Card card : cards) {
            card.setTemplate();
        }
        setAuraEffect();
    }

    private void setAuraEffect() {
        short id = -1;
        for (Card card : cards) {
            if (card.isUse) {
                if (card.id == 956) {
                    id = card.template.aura;
                    break;
                }
            }
        }
        idAuraEff = id;
    }

    public void setStatusItemTime() {
        synchronized (itemTimes) {
            for (ItemTime item : itemTimes) {
                switch (item.id) {
                    case ItemTimeName.CUONG_NO:
                        setCuongNo(true);
                        break;

                    case ItemTimeName.BO_HUYET:
                        setBoHuyet(true);
                        break;

                    case ItemTimeName.BO_KHI:
                        setBoKhi(true);
                        break;

                    case ItemTimeName.GIAP_XEN_BO_HUNG:
                        setGiapXen(true);
                        break;

                    case ItemTimeName.AN_DANH:
                        setAnDanh(true);
                        break;

                    case ItemTimeName.MAY_DO_CAPSULE_KI_BI:
                        setMayDo(true);
                        break;

                    case ItemTimeName.DUOI_KHI:
                        setDuoiKhi(true);
                        break;

                    case ItemTimeName.BANH_PUDDING:
                        setPudding(true);
                        break;

                    case ItemTimeName.XUC_XICH:
                        setXucXich(true);
                        break;

                    case ItemTimeName.KEM_DAU:
                        setKemDau(true);
                        break;

                    case ItemTimeName.MI_LY:
                        setMiLy(true);
                        break;

                    case ItemTimeName.SUSHI:
                        setSushi(true);
                        break;
                }
            }
        }
    }

    public void enter() {
        Server server = DragonBall.getInstance().getServer();
        GameRepo.getInstance().playerRepo.setOnline(this.id, (byte) server.getConfig().getServerID(), new Timestamp(System.currentTimeMillis()));
        updateSkin();
        setMount();
        characterInfo.setCharacterInfo();
        service.sendDataBG();
        service.setTileSet();
        service.setTask();
        service.loadAll();
        service.updateActivePoint();
        service.setMaxStamina();
        service.setStamina();
        service.loadPoint();
        service.specialSkill((byte) 0);
        if (shortcut != null) {
            service.changeOnSkill(shortcut);
        }
        service.updateCoolDown(skills);
        if (amulets == null) {
            amulets = new ArrayList<>();
        }
        if (itemTimes == null) {
            itemTimes = new ArrayList<>();
        }
        if (this.myDisciple == null) {
            service.petInfo((byte) 0);
        } else {
            Disciple deTu = this.myDisciple;
            deTu.setMaster(this);
            deTu.service = new CharService(deTu);
            deTu.followMaster();
            deTu.updateSkin();
            service.petInfo((byte) 1);
        }
        for (int m : Barrack.MAPS) {
            if (m == mapEnter) {
                switch (gender) {
                    case 0:
                        mapEnter = 21;
                        x = 456;
                        y = 336;
                        break;

                    case 1:
                        mapEnter = 22;
                        x = 168;
                        y = 336;
                        break;

                    case 2:
                        mapEnter = 23;
                        x = 432;
                        y = 336;
                        break;
                }
                break;
            }
        }
        boolean isFusion = false;
        for (ItemTime item : itemTimes) {
            if (item.id == 2) {
                isFusion = true;
            }
            service.setItemTime(item);
            if (item.id == 12) {
                isAutoPlay = true;
            }
        }
        if (fusionType == 4 && !isFusion) {
            isNhapThe = true;
            ItemTime itemTime = new ItemTime(ItemTimeName.HOP_THE, gender == 1 ? 3901 : 3790, 10, true);
            addItemTime(itemTime);
        }
        TMap map = MapManager.getInstance().getMap(mapEnter);
        if (!map.isMapSingle()) {
            int zoneId = map.getZoneID();
            map.enterZone(this, zoneId);
        } else {
            enterMapSingle(map);
        }
        if (this.isMask) {
            zone.mapService.updateBody((byte) 0, this);
        }
        service.gameInfo();
        String subName = taskMain.subNames[taskMain.index];
        service.serverMessage(subName);

        if (achievements == null) {
            initAchievement();
        } else {
            for (Achievement achive : achievements) {
                achive.initTemplate();
            }
        }
        if (clan != null) {
            ClanMember clanMember = clan.getMember(id);
            if (clanMember == null) {
                clan = null;
                clanID = -1;
            } else {
                clanMember.receiveItem(this);
                service.clanInfo();
            }
        }
        int mapID = zone.map.mapID;
        if (!(mapID == 39 || mapID == 40 || mapID == 41)) {
            Notification notification = Notification.getInstance();
            if (notification != null && !notification.equals("")) {
                service.openUISay(5, notification.getText(), notification.getAvatar());
            }
        }
        setListAccessMap();
    }

    public void initAchievement() {
        Server server = DragonBall.getInstance().getServer();
        int size = server.getAchievements().size();
        this.achievements = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.achievements.add(new Achievement(i));
        }
    }

    private void addItemTime(ItemTime item) {
        synchronized (this.itemTimes) {
            for (ItemTime itm : this.itemTimes) {
                if (itm.id == item.id) {
                    if (item.id == 11) {
                        itm.seconds += item.seconds;
                        if (itm.seconds > 1800) {
                            itm.seconds = 1800;
                        }
                    } else {
                        itm.seconds = item.seconds;
                    }
                    service.setItemTime(itm);
                    return;
                }
            }
            this.itemTimes.add(item);
            service.setItemTime(item);
        }
    }

    private void enterMapSingle(TMap map) {
        int zoneID = map.autoIncrease++;
        Zone z = null;
        if (map.mapID == MapName.NHA_GOHAN || map.mapID == MapName.NHA_MOORI || map.mapID == MapName.NHA_BROLY) {
            z = new Home(map, zoneID);
        } else if (map.mapID == MapName.RUNG_KARIN) {
            z = new KarinForest(map, zoneID, this);
        } else if (map.mapID == MapName.DONG_NAM_KARIN) {
//            z = new SoutheastKarin(map, zoneID);
        } else if (map.mapID == MapName.THAP_KARIN) {
//            z = new KarinTower(map, zoneID, typeTraining);
        } else {
            z = new MapSingle(map, zoneID);
        }
        map.addZone(z);
        z.enter(this);
    }

    private void setMount() {
        this.isHaveMount = false;
        this.idMount = -1;
        for (Item item : this.itemBody) {
            if (item != null) {
                if (item.template.getType() == 23 || item.template.getType() == 24) {
                    this.isHaveMount = true;
                    this.idMount = item.template.mountID;
                    return;
                }
            }
        }
    }

    public void goHome() {
        lock.lock();
        try {
            int mapID = 0;
            switch (this.gender) {
                case 0:
                    mapID = 21;
                    break;

                case 1:
                    mapID = 22;
                    break;

                case 2:
                    mapID = 23;
                    break;
            }
            teleport(mapID);
        } finally {
            lock.unlock();

        }
    }

    private void teleport(int mapID) {
        TMap map = MapManager.getInstance().getMap(mapID);
        if (map.isBarrack()) {
            if (clan == null || clan.barrack == null) {
                return;
            }
            if (!clan.barrack.isRunning()) {
                service.serverMessage2("Trại độc nhãn đã kết thúc");
                return;
            }
        }
        byte ship = getShip();
        if (ship == 1) {
            setTeleport((byte) 3);
        } else {
            setTeleport((byte) 1);
        }
        if (ship == 1) {
            characterInfo.recovery(CharacterInfo.ALL, 100, true);
        }
        zone.mapService.addTeleport(this.id, this.teleport);
        switch (mapID) {
            case 68:
                this.x = (short) 100;
                break;
            case 19:
                this.x = (short) (map.width - 100);
                break;
            default:
                this.x = calculateX(map);
                break;
        }
        this.y = 0;
        zone.leave(this);
        if (map.isMapSingle()) {
            enterMapSingle(map);
        } else if (map.isBarrack()) {
            clan.barrack.enterMap(mapID, this);
        } else {
            int zoneId = 0;
            if (isGoBack()) {
                zoneId = currZoneId;
                isGoBack = false;
            } else {
                zoneId = map.getZoneID();
            }
            map.enterZone(this, zoneId);

        }
        this.y = zone.map.collisionLand(this.x, this.y);
        setTeleport((byte) 0);
    }

    private short calculateX(TMap map) {
        double g = (double) this.x / (double) zone.map.width;
        short x = (short) (g * map.width);
        return x;
    }


//    public void updateEveryFiveSeconds() {
//        try {
//            if (!isDead) {
//
//                try {
//                    if (zone == null || zone.mapService == null) {
//                        if (zone == null) {
//                            logger.debug("zone is null");
//                        }
//                        if (zone.mapService == null) {
//                            logger.debug("service is null");
//                        }
//                    }
//                    List<Char> list = zone.getListChar(Zone.TYPE_HUMAN, Zone.TYPE_PET);
//                    if (list.size() > 1) {
//                        String[] chats2 = {"Tránh ra đi Xinbatô ơi", "Phân tâm quá", "Nực quá", "Bực bội quá",
//                                "Im đi ông Xinbatô ơi"};
//                        if (characterInfo.getOptions()[8] > 0) {
//                            characterInfo.recovery(CharacterInfo.ALL, characterInfo.getOptions()[8], true);
//                        }
//                        for (Char _c : list) {
//                            if (_c.isDead) {
//                                continue;
//                            }
//
//                            int d = Utils.getDistance(this.x, this.y, _c.x, _c.y);
//                            if (d < DISTANCE_EFFECT) {
//                                boolean isUpdate = false;
//                                if (_c != this) {
//                                    if (characterInfo.getOptions()[8] > 0) {
//                                        _c.characterInfo.setHp(characterInfo.getHp() - Utils.percentOf(_c.characterInfo.getFullHP(), characterInfo.getOptions()[8]));
//                                        _c.characterInfo.setMp(characterInfo.getMp() - Utils.percentOf(_c.characterInfo.getFullMP(), characterInfo.getOptions()[8]));
//                                        if (_c.characterInfo.getHp() <= 0) {
//                                            _c.characterInfo.setHp(1);
//                                        }
//                                        if (_c.characterInfo.getMp() <= 0) {
//                                            _c.characterInfo.setMp(1);
//                                        }
//                                        _c.service.loadPoint();
//
//                                        zone.mapService.playerLoadBody(_c);
//                                    }
//                                    if (isHaveEquipXinbato) {
//                                        if (!_c.isAnDanh) {
////                                            AmbientEffect am = new AmbientEffect(111, info.options[111], 5000);
////                                            if (_c.addAmbientEffect(am)) {
////                                                isUpdate = true;
////                                            }
////                                            zone.service.chat(_c, chats2[Utils.nextInt(chats2.length)]);
//                                        }
//                                    } else if (isHaveEquipBuiBui) {
//                                        AmbientEffect am = new AmbientEffect(24, -95, 5000);
////                                        if (_c.addAmbientEffect(am)) {
////                                            isUpdate = true;
////                                        }
////                                        zone.service.chat(_c, "Nặng quá");
////                                    } else if (info.options[162] > 0) {
////                                        zone.service.chat(_c, "Cute");
////                                    }
//                                }
//                                if (isHaveEquipBulma || isHaveEquipMiNuong) {
////                                    AmbientEffect am = new AmbientEffect(117, info.options[117], 5000);
////                                    if (_c.addAmbientEffect(am)) {
////                                        isUpdate = true;
////                                    }
//                                    String chat;
//                                    if (isHaveEquipMiNuong) {
//                                        chat = "Bắn tim...biu biu";
//                                    } else {
//                                        chat = "Wow, Sexy quá";
//                                    }
//                                    if (_c != this) {
////                                        zone.service.chat(_c, chat);
//                                    }
//                                }
//                                if (isUpdate) {
//                                    _c.info.setInfo();
//                                    _c.service.loadPoint();
//                                }
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    logger.error("update every five seconds - block 1");
//                }
//                try {
//                    if (isHaveEquipInvisible) {
//                        isInvisible = true;
//                        zone.mapService.playerLoadAll(this);
//                        long delay = 1000;
////                        if (this instanceof Yacon) {
////                            delay = 2000;
////                        }
//                        Utils.setTimeout(() -> {
//                            if (zone != null) {
//                                isInvisible = false;
//                                zone.mapService.playerLoadAll(this);
//                            }
//                        }, delay);
//                    }
//                } catch (Exception e) {
//                    logger.error("update every five seconds - block 2");
//                }
//            }
//        } catch (Exception e) {
//            logger.error("updateEveryFiveSeconds error", e.getCause());
//        }
//    }
//
//
//    public void revival(int percent) {
//        if (this.isDead) {
//            this.statusMe = 1;
//            this.isDead = false;
//            service.sendMessage(new Message(Cmd.ME_LIVE));
//            zone.mapService.playerLoadLive(this);
//        }
//        this.characterInfo.recovery(CharacterInfo.ALL, percent, true);
//    }
//
//    public void loadEffectSkillPlayer(Char character) {
//        applyStatusEffects(character);
//        handleRecoveryEnergy(character);
//        handleHoldEffect(character);
//        handleChargeSkill(character);
//    }
//
//    private void handleChargeSkill(Char character) {
//        if (character.isCharge()) {
//            short skillId = (short) character.select.id;
//            byte chargeType = getChargeType(skillId);
//            if (chargeType != -1) {
//                service.skillNotFocus(character.id, skillId, chargeType, null, null);
//            }
//        }
//    }
//
//    private byte getChargeType(short skillId) {
//        switch (skillId) {
//            case SkillName.QUA_CAU_KENH_KHI:
//            case SkillName.MAKANKOSAPPO:
//                return (byte) 4;
//
//            case SkillName.BIEN_HINH:
//                return (byte) 6;
//
//            case SkillName.TU_PHAT_NO:
//                return (byte) 7;
//
//            default:
//                return -1;
//        }
//    }
//
//    private void handleHoldEffect(Char character) {
//        if (character.isHeld && character.hold.getHolder() == character) {
//            applyHoldEffect(character.hold, character.hold.getDetainee());
//        }
//    }
//
//    private void applyHoldEffect(Hold hold, Object detainee) {
//        if (detainee instanceof Mob) {
//            Mob mob = (Mob) detainee;
//            service.setEffect(hold, mob.mobId, Skill.ADD_EFFECT, Skill.MONSTER, (byte) 32);
//        } else if (detainee instanceof Char) {
//            Char detaineeChar = (Char) detainee;
//            service.setEffect(hold, detaineeChar.id, Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 32);
//        }
//    }
//
//    private void handleRecoveryEnergy(Char character) {
//        if (character.isRecoveryEnergy) {
//            service.skillNotFocus(character.id, (short) character.select.id, (byte) 1, null, null);
//        }
//    }
//
//    private void applyStatusEffects(Char character) {
//        if (character.isSleep) {
//            applyEffect(character, (byte) 41);
//        }
//        if (character.isProtected) {
//            applyEffect(character, (byte) 33);
//        }
//        if (character.isBlind) {
//            applyEffect(character, (byte) 40);
//        }
//    }
//
//    private void applyEffect(Char character, byte effectId) {
//        service.setEffect(null, character.getId(), Skill.ADD_EFFECT, Skill.CHARACTER, effectId);
//    }
//
//    public boolean isHaveFood() {
//        return isPudding() || isXucXich() || isKemDau() || isMiLy() || isSushi();
//    }
//
//    public boolean addItem(Item item) {
//        if (handleSpecialItems(item)) {
//            return true;
//        }
//        if (item.template.isUpToUp()) {
//            if (mergeWithExistingItem(item)) {
//                return true;
//            }
//        }
//
//        return addItemToEmptySlot(item);
//    }
//
//    // Xử lý các loại item đặc biệt như Vàng, Kim cương, Khóa kim cương, Bùa
//    private boolean handleSpecialItems(Item item) {
//        switch (item.template.getType()) {
//            case Item.TYPE_GOLD:
//                addGold(item.quantity);
//                return true;
//            case Item.TYPE_DIAMOND:
//                addDiamond(item.quantity);
//                return true;
//            case Item.TYPE_DIAMOND_LOCK:
//                addDiamondLock(item.quantity);
//                return true;
//            case Item.TYPE_AMULET:
//                return handleAmuletItem(item);
//            default:
//                return false;
//        }
//    }
//
//    private void addGold(long gold) {
//        this.gold += gold;
//        service.addGold(gold);
//    }
//
//    // Xử lý thêm bùa vào danh sách hoặc gia hạn thời gian sử dụng bùa
//    private boolean handleAmuletItem(Item item) {
//        Amulet amulet = getAmulet(item.id);
//        if (amulet != null) {
//            amulet.expiredTime += item.quantity;
//        } else {
//            amulet = new Amulet();
//            amulet.id = item.id;
//            amulet.expiredTime = System.currentTimeMillis() + item.quantity;
//            addAmulet(amulet);
//        }
//        return true;
//    }
//
//    // Gộp item mới với item đã tồn tại trong túi (nếu có thể)
//    private boolean mergeWithExistingItem(Item item) {
//        int maxQuantity = Server.getMaxQuantityItem();
//        int index = getIndexBagById(item.id);
//
//        if (index != -1) {
//            Item existingItem = this.itemBag[index];
//
//            if (mergeItemOptions(existingItem, item)) {
//                return true;
//            }
//
//            if (existingItem.quantity + item.quantity > maxQuantity) {
//                return false; // Không thể thêm nếu vượt quá số lượng tối đa
//            }
//
//            existingItem.quantity += item.quantity;
//            updateBagUI(index, existingItem);
//            return true;
//        }
//        return false;
//    }
//
//    // Gộp các tùy chọn của item mới vào item đã tồn tại nếu có tùy chọn tương tự
//    private boolean mergeItemOptions(Item existingItem, Item newItem) {
//        boolean merged = false;
//        for (ItemOption existingOption : existingItem.options) {
//            if (isMergableOption(existingOption)) {
//                for (ItemOption newOption : newItem.options) {
//                    if (existingOption.optionTemplate.id == newOption.optionTemplate.id) {
//                        existingOption.param += newOption.param;
//                        service.setItemBag(); // Cập nhật lại túi đồ sau khi gộp
//                        merged = true;
//                    }
//                }
//            }
//        }
//        return merged;
//    }
//
//    // Kiểm tra nếu tùy chọn của item có thể gộp với item khác
//    private boolean isMergableOption(ItemOption option) {
//        return option.optionTemplate.id == 1 || option.optionTemplate.id == 31 ||
//                option.optionTemplate.id == 11 || option.optionTemplate.id == 12 ||
//                option.optionTemplate.id == 13;
//    }
//
//    // Thêm item vào slot trống trong túi
//    private boolean addItemToEmptySlot(Item item) {
//        for (int i = 0; i < itemBag.length; i++) {
//            if (itemBag[i] == null) {
//                itemBag[i] = item;
//                item.indexUI = i;
//                service.setItemBag(); // Cập nhật túi đồ sau khi thêm item
//                return true;
//            }
//        }
//        return false; // Không có slot trống trong túi
//    }
//
//    // Cập nhật giao diện túi sau khi thay đổi số lượng item
//    private void updateBagUI(int index, Item item) {
//        if (item.template.type == Item.TYPE_DAUTHAN) {
//            service.setItemBag(); // Cập nhật toàn bộ túi
//        } else {
//            service.updateBag(index, item.quantity); // Cập nhật chỉ mục cụ thể trong túi
//        }
//    }
//}

    public void setTypePK(byte typePk) {
        this.typePk = typePk;
        if (zone != null) {
            zone.mapService.playerSetTypePk(this);
        }
    }

    public void taskNext() {
        taskMain.index++;
        taskMain.count = 0;
        service.taskNext();
    }

    protected boolean isLang() {
        return zone.map.mapID == 1 || zone.map.mapID == 27 || zone.map.mapID == 72 || zone.map.mapID == 10
                || zone.map.mapID == 17 || zone.map.mapID == 22 || zone.map.mapID == 32 || zone.map.mapID == 38
                || zone.map.mapID == 43 || zone.map.mapID == 48;
    }

    public void killed(Object killer) {
        throwItem(killer);
    }


    public void throwItem(Message ms) {
        if (isDead) {
            return;
        }
        if (isTrading) {
            return;
        }
    }

    public void throwItem(Object obj) {
        if (isHuman()) {
            int gold = characterInfo.getLevel() * 1000;
            if (gold > this.gold) {
                gold = (int) this.gold;
            }
            if (gold == 0) {
                return;
            }
            int itemID = Utils.getItemGoldByQuantity(gold);
            addGold(-gold);
            Item item = new Item(itemID);
            item.setDefaultOptions();
            item.quantity = gold;
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.item = item;
            itemMap.x = this.x;
            itemMap.y = zone.map.collisionLand(x, y);
            itemMap.playerID = this.id;
            zone.addItemMap(itemMap);
            zone.mapService.addItemMap(itemMap);
        }
    }

    public void throwItem(Item item, byte type) {
        if (characterInfo.getPower() < 1500000) {
            service.serverMessage("Bạn chưa đủ sức mạnh để vứt vật phẩm vui lòng thử lại sau");
            return;
        }
        if (zone.map.isMapSingle()) {
            service.serverMessage("Không thể vứt vật phẩm ở đây");
            return;
        }
        if (item.typeThrow == 2) {
            service.serverMessage("Không thể bỏ vật phẩm này");
            return;
        }
        Item[] items;
        if (type == 0) {
            items = itemBody;
        } else {
            items = itemBag;
        }
        item.lock.lock();
        try {
            int index = item.indexUI;
            if (items[index] == null) {
                return;
            }

            History history = new History(this.id, History.THROW_ITEM);
            history.setBefores(gold, diamond, diamondLock);
            history.setAfters(gold, diamond, diamondLock);
            history.addItem(item);
            history.setZone(zone);
            history.setExtras("Bỏ ra đất");
            history.save();

            items[index] = null;
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.item = item;
            itemMap.x = (short) (Utils.nextInt(-10, 10) + this.x);
            itemMap.y = zone.map.collisionLand(itemMap.x, this.y);
            itemMap.playerID = this.id;
            zone.addItemMap(itemMap);
            if (type == 0) {
                service.setItemBody();
                updateSkin();
                characterInfo.setCharacterInfo();
                service.loadPoint();
                zone.mapService.playerLoadBody(this);
                zone.mapService.updateBody((byte) 0, this);
            } else {
                zone.mapService.throwItem(this, itemMap);
            }
            if (items[index] == null) {
                sort(index, true);
            }
        } finally {
            item.lock.unlock();
        }
    }

    public void sort(int index, boolean isUpdate) {
        int index2 = -1;
        for (int i = index; i < this.numberCellBag; i++) {
            if (this.itemBag[i] != null) {
                index2 = i;
            }
        }
        if (index2 != -1) {
            this.itemBag[index] = this.itemBag[index2];
            this.itemBag[index].indexUI = index;
            this.itemBag[index2] = null;
        }
        if (isUpdate) {
            service.setItemBag();
        }
    }

    private void addGold(long gold) {
        this.gold += gold;
        service.addGold(gold);
    }

    public void startDie() {
        try {
            if (itemLoot != null) {
                dropItemSpe();
//                if (zone instanceof ZBlackDragonBall) {
//                    ZBlackDragonBall z = (ZBlackDragonBall) zone;
//                    z.itemBlackDragonBall.isPickedUp = false;
//                    z.service.addItemMap(z.itemBlackDragonBall);
//                    z.itemBlackDragonBall.countDown = 60;
//                    z.setPlayerHolding(null);
//                }
            }
            if (this.typePk == 3) {
                Char playerPk = zone.findCharByID(this.testCharId);
                clearPk();
                playerPk.clearPk();
                resultPk((byte) 1);
                playerPk.resultPk((byte) 0);
            }
            if (this.mobMe != null) {
                this.mobMe.timeLive = 0;
            }
            if (isTrading) {
                trader.service.serverMessage(Language.TRADE_FAIL);
                service.serverMessage(Language.TRADE_FAIL);
                trader.clearTrade();
                clearTrade();
            }
            if (this.isMonkey) {
                timeOutIsMonkey();
            }
            if (this.hold != null) {
                this.hold.close();
            }
            if (isRecoveryEnergy) {
                stopRecoveryEnery();
            }
            isSkillSpecial = false;
            isCharge = false;
            this.seconds = 0;
            this.isFreeze = false;
            this.isCritFirstHit = false;
            this.statusMe = 5;
            this.characterInfo.setHp(0);
            this.isDead = true;
            this.y = zone.map.collisionLand(x, y);
            clearEffect();
            Message ms = new Message(Cmd.ME_DIE);
            DataOutputStream ds = ms.getWriter();
            ds.writeByte(this.typePk);
            ds.writeShort(this.x);
            ds.writeShort(this.y);
            ds.writeLong(this.characterInfo.getPower());
            service.sendMessage(ms);
            ms.cleanup();

            ms = new Message(Cmd.PLAYER_DIE);
            ds = ms.getWriter();
            ds.writeInt(this.id);
            ds.writeByte(this.typePk);
            ds.writeShort(this.x);
            ds.writeShort(this.y);
            zone.mapService.sendMessage(ms, this);
            ms.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);

        }
    }
    private void dropItemSpe() {
        if (itemLoot != null) {
            itemLoot = null;
            updateBag();
            if (getPhuX() > 0) {
                setPhuX(0);
                characterInfo.setCharacterInfo();
                service.loadPoint();
            }
        }
    }


    public boolean meCanAttack() {
        return !isDead && !isFreeze && !isSleep && !isHeld && select != null && !isStone;
    }

    public boolean meCanMove() {
        return !isDead && !isBlind && !isFreeze && !isSleep && !isCharge && !isStone
                && !(hold != null && hold.getDetainee() == this);
    }

    public void updateEveryOneSeconds() {
        try {
            long now = System.currentTimeMillis();
            if (specialSkill != null) {
                boolean isUpdate = false;
                for (Skill skill : skills) {
                    if (skill.isCooldown()) {
                        if ((specialSkill.id == 4 && skill.template.id == SkillName.TU_PHAT_NO)
                                || (specialSkill.id == 6 && skill.template.id == SkillName.HUYT_SAO)
                                || (specialSkill.id == 14 && skill.template.id == SkillName.QUA_CAU_KENH_KHI)
                                || (specialSkill.id == 23 && skill.template.id == SkillName.TRI_THUONG)
                                || (specialSkill.id == 24 && skill.template.id == SkillName.MAKANKOSAPPO)
                                || (specialSkill.id == 25 && skill.template.id == SkillName.DE_TRUNG)
                                || (specialSkill.id == 28 && skill.template.id == SkillName.KHIEN_NANG_LUONG)
                                || (specialSkill.id == 13 && skill.template.id == SkillName.THAI_DUONG_HA_SAN)) {
                            int p = (int) ((now - skill.lastTimeUseThisSkill) * 100 / skill.coolDown);
                            if (p + specialSkill.param >= 100) {
                                skill.lastTimeUseThisSkill = now - skill.coolDown;
                                isUpdate = true;
                            }
                            break;
                        }
                    }
                }
                if (isUpdate) {
                    service.updateCoolDown(skills);
                }
            }
            if (!isDead) {
                if (isHuman()) {
                    if (zone != null && zone.map.checkBlock(this.x, this.y)) {
                        characterInfo.recovery(CharacterInfo.ALL, -10, true);
                        if (characterInfo.getHp() <= 0) {
                            startDie();
                        }
                    }
                }
                if (characterInfo.getOptions()[162] > 0) {
                    List<Char> list = zone.getListChar(Zone.TYPE_HUMAN, Zone.TYPE_PET);
                    if (list.size() > 1) {
                        for (Char _c : list) {
                            if (_c.isDead) {
                                continue;
                            }
                            int d = Utils.getDistance(this.x, this.y, _c.x, _c.y);
                            if (d < DISTANCE_EFFECT) {
                                _c.characterInfo.recovery(characterInfo.MP, characterInfo.getOptions()[162], true);
                            }
                        }
                    }
                }
            }
//            if (callDragon != null) {
//                if (now - callDragon.time >= 300000) {
//                    callDragon.close();
//                }
//            }
            try {
                this.timePlayed++;
                if (isMonkey) {
                    if (this.timeIsMoneky > 0) {
                        this.timeIsMoneky--;
                    }
                    if (this.timeIsMoneky == 0) {
                        this.timeOutIsMonkey();
                    }
                }
                if (invite != null) {
                    invite.update();
                }
            } catch (Exception e) {
                logger.error("update every one second - block 1");
            }
            try {
                boolean isFlag = false;
                if (this.x < 24) {
                    this.x = 24;
                    isFlag = true;
                }
                if (this.x > zone.map.width - 24) {
                    this.x = (short) (zone.map.width - 24);
                    isFlag = true;
                }
                if (this.y < 0) {
                    this.y = 24;
                    isFlag = true;
                }
                if (this.y > zone.map.height - 24) {
                    this.y = zone.map.collisionLand(x, (short) 24);
                    isFlag = true;
                }
                if (isFlag) {
                    if (zone.mapService != null) {
                        zone.mapService.setPosition(this, (byte) 0);
                    }
                }
            } catch (Exception e) {
                logger.error("update every one second - block 2");
            }
            try {
                updateMessageTime();
                updateAmulet();
                updateTimeLiveMobMe();
                if (this.freezSeconds > 0) {
                    this.freezSeconds--;
                    if (this.freezSeconds == 0) {
                        this.isFreeze = false;
                    }
                }
                updateItemTime();
            } catch (Exception e) {
                logger.error("update every one second - block 3");
            }
        } catch (Exception e) {
            logger.error("updateEveryOneSeconds error", e.getCause());
        }
    }

    private void updateTimeLiveMobMe() {
        if (this.mobMe != null) {
            if (this.mobMe.timeLive > 0) {
                if (!isSetPikkoroDaimao()) {
                    this.mobMe.timeLive--;
                }

            }
            if (this.mobMe.timeLive == 0) {
                zone.mapService.mobMeUpdate(this, null, -1, (byte) -1, (byte) 7);
                this.mobMe = null;
            }
        }
    }

    private void updateAmulet() {
        if (amulets != null) {
            if (!amulets.isEmpty()) {
                ArrayList<Amulet> listRemove = new ArrayList<>();
                long now = System.currentTimeMillis();
                boolean isBuaTriTue = false;
                boolean isBuaManhMe = false;
                boolean isBuaDaTrau = false;
                boolean isBuaOaiHung = false;
                boolean isBuaBatTu = false;
                boolean isBuaDeoDai = false;
                boolean isBuaThuHut = false;
                boolean isBuaDeTu = false;
                boolean isBuaTriTue3 = false;
                boolean isBuaTriTue4 = false;
                for (Amulet amulet : amulets) {
                    if (amulet.expiredTime < now) {
                        listRemove.add(amulet);
                    } else {
                        switch (amulet.id) {
                            case 213:
                                isBuaTriTue = true;
                                break;

                            case 214:
                                isBuaManhMe = true;
                                break;

                            case 215:
                                isBuaDaTrau = true;
                                break;

                            case 216:
                                isBuaOaiHung = true;
                                break;

                            case 217:
                                isBuaBatTu = true;
                                break;

                            case 218:
                                isBuaDeoDai = true;
                                break;

                            case 219:
                                isBuaThuHut = true;
                                break;

                            case 522:
                                isBuaDeTu = true;
                                break;

                            case 671:
                                isBuaTriTue3 = true;
                                break;

                            case 672:
                                isBuaTriTue4 = true;
                                break;
                        }
                    }
                }
                setBuaBatTu(isBuaBatTu);
                setBuaDaTrau(isBuaDaTrau);
                setBuaDeTu(isBuaDeTu);
                setBuaManhMe(isBuaManhMe);
                setBuaDeoDai(isBuaDeoDai);
                setBuaOaiHung(isBuaOaiHung);
                setBuaThuHut(isBuaThuHut);
                setBuaTriTue(isBuaTriTue);
                setBuaTriTue3(isBuaTriTue3);
                setBuaTriTue4(isBuaTriTue4);
                if (listRemove.size() > 0) {
                    amulets.removeAll(listRemove);
                }
            }
        }
    }

    private void updateMessageTime() {
        if (messageTimes != null) {
            synchronized (messageTimes) {
                ArrayList<MessageTime> removes = new ArrayList<>();
                for (MessageTime ms : messageTimes) {
                    ms.update();
                    if (ms.getTime() <= 0) {
                        removes.add(ms);
                    }
                }
                messageTimes.removeAll(removes);
            }
        }
    }


    public boolean addItem(Item item) {
        if (item.template.getType() == Item.TYPE_GOLD) {
            addGold(item.quantity);
            return true;
        }
        if (item.template.getType() == Item.TYPE_DIAMOND) {
            addDiamond(item.quantity);
            return true;
        }
        if (item.template.getType() == Item.TYPE_DIAMOND_LOCK) {
            addDiamondLock(item.quantity);
            return true;
        }
        if (item.template.getType() == Item.TYPE_AMULET) {
            Amulet amulet = getAmulet(item.id);
            if (amulet != null) {
                amulet.expiredTime += item.quantity;
            } else {
                amulet = new Amulet();
                amulet.id = item.id;
                amulet.expiredTime = System.currentTimeMillis() + item.quantity;
                addAmulet(amulet);
            }
            return true;
        }
        if (item.template.isUpToUp()) {
            int maxQuantity = Server.getMaxQuantityItem();
            int index = getIndexBagById(item.id);
            if (index != -1) {
                Item item2 = this.itemBag[index];
                boolean flag = false;
                for (ItemOption o : item2.options) {
                    if (o.optionTemplate.id == 1 || o.optionTemplate.id == 31 || o.optionTemplate.id == 11 || o.optionTemplate.id == 12 || o.optionTemplate.id == 13) {
                        for (ItemOption o2 : item.options) {
                            if (o.optionTemplate.id == o2.optionTemplate.id) {
                                o.param += o2.param;
                                service.setItemBag();
                                flag = true;
                            }
                        }
                    }
                }
                if (flag) {
                    return true;
                }
                if (item2.quantity + item.quantity > maxQuantity) {
                    return false;
                }
                item2.quantity += item.quantity;
                if (item.template.getType() == Item.TYPE_DAUTHAN) {
                    service.setItemBag();
                } else {
                    service.updateBag(index, item2.quantity);
                }
                return true;
            }
        }
        for (int i = 0; i < itemBag.length; i++) {
            if (itemBag[i] == null) {
                itemBag[i] = item;
                item.indexUI = i;
                service.setItemBag();
                return true;
            }
        }
        return false;
    }

    private int getIndexBagById(int id) {
        for (int i = 0; i < this.itemBag.length; i++) {
            Item item = this.itemBag[i];
            if (item != null && item.id == id) {
                return i;
            }
        }
        return -1;
    }

    private void addAmulet(Amulet amulet) {
        this.amulets.add(amulet);
    }

    private Amulet getAmulet(int id) {
        for (Amulet amulet : amulets) {
            if (amulet.id == id) {
                return amulet;
            }
        }
        return null;
    }

    private void addDiamondLock(int diamondLock) {
        this.diamondLock += diamondLock;
        service.loadInfo();
    }

    private void addDiamond(int diamond) {
        this.diamond += diamond;
        service.loadInfo();
    }

    public void setListAccessMap() {
        listAccessMap.clear();
        if (gender == 0) {
            listAccessMap.add(21);
        }
        if (gender == 1) {
            listAccessMap.add(22);
        }
        if (gender == 2) {
            listAccessMap.add(23);
        }
        if (taskMain.id >= 1) {
            listAccessMap.add(0);
            listAccessMap.add(7);
            listAccessMap.add(14);
        }
        if (taskMain.id >= 2) {
            listAccessMap.add(1);
            listAccessMap.add(8);
            listAccessMap.add(15);
        }
        if (taskMain.id > 3 || (taskMain.id == 3 && taskMain.index >= 1)) {
            listAccessMap.add(42);
            listAccessMap.add(43);
            listAccessMap.add(44);
        }
        if (taskMain.id >= 6) {
            listAccessMap.add(2);
            listAccessMap.add(9);
            listAccessMap.add(16);

            listAccessMap.add(24);
            listAccessMap.add(25);
            listAccessMap.add(26);
        }
        if (taskMain.id >= 7) {
            listAccessMap.add(3);
            listAccessMap.add(11);
            listAccessMap.add(17);
        }
        if (taskMain.id >= 8) {
            listAccessMap.add(4);
            listAccessMap.add(12);
            listAccessMap.add(18);
        }
        if (taskMain.id > 8 || (taskMain.id == 8 && taskMain.index >= 3)) {
            listAccessMap.add(47);
        }
        if (taskMain.id > 9 || (taskMain.id == 9 && taskMain.index >= 2)) {
            listAccessMap.add(46);
        }
        if (taskMain.id >= 11) {
            listAccessMap.add(5);
            listAccessMap.add(13);
            listAccessMap.add(20);
        }
        if (taskMain.id >= 13) {
            listAccessMap.add(27);
            listAccessMap.add(28);
            listAccessMap.add(29);

            listAccessMap.add(31);
            listAccessMap.add(32);
            listAccessMap.add(33);

            listAccessMap.add(35);
            listAccessMap.add(36);
            listAccessMap.add(37);
        }
        if (taskMain.id >= 15) {
            listAccessMap.add(30);
            listAccessMap.add(34);
            listAccessMap.add(38);
        }
        if (this.typeTraining >= 2) {
            listAccessMap.add(45);
        }
        if (characterInfo.getPower() < 1500000) {
            listAccessMap.add(111);
        }
    }

    public Skill getSkillByID(int id) {
        for (Skill skill : skills) {
            if (skill.template.id == id) {
                return skill;
            }
        }
        return null;
    }
}