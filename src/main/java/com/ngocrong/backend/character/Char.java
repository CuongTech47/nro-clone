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
import com.ngocrong.backend.server.Config;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.shop.Shop;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.skill.SkillBook;
import com.ngocrong.backend.skill.SpecialSkill;
import com.ngocrong.backend.task.Task;
import com.ngocrong.backend.task.TaskText;
import com.ngocrong.backend.top.Top;
import com.ngocrong.backend.top.TopInfo;
import com.ngocrong.backend.util.Utils;
import org.apache.commons.lang3.StringUtils;
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
    private ArrayList<KeyValue> listMapTransport;

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
    private boolean isNewMember;
    private boolean isNhapThe;
    private boolean isLoggedOut;
    private boolean isMask;
    private boolean isFreeze, isSleep, isBlind, isProtected, isHuytSao, isHeld, isCritFirstHit;
    private boolean isBuaTriTue, isBuaManhMe, isBuaDaTrau, isBuaOaiHung, isBuaBatTu, isBuaDeoDai, isBuaThuHut,
            isBuaDeTu, isBuaTriTue3, isBuaTriTue4;
    private boolean isDead;
    private boolean isKhangTDHS;
    private boolean isHaveMount;
    private boolean isGoBack;
    private boolean isCuongNo, isBoHuyet, isGiapXen, isBoKhi, isAnDanh, isMayDo, isDuoiKhi, isPudding, isXucXich, isKemDau, isMiLy, isSushi;
    private boolean isNoNeedToConfirm;
    private boolean isVoHinh;
    private boolean isUnaffectedCold;
    private boolean isChocolate, isStone;
    private boolean isSkillSpecial;
    private boolean isTrading;
    private boolean isCharge, isRecoveryEnergy;
    private boolean isMonkey;
    private boolean isHaveEquipTeleport;
    private boolean isHaveEquipSelfExplosion;
    private boolean isExploded;
    private boolean isAutoPlay;
    private boolean setThienXinHang, setKirin, setSongoku, setPicolo, setOcTieu, setPikkoroDaimao, setKakarot, setCaDic,
            setNappa, setThanLinh;
    private boolean isCold;
    private boolean isMod;
    private boolean isHaveEquipInvisible, isHaveEquipTransformIntoChocolate, isHaveEquipTransformIntoStone,
            isHaveEquipMiNuong, isHaveEquipBulma, isHaveEquipXinbato, isHaveEquipBuiBui, isDoSaoPhaLe;
    private boolean isInvisible;
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
    private KeyValue currMap;

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
                this.body = itemBody[0].template.part;
            }
            if (itemBody[1] != null) {
                this.leg = itemBody[1].template.part;
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
            if (itemBody != null && itemBody[5] != null && itemBody[5].template.part == -1 && itemBody[5].isNhapThe) {
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
                this.head = itemBody[5].template.part;
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
            switch (itemBody[8].template.id) {
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

    public void setTimeForItemtime(int i, int seconds) {
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
                System.out.println("item :: " + item);
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

    public void addItemTime(ItemTime item) {
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
                if (item.template.type == 23 || item.template.type == 24) {
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

    public void addGold(long gold) {
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
        if (item.template.type == Item.TYPE_GOLD) {
            addGold(item.quantity);
            return true;
        }
        if (item.template.type == Item.TYPE_DIAMOND) {
            addDiamond(item.quantity);
            return true;
        }
        if (item.template.type == Item.TYPE_DIAMOND_LOCK) {
            addDiamondLock(item.quantity);
            return true;
        }
        if (item.template.type == Item.TYPE_AMULET) {
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
                if (item.template.type == Item.TYPE_DAUTHAN) {
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

    public void removeItem(int index, int quantity) {
        Item item = this.itemBag[index];
        if (item != null) {
            int quant = item.quantity;
            quant -= quantity;
            if (quant <= 0) {
                this.itemBag[index] = null;
                quant = 0;
            } else {
                item.quantity = quant;
            }
            service.updateBag(index, quant);
        }
    }

    public boolean isBagFull() {
        for (Item item : this.itemBag) {
            if (item == null) {
                return false;
            }
        }
        return true;
    }

    public Item getItemInBag(int thoiVang) {
        for (Item item : this.itemBag) {
            if (item != null && item.id == id) {
                return item;
            }
        }
        return null;
    }

    public void checkMove(Message mss) {
        try {
            long now = System.currentTimeMillis();
            int s = (int) (now - lastTick) / 1000;
            int seconds = mss.getReader().readInt();
            int m = 100 / (characterInfo.getSpeed() / 2);
            if (tickMove != 100 || Math.abs(seconds - s) > 2 || s < m) {
                numCheck++;
            }
            tickMove = 0;
        } catch (IOException ex) {
            logger.error("check move", ex);
        }
    }

    public void requestPean() {
        if (isAutoPlay && !magicTree.isUpgrade) {
            boolean flag2 = false;
            for (int j = 0; j < this.itemBag.length; j++) {
                Item item = this.itemBag[j];
                if (item != null && item.template.type == 6) {
                    flag2 = true;
                    break;
                }
            }
            if (!flag2 && magicTree != null) {
                magicTree.harvest(this);
            }
        }
    }

    public void achievement(Message mss) {
        try {
            if (zone.map.mapID != 47 && zone.map.mapID != 84) {
                return;
            }
            byte index = mss.getReader().readByte();
            if (index < 0 || index >= achievements.size()) {
                return;
            }
            Achievement achive = this.achievements.get(index);
            if (achive.isFinish() && !achive.isRewarded()) {
                int reward = achive.getReward();
                achive.setIsRewarded(true);
                addDiamondLock(reward);
                service.achievement((byte) 1, index);
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void finishLoadMap() {
        if (taskMain != null && taskMain.id == 4 && taskMain.index == 0) {
            taskNext();
        }
        //}
        int mapID = -1;

        if (zone != null) {
            mapID = zone.map.mapID;
        }

        if (mapID == 39 || mapID == 40 || mapID == 41) {
            String petName = new String[]{"Puaru", "Piano", "Icarus"}[gender];
            service.openUISay((short) NpcName.CON_MEO, String.format(
                    "Chào mừng bạn đến với thế giới Ngọc Rồng!\nMình là %s sẽ đồng hành cùng bạn ở thế giới này\nĐể di chuyển, hãy chạm 1 lần vào nơi muốn đến",
                    petName), (short) getPetAvatar());
        }
        if (taskMain != null) {
            Task task = taskMain;
            if (task.id == 0 && task.index == 1 && task.mapTasks[1] == mapID) {
                String text = TaskText.TASK_0_1[gender];
                service.openUISay(NpcName.CON_MEO, text, getPetAvatar());
                taskNext();
            }
            if (mapID == 47) {
                if (task.id == 8 && task.index == 3) {
                    updateTask(9);
                }
            }
            if (mapID == 46) {
                if (task.id == 9 && task.index == 2) {
                    taskNext();
                }
            }
            if (task.id == 11 && task.index == 0) {
                int map = (new int[]{5, 13, 20})[gender];
                if (mapID == map) {
                    taskNext();
                }
            }
        }
        loadEffectFreeze();
        loadEffectSkillOnMob();
        if (this.mobMe != null) {
            service.mobMeUpdate(this, null, -1, (byte) -1, (byte) 0);
        }
        if (this.petFollow != null) {
            service.petFollow(this, (byte) 1);
        }
        service.updateBag(this);
        List<Char> list = zone.getListChar(Zone.TYPE_ALL);
        for (Char _c : list) {
            if (_c != this) {
                service.playerAdd(_c);
                if (_c.petFollow != null) {
                    service.petFollow(_c, (byte) 1);
                }
                loadEffectSkillPlayer(_c);
                if (_c.mobMe != null) {
                    service.mobMeUpdate(_c, null, -1, (byte) -1, (byte) 0);
                }
            }
        }
        if (isAutoPlay) {
            service.autoPlay(true);
        }
    }

    private void loadEffectSkillOnMob() {
        List<Mob> list2 = zone.getListMob();
        for (Mob mob : list2) {
            if (mob.status == 0) {
                continue;
            }
            if (mob.isBlind) {
                service.setEffect(null, mob.mobId, Skill.ADD_EFFECT, Skill.MONSTER, (byte) 40);
            }
            if (mob.isSleep) {
                service.setEffect(null, mob.mobId, Skill.ADD_EFFECT, Skill.MONSTER, (byte) 41);
            }
            if (mob.isChangeBody) {
                service.changeBodyMob(mob, (byte) 1);
            }
        }
    }

    private void loadEffectSkillPlayer(Char _char) {
        if (_char.isSleep) {
            service.setEffect(null, _char.id, Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 41);
        }
        if (_char.isProtected) {
            service.setEffect(null, _char.id, Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 33);
        }
        if (_char.isBlind) {
            service.setEffect(null, _char.id, Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 40);
        }
        if (_char.isRecoveryEnergy) {
            service.skillNotFocus(_char.id, (short) _char.select.id, (byte) 1, null, null);
        }
        if (_char.isHeld && _char.hold.getHolder() == _char) {
            if (_char.hold.getDetainee() instanceof Mob) {
                Mob mob = (Mob) _char.hold.getDetainee();
                service.setEffect(_char.hold, mob.mobId, Skill.ADD_EFFECT, Skill.MONSTER, (byte) 32);
            } else {
                Char _c = (Char) _char.hold.getDetainee();
                service.setEffect(_char.hold, _c.id, Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 32);
            }
        }
        if (_char.isCharge()) {
            switch (_char.select.template.id) {
                case SkillName.QUA_CAU_KENH_KHI:
                case SkillName.MAKANKOSAPPO:
                    service.skillNotFocus(_char.id, (short) _char.select.id, (byte) 4, null, null);
                    break;

                case SkillName.BIEN_HINH:
                    service.skillNotFocus(_char.id, (short) _char.select.id, (byte) 6, null, null);
                    break;

                case SkillName.TU_PHAT_NO:
                    service.skillNotFocus(_char.id, (short) _char.select.id, (byte) 7, null, null);
                    break;
            }
        }
    }

    private void loadEffectFreeze() {
        ArrayList<Char> chars = new ArrayList<>();
        ArrayList<Mob> mobs = new ArrayList<>();
        if (zone != null) {
            List<Char> list = zone.getListChar(Zone.TYPE_ALL);
            for (Char _char : list) {
                if (_char != this) {
                    if (_char.isFreeze) {
                        chars.add(_char);
                    }
                }
            }
            List<Mob> list2 = zone.getListMob();
            for (Mob mob : list2) {
                if (mob.isFreeze) {
                    mobs.add(mob);
                }
            }
        }
        service.skillNotFocus(this.id, (short) 42, (byte) 0, mobs, chars);
    }

    private void updateTask(int taskID) {
        if (taskMain != null) {
            int power = taskMain.rewardPower;
            int potential = taskMain.rewardPotential;
            int gold = taskMain.rewardGold;
            int gem = taskMain.rewardGem;
            int gemLock = taskMain.rewardGemLock;
            if (power > 0) {
                addExp(CharacterInfo.POWER, power, false, false);
                service.serverMessage(String.format("Bạn được thưởng %d sức mạnh", power));
            }
            if (potential > 0) {
                addExp(CharacterInfo.POTENTIAL, potential, false, false);
            }
            if (gold > 0) {
                addGold(gold);
            }
            if (gem > 0) {
                addDiamond(gem);
            }
            if (gemLock > 0) {
                addDiamondLock(gem);
            }
        }
        Task task = new Task();
        task.id = taskID;
        task.count = 0;
        task.index = 0;
        task.initTask(this.gender);
        taskMain = task;
        setListAccessMap();
        service.setTask();
    }

    public void addExp(byte type, long exp, boolean canX2, boolean isAddForMember) {
        if (characterInfo.getPower() >= characterInfo.getPowerLimitMark().getPower()) {
            return;
        }
        if (characterInfo.getPower() + exp >= characterInfo.getPowerLimitMark().getPower()) {
            exp = characterInfo.getPowerLimitMark().getPower() - characterInfo.getPower();
        }
        if (canX2) {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            exp *= config.getExp();
            if (isDuoiKhi) {
                exp *= 2;
            }
            int mul = 1;
            if (isBuaTriTue4) {
                if (isBuaTriTue) {
                    mul = 6;
                } else {
                    mul = 4;
                }
            } else if (isBuaTriTue3) {
                if (isBuaTriTue) {
                    mul = 5;
                } else {
                    mul = 3;
                }
            } else if (isBuaTriTue) {
                mul = 2;
            }
            exp *= mul;
        }
        if (exp <= 0) {
            return;
        }
        if (isDisciple()) {
            Disciple disciple = (Disciple) this;
            int dLevel = this.characterInfo.getLevel() - disciple.master.characterInfo.getLevel();
            int percent = -20;
            if (dLevel < 0) {
                percent = dLevel * 10;
            }
            long exp2 = exp;
            exp2 += exp2 * percent / 100;
            if (exp2 <= 0) {
                exp2 = 1;
            }
            disciple.master.addExp(type, exp2, canX2, isAddForMember);

        } else if (isAddForMember) {
            if (clan != null) {
                exp = exp * 90 / 100;
                List<Char> list = zone.getMemberSameClan(this);
                if (list.size() - 1 > 0) {
                    clan.powerPoint += exp;
                    for (Char _c : list) {
                        int d = Math.abs(characterInfo.getLevel() - _c.characterInfo.getLevel());
                        exp -= exp * (d * 5L) / 100;
                        _c.addExp(CharacterInfo.POTENTIAL, exp, false, false);
                    }
                }
            }
        }
        long prePower = characterInfo.getPower();
        characterInfo.addPowerOrPotential(type, exp);
        Top topPower = Top.getTop(Top.TOP_POWER);
        if (topPower != null) {
            if (characterInfo.getPower() > topPower.getLowestScore()) {
                TopInfo in = topPower.getTopInfo(this.id);
                if (in != null) {
                    in.score = characterInfo.getPower();
                    in.head = this.head;
                    in.body = this.body;
                    in.leg = this.leg;
                    in.info = String.format("Sức mạnh: %s", Utils.currencyFormat(characterInfo.getPower()));
                } else {
                    in = new TopInfo();
                    in.playerID = this.id;
                    in.name = this.name;
                    in.score = characterInfo.getPower();
                    in.head = this.head;
                    in.body = this.body;
                    in.leg = this.leg;
                    in.info = String.format("Sức mạnh: %s", Utils.currencyFormat(characterInfo.getPower()));
                    in.info2 = "";
                    topPower.addTopInfo(in);
                }
                topPower.updateLowestScore();
            }
        }
        if (!isDisciple()) {
            if (prePower < 1500000 && characterInfo.getPower() >= 1500000) {
                setListAccessMap();
            }
        }
        if (taskMain != null) {
            if (taskMain.id == 7 && taskMain.index == 0) {
                if (characterInfo.getPower() >= 16000) {
                    taskNext();
                }
            }
            if (taskMain.id == 8 && taskMain.index == 0) {
                if (characterInfo.getPower() >= 40000) {
                    taskNext();
                }
            }
            if (taskMain.id == 14 && taskMain.index == 0) {
                if (characterInfo.getPower() >= 200000) {
                    taskNext();
                }
            }
            if (taskMain.id == 15 && taskMain.index == 0) {
                if (characterInfo.getPower() >= 500000) {
                    taskNext();
                }
            }
        }
    }

    public void collectionBookACtion(Message ms) {
        try {
            byte action = ms.getReader().readByte();
            int id = -1;
            if (ms.getReader().available() > 0) {
                id = ms.getReader().readShort();
            }
            if (action == 0) {
                service.viewCollectionBook();
            }
            if (action == 1) {
                Card c = getCollectionCard(id);
                if (c != null) {
                    if (c.level > 0) {
                        if (!c.isUse) {
                            long size = cards.stream().filter(a -> a.isUse).count();
                            if (size >= 3) {
                                return;
                            }
                        }
                        c.isUse = !c.isUse;
                        service.useCard(c.id, c.isUse);
                        characterInfo.setCharacterInfo();
                        service.loadPoint();
                        zone.mapService.playerLoadBody(this);
                        int eff = idAuraEff;
                        setAuraEffect();
                        if (eff != idAuraEff) {
                            zone.mapService.setIDAuraEff(this.id, this.idAuraEff);
                        }
                    }
                    return;
                }

            }
        } catch (IOException ex) {
            logger.error("collectionBookAction error!", ex);
        }
    }

    private Card getCollectionCard(int id) {
        for (Card c : cards) {
            if (c.id == id) {
                return c;
            }
        }
        return null;
    }

    public void specialSkill(Message mss) {
        try {
            byte index = mss.getReader().readByte();
            if (index == 0) {
                menus.clear();
                StringBuilder sb = new StringBuilder();
                sb.append("Nội tại là một kỹ năng bị động hỗ trợ đặc biệt");
                sb.append("\n");
                sb.append("Bạn có muốn mở hoặc thay đổi nội tại không?");
                menus.add(new KeyValue(1111, "Xem\ntất cả\nNội Tại"));
                menus.add(new KeyValue(1112, "Mở\nNội Tại"));
                menus.add(new KeyValue(1113, "Mở VIP"));
                menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
                service.openUIConfirm(NpcName.CON_MEO,sb.toString(),getPetAvatar(),menus);
            }
        } catch (IOException ex) {
            logger.debug("special skill error", ex);
        }
    }

    public void mapTransport(Message ms) {
        if (escortedPerson != null) {
            service.serverMessage(String.format("Bạn đang hộ tống %s, không thể thực hiện.", escortedPerson.getName()));
            return;
        }
        if (listMapTransport == null) {
            return;
        }
        lock.lock();
        try {
            int index = ms.getReader().readByte();
            if (index < 0 || index >= listMapTransport.size()) {
                return;
            }
            int cmd = getCommandTransport();
            if (cmd == 0) {
                Item item = itemBag[capsule];
                if (item != null && (item.id == 193 || item.id == 194)) {
                    if (item.id == 193) {
                        removeItem(capsule, 1);
                    }
                } else {
                    return;
                }
                KeyValue<Integer, String> keyValue = listMapTransport.get(index);
                int mapID = keyValue.getKey();
                TMap curr = zone.map;
                Zone z = zone;
                String planet = "";
                switch (curr.planet) {
                    case 0:
                        planet = "Trái đất";
                        break;

                    case 1:
                        planet = "Namêc";
                        break;

                    case 2:
                        planet = "Xay da";
                        break;
                }
                if (curr.isCold()) {
                    planet = "Cold";
                } else if (curr.isFuture()) {
                    planet = "Tương lai";
                } else if (curr.isNappa()) {
                    planet = "Fide";
                }
                isGoBack = currMap == keyValue;
                TMap map = zone.map;
                if (!map.isCantGoBack()) {
                    currMap = new KeyValue(curr.mapID, "Về chỗ cũ: " + curr.name, planet);
                } else {
                    currMap = null;
                }
                teleport(mapID);
                currZoneId = z.zoneID;
                listMapTransport = null;
            } else if (cmd == 1) {
//                if (MapManager.getInstance().blackDragonBall == null) {
//                    service.serverMessage2("Đã kết thúc");
//                    return;
//                }
                KeyValue<Integer, String> keyValue = listMapTransport.get(index);
                int mapID = keyValue.getKey();
                TMap map = MapManager.getInstance().getMap(mapID);
                short x = calculateX(map);
                zone.leave(this);
                this.x = x;
                this.y = map.collisionLand(x, (short) 24);
                int zoneID = map.getZoneID();
                map.enterZone(this, zoneID);
            }

        } catch (IOException ex) {
            logger.error("failed!", ex);
        } finally {
            lock.unlock();
        }
    }

    public void move(Message ms) {
        try {
            tickMove++;
            if (tickMove > 105) {
                numCheck++;
            }
            if (tickMove == 1) {
                lastTick = System.currentTimeMillis();
            }
            if (this.isRecoveryEnergy) {
                stopRecoveryEnery();
            }
            if (!meCanMove()) {
                return;
            }
            if (taskMain != null) {
                if (taskMain.id == 0 && taskMain.index == 0) {
                    String text = TaskText.TASK_0_0[gender];
                    service.openUISay(NpcName.CON_MEO, text, getPetAvatar());
                    taskNext();
                }
            }
            if (this.hold != null) {
                if (this.hold.getHolder() == this) {
                    this.hold.close();
                } else {
                    return;
                }
            }
            byte type = ms.getReader().readByte();// 0 duoi dat, 1 bay
            this.preX = this.x;
            this.preY = this.y;
            this.x = ms.getReader().readShort();
            if (ms.getReader().available() > 0) {
                this.y = ms.getReader().readShort();
            }

            if (this.mobMe != null) {
                this.mobMe.x = this.x;
                this.mobMe.y = (short) (this.y - 40);
            }
            if (type == 0) {
                this.y = zone.map.collisionLand(this.x, this.y);
            } else {
                achievements.get(5).addCount(Utils.getDistance(this.preX, this.preY, this.x, this.y) / 10);// Khinh công
                // thành thạo
                if ((zone.map.tileTypeAtPixel(this.x, this.y)) == TMap.T_EMPTY) {
                    if (!this.isHaveMount) {
//                        info.mp -= this.info.originalMP / 100 * (!isMonkey ? 1 : 2);
                        characterInfo.setMp(characterInfo.getMp() - this.characterInfo.getBaseMP() / 100 * (!isMonkey ? 1 : 2));
                        if (this.characterInfo.getMp() < 0) {
                            this.characterInfo.setMp(0);
                        }
                    }
                }
            }
            if (myDisciple != null && myDisciple.discipleStatus == 0) {
                myDisciple.move();
            }
            if (miniDisciple != null) {
                miniDisciple.move();
            }
            if (escortedPerson != null) {
                escortedPerson.move();
            }
            this.zone.mapService.move(this);
            this.lastTimeMove = System.currentTimeMillis();
            if ((this.x <= 24 || this.x >= this.zone.map.width - 24 || this.y < 0
                    || this.y >= this.zone.map.height - 24) && zone.map.findWaypoint(x, y) == null) {
                this.x = this.preX;
                this.y = this.preY;
                zone.mapService.setPosition(this, (byte) 0);
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void chatMap(Message ms) {
        try {
            String text = ms.getReader().readUTF();
            if (!text.isEmpty()) {
                if (text.length() > 100) {
                    return;
                }
                if (myDisciple != null && !isNhapThe) {
                    String tmp = Utils.unaccent(text);
                    if (tmp.equalsIgnoreCase("di theo") || tmp.equalsIgnoreCase("follow")) {
                        petStatus((byte) 0);
                    }
                    if (tmp.equalsIgnoreCase("bao ve") || text.equalsIgnoreCase("protect")) {
                        petStatus((byte) 1);
                    }
                    if (tmp.equalsIgnoreCase("tan cong") || tmp.equalsIgnoreCase("attack")) {
                        petStatus((byte) 2);
                    }
                    if (tmp.equalsIgnoreCase("ve nha") || tmp.equalsIgnoreCase("go home")) {
                        petStatus((byte) 3);
                    }
                    String words = "ten con la ";
                    if (tmp.startsWith(words)) {
                        String name = text.substring(words.length()).trim();
                        if (StringUtils.isBlank(tmp)) {
                            return;
                        }
                        int length = name.length();
                        if (length >= 5 && length <= 15) {
                            int index = getIndexBagById(400);
                            if (index == -1) {
                                return;
                            }
                            removeItem(index, 1);
                            myDisciple.setName(name);
                            zone.mapService.playerLoadAll(myDisciple);
                            service.chat(myDisciple, "Cảm ơn sư phụ, tên con từ nay sẽ là " + name);
                        } else {
                            service.serverMessage("Tên không hợp lệ!");
                        }
                        return;
                    }

                }
                zone.mapService.chat(this, text);
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void petStatus(byte status) {
        if (myDisciple != null) {
            myDisciple.discipleStatus = status;
            if (isNhapThe) {
                service.serverMessage("Không thể thực hiện");
                return;
            }
            switch (status) {
                case 0:
                    service.chat(myDisciple, "Ok con theo sư phụ");
                    break;

                case 1:
                    service.chat(myDisciple, "Ok con sẽ bảo vệ sư phụ");
                    break;

                case 2:
                    service.chat(myDisciple, "Ok sư phụ để con lo cho");
                    break;

                case 3:
                    if (myDisciple.zone != null) {
                        service.chat(myDisciple, "Ok con về, bibi sư phụ");
                        if (!myDisciple.isDead()) {
                            myDisciple.clearEffect();
                            if (myDisciple.isMonkey()) {
                                myDisciple.timeOutIsMonkey();
                            }
                            Utils.setTimeout(() -> {
                                if (myDisciple.zone != null) {
                                    zone.leave(myDisciple);
                                }
                            }, 2000);
                        }
                    }
                    break;

                case 4:
                    myDisciple.discipleStatus = 3;
                    if (zone.map.isDauTruong() || isDead() || myDisciple.isDead()) {
                        service.serverMessage("Không thể thực hiện");
                        return;
                    }
                    long now = System.currentTimeMillis();
                    if (now - timeAtSplitFusion >= 600000) {
                        typePorata = 0;
                        fusion((byte) 4);
                    } else {
                        long timeAgo = 600000 - (now - timeAtSplitFusion);
                        service.serverMessage(String.format("Chỉ có thể thực hiện sau %s", Utils.timeAgo((int) (timeAgo / 1000))));
                    }
                    break;

                case 5:
                    if (gender != 1) {
                        return;
                    }
                    if (myDisciple.isDead()) {
                        service.serverMessage("Không thể thực hiện");
                        return;
                    }
                    if (!isNhapThe) {
                        long power = myDisciple.characterInfo.getPower();
                        if (power > 0) {
                            addExp(CharacterInfo.ALL, power, false, false);
                        }
                        fusion((byte) 5);
                        deleteDisciple();
                    } else {
                        service.serverMessage("Không thể thực hiện");
                    }
                    break;
            }
            if (myDisciple != null && myDisciple.isDead()) {
                service.chat(myDisciple, "Sư phụ ơi cho con đậu thần");
            }
        }
    }

    private void deleteDisciple() {
        try {
            if (myDisciple != null) {
                if (myDisciple.zone != null) {
                    myDisciple.zone.leave(myDisciple);
                }
                GameRepo.getInstance().discipleRepo.deleteById(myDisciple.getId());
                myDisciple = null;
            }
            service.petInfo((byte) 0);
        } catch (Exception e) {
            logger.error("failed!", e);
        }
    }

    private void fusion(byte type) {
        if (type != 5) {
            this.fusionType = type;
        }
        if (type == 4 || type == 6) {// hợp thể
            if (!isNhapThe) {
                myDisciple.clearEffect();
                if (myDisciple.isMonkey()) {
                    myDisciple.timeOutIsMonkey();
                }
                if (zone != null) {
                    zone.leave(myDisciple);
                }
                myDisciple.discipleStatus = 3;
                this.isNhapThe = true;
                if (type == 4) {
                    ItemTime item = new ItemTime(ItemTimeName.HOP_THE, gender == 1 ? 3901 : 3790, 600, true);
                    addItemTime(item);
                }
            }

        } else if (type == 1) {// tách hợp thể
            if (isNhapThe) {
                this.isNhapThe = false;
                myDisciple.discipleStatus = 1;
                if (zone != null && !zone.map.isMapSingle()) {
                    myDisciple.followMaster();
                    zone.enter(myDisciple);
                }
            }
        }
        lastTimeUsePorata = System.currentTimeMillis();
        updateSkin();
        myDisciple.characterInfo.setCharacterInfo();
        characterInfo.setCharacterInfo();
        service.loadPoint();
        zone.mapService.playerLoadBody(this);
        zone.mapService.updateBody((byte) 0, this);
        if (isNhapThe) {
            characterInfo.recovery(CharacterInfo.ALL, 100, true);
        }
        zone.mapService.fusion(this, type);
    }

    public void requestChangeMap() {
        lock.lock();
        try {
            if (this.isDead) {
                return;
            }
            Waypoint way = (Waypoint) zone.map.findWaypoint(this.x, this.y);
            if (way != null) {
                int mapID = way.next;
                boolean flag = false;
                if (zone.map.isBarrack() || zone.map.isTreasure()) {
                    List<Mob> list2 = zone.getListMob();
                    for (Mob mob : list2) {
                        if (!mob.isDead()) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        List<Char> list = zone.getListChar(Zone.TYPE_BOSS);
                        flag = !list.isEmpty();
                    }
                    TMap map = MapManager.getInstance().getMap(mapID);
                    if (flag) {
                        way = map.getWaypointByNextID(zone.map.mapID);
                        if (way != null) {
                            this.x = way.x;
                            this.y = way.y;
                        } else {
                            this.x = this.preX;
                            this.y = this.preY;
                        }
                        service.resetPoint();
                        service.serverMessage("Chưa hạ hết đối thủ");
                    } else {
                        this.x = way.x;
                        this.y = way.y;
                        if (map.isBarrack() || map.isTreasure()) {
                            if (clan != null) {
                                if (map.isBarrack()) {
                                    zone.leave(this);
                                    clan.barrack.enterMap(mapID, this);
                                } else if (map.isTreasure()) {
                                    zone.leave(this);
                                    clan.treasure.enterMap(mapID, this);
                                }
                            } else {
                                goHome();
                            }
                        } else {
                            zone.leave(this);
                            int zoneId = map.getZoneID();
                            map.enterZone(this, zoneId);
                        }
                    }
                } else {
                    TMap map = MapManager.getInstance().getMap(mapID);
                    if (checkCanEnter(mapID)) {
                        zone.leave(this);
                        this.x = way.x;
                        this.y = way.y;
                        if (map.isMapSingle()) {
                            enterMapSingle(map);
                        } else {
                            int zoneId = map.getZoneID();
                            map.enterZone(this, zoneId);
                        }
                    } else {
                        way = map.getWaypointByNextID(zone.map.mapID);
                        if (way != null) {
                            this.x = way.x;
                            this.y = way.y;
                        } else {
                            this.x = this.preX;
                            this.y = this.preY;
                        }
                        service.resetPoint();
                        service.serverMessage("Bạn chưa thể đến khu vực này");
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }


    public boolean isHaveFood() {
        return isPudding() || isXucXich() || isKemDau() || isMiLy() || isSushi();
    }

    public long getTotalGem() {
        return (long) getDiamond() + (long) getDiamondLock();
    }

    public void subDiamond(int diamond) {
        if (this.diamondLock < diamond) {
            diamond -= this.diamondLock;
            this.diamondLock = 0;
            this.diamond -= diamond;
        } else {
            this.diamondLock -= diamond;
        }
        service.loadInfo();
    }

    public void transformIntoChocolate(short damage, int time) {
        isChocolate = true;
        this.dameDown = dameDown;
        ItemTime item = new ItemTime(ItemTimeName.SOCOLA, 4127, time, false);
        addItemTime(item);
        updateSkin();
        if (dameDown == 0) {
            characterInfo.setCharacterInfo();
            service.loadPoint();
            zone.mapService.playerLoadBody(this);
        }
        zone.mapService.updateBody((byte) 0, this);
    }

    public boolean useSkill(Object obj) {
        long now = System.currentTimeMillis();
        lastAttack = now;
        Skill skill = this.select;
        long manaUse = skill.manaUse;
        if (this.select.template.manaUseType == 1) {
            manaUse = Utils.percentOf(this.characterInfo.getFullMP(), manaUse);
        }
        if (isBoss()) {
            manaUse = 0;
        }
        if (skill.template.id == SkillName.TRI_THUONG) {
            if (obj instanceof Char) {
                if (achievements != null) {
                    achievements.get(14).addCount(1);// kỹ năng thành thạo
                }
                skill.lastTimeUseThisSkill = now;
                Char _char = (Char) obj;
                int distance = Utils.getDistance(0, 0, skill.dx, skill.dy);
                List<Char> list = zone.getListChar(Zone.TYPE_HUMAN, Zone.TYPE_PET);
                for (Char _c : list) {
                    if (_c != this) {
                        int distance2 = Utils.getDistance(_c.x, _c.y, _char.x, _char.y);
                        if (distance2 < distance) {
                            zone.mapService.chat(_c, String.format("Cảm ơn %s đã cứu mình", this.name));
                            _c.revival(skill.damage);
                        }
                    }
                }
                this.characterInfo.recovery(CharacterInfo.HP, skill.damage, true);
                characterInfo.setMp(characterInfo.getMp() - manaUse);
            }
            return false;
        }
        if (skill.template.id == SkillName.KAIOKEN) {
            long percent = this.characterInfo.getHp() * 100 / this.characterInfo.getFullHP();
            if (percent <= 10) {
                return false;
            }
            this.characterInfo.recovery(CharacterInfo.HP, -10, true);
        }
        if (skill.template.id == SkillName.TROI) {
            if (obj instanceof Mob) {
                Mob mob = (Mob) obj;
                if (mob.hold != null) {
                    return false;
                }
            } else {
                Char _c = (Char) obj;
                if (_c.hold != null) {
                    return false;
                }
            }
            skill.lastTimeUseThisSkill = now;
            Hold hold = new Hold(this.zone, this, obj, skill.damage);
            hold.start();
            if (specialSkill != null) {
                if (specialSkill.id == 7) {
                    setPercentDamageBonus(specialSkill.param);
                }
            }
            this.isCritFirstHit = true;
            characterInfo.setMp(characterInfo.getMp() - manaUse);
            return false;
        }
        return true;
    }

    private void revival(short damage) {
        if (this.isDead) {
            this.statusMe = 1;
            this.isDead = false;
            service.sendMessage(new Message(Cmd.ME_LIVE));
            zone.mapService.playerLoadLive(this);
        }
        this.characterInfo.recovery(CharacterInfo.ALL, damage, true);
    }

    public void kill(Object victim) {
        if (victim instanceof Char) {
            Char v = (Char) victim;
            if (!isAnDanh && isHuman() && v.isHuman()) {
                v.addEnemy(this);
            }
            if (isHuman() && v.isBoss()) {
//                if (v instanceof Broly) {
//                    Broly broly = (Broly) v;
//                    if (broly.isSuper) {
//                        if (myDisciple == null) {
//                            createDisciple(1);
//                        }
//                    }
//                }
            }
            if (isHuman() && v.isHuman()) {
                if (testCharId == v.id && v.testCharId == this.id && betAmount == 0) {
//                    removeEnemy(v);
                }
                if (zone.map.isBaseBabidi() && this.flag != v.flag) {
                    addAccumulatedPoint(5);
                    v.addAccumulatedPoint(-5);
                }
            }
        } else {
            Mob mob = (Mob) victim;
            if (achievements != null) {
//                if (mob.templateId == MobName.MOC_NHAN) {
//                    achievements.get(7).addCount(1);// tập luyện bài bản
//                }
//                if (mob.type == 4) {
//                    achievements.get(6).addCount(1);// thợ săn thiện xạ
//                }
//                if (mob.levelBoss != 0) {
//                    achievements.get(12).addCount(1);// đánh bại siêu quái
//                }
            }
            if (taskMain != null) {
                Task task = taskMain;
                switch (task.id) {
                    case 1:
//                        if (task.index == 0) {
//                            if (mob.templateId == MobName.MOC_NHAN) {
//                                updateTaskCount(1);
//                            }
//                        }
                        break;

                    case 6:// nhiệm vụ
//                        if (task.index <= 2) {
//                            int[][] mobTask = {{MobName.KHUNG_LONG_ME, MobName.LON_LOI_ME, MobName.QUY_DAT_ME}, {MobName.LON_LOI_ME, MobName.QUY_DAT_ME, MobName.KHUNG_LONG_ME}, {MobName.QUY_DAT_ME, MobName.KHUNG_LONG_ME, MobName.LON_LOI_ME}};
//                            int mobId = mobTask[gender][task.index];
//                            if (mob.templateId == mobId) {
//                                updateTaskCount(1);
//                            }
//                        }
//                        break;

                    case 7:// nhiệm vụ giải cứu
//                        if (task.index == 1) {
//                            int mobId = (new int[]{MobName.THAN_LAN_BAY, MobName.PHI_LONG, MobName.QUY_BAY})[gender];
//                            if (mob.templateId == mobId) {
//                                updateTaskCount(1);
//                            }
//                        }
                        break;

                    case 13: {// nhiem vu danh heo
//                        int[] mobs = {MobName.HEO_RUNG, MobName.HEO_DA_XANH, MobName.HEO_XAYDA};
//                        if (task.index < 3 && mob.templateId == mobs[task.index]) {
//                            List<Char> mems = zone.getMemberSameClan(this);
//                            if (mems.size() - 1 >= 2) {
//                                updateTaskCount(1);
//                            }
//                        }
//                        break;
                    }

                    case 15: {// nhiem vu bulon
//                        int[] mobs = {MobName.BULON, MobName.UKULELE, MobName.QUY_MAP};
//                        if (task.index > 0 && task.index < 4 && mob.templateId == mobs[task.index - 1]) {
//                            List<Char> mems = zone.getMemberSameClan(this);
//                            if (mems.size() - 1 >= 2) {
//                                updateTaskCount(1);
//                            }
//                        }
//                        break;
                    }
                }
            }
        }
    }

    private void createDisciple(int type) {
        try {
            if (myDisciple == null) {
                lastAttack = System.currentTimeMillis();
                Disciple disciple = new Disciple();
                disciple.typeDisciple = (byte) type;
                disciple.setId(-this.id);
                disciple.setName("Đệ tử");
                disciple.itemBody = new Item[10];
                disciple.setClassId((byte) Utils.nextInt(3));
                disciple.setGender(disciple.getClassId());
                if (type != 0) {
                    disciple.setClassId(this.gender);
                    disciple.setGender(disciple.getClassId());
                }
                disciple.characterInfo = new CharacterInfo(disciple);
                disciple.characterInfo.setCharacter(disciple);
                disciple.characterInfo.setPowerLimited();
                disciple.characterInfo.applyCharLevelPercent();
                disciple.characterInfo.setSatamina();
                disciple.setSkills(new ArrayList<>());
                disciple.skillOpened = 0;
                disciple.learnSkill();
                disciple.discipleStatus = 0;
                disciple.characterInfo.setCharacterInfo();
                disciple.characterInfo.recovery(CharacterInfo.ALL, 100, false);
                disciple.service = new CharService(disciple);
                disciple.setDefaultPart();
                myDisciple = disciple;
                disciple.saveData();
                disciple.setMaster(this);
                disciple.followMaster();
                service.petInfo((byte) 1);
                zone.enter(myDisciple);
                service.chat(myDisciple, "Sư phụ hãy nhận con làm đệ tử");
            }
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    private void learnSkill(Skill skill, int type, int index, Item item) {
        try {
            if (type == 0) {
                skills.add(skill.clone());
            }
            if (type == 1) {
                skills.set(index, skill.clone());
            }
        } catch (CloneNotSupportedException ex) {
            logger.error("failed!", ex);
        }
        characterInfo.recovery(CharacterInfo.ALL, 100, false);
        service.loadSkill();
        int indexItem = item.indexUI;
        removeItem(indexItem, 1);
        service.updateBag(indexItem, 0);
        service.serverMessage("Bạn học thành công " + skill.template.name + " cấp " + skill.point);
    }

    private void addEnemy(Char _char) {
        Friend _check = enemies.stream().filter(f -> f.getName().equals(_char.name)).findAny().orElse(null);
        if (_check != null) {
            return;
        }
        Friend friend = new Friend();
        friend.setId(_char.getId());
        friend.setName(_char.getName());
        friend.setHead(_char.getHead());
        friend.setBody(_char.getBody());
       
        friend.setBag(_char.getBag());
        friend.setLeg(_char.getLeg()); 
        friend.setPower(_char.characterInfo.getPower()); 
        this.enemies.add(friend);
    }

    public void mapOffline() {
        if (this.isDead) {
            return;
        }
        int mapId = 0;
        short x = 0;
        short y = 0;
        if ((this.gender == 0 && zone.map.mapID == 0) || (this.gender == 1 && zone.map.mapID == 7)
                || (this.gender == 2 && zone.map.mapID == 14)) {
            switch (this.gender) {
                case 0:
                    mapId = 21;
                    x = 456;
                    y = 336;
                    break;

                case 1:
                    mapId = 22;
                    x = 168;
                    y = 336;
                    break;

                case 2:
                    mapId = 23;
                    x = 432;
                    y = 336;
                    break;
            }
        }
        if (zone.map.mapID == 21) {
            mapId = 0;
            x = 288;
            y = 432;
        }
        if (zone.map.mapID == 22) {
            mapId = 7;
            x = 384;
            y = 432;
        }
        if (zone.map.mapID == 23) {
            mapId = 14;
            x = 540;
            y = 408;
        }

        if (zone.map.mapID == 39) {
            mapId = 21;
            x = 100;
            y = 336;
        }
        if (zone.map.mapID == 40) {
            mapId = 22;
            x = 100;
            y = 336;
        }
        if (zone.map.mapID == 41) {
            mapId = 23;
            x = 100;
            y = 336;
        }
        this.zone.leave(this);
        this.x = x;
        this.y = y;
        int zoneId = 0;
        TMap map = MapManager.getInstance().getMap(mapId);
        if (!map.isMapSingle()) {
            zoneId = map.getZoneID();
            map.enterZone(this, zoneId);
        } else {
            enterMapSingle(map);
        }
    }

    public void getItem(Message ms) {
        try {
            byte type = ms.getReader().readByte();
            byte index = ms.getReader().readByte();
            switch (type) {

                case Item.BOX_BAG:
                    itemBoxToBag(index);
                    break;

                case Item.BAG_BODY:
                    itemBagToBody(index);
                    break;

                case Item.BAG_BOX:
                    itemBagToBox(index);
                    break;

                case Item.BODY_BAG:
                    itemBodyToBag(index);
                    break;

                case Item.BODY_BOX:
                    itemBodyToBox(index);
                    break;

                case Item.BOX_BODY:
//                     itemBoxBody(index);
                    break;

                case Item.BAG_PET:
                    itemBagToPet(index);
                    break;

                case Item.PET_BAG:
                    itemPetToBag(index);
                    break;
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void itemPetToBag(byte index) {
        if (myDisciple == null) {
            return;
        }
        if (isDead) {
            return;
        }
        if (isTrading) {
            service.serverMessage(Language.TRADE_FAIL2);
            return;
        }
        if (index < 0 || index > myDisciple.itemBody.length) {
            return;
        }
        Item item = myDisciple.itemBody[index];
        if (item != null) {
            if (getSlotNullInBag() == 0) {
                service.serverMessage2(Language.ME_BAG_FULL);
                return;
            }
            for (int i = 0; i < this.itemBag.length; i++) {
                if (this.itemBag[i] == null) {
                    this.itemBag[i] = item.clone();
                    this.itemBag[i].indexUI = i;
                    myDisciple.itemBody[index] = null;
                    myDisciple.characterInfo.setCharacterInfo();
                    if (isNhapThe) {
                        characterInfo.setCharacterInfo();
                    }
                    myDisciple.updateSkin();
                    service.setItemBag();
                    service.petInfo((byte) 2);
                    if (myDisciple.isMask()) {
                        zone.mapService.updateBody((byte) 0, myDisciple);
                    } else {
                        zone.mapService.updateBody((byte) -1, myDisciple);
                    }
                    service.loadPoint();
                    zone.mapService.playerLoadBody(myDisciple);
                    myDisciple.update(item.template.type);
                    return;
                }
            }
        }
    }

    private void itemBagToPet(byte index) {
        if (myDisciple == null) {
            return;
        }
        if (isDead) {
            return;
        }
        if (isTrading) {
            service.serverMessage(Language.TRADE_FAIL2);
            return;
        }
        if (myDisciple.characterInfo.getPower() < 1500000) {
            service.serverMessage(String.format("Yêu cầu sức mạnh đệ tử %s trở lên", 1500000));
            return;
        }
        if (index < 0 || index > this.itemBag.length) {
            return;
        }
        Item item = this.itemBag[index];
        if (item != null) {
            if (item.template.isSuPhu()) {
                service.serverMessage("Chỉ dành cho sự phụ");
                return;
            }
            if (item.template.gender <= 2 && item.template.gender != myDisciple.getGender()) {
                service.serverMessage(Language.WE_CANT_USE_EQUIP);
                return;
            }
            if (item.require > myDisciple.characterInfo.getPower()) {
                service.serverMessage("Sức mạnh không đạt yêu cầu.");
                return;
            }
            byte type = item.template.type;
            if (type == 32) {
                type = 6;
            } else if (type == 23 || type == 24) {
                type = 7;
            } else if (type == 11) {
                type = 8;
            } else if (type == 40) {
                type = 9;
            }
            if (type >= this.itemBody.length) {
                return;
            }
            Item item2 = myDisciple.itemBody[type];
            myDisciple.itemBody[type] = item.clone();
            myDisciple.itemBody[type].indexUI = type;
            if (item2 != null) {
                Item clone = item2.clone();
                this.itemBag[index] = clone;
                this.itemBag[index].indexUI = index;
            } else {
                this.itemBag[index] = null;
                sort(index, false);
            }
            myDisciple.characterInfo.setCharacterInfo();
            if (isNhapThe) {
                characterInfo.setCharacterInfo();
            }
            myDisciple.updateSkin();
            service.setItemBag();
            service.petInfo((byte) 2);
            if (myDisciple.isMask()) {
                zone.mapService.updateBody((byte) 0, myDisciple);
            } else {
                zone.mapService.updateBody((byte) -1, myDisciple);
            }
            zone.mapService.playerLoadBody(myDisciple);
            myDisciple.update(item.template.type);
        }
    }

    private void itemBodyToBox(byte index) {
        if (isDead) {
            return;
        }
        if (isTrading) {
            service.serverMessage(Language.TRADE_FAIL2);
            return;
        }
        if (index < 0 || index > this.itemBody.length) {
            return;
        }
        Item item = this.itemBody[index];
        if (item != null) {
            if (getSlotNullInBox() == 0) {
                service.serverMessage2(Language.ME_BOX_FULL);
                return;
            }
            for (int i = 0; i < this.itemBox.length; i++) {
                if (this.itemBox[i] == null) {
                    this.itemBox[i] = item.clone();
                    this.itemBox[i].indexUI = i;
                    this.itemBody[index] = null;
                    characterInfo.setCharacterInfo();
                    updateSkin();
                    service.setItemBox();
                    service.setItemBody();
                    if (this.isMask) {
                        zone.mapService.updateBody((byte) 0, this);
                    } else {
                        zone.mapService.updateBody((byte) -1, this);
                    }
                    service.loadPoint();
                    zone.mapService.playerLoadBody(this);
                    update(item.template.type);
                    return;
                }
            }
        }
    }

    private void itemBodyToBag(byte index) {
        if (isDead) {
            return;
        }
        if (isTrading) {
            service.serverMessage(Language.TRADE_FAIL2);
            return;
        }
        if (index < 0 || index > this.itemBody.length) {
            return;
        }
        Item item = this.itemBody[index];
        if (item != null) {
            if (getSlotNullInBag() == 0) {
                service.serverMessage2(Language.ME_BAG_FULL);
                return;
            }
            for (int i = 0; i < this.itemBag.length; i++) {
                if (this.itemBag[i] == null) {
                    this.itemBag[i] = item.clone();
                    this.itemBag[i].indexUI = i;
                    this.itemBody[index] = null;
                    characterInfo.setCharacterInfo();
                    updateSkin();
                    service.setItemBag();
                    service.setItemBody();
                    if (this.isMask) {
                        zone.mapService.updateBody((byte) 0, this);
                    } else {
                        zone.mapService.updateBody((byte) -1, this);
                    }
                    service.loadPoint();
                    zone.mapService.playerLoadBody(this);
                    update(item.template.type);
                    return;
                }
            }
        }
    }

    private void itemBagToBox(byte index) {
        if (index < 0 || index > this.itemBag.length) {
            return;
        }
        if (isDead) {
            return;
        }
        if (!zone.map.isHome()) {
            return;
        }
        if (isTrading) {
            service.serverMessage(Language.TRADE_FAIL2);
            return;
        }
        Item item = this.itemBag[index];
        if (item != null) {
            if (getSlotNullInBox() == 0) {
                service.serverMessage2(Language.ME_BOX_FULL);
                return;
            }
            int quantityMax = Server.getMaxQuantityItem();
            int quantityCanAdd = 0;
            int having = 0;
            if (item.template.type == Item.TYPE_DAUTHAN) {
                for (Item itm : itemBox) {
                    if (itm != null && itm.template.type == Item.TYPE_DAUTHAN) {
                        having += itm.quantity;
                    }
                }
            } else {
                having = getQuantityInBoxById(item.id);
            }

            quantityCanAdd = quantityMax - having;
            if (quantityCanAdd > item.quantity) {
                quantityCanAdd = item.quantity;
            }
            if (quantityCanAdd <= 0) {
                return;
            }
            boolean added = false;
            if (item.template.isUpToUp()) {
                int indexItem = getIndexBoxById(item.id);
                if (indexItem != -1) {
                    this.itemBox[indexItem].quantity += quantityCanAdd;
                    item.quantity -= quantityCanAdd;
                    // if (item.id == ItemName.TU_DONG_LUYEN_TAP) {
                    // this.itemBox[indexItem].options.get(0).param += item.options.get(0).param;
                    // this.itemBox[indexItem].quantity = 1;
                    // item.quantity = 0;
                    // }
                    for (ItemOption o : item.options) {
                        if (o.optionTemplate.id == 1 || o.optionTemplate.id == 31) {
                            for (ItemOption o2 : this.itemBox[indexItem].options) {
                                if (o.optionTemplate.id == o2.optionTemplate.id) {
                                    o2.param += o.param;
                                    this.itemBox[indexItem].quantity = 1;
                                    item.quantity = 0;
                                    break;
                                }
                            }
                        }
                    }
                    if (item.quantity <= 0) {
                        this.itemBag[index] = null;
                        sort(index, false);
                    }
                    added = true;
                }
            }

            if (!added) {
                for (int i = 0; i < this.itemBox.length; i++) {
                    if (this.itemBox[i] == null) {
                        this.itemBox[i] = item.clone();
                        this.itemBox[i].indexUI = i;
                        this.itemBox[i].quantity = quantityCanAdd;
                        item.quantity -= quantityCanAdd;
                        if (item.quantity <= 0) {
                            this.itemBag[index] = null;
                            sort(index, false);
                        }
                        break;
                    }
                }
            }
            service.setItemBag();
            service.setItemBox();
            if (item.template.type == 23 || item.template.type == 24) {
                setMount();
            }
        }
    }

    private int getIndexBoxById(int id) {
        for (int i = 0; i < this.itemBox.length; i++) {
            Item item = this.itemBox[i];
            if (item != null && item.id == id) {
                return i;
            }
        }
        return -1;
    }

    private int getQuantityInBoxById(int id) {
        int quantity = 0;
        for (Item itm : itemBox) {
            if (itm != null && itm.id == id) {
                quantity += itm.quantity;
            }
        }
        return quantity;
    }

    private int getSlotNullInBox() {
        int number = 0;
        for (Item item : this.itemBox) {
            if (item == null) {
                number++;
            }
        }
        return number;
    }

    private void itemBagToBody(byte index) {
        if (isDead) {
            return;
        }
        if (isTrading) {
            service.serverMessage(Language.TRADE_FAIL2);
            return;
        }
        if (index < 0 || index > this.itemBag.length) {
            return;
        }
        Item item = this.itemBag[index];
        if (item != null) {
            if (item.template.isDeTu()) {
                service.serverMessage("Chỉ dành cho đệ tử");
                return;
            }
            if (item.template.gender <= 2 && item.template.gender != this.gender) {
                service.serverMessage(Language.WE_CANT_USE_EQUIP);
                return;
            }
            if (item.require > this.characterInfo.getPower()) {
                service.serverMessage("Sức mạnh không đạt yêu cầu.");
                return;
            }
            byte type = item.template.type;
            if (type == 32) {
                type = 6;
            } else if (type == 23 || type == 24) {
                type = 7;
            } else if (type == 11) {
                type = 8;
            } else if (type == 40) {
                type = 9;
            }
            if (type >= this.itemBody.length) {
                return;
            }
            Item item2 = this.itemBody[type];
            this.itemBody[type] = item.clone();
            this.itemBody[type].indexUI = type;
            if (item2 != null) {
                Item clone = item2.clone();
                this.itemBag[index] = clone;
                this.itemBag[index].indexUI = index;
            } else {
                this.itemBag[index] = null;
                sort(index, false);
            }
            characterInfo.setCharacterInfo();
            updateSkin();
            service.setItemBag();
            service.setItemBody();
            if (this.isMask) {
                zone.mapService.updateBody((byte) 0, this);
            } else {
                zone.mapService.updateBody((byte) -1, this);
            }
            service.loadPoint();
            zone.mapService.playerLoadBody(this);
            update(item.template.type);
        }
    }

    public void update(int type) {
        if (zone != null) {
            switch (type) {
                case 0:
                    zone.mapService.playerLoadAo(this);
                    break;

                case 1:
                    zone.mapService.playerLoadQuan(this);
                    break;

                case 5:
                    zone.mapService.playerLoadAll(this);
                    break;
            }
        }
    }

    private void itemBoxToBag(byte index) {
        if (index < 0 || index > this.itemBox.length) {
            return;
        }
        if (isDead) {
            return;
        }
        if (!zone.map.isHome()) {
            return;
        }
        if (isTrading) {
            service.serverMessage(Language.TRADE_FAIL2);
            return;
        }
        Item item = this.itemBox[index];
        if (item != null) {
            if (getSlotNullInBag() == 0) {
                service.serverMessage2(Language.ME_BOX_FULL);
                return;
            }
            int quantityCanAdd = 0;
            int having = 0;
            int quantityMax = Server.getMaxQuantityItem();
            if (item.template.type == Item.TYPE_DAUTHAN) {
                for (Item itm : itemBag) {
                    if (itm != null && itm.template.type == Item.TYPE_DAUTHAN) {
                        having += itm.quantity;
                    }
                }
            } else {
                having = getQuantityInBagById(item.id);
            }
            quantityCanAdd = quantityMax - having;
            if (quantityCanAdd > item.quantity) {
                quantityCanAdd = item.quantity;
            }
            if (quantityCanAdd <= 0) {
                return;
            }
            boolean added = false;
            if (item.template.isUpToUp()) {
                int indexItem = getIndexBagById(item.id);
                if (indexItem != -1) {
                    this.itemBag[indexItem].quantity += quantityCanAdd;
                    item.quantity -= quantityCanAdd;
                    // if (item.id == ItemName.TU_DONG_LUYEN_TAP) {
                    // this.itemBag[indexItem].options.get(0).param += item.options.get(0).param;
                    // this.itemBag[indexItem].quantity = 1;
                    // item.quantity = 0;
                    // }
                    for (ItemOption o : item.options) {
                        if (o.optionTemplate.id == 1 || o.optionTemplate.id == 31) {
                            for (ItemOption o2 : this.itemBag[indexItem].options) {
                                if (o.optionTemplate.id == o2.optionTemplate.id) {
                                    o2.param += o.param;
                                    this.itemBag[indexItem].quantity = 1;
                                    item.quantity = 0;
                                    break;
                                }
                            }
                        }
                    }
                    if (item.quantity <= 0) {
                        this.itemBox[index] = null;
                    }
                    added = true;
                }
            }
            if (!added) {
                for (int i = 0; i < this.itemBag.length; i++) {
                    if (this.itemBag[i] == null) {
                        this.itemBag[i] = item.clone();
                        this.itemBag[i].indexUI = i;
                        this.itemBag[i].quantity = quantityCanAdd;
                        item.quantity -= quantityCanAdd;
                        if (item.quantity <= 0) {
                            this.itemBox[index] = null;
                        }
                        break;
                    }
                }
            }
            if (taskMain != null && taskMain.id == 0 && taskMain.index == 3) {
                taskNext();
            }
            service.setItemBag();
            service.setItemBox();
            if (item.template.type == 23 || item.template.type == 24) {
                setMount();
            }
        }
    }

    private int getSlotNullInBag() {
        int number = 0;
        for (Item item : this.itemBag) {
            if (item == null) {
                number++;
            }
        }
        return number;
    }

    private int getQuantityInBagById(int itemId) {
        int quantity = 0;
        for (Item itm : itemBag) {
            if (itm != null && itm.id == itemId) {
                quantity += itm.quantity;
            }
        }
        return quantity;
    }

    public void changeOnSkill(Message ms) {
        try {
            if (isDead) {
                return;
            }
            byte[] ab = new byte[10];
            ms.getReader().read(ab);
            this.shortcut = ab;
            // service.loadRms(ab);
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void openUIMenu(Message mss) {
    }

    public void menu(Message ms) {
        try {
            if (menus == null) {
                return;
            }
            int npcId = ms.getReader().readUnsignedByte();
            int menuId = ms.getReader().readUnsignedByte();
            int optionId = ms.getReader().readUnsignedByte();
            if (menus.isEmpty() || menuId >= menus.size()) {
                return;
            }
            KeyValue<Integer, String> keyValue = menus.get(menuId);
            if (keyValue == null) {
                return;
            }
            menus.clear();
            Npc npc = zone.findNpcByID(npcId);
            if (npc != null) {
                confirmKeyValue(keyValue, npc);
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void confirmKeyValue(KeyValue<Integer, String> keyValue, Npc npc) {

    }
}