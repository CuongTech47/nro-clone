package com.ngocrong.backend.character;

import com.google.gson.annotations.SerializedName;
import com.ngocrong.backend.bot.TrongTai;
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
import com.ngocrong.backend.model.Caption;
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

    private transient long hpFullTemp;
    private transient long mpFullTemp;

    private transient int accuracy;
    private transient int accuracyPercent;
    private transient int evasion;
    private transient int evasionPercent;


    private transient int level;
    private transient long levelPercent;

    // Các tùy chọn và phần thưởng
    private transient int[] options;
    private ArrayList<Integer> damageOptions ;
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
        }else if (_character instanceof TrongTai){
            this.baseHP = 500;
            this.baseMP = 500;
            this.baseDamage = 0;
            this.baseCritical = 0;
            this.baseDefense = 0;
            this.power = 0;
            this.potential = 0;
        }

        this.fullHP = this.baseHP;
        this.fullMP = this.baseMP;
    }

    public void setCharacter(Char _character) {
        this._character = _character;
    }


    // Thiết lập giới hạn sức mạnh
    public void setPowerLimited() {
        this.powerLimitMark = PowerLimitMark.limitMark.get(this.openPowerCount);
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
        this.options = new int[server.iOptionTemplates.size()];
        if (damageOptions == null) {
            damageOptions = new ArrayList<>();
        } else {
            damageOptions.clear();
        }
        if (hpOptions == null) {
            hpOptions = new ArrayList<>();
        } else {
            hpOptions.clear();
        }
        if (mpOptions == null) {
            mpOptions = new ArrayList<>();
        } else {
            mpOptions.clear();
        }
        this.speed = 6;
        this.hpFullTemp = this.baseHP;
        this.mpFullTemp = this.baseMP;
        this.fullDamage = this.baseDamage;
        this.fullCritical = this.baseCritical;
        this.fullDefense = this.baseDefense * 4;
        int giapLuyenTap = -1;
        boolean isMacGiapLuyenTap = false;
        boolean isVoHinh = false;
        boolean isUnaffectedCold = false;
        boolean isHaveEquipTeleport = false;
        boolean isHaveEquipSelfExplosion = false;
        boolean isHaveEquipInvisible = false;
        boolean isHaveEquipTransformIntoChocolate = false;
        boolean isHaveEquipTransformIntoStone = false;
        boolean isHaveEquipMiNuong = false;
        boolean isHaveEquipBulma = false;
        boolean isHaveEquipXinbato = false;
        boolean isHaveEquipBuiBui = false;
        boolean isKhangTDHS = false;
        boolean isDoSaoPhaLe = false;
        int setThienXinHang = 0, setKirin = 0, setSongoku = 0, setPicolo = 0, setOcTieu = 0, setPikkoroDaimao = 0, setKakarot = 0, setCaDic = 0, setNappa = 0;
        int setThanLinh = 0;
        int upgradeMin = -1;
        int n = 0;
        if (_character.itemBody != null) {
            for (Item item : _character.itemBody) {
                if (item != null) {
                    if (item.template.type < 5) {
                        n++;
                    }
                    if (item.isNhapThe && !_character.isNhapThe()) {
                        continue;
                    }
                    if (item.template.type == 32) {
                        if (giapLuyenTap == -1) {
                            giapLuyenTap = item.id;
                            isMacGiapLuyenTap = true;
                        }
                    }
                    if (item.id == 464 || item.id == 584) {
                        isHaveEquipBulma = true;
                    }
                    if (item.id == 860) {
                        isHaveEquipMiNuong = true;
                    }
                    if (item.id == 458) {
                        isHaveEquipXinbato = true;
                    }
                    if (item.id == 575) {
                        isHaveEquipBuiBui = true;
                    }
                    if (item.template.isThanLinh()) {
                        setThanLinh++;
                    }
                    ArrayList<ItemOption> options = item.getOptions();
                    for (ItemOption o : options) {
                        int id = o.optionTemplate.id;
                        int param = o.param;
                        addOption(id, param);
                        if (id == 72) {
                            if (upgradeMin == -1 || upgradeMin > param) {
                                upgradeMin = param;
                            }
                        }
                        if (id == 110) {
                            isDoSaoPhaLe = true;
                        }
                        if (id == 116) {
                            isKhangTDHS = true;
                        }
                        if (id == 127) {
                            setThienXinHang++;
                        }
                        if (id == 128) {
                            setKirin++;
                        }
                        if (id == 129) {
                            setSongoku++;
                        }
                        if (id == 130) {
                            setPicolo++;
                        }
                        if (id == 131) {
                            setOcTieu++;
                        }
                        if (id == 132) {
                            setPikkoroDaimao++;
                        }
                        if (id == 133) {
                            setKakarot++;
                        }
                        if (id == 134) {
                            setCaDic++;
                        }
                        if (id == 135) {
                            setNappa++;
                        }
                        if (id == 25) {
                            isHaveEquipInvisible = true;
                        }
                        if (id == 26) {
                            isHaveEquipTransformIntoStone = true;
                        }
                        if (id == 105) {
                            isVoHinh = true;
                        }
                        if (id == 106) {
                            isUnaffectedCold = true;
                        }
                        if (id == 29) {
                            isHaveEquipTransformIntoChocolate = true;
                        }
                        if (id == 33) {
                            isHaveEquipTeleport = true;
                        }
                        if (id == 153) {
                            isHaveEquipSelfExplosion = true;
                        }
                    }
                }
            }
        }
        if (n == 5) {
            _character.setIdEffSetItem((short) upgradeMin);
        }
        List<AmbientEffect> ambientEffects = _character.getAmbientEffects();
        if (ambientEffects != null) {
            for (AmbientEffect am : ambientEffects) {
                int[] o = am.getItemOption();
                addOption(o[0], o[1]);
            }
        }
        _character.setVoHinh(isVoHinh);
        _character.setDoSaoPhaLe(isDoSaoPhaLe);
        _character.setUnaffectedCold(isUnaffectedCold);
        _character.setHaveEquipTeleport(isHaveEquipTeleport);
        _character.setHaveEquipSelfExplosion(isHaveEquipSelfExplosion);
        _character.setHaveEquipInvisible(isHaveEquipInvisible);
        _character.setHaveEquipTransformIntoChocolate(isHaveEquipTransformIntoChocolate);
        _character.setHaveEquipTransformIntoStone(isHaveEquipTransformIntoStone);
        _character.setHaveEquipBulma(isHaveEquipBulma);
        _character.setHaveEquipMiNuong(isHaveEquipMiNuong);
        _character.setHaveEquipBuiBui(isHaveEquipBuiBui);
        _character.setHaveEquipXinbato(isHaveEquipXinbato);
        _character.setKhangTDHS(isKhangTDHS);
        // set kich hoat
        _character.setSetCaDic(setCaDic == 5);
        _character.setSetKakarot(setKakarot == 5);
        _character.setSetKirin(setKirin == 5);
        _character.setSetNappa(setNappa == 5);
        _character.setSetOcTieu(setOcTieu == 5);
        _character.setSetPicolo(setPicolo == 5);
        _character.setSetPikkoroDaimao(setPikkoroDaimao == 5);
        _character.setSetSongoku(setSongoku == 5);
        _character.setSetThienXinHang(setThienXinHang == 5);
        _character.setSetThanLinh(setThanLinh == 5);
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
        MiniDisciple mini = _character.getMiniDisciple();
        if (mini != null) {
            if (mini.item != null) {
                ArrayList<ItemOption> options = mini.item.getOptions();
                for (ItemOption o : options) {
                    addOption(o.optionTemplate.id, o.param);
                }
            }
        }
        if (_character.isNhapThe() && _character.myDisciple != null) {
            if (_character.typePorata == 1) {
                Item item = _character.getItemInBag(ItemName.BONG_TAI_PORATA_CAP_2);
                if (item != null) {
                    ArrayList<ItemOption> options = item.getOptions();
                    for (ItemOption itemOption : options) {
                        addOption(itemOption.optionTemplate.id, itemOption.param);
                    }
                }
            }
        }
        this.fullDamage += this.options[0];
        this.hpFullTemp += ((this.options[2] + this.options[22]) * 1000L) + this.options[6] + this.options[48];
        this.mpFullTemp += ((this.options[2] + this.options[23]) * 1000L) + this.options[7] + this.options[48];
        this.fullCritical += this.options[14] + this.options[192];
        for (int param : damageOptions) {
            this.fullDamage += Utils.percentOf(this.fullDamage, param);
        }
        for (int param : hpOptions) {
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, param);
        }
        for (int param : mpOptions) {
            this.mpFullTemp += Utils.percentOf(this.mpFullTemp, param);
        }
        if (_character.isMonkey()) {
            this.fullCritical += 100;
        }
        this.fullDefense += this.options[47];
        if (_character.itemBag != null) {
            for (Item item : _character.itemBag) {
                if (item != null) {
                    if (item.template.type == 32) {
                        if (giapLuyenTap == -1) {
                            ArrayList<ItemOption> options = item.getOptions();
                            for (ItemOption option : options) {
                                if (option.optionTemplate.id == 9) {
                                    if (option.param > 0) {
                                        giapLuyenTap = item.id;
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        int dameAdd = 0;
        switch (giapLuyenTap) {
            case 529:
            case 534:
                dameAdd = 10;
                break;

            case 530:
            case 535:
                dameAdd = 20;
                break;

            case 531:
            case 536:
                dameAdd = 30;
                break;
        }
        if (isMacGiapLuyenTap) {
            dameAdd *= -1;
        }
        this.accuracy = this.options[10];
        this.accuracyPercent = this.options[18];
        this.evasion = this.options[17];
        this.evasionPercent = this.options[108];
        this.fullDamage += Utils.percentOf(this.fullDamage, (dameAdd - _character.getDameDown()));
        if (this.options[155] > 0) {
            this.hpFullTemp /= 2;
            this.mpFullTemp /= 2;
            this.fullDamage /= 2;
        }
        if (_character.isNhapThe() && _character.myDisciple != null) {
            Disciple disciple = _character.myDisciple;
            this.hpFullTemp += disciple.characterInfo.getFullHP();
            this.mpFullTemp += disciple.characterInfo.getFullMP();
            this.fullDamage += disciple.characterInfo.fullDamage;
            if (disciple.typeDisciple > 0) {
                this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 10);
                this.mpFullTemp += Utils.percentOf(this.mpFullTemp, 10);
                this.fullDamage += Utils.percentOf(this.fullDamage, 10);
            }
        }
        if (_character.isSetNappa()) {
            this.hpFullTemp += Utils.percentOf(this.hpFullTemp, 80);
        }
        if (_character.isMonkey()) {
            this.hpFullTemp *= 2;
            this.mpFullTemp *= 2;
            int skillLevel = _character.getSkill(13).point;
            this.fullDamage += Utils.percentOf(this.fullDamage, skillLevel * 5L);
            this.speed += 2;
        }
        if (_character.zone instanceof GravityRoom) {
            this.speed = 2;
        }
        if (_character.isHuytSao()) {
            long hpAdd = Utils.percentOf(this.hpFullTemp, this._character.getHpPercent());
            this.hpFullTemp += hpAdd;
        }
        if (_character.isBoHuyet()) {
            this.hpFullTemp *= 2;
        }
        if (_character.isBoKhi()) {
            this.mpFullTemp *= 2;
        }
        if (_character.isCuongNo()) {
            this.fullDamage *= 2;
        }
        if (_character.clan != null) {
            ClanMember mem = _character.clan.getMember(_character.getId());
            if (mem != null) {
                for (ClanReward clanReward : mem.rewards) {
                    switch (clanReward.getStar()) {
                        case 1:
                            this.fullDamage += fullDamage * 15 / 100;
                            break;
                        case 2:
                            this.hpFullTemp += hpFullTemp * 20 / 100;
                            break;
                        case 3:
                            this.mpFullTemp += mpFullTemp * 20 / 100;
                            break;
                        case 4:
                            options[101] += 20;
                            break;
                        case 5:
                            options[108] += 10;
                            break;
                        case 6:
                            options[94] += 10;
                            break;
                        case 7:
                            options[95] += 10;
                            options[96] += 10;
                            break;
                    }
                }
            }
        }
        int phuX = _character.getPhuX();
        if (phuX > 0) {
            this.hpFullTemp *= phuX;
            this.mpFullTemp *= phuX;
            this.fullDamage *= phuX;
        }
        if (_character.isHaveFood()) {
            this.fullDamage += this.fullDamage / 10;
        }
        if (_character.zone != null && _character.zone.map.isCold()) {
            if (!isUnaffectedCold) {
                this.hpFullTemp /= 2;
                this.fullDamage /= 2;
            }
        }

        if (this.hpFullTemp <= 0) {
            this.hpFullTemp = 1;
        }
        if (this.mpFullTemp <= 0) {
            this.mpFullTemp = 1;
        }
        if (this.hpFullTemp > Integer.MAX_VALUE) {
            this.hpFullTemp = Integer.MAX_VALUE;
        }
        if (this.mpFullTemp > Integer.MAX_VALUE) {
            this.mpFullTemp = Integer.MAX_VALUE;
        }
        this.fullHP = this.hpFullTemp;
        this.fullHP = this.mpFullTemp;
        if (this.hp > this.fullHP) {
            this.hp = this.fullHP;
        }
        if (this.mp > this.fullMP) {
            this.mp = this.fullMP;
        }


    }





















    public void applyCharLevelPercent() {
        try {
            long num = 1L;
            long num2 = 0L;
            int num3 = 0;
            Server server = DragonBall.getInstance().getServer();
            int size = server.powers.size();
            for (int i = size - 1; i >= 0; i--) {
                if (this.power >= server.powers.get(i)) {
                    if (i == size - 1) {
                        num = 1L;
                    } else {
                        num = server.powers.get(i + 1) - server.powers.get(i);
                    }
                    num2 = this.power - server.powers.get(i);
                    num3 = i;
                    break;
                }
            }
            this.level = num3;
            this.levelPercent = num2 * 10000L / num;
        } catch (Exception ignored) {
        }
    }


    public String getStrLevel() {
        return Caption.getCaption(_character.getClassId()).get(this.level) + "+" + (this.levelPercent / 100L) + "." + (this.levelPercent % 100L) + "%";
    }

    public void addPowerOrPotential(byte type, long exp) {
        if (this.power >= this.powerLimitMark.getPower()) {
            return;
        }
        if (this.power + exp >= this.powerLimitMark.getPower()) {
            exp = this.powerLimitMark.getPower() - this.power;
        }
        if (exp <= 0) {
            return;
        }
        switch (type) {
            case POWER_AND_POTENTIAL:
                this.power += exp;
                this.potential += exp;
                break;

            case POWER:
                this.power += exp;
                break;

            case POTENTIAL:
                this.potential += exp;
                break;
        }
        // update level
        if (type == POWER || type == POWER_AND_POTENTIAL) {
            Server server = DragonBall.getInstance().getServer();
            if (this.level < server.powers.size() - 1) {
                if (this.power >= server.powers.get(this.level + 1)) {
                    this.level++;
                    if (_character.isDisciple()) {
                        Disciple p = (Disciple) _character;
                        p.master.service.chat(p, "Sự phụ ơi, con lên cấp rồi");
                        setMaxStamina();
                    }
                }
            }
        }
        _character.service.addExp(type, exp);
    }

}