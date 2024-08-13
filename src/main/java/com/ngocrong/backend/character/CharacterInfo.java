package com.ngocrong.backend.character;

import com.google.gson.annotations.SerializedName;
import com.ngocrong.backend.clan.ClanMember;
import com.ngocrong.backend.clan.ClanReward;
import com.ngocrong.backend.collection.Card;
import com.ngocrong.backend.consts.ItemName;
import com.ngocrong.backend.disciple.Disciple;
import com.ngocrong.backend.disciple.MiniDisciple;
import com.ngocrong.backend.effect.AmbientEffect;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.item.ItemOption;
import com.ngocrong.backend.map.tzone.GravityRoom;
import com.ngocrong.backend.model.PowerLimitMark;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.util.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CharacterInfo {

    private static final Logger logger = Logger.getLogger(CharacterInfo.class);

    // Các hằng số để xác định các chỉ số khác nhau
    public static final byte ALL = 0;
    public static final byte HP = 1;
    public static final byte MP = 2;

    // Các hằng số để quản lý sức mạnh
    public static final byte POWER = 0;
    public static final byte POTENTIAL = 1;
    public static final byte POWER_AND_POTENTIAL = 2;



    // Chỉ số cơ bản
    @SerializedName("hp_goc") // Mã hóa và giải mã JSON với tên "hp_goc"
    private long baseHP;

    @SerializedName("mp_goc") // Mã hóa và giải mã JSON với tên "mp_goc"
    private long baseMP;

    @SerializedName("damage") // Mã hóa và giải mã JSON với tên "damage"
    private long baseDamage;

    @SerializedName("defense") // Mã hóa và giải mã JSON với tên "defense"
    private int baseDefense;

    @SerializedName("critical") // Mã hóa và giải mã JSON với tên "critical"
    private int baseCritical;

    // Chỉ số hiện tại
    @SerializedName("power") // Mã hóa và giải mã JSON với tên "power"
    private long power;

    @SerializedName("potential") // Mã hóa và giải mã JSON với tên "potential"
    private long potential;

    @SerializedName("stamina") // Mã hóa và giải mã JSON với tên "stamina"
    private short stamina;

    @SerializedName("max_stamina") // Mã hóa và giải mã JSON với tên "max_stamina"
    private short maxStamina;

    @SerializedName("hp") // Mã hóa và giải mã JSON với tên "hp"
    private long hp;

    @SerializedName("mp") // Mã hóa và giải mã JSON với tên "mp"
    private long mp;

    @SerializedName("open_power") // Mã hóa và giải mã JSON với tên "open_power"
    private byte openPowerCount;

    @SerializedName("active_point") // Mã hóa và giải mã JSON với tên "active_point"
    private int activePoints;

    // Các thuộc tính khác
    private transient PowerLimitMark powerLimitMark; // Đánh dấu giới hạn sức mạnh không được lưu vào JSON
    private transient Char _character; // Đối tượng nhân vật không được lưu vào JSON

    // Chỉ số sau khi đã được điều chỉnh
    private transient long fullHP;
    private transient long fullMP;
    private transient long fullDamage;
    private transient int fullDefense;
    private transient int fullCritical;
    private transient byte speed;
    private transient int accuracy;
    private transient int accuracyPercent;
    private transient int evasion;
    private transient int evasionPercent;

    private transient long hpFullTemp;
    private transient long mpFullTemp;

    private transient int level;
    private transient long levelPercent;

    // Các tùy chọn và phần thưởng
    private transient int[] options;
    private ArrayList<Integer> damageOptions;
    private ArrayList<Integer> hpOptions;
    private ArrayList<Integer> mpOptions;

    // Constructor initializes base stats based on planet
    public CharacterInfo(byte planet) {
        switch (planet) {
            case 0: // Earth
                this.baseHP = 200;
                this.baseMP = 100;
                this.baseDamage = 12;
                break;

            case 1: // Namek
                this.baseHP = 100;
                this.baseMP = 200;
                this.baseDamage = 12;
                break;

            case 2: // Saiyan
                this.baseHP = 100;
                this.baseMP = 100;
                this.baseDamage = 15;
                break;
        }
        this.baseCritical = 0;
        this.baseDefense = 0;
        this.power = 1200;
        this.potential = 1200;
        this.fullHP = this.baseHP;
        this.fullMP = this.baseMP;
    }

    public CharacterInfo(Char _character) {
        this._character = _character;

        if (_character.isDisciple()) {
            this.baseHP = Utils.nextInt(9, 30) * 100;
            this.baseMP = Utils.nextInt(9, 30) * 100;
            this.baseDamage = Utils.nextInt(50, 100);
            this.baseCritical = Utils.nextInt(1, 4);
            this.baseDefense = Utils.nextInt(15, 30);
            this.power = 2000;
            this.potential = 2000;

        } else if (_character.isMiniDisciple()) {
            this.baseHP = 5000000;
            this.baseMP = 5000000;
            this.baseDamage = 0;
            this.baseCritical = 0;
            this.baseDefense = 0;
            this.power = 0;
            this.potential = 0;
        }

//        } else if (_character instanceof TrongTai) {
//            this.baseHP = 500;
//            this.baseMP = 500;
//            this.baseDamage = 0;
//            this.baseCritical = 0;
//            this.baseDefense = 0;
//            this.power = 0;
//            this.potential = 0;
//        }

        this.fullHP = this.baseHP;
        this.fullMP = this.baseMP;
    }

    public void setCharacter(Char _character) {
        this._character = _character;
    }


    // Thiết lập giới hạn sức mạnh
    public void setPowerLimited() {
        this.powerLimitMark = PowerLimitMark.limitMarks.get(this.openPowerCount);
    }

    // Thiết lập thể lực
    public void setSatamina() {
        if (_character != null && _character.isDisciple()) {
            setMaxStamina();
            this.stamina = this.maxStamina;
        }else {
            this.stamina = 10000;
            this.maxStamina = 10000;
        }
    }
    // Cập nhật thể lực
    public void updateSatamina(int add) {
        this.stamina += add;
        if (this.stamina > this.maxStamina) {
            this.stamina = this.maxStamina;
        }

        if (this.stamina < 0) {
            this.stamina = 0;
        }
    }


    public void recovery(int recoveryType , int percent , boolean isUpdate) {
        // Lưu trữ HP và MP ban đầu để so sánh sau khi hồi phục
        long initialHp = this.hp;
        long initialMp = this.mp;

        // Tính toán lượng HP và MP sẽ hồi phục dựa trên phần trăm đầu vào
        long recoveredHp = Utils.percentOf(this.fullHP , percent);
        long recoveredMp  = Utils.percentOf(this.fullMP , percent);

        // Hồi phục dựa trên loại hồi phục (HP, MP, hoặc cả hai)
        switch (recoveryType) {
            case ALL:
                this.hp += recoveredHp;
                this.mp += recoveredMp;
                break;
            case HP:
                this.hp += recoveredHp;
                break;
            case MP:
                this.mp += recoveredMp;
                break;

        }
        if (this.hp > this.fullHP) {
            this.hp = this.fullHP;
        }
        if (this.mp > this.fullMP) {
            this.mp = this.fullMP;
        }
        if (isUpdate) {
           switch (recoveryType) {
               case ALL:
                   if (this.hp != initialHp) {
                       _character.service.loadHP();
                   }
                   if (this.mp != initialMp) {
                       _character.service.loadMP();
                   }
                   break;
                   case HP:
                       if (this.hp != initialHp) {
                           _character.service.loadHP();
                       }break;
                       case MP:
                           if (this.mp != initialMp) {
                               _character.service.loadMP();
                           }
                           break;

           }
        }
    }

    public void recovery (int type, long amount) {
        long initialHp = this.hp;
        long initialMp = this.mp;

        // Cập nhật HP và MP dựa trên loại hồi phục
        switch (type) {
            case ALL:
                this.hp += amount;
                this.mp += amount;
                break;

            case HP:
                this.hp += amount;
                break;

            case MP:
                this.mp += amount;
                break;
        }

        // Đảm bảo HP và MP không vượt quá giá trị tối đa
        if (this.hp > this.fullHP) {
            this.hp = this.fullHP;
        }
        if (this.mp > this.fullMP) {
            this.mp = this.fullMP;
        }

        // Kiểm tra và cập nhật nếu có sự thay đổi
//        if (_character.zone != null) {
//            if (this.hp != initialHp) {
//                _character.charService.loadHP();
////                _character.zone.service.playerLoadBody(_char);
//            }
//            if (this.mp != initialMp) {
//                _character.charService.loadMP();
//            }
//        }
    }

    // Thiết lập tối đa thể lực
    private void setMaxStamina() {
        this.maxStamina = (short) ((50 * this.level) + 450);
    }

    public void addOption(int option , int params) {
        switch (option) {
            case 77:
            case 196:
                hpOptions.add(params);
                break;

            case 103:
            case 197:
                mpOptions.add(params);
                break;

            case 49:
            case 50:
            case 147:
            case 195:
                damageOptions.add(params);
                break;

            case 16:
            case 148:
            case 114:
                this.speed += (byte) Utils.percentOf(this.speed, params);
                break;

            default:
                this.options[option] += params;
                break;
        }
    }

    public void setCharacterInfo() {
        Server server = DragonBall.getInstance().getServer();
        initializeOptions(server);
        resetStats();
        int upgradeMin = -1;
        int giapLuyenTap = initializeItemsAndSetOptions(upgradeMin);
        processAmbientEffects();
        processCardsAndMiniDisciple();
        applyFusionOptions();
        calculateFinalStats(giapLuyenTap);
        applyClanRewards();
        applyPhuX();
        applyFoodAndColdEffects();
        finalizeStats();

    }

    private void finalizeStats() {
        if(this.hpFullTemp <= 0) this.hpFullTemp = 1;
        if (this.mpFullTemp <= 0) this.mpFullTemp = 1;
        if (this.hpFullTemp > Integer.MAX_VALUE) this.hpFullTemp = Integer.MAX_VALUE;
        if (this.mpFullTemp > Integer.MAX_VALUE) this.mpFullTemp = Integer.MAX_VALUE;

        this.fullHP = this.hpFullTemp;
        this.fullMP = this.mpFullTemp;

        if (this.hp > this.fullHP) this.hp = this.fullHP;
        if (this.mp > this.fullMP) this.mp = this.fullMP;
    }

    private void applyFoodAndColdEffects() {
        boolean isUnaffectedCold = false;
        if (_character.isHaveFood()) {
            this.fullDamage += this.fullDamage / 10;
        }
        if (_character.zone != null && _character.zone.map.isCold()) {
            if (!isUnaffectedCold) {
                this.hpFullTemp /= 2;
                this.fullDamage /= 2;
            }
        }
    }

    private void applyPhuX() {
        int phuX = _character.getPhuX();
        if (phuX > 0) {
            this.hpFullTemp *= phuX;
            this.mpFullTemp *= phuX;
            this.fullDamage *= phuX;
        }
    }

    private void applyClanRewards() {
        if (_character.clan != null) {
            ClanMember mem = _character.clan.getMember(_character.getId());
            if (mem != null) {
                for (ClanReward clanReward : mem.rewards) {
                    applyClanReward(clanReward);
                }
            }
        }
    }

    private void applyClanReward(ClanReward clanReward) {
        switch (clanReward.getStar()) {
            case 1 -> this.fullDamage += fullDamage * 15 / 100;
            case 2 -> this.hpFullTemp += hpFullTemp * 20 / 100;
            case 3 -> this.mpFullTemp += mpFullTemp * 20 / 100;
            case 4 -> options[101] += 20;
            case 5 -> options[108] += 10;
            case 6 -> options[94] += 10;
            case 7 -> {
                options[95] += 10;
                options[96] += 10;
            }
        }
    }

    private int initializeItemsAndSetOptions(int upgradeMin) {
        int giapLuyenTap = -1;
        int n = 0;
        if (_character.itemBody != null) {
            for (Item item : _character.itemBody) {
                if (item == null) continue;
                n += processItem(item);
            }
        }
        if (n == 5) _character.setIdEffSetItem((short) upgradeMin);
        return giapLuyenTap;
    }


    private void initializeOptions(Server server) {
        this.options = new int[server.iOptionTemplates.size()];
        resetOptions(damageOptions);
        resetOptions(hpOptions);
        resetOptions(mpOptions);
    }

    private void resetStats() {
        this.speed = 6;
        this.hpFullTemp = this.baseHP;
        this.mpFullTemp = this.baseMP;
        this.fullDamage = this.baseDamage;
        this.fullCritical = this.baseCritical;
        this.fullDefense = this.baseDefense * 4;
    }

    private int processItem (Item item) {
        if (item.template.getType()  < 5) return 1;
        if (item.isNhapThe && !_character.isNhapThe()) return 0;

        ArrayList<ItemOption> options = item.getItemOptions();
        for (ItemOption option : options) {
            addOption(option.optionTemplate.id, option.param);
            processItemOption(option);
        }
        return 0;
    }

    private void processItemOption(ItemOption option) {
        int id = option.optionTemplate.id;
        int param = option.param;

        boolean isHaveEquipInvisible;
        boolean isHaveEquipTransformIntoStone;
        boolean isHaveEquipTransformIntoChocolate;
        boolean isHaveEquipTeleport;
        boolean isVoHinh;
        boolean isUnaffectedCold;
        boolean isDoSaoPhaLe;
        boolean isKhangTDHS;
        boolean isHaveEquipSelfExplosion;

        int upgradeMin = -1;

        int setThienXinHang = 0, setKirin = 0, setSongoku = 0, setPicolo = 0, setOcTieu = 0, setPikkoroDaimao = 0, setKakarot = 0, setCaDic = 0, setNappa = 0 , setThanLinh = 0;
        switch (id) {
            case 25 -> isHaveEquipInvisible = true;
            case 26 -> isHaveEquipTransformIntoStone = true;
            case 29 -> isHaveEquipTransformIntoChocolate = true;
            case 33 -> isHaveEquipTeleport = true;
            case 105 -> isVoHinh = true;
            case 106 -> isUnaffectedCold = true;
            case 110 -> isDoSaoPhaLe = true;
            case 116 -> isKhangTDHS = true;
            case 153 -> isHaveEquipSelfExplosion = true;
            case 72 -> {
                if (param >= 0 && (upgradeMin == -1 || upgradeMin > param)) {
                    upgradeMin = param;
                }
            }
            case 127 -> setThienXinHang++;
            case 128 -> setKirin++;
            case 129 -> setSongoku++;
            case 130 -> setPicolo++;
            case 131 -> setOcTieu++;
            case 132 -> setPikkoroDaimao++;
            case 133 -> setKakarot++;
            case 134 -> setCaDic++;
            case 135 -> setNappa++;
        }

    }

    private int updateGiapLuyenTap(Item item, int giapLuyenTap) {
        boolean isMacGiapLuyenTap ;
        if (item.template.getType() == 32 && giapLuyenTap == -1) {
            giapLuyenTap = item.id;
            isMacGiapLuyenTap = true;
        }
        return giapLuyenTap;
    }

    private void processAmbientEffects() {

        List<AmbientEffect> ambientEffects = _character.getAmbientEffects();
        if (ambientEffects != null) {
            for (AmbientEffect am : ambientEffects) {
                int[] o = am.getItemOption();
                addOption(o[0], o[1]);
            }
        }
    }

    private void processCardsAndMiniDisciple() {
        processCards();
        processMiniDisciple();
    }

    private void processMiniDisciple() {
        MiniDisciple mini = _character.getMiniDisciple();
        if (mini != null && mini.item != null) {
            ArrayList<ItemOption> options = mini.item.getItemOptions();
            for (ItemOption option : options) {
                addOption(option.optionTemplate.id, option.param);
            }
        }
    }

    private void processCards() {
        ArrayList<Card> cards = _character.getCards();
        if (cards != null) {
            for (Card c : cards) {
                if (c.isUse) {
                    for (ItemOption o : c.template.options) {
                        if (c.level >= o.activeCard) {
                            addOption(o.optionTemplate.id, o.param);
                        }
                    }
                }
            }
        }
    }

    private void applyFusionOptions() {
        if (_character.isNhapThe() && _character.getMyDisciple() != null) {
            if (_character.getTypePorata() == 1) {
                Item item = _character.getIemInBag(ItemName.BONG_TAI_PORATA_CAP_2);
                if (item != null) {
                    ArrayList<ItemOption> options = item.getItemOptions();
                    for (ItemOption itemOption : options) {
                        addOption(itemOption.optionTemplate.id, itemOption.param);
                    }
                }
            }
        }
    }


    private void resetOptions(List<Integer> optionList) {
        if (optionList == null) {
            optionList = new ArrayList<>();
        } else {
            optionList.clear();
        }
    }

    private void calculateFinalStats(int giapLuyenTap) {
        this.fullDamage += this.options[0];
        this.hpFullTemp += ((this.options[2] + this.options[22]) * 1000L) + this.options[6] + this.options[48];
        this.mpFullTemp += ((this.options[2] + this.options[23]) * 1000L) + this.options[7] + this.options[48];
        this.fullCritical += this.options[14] + this.options[192];

        applyPercentModifiers(damageOptions, this.fullDamage );
        applyPercentModifiers(hpOptions, this.hpFullTemp);
        applyPercentModifiers(mpOptions, this.mpFullTemp);

        applyCharacterSpecificModifiers();
        applyDefenseModifiers(giapLuyenTap);
    }

    private void applyDefenseModifiers(int giapLuyenTap) {
        int dameAdd = calculateDefenseBasedDamage(giapLuyenTap);

        this.fullDamage += Utils.percentOf(this.fullDamage,(dameAdd - _character.getDameDown()));


        if (this.options[155] > 0) {
            this.hpFullTemp /= 2;
            this.mpFullTemp /= 2;
            this.fullDamage /= 2;
        }

        applyFusionBonuses();
    }

    private void applyFusionBonuses() {
        if (_character.isNhapThe() && _character.getMyDisciple() != null) {
            Disciple disciple = _character.getMyDisciple();
            this.hpFullTemp += disciple.getCharacterInfo().fullHP;
            this.mpFullTemp += disciple.getCharacterInfo().fullMP;
            this.fullDamage += disciple.getCharacterInfo().fullDamage;

            if (disciple.typeDisciple >0) {
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 10);
                this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 10);
                this.fullDamage += Utils.percentOf(this.fullDamage,10);
            }

            if (_character.isSetNappa()) {
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 80);
            }

            if (_character.isMonkey()) {
               this.hpFullTemp += 2;
               this.mpFullTemp += 2;
               int skillLevel = _character.getSkill(13).point;
                this.fullDamage += Utils.percentOf(this.fullDamage, skillLevel *5L);
                this.speed +=2;
            }
            if (_character.zone instanceof GravityRoom) {
                this.speed = 2;
            }

        }
    }

    private int calculateDefenseBasedDamage(int giapLuyenTap) {
        int dameAdd = 0;
        boolean isMacGiapLuyenTap = false;
        switch (giapLuyenTap) {
            case 529, 534 -> dameAdd = 10;
            case 530, 535 -> dameAdd = 20;
            case 531, 536 -> dameAdd = 30;
        }
        if (isMacGiapLuyenTap) dameAdd *= -1;
        return dameAdd;
    }

    private void applyCharacterSpecificModifiers() {
        if(_character.isMonkey()) {
            this.fullCritical += 100;
        }

        this.fullCritical += this.options[47];
    }

    private void applyPercentModifiers(ArrayList<Integer> options, long stat) {
        for (int param : options) {
            stat += Utils.percentOf(stat, param);
        }
    }
}