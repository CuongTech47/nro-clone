package com.ngocrong.backend.map.tzone;

import com.ngocrong.backend.bot.Boss;
import com.ngocrong.backend.bot.Escort;
import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.character.CharacterInfo;
import com.ngocrong.backend.consts.ItemTimeName;
import com.ngocrong.backend.consts.SkillName;
import com.ngocrong.backend.disciple.Disciple;
import com.ngocrong.backend.disciple.MiniDisciple;
import com.ngocrong.backend.item.ItemMap;
import com.ngocrong.backend.item.ItemTime;
import com.ngocrong.backend.map.MapService;
import com.ngocrong.backend.mob.Mob;
import com.ngocrong.backend.model.Npc;
import com.ngocrong.backend.network.Message;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.skill.SpecialSkill;
import com.ngocrong.backend.task.Task;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Zone extends Thread{
    private static final Logger logger = Logger.getLogger(Zone.class);

    public Zone zone;

    public static final byte PTS_GREEN = 0;
    public static final byte PTS_YELLOW = 1;
    public static final byte PTS_RED = 2;
    public static final byte TYPE_ALL = 0;
    public static final byte TYPE_HUMAN = 1;
    public static final byte TYPE_PET = 2;
    public static final byte TYPE_BOSS = 3;
    public static final byte TYPE_MINIPET = 4;
    public static final byte TYPE_ESCORT = 5;

    public short autoIncrease;

    public ReadWriteLock lockChar = new ReentrantReadWriteLock();
    public ReadWriteLock lockMob = new ReentrantReadWriteLock();
    public ReadWriteLock lockItemMap = new ReentrantReadWriteLock();
    public ReadWriteLock lockNpc = new ReentrantReadWriteLock();
    public ReadWriteLock lockSatellite = new ReentrantReadWriteLock();
    public ReadWriteLock lockRespawn = new ReentrantReadWriteLock();

    public TMap map;
    public MapService mapService;
    public int zoneID;
    public boolean running;
    public ArrayList<Char> chars;

    protected ArrayList<Mob> mobs;
    protected ArrayList<Npc> npcs;
    protected int numPlayer, maxPlayer;
    protected ArrayList<ItemMap> satellites;
    protected ArrayList<ItemMap> items;

    public Zone(TMap map, int zoneId) {
        this.map = map;
        this.zoneID = zoneId;
        this.running = true;
        initial();
        start();
    }

    private void initial() {
        long now = System.currentTimeMillis();

    }


    public List<ItemMap> getListSatellite() {
        ArrayList<ItemMap> items = new ArrayList<>();
        lockSatellite.readLock().lock();
        try {
            for (ItemMap item : this.satellites) {
                items.add(item);
            }
        }finally {
            lockSatellite.readLock().unlock();
        }

        return items;
    }

    public void enter(Char character) {
        try {
            handleDiscipleEntry(character);  // Xử lý việc đệ tử của nhân vật có nên theo vào zone hay không.
            handleColdMapEffect(character);  // Áp dụng hiệu ứng của map lạnh (Cold Map) nếu có.
            setOwnerForSatellite(character); // Thiết lập chủ sở hữu cho vệ tinh, nếu có.
            character.service.setMapInfo();  // Cập nhật thông tin bản đồ cho nhân vật.
        } finally {
            finalizeCharacterEntry(character);  // Hoàn tất các bước sau khi nhân vật đã vào zone.
        }
        handleBabidiBaseEffect(character);  // Xử lý hiệu ứng đặc biệt nếu bản đồ không phải Base Babidi.
        synchronizeDiscipleFlag(character); // Đồng bộ hóa cờ trạng thái của đệ tử với chủ nhân.
        reviveIfDead(character);            // Hồi sinh nhân vật nếu nhân vật đang chết.
        postEntryUpdates(character);        // Thực hiện các cập nhật cần thiết sau khi nhân vật vào zone.
    }

    private void postEntryUpdates(Char character) {
        mapService.playerAdd(character);
        mapService.updateBag(character);
        character.setFreeze(false);
        character.setFreezSeconds(0);

        List<Char> charactersInZone = getListChar(Zone.TYPE_ALL);
        for (Char otherCharacter : charactersInZone) {
            if (otherCharacter != character) {
//                character.loadEffectSkillPlayer(otherCharacter);
                handlePetFollow(character, otherCharacter);
                handleMobMeUpdate(character, otherCharacter);
            }
        }
    }

    private void handleMobMeUpdate(Char character, Char otherCharacter) {
        if (character.getMobMe() != null) {
            otherCharacter.service.mobMeUpdate(character, null, -1, (byte) -1, (byte) 0);
        }
    }

    private void handlePetFollow(Char character, Char otherCharacter) {
        if (character.getPetFollow() != null) {
            otherCharacter.service.petFollow(character, (byte) 1);
        }
    }

    private void reviveIfDead(Char character) {
        if (character.isDead()) {
//            character.revival(100);
        }
    }

    private void synchronizeDiscipleFlag(Char character) {
        if (character.myDisciple != null && character.myDisciple.getFlag() != character.getFlag()) {
            character.myDisciple.setFlag();
        }
    }

    private void handleBabidiBaseEffect(Char character) {
        if (!map.isBaseBabidi() && (character.getFlag() == 9 || character.getFlag() == 10)) {
            character.setAccumulatedPoint(null);
            character.setFlag((byte) 0);
        }
    }

    private void finalizeCharacterEntry(Char aChar) {
    }

    private void setOwnerForSatellite(Char aChar) {
    }

    private void handleColdMapEffects(Char _char) {
        if ((_char.isHuman() || _char.isDisciple()) && map.isCold() != _char.isCold()) {
            _char.getCharacterInfo().setCharacterInfo();
            _char.service.loadPoint();
            mapService.playerLoadBody(_char);
            _char.setCold(map.isCold());
            notifyColdEffects(_char);
        }
    }

    private void notifyColdEffects(Char _char) {
        if (map.isCold()) {
            _char.service.serverMessage("Bạn đã đến hành tinh Cold");
            _char.service.serverMessage("Sức tấn công và HP của bạn bị giảm 50% vì lạnh");
        } else {
            _char.service.serverMessage("Sức tấn công và HP của bạn đã trở lại bình thường");
        }
    }
    private void handleColdMapEffect(Char character) {
        if (shouldApplyColdEffect(character)) {
            character.characterInfo.setCharacterInfo();
            character.service.loadPoint();
            mapService.playerLoadBody(character);
            character.setCold(map.isCold());

            if (map.isCold()) {
                character.service.serverMessage("Bạn đã đến hành tinh Cold");
                character.service.serverMessage("Sức tấn công và HP của bạn bị giảm 50% vì lạnh");
            } else {
                character.service.serverMessage("Sức tấn công và HP của bạn đã trở lại bình thường");
            }
        }
    }

    private boolean shouldApplyColdEffect(Char character) {
        return (character.isHuman() || character.isDisciple()) && map.isCold() != character.isCold();
    }

    private void handleDiscipleEntry(Char character) {
        character.zone = this;
        if (character.myDisciple != null && shouldDiscipleFollow(character)) {
            if (!map.isMapSingle() && !map.isDauTruong()) {
                character.myDisciple.followMaster();
                enter(character.myDisciple);
            } else {
                clearDiscipleEffects(character.myDisciple);
            }
        }
    }

    private boolean shouldDiscipleFollow(Char character) {
        return !character.myDisciple.isDead()
                && character.myDisciple.discipleStatus != 3
                && !character.isNhapThe();
    }

    private void clearDiscipleEffects(Char disciple) {
        disciple.clearEffect();
        if (disciple.isMonkey()) {
            disciple.timeOutIsMonkey();
        }
    }

    private void setCharacterZone(Char _Char) {
        _Char.zone = this;
    }


    public List<Mob> getListMob() {
        List<Mob> list = new ArrayList<>();
        lockMob.readLock().lock();
        try {
            for (Mob mob : mobs) {
                list.add(mob);
            }
        } finally {
            lockMob.readLock().unlock();
        }
        return list;
    }

    public List<Npc> getListNpc(Char _c) {
        List<Npc> list = new ArrayList<>();
        lockNpc.readLock().lock();
        try {
            for (Npc npc : npcs) {
                if (npc.templateId == 42) {
                    if (_c.getCharacterInfo().getPower() < 17999000000L) {
                        continue;
                    }
                }
                if ((map.mapID == 27 || map.mapID == 28 || map.mapID == 29) && npc.templateId == 38) {
                    if (Utils.nextInt(2) == 0) {
                        continue;
                    }
                    if (_c.getTaskMain() != null && _c.checkCanEnter(102)) {
                        npc.x = (short) Utils.nextInt(100, map.width - 100);
                        npc.y = map.collisionLand(npc.x, (short) 24);
                    }
                }
                list.add(npc);
            }
        } finally {
            lockNpc.readLock().unlock();
        }
        return list;
    }


    public List<Char> getListChar(int... arrs) {
        List<Char> list = new ArrayList<>();
        lockChar.readLock().lock();
        try {
            for (Char _c : chars) {
                if (!_c.isLoggedOut()) {
                    boolean flag = false;
                    if (arrs.length > 0) {
                        for (int t : arrs) {
                            if (t == TYPE_ALL || (t == TYPE_HUMAN && _c.isHuman()) || (t == TYPE_PET && _c.isDisciple())
                                    || (t == TYPE_BOSS && _c.isBoss()) || (t == TYPE_MINIPET && _c.isMiniDisciple())
                                    || (t == TYPE_ESCORT && _c.isEscort())) {
                                flag = true;
                                break;
                            }
                        }
                    } else {
                        flag = true;
                    }
                    if (flag) {
                        list.add(_c);
                    }
                }
            }
        } finally {
            lockChar.readLock().unlock();
        }
        return list;
    }

    public void leave(Char _char) {
        try {
            if (_char.myDisciple != null && !_char.myDisciple.isDead() && _char.myDisciple.discipleStatus != 3 && !_char.isNhapThe()) {
                Zone z = _char.myDisciple.zone;
                if (z != null) {
                    z.leave(_char.myDisciple);
                }
            }
            if (_char.getHold() != null) {
                _char.getHold().close();
            }
            if (_char.getMobMe() != null) {
                mapService.mobMeUpdate(_char, null, -1, (byte) -1, (byte) 7);
            }
        } finally {
            removeChar(_char);
            MiniDisciple mini = _char.getMiniDisciple();
            if (mini != null) {
                leave(mini);
            }
            Escort escort = _char.getEscortedPerson();
            if (escort != null) {
                leave(escort);
            }
        }
        _char.clearMap();
        mapService.playerRemove(_char);
    }

    private void removeChar(Char aChar) {
        lockChar.writeLock().lock();
        try {
            chars.remove(aChar);
            if (aChar.isHuman()) {
                this.numPlayer = getListChar(TYPE_HUMAN).size();
            }
        }finally {
            lockChar.writeLock().unlock();
        }
    }


    public int getPts() {
        if (this.numPlayer < 8) {
            return PTS_GREEN;
        } else if (this.numPlayer < 10) {
            return PTS_YELLOW;
        }
        return PTS_RED;
    }

    public List<ItemMap> getListItemMap(Task... tasks) {
        ArrayList<ItemMap> items = new ArrayList<>();
        lockItemMap.readLock().lock();
        try {
            for (ItemMap item : this.items) {
                items.add(item);
            }
        } finally {
            lockItemMap.readLock().unlock();
        }
        lockSatellite.readLock().lock();
        try {
            for (ItemMap item : this.satellites) {
                items.add(item);
            }
        } finally {
            lockSatellite.readLock().unlock();
        }
        return items;
    }

    public Char findCharByID(int id) {
        lockChar.readLock().lock();
        try {
            for (Char p : chars) {
                if (p.getId() == id) {
                    return p;
                }
            }
            return null;
        } finally {
            lockChar.readLock().unlock();
        }
    }

    public void addItemMap(ItemMap itemMap) {
        if (itemMap.item.template.type == 22) {
            lockSatellite.writeLock().lock();
            try {
                satellites.add(itemMap);
            } finally {
                lockSatellite.writeLock().unlock();
            }
        } else {
            lockItemMap.writeLock().lock();
            try {
                items.add(itemMap);
            } finally {
                lockItemMap.writeLock().unlock();
            }
        }
    }

    public List<Char> getMemberSameClan(Char _c) {
        List<Char> list = new ArrayList<>();
        if (_c != null && _c.clan != null) {
            lockChar.readLock().lock();
            try {
                for (Char _char : chars) {
                    if (_char.clan == _c.clan) {
                        list.add(_char);
                    }
                }
            } finally {
                lockChar.readLock().unlock();
            }
        }
        return list;
    }

    public void attackPlayer(Char _char, Char target) {
        if (_char.isDead() || (target.isDead() && _char.select.template.id != SkillName.TRI_THUONG)) {
            return;
        }
        ArrayList<Char> targets = new ArrayList<>();
        targets.add(target);
        Skill skill = _char.select;
        if (skill == null) {
            return;
        }
        if (_char.characterInfo.getStamina() <= 0) {
            _char.service.serverMessage("Thể lực đã cạn, hãy nghỉ ngơi lấy lại sức");
            return;
        }
        long manaUse = skill.manaUse;
        if (skill.template.manaUseType == 1) {
            manaUse = Utils.percentOf(_char.characterInfo.getFullMP(), manaUse);
        }
        if (_char.isSkillSpecial() || _char.isBoss()) {
            manaUse = 0;
        }
        if (_char.characterInfo.getMp() < manaUse) {
            _char.service.serverMessage("Không đủ KI đế sử dụng");
            return;
        }
        if (skill.template.type == 3) {
            return;
        }
        int distance1 = Utils.getDistance(_char.getX(), _char.getY(), target.getX(), target.getY());
        int distance2 = Utils.getDistance(0, 0, skill.dx, skill.dy);
        //if (!_char.select.isCooldown() || (_char.isSkillSpecial() && ((!_char.isCharge() && _char.getSeconds() == 0) || (_char.getSeconds() >= 500 && _char.getSeconds() <= 1000)))) {
        if (!skill.isCooldown() || _char.isSkillSpecial()) {
            int percentDame = skill.damage;
            boolean isMiss = Utils.nextInt(100) < target.characterInfo.getAccuracyPercent();
            boolean isCrit = Utils.nextInt(100) < _char.characterInfo.getFullCritical();
            if (_char.isCritFirstHit()) {
                isCrit = true;
                _char.setCritFirstHit(false);
            }
            if (target.getHold() != null && target.isHeld() && target.getHold().getDetainee() == target) {
                isCrit = true;
            }
            SpecialSkill sp = _char.getSpecialSkill();
            switch (skill.template.id) {
                case SkillName.QUA_CAU_KENH_KHI:
                    isMiss = false;
                    break;
                case SkillName.THOI_MIEN: {

                    _char.characterInfo.setMp(_char.characterInfo.getMp() - manaUse);
                    mapService.setSkillPaint_2(_char, targets, (byte) skill.id);
                    ItemTime item = new ItemTime(ItemTimeName.THOI_MIEN, 3782, percentDame, false);
                    target.addItemTime(item);
                    target.setSleep(true);
                    mapService.setEffect(null, target.getId(), Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 41);
                    if (sp != null) {
                        if (sp.id == 17) {
                            _char.setPercentDamageBonus(sp.param);
                        }
                    }
                    return;
                }
                case SkillName.DICH_CHUYEN_TUC_THOI: {
                    _char.setX(target.getX());
                    _char.setY(target.getY());
                    mapService.setEffect(null, target.getId(), Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 40);
                    ItemTime item = new ItemTime(ItemTimeName.DICH_CHUYEN_TUC_THOI, 3779, 3, false);
                    target.addItemTime(item);
                    target.setBlind(true);
                    percentDame = 200 + (skill.point * 10);
                    _char.setCritFirstHit(true);
                    if (sp != null) {
                        if (sp.id == 16) {
                            _char.setPercentDamageBonus(sp.param);
                        }
                    }
                    break;
                }
                case SkillName.BIEN_SOCOLA: {
                    target.transformIntoChocolate(skill.damage, 30);
                    _char.characterInfo.setMp(_char.characterInfo.getMp() - manaUse);
                    mapService.setSkillPaint_2(_char, targets, (byte) skill.id);
                    _char.setCritFirstHit(true);
                    if (sp != null) {
                        if (sp.id == 27) {
                            _char.setPercentDamageBonus(sp.param);
                        }
                    }
                    return;
                }

                case SkillName.CHIEU_KAMEJOKO:
                case SkillName.CHIEU_MASENKO:
                case SkillName.CHIEU_ANTOMIC:
                    if (_char.achievements != null) {
                        _char.achievements.get(4).addCount(1);// Nội công cao cường
                    }
                    break;
                default:
                    break;
            }
            long damageFull = _char.characterInfo.getFullDamage();
//            if (_char instanceof Broly) {
//                damageFull = ((Broly) _char).info.hp / 100;
//            }
            long dame = damageFull + Utils.percentOf(damageFull, (percentDame - 100));
            dame = Utils.nextLong(dame - (dame / 10), dame);
            dame -= Utils.percentOf(dame, target.characterInfo.getOptions()[94]);
            if ((skill.template.id == SkillName.CHIEU_DAM_GALICK && _char.isSetKakarot()) || (skill.template.id == SkillName.CHIEU_KAMEJOKO && _char.isSetSongoku())) {
                dame *= 2;
            }
            boolean xuyenGiap = false;
            if (_char.characterInfo.getOptions()[98] > 0 && (skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_MASENKO || skill.template.id == SkillName.CHIEU_ANTOMIC)) {
                int rd = Utils.nextInt(100);
                if (rd < _char.characterInfo.getOptions()[98]) {
                    xuyenGiap = true;
                }
            }
            int pPhanDonCanChien = 0;
            if (skill.template.id == SkillName.CHIEU_DAM_DRAGON || skill.template.id == SkillName.CHIEU_DAM_DEMON || skill.template.id == SkillName.CHIEU_DAM_GALICK || skill.template.id == SkillName.KAIOKEN || skill.template.id == SkillName.LIEN_HOAN) {
                if (_char.characterInfo.getOptions()[99] > 0) {
                    int rd = Utils.nextInt(100);
                    if (rd < _char.characterInfo.getOptions()[99]) {
                        xuyenGiap = true;
                    }
                }
                pPhanDonCanChien = _char.characterInfo.getOptions()[15];
            }
            if (!xuyenGiap) {
                dame -= target.characterInfo.getFullDefense();
            }
            if (map.isBaseBabidi() && !target.isBoss() && !_char.isBoss()) {
                dame = target.characterInfo.getFullHP() / 10;
            }
            if (dame <= 0) {
                dame = 1;
            }

//            if (target instanceof GeneralWhite) {
//                Mob mob = findMobByTemplateID(22, false);
//                if (mob != null) {
//                    isMiss = true;
//                }
//            }
            if (target.characterInfo.getOptions()[3] > 0 && (skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_MASENKO || skill.template.id == SkillName.CHIEU_ANTOMIC)) {
                long mp = Utils.percentOf(dame, target.characterInfo.getOptions()[3]);
                target.characterInfo.recovery(CharacterInfo.MP, mp);
                isMiss = true;
            }
            if (target.isGiapXen()) {
                dame /= 2;
            }
            if (target.characterInfo.getOptions()[157] > 0) {
                long pM = target.characterInfo.getMp() * 100 / target.characterInfo.getFullMP();
                if (pM < 20) {
                    dame -= Utils.percentOf(dame, target.characterInfo.getOptions()[157]);
                }
            }
            if (sp != null) {
                if ((sp.id == 1 && skill.template.id == SkillName.CHIEU_DAM_GALICK) || (sp.id == 2 && skill.template.id == SkillName.CHIEU_ANTOMIC) || (sp.id == 3 && _char.isMonkey())
                        || (sp.id == 11 && skill.template.id == SkillName.CHIEU_DAM_DRAGON) || (sp.id == 12 && skill.template.id == SkillName.CHIEU_KAMEJOKO)
                        || (sp.id == 21 && skill.template.id == SkillName.CHIEU_DAM_DEMON) || (sp.id == 22 && skill.template.id == SkillName.CHIEU_MASENKO) || (sp.id == 26 && skill.template.id == SkillName.LIEN_HOAN)) {
                    dame += Utils.percentOf(dame, sp.param);
                }
                if (sp.id == 31) {
                    long pHP = _char.characterInfo.getHp() * 100 / _char.characterInfo.getFullHP();
                    if (pHP < sp.param) {
                        isCrit = true;
                    }
                }
            }
            if (skill.template.id == SkillName.CHIEU_DAM_DRAGON || skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_DAM_DEMON || skill.template.id == SkillName.CHIEU_MASENKO || skill.template.id == SkillName.CHIEU_DAM_GALICK || skill.template.id == SkillName.CHIEU_ANTOMIC || skill.template.id == SkillName.LIEN_HOAN || skill.template.id == SkillName.KAIOKEN) {
                int percentDamageBonus = _char.getPercentDamageBonus();
                if (percentDamageBonus > 0) {
                    dame += Utils.percentOf(dame, percentDamageBonus);
                    _char.setPercentDamageBonus(0);
                }
            }
            if (skill.template.id == SkillName.MAKANKOSAPPO) {
                dame = Utils.percentOf(_char.characterInfo.getMp(), percentDame);
                if (_char.isSetPicolo()) {
                    dame += dame / 2;
                }
                isCrit = false;
                isMiss = false;
                _char.characterInfo.setMp(1);
            } else if (skill.template.id == SkillName.QUA_CAU_KENH_KHI) {
                long hp = getTotalHP();
                dame = (hp / 10) + (_char.characterInfo.getFullDamage() * 10);
                if (target.isBoss()) {
                    dame /= 2;
                }
                if (_char.isSetKirin()) {
                    dame *= 2;
                }
            } else if (skill.template.id == SkillName.LIEN_HOAN) {
                if (_char.isSetOcTieu()) {
                    dame += dame / 2;
                }
            }
            if (_char.characterInfo.getOptions()[111] > Utils.nextInt(100)) {
                isMiss = true;
            }
            if (_char.isSkillSpecial()) {
                isCrit = false;
            }
            if (isCrit) {
                int rd = Utils.nextInt(100);
                if (rd < target.characterInfo.getOptions()[191]) {
                    isCrit = false;
                }
            }
            if (isCrit) {
                dame *= 2;
                dame += Utils.percentOf(dame, _char.characterInfo.getOptions()[5]);
            }
            if (!_char.isSkillSpecial()) {
//                if (target instanceof Broly) {
//                    long p = ((Broly) target).info.hpFull / 100;
//                    if (dame > p) {
//                        dame = p;
//                    }
//                }
            }
            if (dame > 0) {
                long reactDame = Utils.percentOf(dame, target.characterInfo.getOptions()[97] + pPhanDonCanChien);
                if (reactDame >= _char.characterInfo.getHp()) {
                    reactDame = _char.characterInfo.getHp() - 1;
                }
                if (reactDame > 0) {
                    if (_char.characterInfo.getHp() > 1) {

                        _char.characterInfo.setHp(_char.characterInfo.getHp() - reactDame);
                        mapService.attackPlayer(_char, reactDame, false, (byte) 36);
                    } else if (_char.characterInfo.getHp() == 1) {
                        mapService.attackPlayer(_char, 0, false, (byte) 36);
                    }
                }
            }
            if (!isMiss && target.isProtected()) {
                if (dame > target.characterInfo.getFullHP()) {
                    target.setTimeForItemtime(0, 0);
                    target.service.serverMessage("Khiên năng lượng đã vỡ");
                }
                dame = 1;
                if (target.characterInfo.getHp() <= dame) {
                    isMiss = true;
                }
            }
            if (isMiss) {
                dame = 0;
            }
            if (dame == 0) {
                isCrit = false;
            }
            if (distance1 > distance2 + 50) {
                dame = 0;
            }
//            if (target instanceof MajorMetallitron) {
//                long p = target.characterInfo.getFullHP() / 100;
//                if (dame > p) {
//                    int skillTemplateId = skill.template.id;
//                    if (skillTemplateId == SkillName.CHIEU_DAM_DRAGON || skillTemplateId == SkillName.CHIEU_DAM_DEMON || skillTemplateId == SkillName.CHIEU_DAM_GALICK) {
//                        dame = p;
//                    }
//                }
//            }
            if (dame < 0) {
                dame = 0;
            }
            if (_char.useSkill(target)) {
                if (!_char.isSkillSpecial()) {
                    long now = System.currentTimeMillis();
                    skill.lastTimeUseThisSkill = now;
                }
                _char.characterInfo.setMp(_char.characterInfo.getMp() - manaUse);
                if (!_char.isBoss()) {
                    _char.characterInfo.updateSatamina(-1);
                }
                target.getLock().lock();
                try {
                    if (_char.isDead() || target.isDead()) {
                        return;
                    }
                    mapService.setSkillPaint_2(_char, targets, (byte) skill.id);
                    if (skill.template.id == SkillName.DICH_CHUYEN_TUC_THOI) {
                        mapService.setPosition(_char, (byte) 1);
                    }
                    if (target.myDisciple != null && target.myDisciple.zone == this) {
                        target.myDisciple.addTarget(_char);
                    }
                    if (target.isDisciple()) {
                        Disciple disciple = (Disciple) target;
                        disciple.addTarget(_char);
                    }
                    if (dame > 0) {

                        target.characterInfo.setHp(target.characterInfo.getHp() - dame);
                        if (skill.template.id == SkillName.QUA_CAU_KENH_KHI) {
                            lockMob.readLock().lock();
                            try {
                                for (Mob mob : mobs) {
                                    if (!mob.isDead()) {
                                        int distance = Utils.getDistance(mob.x, mob.y, target.getX(), target.getY());
                                        if (distance < distance2) {
                                            mob.hp -= dame;
                                            mapService.attackNpc(dame, isCrit, mob, (byte) -1);
                                            if (mob.hp <= 0) {
                                                _char.kill(mob);
                                                mob.startDie(dame, isCrit, _char);
                                            }
                                        }
                                    }
                                }
                            } finally {
                                lockMob.readLock().unlock();
                            }
                        }
                        if (target.isBoss()) {
                            Boss boss = (Boss) target;
                            boss.addTarget(_char);
                        }
                        _char.characterInfo.recovery(CharacterInfo.HP, Utils.percentOf(dame, _char.characterInfo.getOptions()[95]));
                        _char.characterInfo.recovery(CharacterInfo.MP, Utils.percentOf(dame, _char.characterInfo.getOptions()[96]));
                        if (target.isBoss()) {
                            long exp = dame / 10;
                            if (exp <= 0) {
                                exp = 1;
                            }
                            _char.addExp(CharacterInfo.POWER_AND_POTENTIAL, exp, true, true);
                        }
                    }
                    mapService.attackPlayer(target, dame, isCrit, (byte) -1);
                    if (target.characterInfo.getHp() <= 0) {
                        _char.kill(target);
                        target.killed(_char);
                        target.startDie();
                        if (_char.isAutoPlay()) {
                            Mob mob = findMob();
                            if (mob != null) {
                                _char.setX(mob.x);
                                _char.setY(mob.y);
                                mapService.setPosition(_char, (byte) 0);
                            }
                        }
                    }
                } finally {
                    target.getLock().unlock();
                }
            }
            _char.setSkillSpecial(false);
            if (_char.getMobMe() != null) {
                _char.getMobMe().attack(_char, target);
            }
        }
    }

    private Mob findMob() {
        lockMob.readLock().lock();
        try {
            for (Mob mob : mobs) {
                if (!mob.isDead()) {
                    return mob;
                }
            }
        } finally {
            lockMob.readLock().unlock();
        }
        return null;
    }

    private long getTotalHP() {
        long totalHP = 0;
        lockChar.readLock().lock();
        try {
            for (Char _c : chars) {
                if (_c.isHuman()) {
                    totalHP += _c.characterInfo.getHp();
                }
            }
        } finally {
            lockChar.readLock().unlock();
        }
        lockMob.readLock().lock();
        try {
            for (Mob mob : mobs) {
                totalHP += mob.hp;
            }
        } finally {
            lockMob.readLock().unlock();
        }
        return totalHP;
    }

    private Mob findMobByTemplateID(int id, boolean isDead) {
        lockMob.readLock().lock();
        try {
            for (Mob mob : mobs) {
                if (mob.isDead() == isDead && mob.templateId == id) {
                    return mob;
                }
            }
        } finally {
            lockMob.readLock().unlock();
        }
        return null;
    }


    public Npc findNpcByID(int npcId) {
        lockNpc.readLock().lock();
        try {
            for (Npc npc : this.npcs) {
                if (npc.template.npcTemplateId == npcId) {
                    return npc;
                }
            }
        } finally {
            lockNpc.readLock().unlock();
        }
        return null;
    }
}
