package com.ngocrong.backend.character;


import com.ngocrong.backend.bot.Escort;
import com.ngocrong.backend.clan.Clan;
import com.ngocrong.backend.collection.Card;
import com.ngocrong.backend.consts.*;
import com.ngocrong.backend.crackball.CrackBall;
import com.ngocrong.backend.disciple.Disciple;
import com.ngocrong.backend.disciple.MiniDisciple;
import com.ngocrong.backend.effect.AmbientEffect;
import com.ngocrong.backend.effect.EffectChar;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.item.ItemMap;
import com.ngocrong.backend.item.ItemTemplate;
import com.ngocrong.backend.item.ItemTime;
import com.ngocrong.backend.lib.KeyValue;
import com.ngocrong.backend.map.BaseBabidi;
import com.ngocrong.backend.map.MapManager;
import com.ngocrong.backend.map.tzone.TMap;
import com.ngocrong.backend.mob.Mob;
import com.ngocrong.backend.model.*;
import com.ngocrong.backend.network.Message;
import com.ngocrong.backend.shop.Shop;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.map.tzone.Zone;
import com.ngocrong.backend.task.Task;
import com.ngocrong.backend.util.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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
    private int dameDown;
    public int idMount;
    private int id;
    private int freezSeconds;
    private int seconds;
    private int timeIsMoneky;
    private int fusionType;
    private int clanID;
    private int phuX;
    private int testCharId = -9999;
    private int betAmount;

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
    public ArrayList<Integer> listAccessMap;
    public ArrayList<KeyValue> menus = new ArrayList<>();
    public ArrayList<Amulet> amulets;
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
    private MagicTree magicTree;
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
    private byte typePk;
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
            zone.mapService.skillNotFocus(this,(byte) 3,null,null);
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

    private void updateSkin() {
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
        this.combine = null;
        this.lucky = null;
        if (callDragon != null) {
            callDragon.close();
        }
        clearAmbientEffect();
    }

    private void clearTrade() {
        if (!(this.status == Status.DONG_Y_GIAO_DICH)) {
            this.service.giaoDich(null, (byte) 7, -1);
        }
        this.isTrading = false;
        this.goldTrading = 0;
        this.itemsTrading = null;
        this.trader = null;
        this.status = Status.NORMAL;
    }

    private void resultPk(byte b) {
        switch (getCommandPK()) {
            case CMDPk.THACH_DAU: {
                if (type == 0) {
                    int gold = this.betAmount * 2;
                    gold -= gold / 10;
                    addGold(gold);
                    service.serverMessage("Đối thủ đã kiệt sức, bạn thắng được " + gold + " vàng");
                }
                if (type == 1) {
                    service.serverMessage("Bạn đã thua vì kiệt sức");
                }
                if (type == 2) {
                    int gold = this.betAmount * 2;
                    gold -= gold / 10;
                    addGold(gold);
                    service.serverMessage("Đối thủ đã bỏ chạy, bạn thắng được " + gold + " vàng");
                }
                if (type == 3) {
                    service.serverMessage("Bạn đã thua vì bỏ chạy");
                }
            }
            break;

            case CMDPk.TRA_THU: {
                if (type == 3) {
                    service.serverMessage("Bạn đã bị xử thua");
                }
            }
            break;

            case CMDPk.DAI_HOI_VO_THUAT:
                Arena arena = (Arena) zone;
                arena.checkResult();
                break;
        }
        setCommandPK(CMDPk.NORMAL);
        if (type == 0 || type == 2) {
            if (achievements != null) {
                achievements.get(3).addCount(1);// trăm trận trăm tháng
            }
        }
    }

    private void clearPk() {
        this.testCharId = -9999;
        setTypePk((byte) 0);
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



    public void updateEveryFiveSeconds() {
        try {
            if (!isDead) {

                try {
                    if (zone == null || zone.mapService == null) {
                        if (zone == null) {
                            logger.debug("zone is null");
                        }
                        if (zone.mapService == null) {
                            logger.debug("service is null");
                        }
                    }
                    List<Char> list = zone.getListChar(Zone.TYPE_HUMAN, Zone.TYPE_PET);
                    if (list.size() > 1) {
                        String[] chats2 = {"Tránh ra đi Xinbatô ơi", "Phân tâm quá", "Nực quá", "Bực bội quá",
                                "Im đi ông Xinbatô ơi"};
                        if (characterInfo.getOptions()[8] > 0) {
                            characterInfo.recovery(CharacterInfo.ALL, characterInfo.getOptions()[8], true);
                        }
                        for (Char _c : list) {
                            if (_c.isDead) {
                                continue;
                            }

                            int d = Utils.getDistance(this.x, this.y, _c.x, _c.y);
                            if (d < DISTANCE_EFFECT) {
                                boolean isUpdate = false;
                                if (_c != this) {
                                    if (characterInfo.getOptions()[8] > 0) {
                                        _c.characterInfo.setHp(characterInfo.getHp() - Utils.percentOf(_c.characterInfo.getFullHP(), characterInfo.getOptions()[8]));
                                        _c.characterInfo.setMp(characterInfo.getMp() - Utils.percentOf(_c.characterInfo.getFullMP(), characterInfo.getOptions()[8]));
                                        if (_c.characterInfo.getHp() <= 0) {
                                            _c.characterInfo.setHp(1);
                                        }
                                        if (_c.characterInfo.getMp() <= 0) {
                                            _c.characterInfo.setMp(1);
                                        }
                                        _c.service.loadPoint();

                                        zone.mapService.playerLoadBody(_c);
                                    }
                                    if (isHaveEquipXinbato) {
                                        if (!_c.isAnDanh) {
                                            AmbientEffect am = new AmbientEffect(111, info.options[111], 5000);
                                            if (_c.addAmbientEffect(am)) {
                                                isUpdate = true;
                                            }
                                            zone.service.chat(_c, chats2[Utils.nextInt(chats2.length)]);
                                        }
                                    } else if (isHaveEquipBuiBui) {
                                        AmbientEffect am = new AmbientEffect(24, -95, 5000);
                                        if (_c.addAmbientEffect(am)) {
                                            isUpdate = true;
                                        }
                                        zone.service.chat(_c, "Nặng quá");
                                    } else if (info.options[162] > 0) {
                                        zone.service.chat(_c, "Cute");
                                    }
                                }
                                if (isHaveEquipBulma || isHaveEquipMiNuong) {
                                    AmbientEffect am = new AmbientEffect(117, info.options[117], 5000);
                                    if (_c.addAmbientEffect(am)) {
                                        isUpdate = true;
                                    }
                                    String chat;
                                    if (isHaveEquipMiNuong) {
                                        chat = "Bắn tim...biu biu";
                                    } else {
                                        chat = "Wow, Sexy quá";
                                    }
                                    if (_c != this) {
                                        zone.service.chat(_c, chat);
                                    }
                                }
                                if (isUpdate) {
                                    _c.info.setInfo();
                                    _c.service.loadPoint();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("update every five seconds - block 1");
                }
                try {
                    if (isHaveEquipInvisible) {
                        isInvisible = true;
                        zone.mapService.playerLoadAll(this);
                        long delay = 1000;
//                        if (this instanceof Yacon) {
//                            delay = 2000;
//                        }
                        Utils.setTimeout(() -> {
                            if (zone != null) {
                                isInvisible = false;
                                zone.mapService.playerLoadAll(this);
                            }
                        }, delay);
                    }
                } catch (Exception e) {
                    logger.error("update every five seconds - block 2");
                }
            }
        } catch (Exception e) {
            logger.error("updateEveryFiveSeconds error", e.getCause());
        }
    }


    public void revival(int percent) {
        if (this.isDead) {
            this.statusMe = 1;
            this.isDead = false;
            service.sendMessage(new Message(Cmd.ME_LIVE));
            zone.mapService.playerLoadLive(this);
        }
        this.characterInfo.recovery(CharacterInfo.ALL, percent, true);
    }

    public void loadEffectSkillPlayer(Char character) {
        applyStatusEffects(character);
        handleRecoveryEnergy(character);
        handleHoldEffect(character);
        handleChargeSkill(character);
    }

    private void handleChargeSkill(Char character) {
        if (character.isCharge()) {
            short skillId = (short) character.select.id;
            byte chargeType = getChargeType(skillId);
            if (chargeType != -1) {
                service.skillNotFocus(character.id, skillId, chargeType, null, null);
            }
        }
    }

    private byte getChargeType(short skillId) {
        switch (skillId) {
            case SkillName.QUA_CAU_KENH_KHI:
            case SkillName.MAKANKOSAPPO:
                return (byte) 4;

            case SkillName.BIEN_HINH:
                return (byte) 6;

            case SkillName.TU_PHAT_NO:
                return (byte) 7;

            default:
                return -1;
        }
    }

    private void handleHoldEffect(Char character) {
        if (character.isHeld && character.hold.getHolder() == character) {
            applyHoldEffect(character.hold, character.hold.getDetainee());
        }
    }

    private void applyHoldEffect(Hold hold, Object detainee) {
        if (detainee instanceof Mob) {
            Mob mob = (Mob) detainee;
            service.setEffect(hold, mob.mobId, Skill.ADD_EFFECT, Skill.MONSTER, (byte) 32);
        } else if (detainee instanceof Char) {
            Char detaineeChar = (Char) detainee;
            service.setEffect(hold, detaineeChar.id, Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 32);
        }
    }

    private void handleRecoveryEnergy(Char character) {
        if (character.isRecoveryEnergy) {
            service.skillNotFocus(character.id, (short) character.select.id, (byte) 1, null, null);
        }
    }

    private void applyStatusEffects(Char character) {
        if (character.isSleep) {
            applyEffect(character, (byte) 41);
        }
        if (character.isProtected) {
            applyEffect(character, (byte) 33);
        }
        if (character.isBlind) {
            applyEffect(character, (byte) 40);
        }
    }

    private void applyEffect(Char character, byte effectId) {
        service.setEffect(null, character.getId(), Skill.ADD_EFFECT, Skill.CHARACTER, effectId);
    }

    public boolean isHaveFood() {
        return isPudding() || isXucXich() || isKemDau() || isMiLy() || isSushi();
    }

    public boolean addItem(Item item) {
        if (handleSpecialItems(item)) {
            return true;
        }
        if (item.template.isUpToUp()) {
            if (mergeWithExistingItem(item)) {
                return true;
            }
        }

        return addItemToEmptySlot(item);
    }

    // Xử lý các loại item đặc biệt như Vàng, Kim cương, Khóa kim cương, Bùa
    private boolean handleSpecialItems(Item item) {
        switch (item.template.getType()) {
            case Item.TYPE_GOLD:
                addGold(item.quantity);
                return true;
            case Item.TYPE_DIAMOND:
                addDiamond(item.quantity);
                return true;
            case Item.TYPE_DIAMOND_LOCK:
                addDiamondLock(item.quantity);
                return true;
            case Item.TYPE_AMULET:
                return handleAmuletItem(item);
            default:
                return false;
        }
    }

    private void addGold(long gold) {
        this.gold += gold;
        service.addGold(gold);
    }

    // Xử lý thêm bùa vào danh sách hoặc gia hạn thời gian sử dụng bùa
    private boolean handleAmuletItem(Item item) {
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

    // Gộp item mới với item đã tồn tại trong túi (nếu có thể)
    private boolean mergeWithExistingItem(Item item) {
        int maxQuantity = Server.getMaxQuantityItem();
        int index = getIndexBagById(item.id);

        if (index != -1) {
            Item existingItem = this.itemBag[index];

            if (mergeItemOptions(existingItem, item)) {
                return true;
            }

            if (existingItem.quantity + item.quantity > maxQuantity) {
                return false; // Không thể thêm nếu vượt quá số lượng tối đa
            }

            existingItem.quantity += item.quantity;
            updateBagUI(index, existingItem);
            return true;
        }
        return false;
    }

    // Gộp các tùy chọn của item mới vào item đã tồn tại nếu có tùy chọn tương tự
    private boolean mergeItemOptions(Item existingItem, Item newItem) {
        boolean merged = false;
        for (ItemOption existingOption : existingItem.options) {
            if (isMergableOption(existingOption)) {
                for (ItemOption newOption : newItem.options) {
                    if (existingOption.optionTemplate.id == newOption.optionTemplate.id) {
                        existingOption.param += newOption.param;
                        service.setItemBag(); // Cập nhật lại túi đồ sau khi gộp
                        merged = true;
                    }
                }
            }
        }
        return merged;
    }

    // Kiểm tra nếu tùy chọn của item có thể gộp với item khác
    private boolean isMergableOption(ItemOption option) {
        return option.optionTemplate.id == 1 || option.optionTemplate.id == 31 ||
                option.optionTemplate.id == 11 || option.optionTemplate.id == 12 ||
                option.optionTemplate.id == 13;
    }

    // Thêm item vào slot trống trong túi
    private boolean addItemToEmptySlot(Item item) {
        for (int i = 0; i < itemBag.length; i++) {
            if (itemBag[i] == null) {
                itemBag[i] = item;
                item.indexUI = i;
                service.setItemBag(); // Cập nhật túi đồ sau khi thêm item
                return true;
            }
        }
        return false; // Không có slot trống trong túi
    }

    // Cập nhật giao diện túi sau khi thay đổi số lượng item
    private void updateBagUI(int index, Item item) {
        if (item.template.type == Item.TYPE_DAUTHAN) {
            service.setItemBag(); // Cập nhật toàn bộ túi
        } else {
            service.updateBag(index, item.quantity); // Cập nhật chỉ mục cụ thể trong túi
        }
    }
}
