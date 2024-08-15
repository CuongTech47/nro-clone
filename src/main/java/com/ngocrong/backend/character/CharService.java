package com.ngocrong.backend.character;


import com.ngocrong.backend.clan.Clan;
import com.ngocrong.backend.clan.ClanMember;
import com.ngocrong.backend.clan.ClanMessage;
import com.ngocrong.backend.consts.Cmd;
import com.ngocrong.backend.consts.Language;
import com.ngocrong.backend.effect.EffectChar;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.item.ItemOption;
import com.ngocrong.backend.lib.KeyValue;
import com.ngocrong.backend.map.tzone.TMap;
import com.ngocrong.backend.map.tzone.Zone;
import com.ngocrong.backend.mob.Mob;
import com.ngocrong.backend.model.*;
import com.ngocrong.backend.network.Message;
import com.ngocrong.backend.network.Service;
import com.ngocrong.backend.network.Session;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.skill.SpecialSkill;
import com.ngocrong.backend.skill.SpecialSkillTemplate;
import com.ngocrong.backend.util.Utils;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CharService  extends Service {

    private static final Logger logger = Logger.getLogger(Service.class);

    public CharService(Session session) {
        super(session);
    }

    public CharService(Char deTu) {
        super(deTu);
    }


    @Override
    public void loadHP() {
        super.loadHP();
    }

    public void playerLoadBody(Char aChar) {
        try {
            Message message = messageSubCommand(Cmd.PLAYER_LOAD_BODY);
            DataOutputStream ds = message.getWriter();
            ds.writeInt(aChar.getId());
            ds.writeLong(aChar.getCharacterInfo().getHp());
            ds.writeLong(aChar.getCharacterInfo().getFullHP());
            ds.writeShort(aChar.getEff5buffhp());
            ds.writeShort(aChar.getEff5buffmp());
            ds.flush();
            sendMessage(message);
            message.cleanup();
        }catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void mobMeUpdate(Char character, Object target, long damage, byte skillId, byte type) {
        try (Message message = new Message(Cmd.MOB_ME_UPDATE);
             DataOutputStream dataOutput = message.getWriter()) {

            dataOutput.writeByte(type);
            dataOutput.writeInt(character.getId());

            switch (type) {
                case 0:
                    writeMobMeUpdate(dataOutput, character);
                    break;
                case 1:
                    writeMobTargetUpdate(dataOutput, target);
                    break;
                case 2:
                    writeCharTargetUpdate(dataOutput, target, damage);
                    break;
                case 3:
                    writeMobDamageUpdate(dataOutput, target, damage);
                    break;
                case 5:
                    writeSkillAttackUpdate(dataOutput, target, skillId, damage);
                    break;
                case 6:
                case 7:
                    // No additional data needed for these types
                    break;
                default:
                    logger.warn("Unknown type for mobMeUpdate: " + type);
            }

            dataOutput.flush();
            sendMessage(message);
        } catch (Exception ex) {
            logger.error("Failed to update mobMe!", ex);
        }
    }

    private void writeMobMeUpdate(DataOutputStream dataOutput, Char character) throws IOException {
        dataOutput.writeShort(character.getMobMe().templateId);
        dataOutput.writeLong(character.getMobMe().hp);
    }

    private void writeMobTargetUpdate(DataOutputStream dataOutput, Object target) throws IOException {
        Mob mob = (Mob) target;
        dataOutput.writeInt(mob.mobId);
    }

    private void writeCharTargetUpdate(DataOutputStream dataOutput, Object target, long damage) throws IOException {
        Char targetChar = (Char) target;
        dataOutput.writeInt(targetChar.getId());
        dataOutput.writeLong(damage);
        dataOutput.writeLong(targetChar.getCharacterInfo().getHp());
    }

    private void writeMobDamageUpdate(DataOutputStream dataOutput, Object target, long damage) throws IOException {
        Mob mob = (Mob) target;
        dataOutput.writeInt(mob.mobId);
        dataOutput.writeLong(mob.hp);
        dataOutput.writeLong(damage);
    }

    private void writeSkillAttackUpdate(DataOutputStream dataOutput, Object target, byte skillId, long damage) throws IOException {
        Mob mob = (Mob) target;
        dataOutput.writeByte(skillId);
        dataOutput.writeInt(mob.mobId);
        dataOutput.writeLong(damage);
        dataOutput.writeLong(mob.hp);
    }

    public void move(Char _char) {
        try {
            Message mss = new Message(Cmd.PLAYER_MOVE);
            DataOutputStream ds = mss.getWriter();
            ds.writeInt(_char.getId());
            ds.writeShort(_char.getX());
            ds.writeShort(_char.getY());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void playerSpeed(Char _char) {
        try {
            Message mss = messageSubCommand(Cmd.PLAYER_SPEED);
            DataOutputStream ds = mss.getWriter();
            ds.writeInt(_char.getId());
            ds.writeByte(_char.getCharacterInfo().getSpeed());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void playerRemove(Char aChar) {
        try {
            Message mss = messageSubCommand(Cmd.PLAYER_REMOVE);
            DataOutputStream ds = mss.getWriter();
            ds.writeInt(aChar.getId());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        }catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void updateBag(Char aChar) {
        try {
            Message mss = new Message(Cmd.UPDATE_BAG);
            DataOutputStream ds = mss.getWriter();
            ds.writeInt(aChar.getId());
            ds.writeByte(aChar.getBag());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void updateBag(int index, int quantity) {
        try {
            Message mss = new Message(Cmd.BAG);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(2);
            ds.writeByte(index);
            ds.writeInt(quantity);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void playerAdd(Char _char) {
        try {
            Message mss = new Message(Cmd.PLAYER_ADD);
            DataOutputStream ds = mss.getWriter();
            ds.writeInt(_char.getId());
            ds.writeInt(_char.getClanID());
            writeCharInfo(mss, _char);
            ds.writeByte(_char.getTeleport());//teleport
            ds.writeBoolean(_char.isMonkey());
            ds.writeShort(_char.getIdMount());
            ds.writeByte(_char.getFlag());
            ds.writeBoolean(_char.isNhapThe());
            ds.writeShort(_char.getIdAuraEff());
            ds.writeShort(_char.getIdEffSetItem());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void writeCharInfo(Message mss, Char _char) {
        try {
            String name = _char.getName();
            if (_char.isDisciple()) {
                name = "$" + name;
            } else if (_char.isMiniDisciple()) {
                name = "#" + name;
            }
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(_char.characterInfo.getLevel());
            ds.writeBoolean(_char.isInvisible());
            ds.writeByte(_char.getTypePk());
            ds.writeByte(_char.getClassId());
            ds.writeByte(_char.getGender());
            ds.writeShort(_char.getHead());
            ds.writeUTF(name);
            ds.writeLong(_char.characterInfo.getHp());
            ds.writeLong(_char.characterInfo.getFullHP());
            ds.writeShort(_char.getBody());
            ds.writeShort(_char.getLeg());
            ds.writeByte(_char.getBag());
            ds.writeByte(0);
            ds.writeShort(_char.getX());
            ds.writeShort(_char.getY());
            ds.writeShort(_char.getEff5buffhp());
            ds.writeShort(_char.getEff5buffmp());
            ds.writeByte(_char.effects.size());
            for (EffectChar eff : _char.effects) {
                ds.writeByte(eff.template.id);
                ds.writeInt(eff.timeStart);
                ds.writeInt(eff.timeLenght);
                ds.writeShort(eff.param);
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void setEffect(Hold hold, int id, byte status, byte type, short effId) {
        try {
            Message mss = new Message(Cmd.HOLD);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(status);
            ds.writeByte(type);
            if (type == Skill.CHARACTER) {
                if (status == Skill.REMOVE_ALL_EFFECT) {
                    ds.writeInt(id);
                }
                ds.writeByte(effId);
                ds.writeInt(id);
                if (effId == 32) {
                    if (status == Skill.ADD_EFFECT) {
                        ds.writeInt(hold.getHolder().getId());
                    }
                }
            }
            if (type == Skill.MONSTER) {
                ds.writeByte(effId);
                ds.writeInt(id);
                if (effId == 32) {
                    if (status == Skill.ADD_EFFECT) {
                        ds.writeInt(hold.getHolder().getId());
                    }
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void skillNotFocus(int charId, short skillId, byte type, ArrayList<Mob> mobs, ArrayList<Char> chars) {
        try {
            Message mss = new Message(Cmd.SKILL_NOT_FOCUS);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(type);
            ds.writeInt(charId);
            ds.writeShort(skillId);
            if (type == 0) {
                ds.writeByte(mobs.size());
                for (Mob mob : mobs) {
                    ds.writeInt(mob.mobId);
                    ds.writeByte(mob.seconds);
                }
                ds.writeByte(chars.size());
                for (Char _c : chars) {
                    ds.writeInt(_c.getId());
                    ds.writeByte(_c.getFreezSeconds());
                }
            }
            if (type == 4 || type == 7) {
                ds.writeShort(player.getSeconds());
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception e) {
            logger.error("failed!", e);
        }
    }

    public void playerLoadAll(Char _char) {
        try {
            Message mss = messageSubCommand(Cmd.PLAYER_LOAD_ALL);
            DataOutputStream ds = mss.getWriter();
            ds.writeInt(_char.getId());
            ds.writeInt(_char.getClanID());
            writeCharInfo(mss, _char);
            ds.writeShort(_char.getIdAuraEff());
            ds.writeShort(_char.getIdEffSetItem());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void petFollow(Char _c, byte type) {
        try {
            Message mss = new Message(Cmd.STATUS_PET);
            DataOutputStream ds = mss.getWriter();
            ds.writeInt(_c.getId());
            ds.writeByte(type);
            if (type == 1) {
                PetFollow pet = _c.getPetFollow();
                ds.writeShort(pet.getSmallID());
                ds.writeByte(pet.getImg());
                byte[] frame = pet.getFrame();
                ds.writeByte(frame.length);
                for (byte f : frame) {
                    ds.writeByte(f);
                }
                ds.writeShort(pet.getW());
                ds.writeShort(pet.getH());
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }

    }

    public void setPowerInfo(PowerInfo p) {
        try {
            Message mss = new Message(Cmd.POWER_INFO);
            DataOutputStream ds = mss.getWriter();
            ds.writeUTF(p.getInfo());
            ds.writeShort(p.getPoint());
            ds.writeShort(p.getMaxPoint());
            ds.writeShort(p.getSeconds());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void addGold(long gold) {
        try {
            Message mss = new Message(Cmd.ME_UP_COIN_BAG);
            DataOutputStream ds = mss.getWriter();
            ds.writeLong(gold);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void chatVip(String text) {
        try {
            Message mss = new Message(Cmd.CHAT_VIP);
            DataOutputStream ds = mss.getWriter();
            ds.writeUTF(text);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void addBigMessage(short avatar, String chat, byte type, String p, String caption) {
        try {
            Message mss = new Message(Cmd.BIG_MESSAGE);
            DataOutputStream ds = mss.getWriter();
            ds.writeShort(avatar);
            ds.writeUTF(chat);
            ds.writeByte(type);
            if (type == 1) {
                ds.writeUTF(p);
                ds.writeUTF(caption);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void sendDataBG() {
        try {
            Message mss = new Message(Cmd.ITEM_BACKGROUND);
            DataOutputStream ds = mss.getWriter();
            ds.write(BgItem.data);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void setTileSet() {
        try {
            Message mss = new Message(Cmd.TILE_SET);
            DataOutputStream ds = mss.getWriter();
            int lent = TMap.tileIndex.length;
            ds.writeByte(lent);
            for (int i = 0; i < lent; i++) {
                int lent2 = TMap.tileIndex[i].length;
                ds.writeByte(lent2);
                for (int j = 0; j < lent2; j++) {
                    ds.writeInt(TMap.tileType[i][j]);
                    int lent3 = TMap.tileIndex[i][j].length;
                    ds.writeByte(lent3);
                    for (int k = 0; k < lent3; k++) {
                        ds.writeByte(TMap.tileIndex[i][j][k]);
                    }
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void setTask() {
        try {
            Message mss = new Message(Cmd.TASK_GET);
            DataOutputStream ds = mss.getWriter();
            ds.writeShort(player.getTaskMain().id);
            ds.writeByte(player.getTaskMain().index);
            ds.writeUTF(player.getTaskMain().name);
            ds.writeUTF(player.getTaskMain().detail);
            int lent = player.getTaskMain().subNames.length;
            ds.writeByte(lent);
            for (int i = 0; i < lent; i++) {
                ds.writeUTF(player.getTaskMain().subNames[i]);
                ds.writeByte(player.getTaskMain().tasks[i]);
                ds.writeShort(player.getTaskMain().mapTasks[i]);
                ds.writeUTF(player.getTaskMain().contents[i]);
            }
            ds.writeShort(player.getTaskMain().count);
            for (int i = 0; i < lent; i++) {
                ds.writeShort(player.getTaskMain().counts[i]);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void loadAll() {
        try {
            Server server = DragonBall.getInstance().getServer();
            String name = player.getName();
            if (player.isDisciple()) {
                name = "$" + name;
            } else if (player.isMiniDisciple()) {
                name = "#" + name;
            }
            Message mss = messageSubCommand(Cmd.ME_LOAD_ALL);
            DataOutputStream ds = mss.getWriter();
            ds.writeInt(player.getId());
            ds.writeByte(player.getTaskMain().id);
            ds.writeByte(player.getGender());
            ds.writeShort(player.getHead());
            ds.writeUTF(name);
            ds.writeByte(player.getPointPk());
            ds.writeByte(player.getTypePk());
            ds.writeLong(player.characterInfo.getPower());
            ds.writeShort(player.getEff5buffhp());
            ds.writeShort(player.getEff5buffmp());
            ds.writeByte(player.getClassId());//class
            ds.writeByte(player.getSkills().size());
            for (Skill skill : player.getSkills()) {
                ds.writeShort(skill.id);
            }
            ds.writeLong(player.getGold());
            ds.writeInt(player.getDiamondLock());
            ds.writeInt(player.getDiamond());

            ds.writeByte(player.itemBody.length);
            for (Item item : player.itemBody) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeInt(item.quantity);
                    ds.writeUTF(item.info);
                    ds.writeUTF(item.content);
                    ArrayList<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());
                    for (ItemOption option : options) {
                        int[] format = option.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.writeByte(player.itemBag.length);
            for (Item item : player.itemBag) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeInt(item.quantity);
                    ds.writeUTF(item.info);
                    ds.writeUTF(item.content);
                    ArrayList<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());// so cs
                    for (ItemOption option : options) {
                        int[] format = option.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.writeByte(player.itemBox.length);
            for (Item item : player.itemBox) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeInt(item.quantity);
                    ds.writeUTF(item.info);
                    ds.writeUTF(item.content);
                    ArrayList<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());
                    for (ItemOption option : options) {
                        int[] format = option.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }

            ds.writeShort(server.idHead.length);
            for (int i = 0; i < server.idHead.length; i++) {
                ds.writeShort(server.idHead[i]);
                ds.writeShort(server.idAvatar[i]);
            }
            int[] pet = PET[player.getClassId()];
            ds.writeShort(pet[0]);
            ds.writeShort(pet[1]);
            ds.writeShort(pet[2]);
            ds.writeBoolean(player.isNhapThe());
            ds.writeInt(player.getDeltaTime());
            ds.writeBoolean(player.isNewMember());
            ds.writeShort(player.getIdAuraEff());
            ds.writeByte(-1);
            ds.writeShort(player.getIdEffSetItem());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void updateActivePoint() {
        try {
            Message ms = new Message(Cmd.UPDATE_ACTIVEPOINT);
            DataOutputStream ds = ms.getWriter();
            ds.writeInt(player.characterInfo.getActivePoints());
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    public void setMaxStamina() {
        try {
            Message mss = new Message(Cmd.MAXSTAMINA);
            DataOutputStream ds = mss.getWriter();
            ds.writeShort(player.characterInfo.getMaxStamina());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void setStamina() {
        try {
            Message mss = new Message(Cmd.STAMINA);
            DataOutputStream ds = mss.getWriter();
            ds.writeShort(player.characterInfo.getStamina());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void specialSkill(byte type) {
        try {
            Message msg = new Message(Cmd.SPEACIAL_SKILL);
            DataOutputStream ds = msg.getWriter();
            ds.writeByte(type);
            if (type == 0) {
                SpecialSkill skill = player.getSpecialSkill();
                if (skill != null) {
                    ds.writeShort(skill.getIcon());
                    ds.writeUTF(skill.getInfo2());
                } else {
                    ds.writeShort(5223);
                    ds.writeUTF(Language.NO_SPECIAL_SKILLS_YET);
                }
            }
            if (type == 1) {
                List<SpecialSkillTemplate> list = SpecialSkill.getListSpecialSkill(player.getGender());
                ds.writeByte(1);
                ds.writeUTF(Language.MENU_SPECIAL_SKILL_NAME);
                ds.writeByte(list.size());
                for (SpecialSkillTemplate sp : list) {
                    ds.writeShort(sp.getIcon());
                    ds.writeUTF(sp.getInfo());
                }
            }
            ds.flush();
            sendMessage(msg);
            msg.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void changeOnSkill(byte[] shortcut) {
        try {
            Message ms = new Message(Cmd.CHANGE_ONSKILL);
            DataOutputStream ds = ms.getWriter();
            ds.write(shortcut);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    public void updateCoolDown(ArrayList<Skill> skills) {
        try {
            long now = System.currentTimeMillis();
            Message ms = new Message(Cmd.UPDATE_COOLDOWN);
            DataOutputStream ds = ms.getWriter();
            for (Skill skill : skills) {
                long time = now - skill.lastTimeUseThisSkill;
                ds.writeShort(skill.id);
                ds.writeInt((int) (skill.coolDown - time));
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    public void petInfo(byte type) {
        try {
            Message mss = new Message(Cmd.DISCIPLE_INFO);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(type);
            if (type == 2) {
                player.myDisciple.characterInfo.applyCharLevelPercent();
                ds.writeShort(player.myDisciple.getHead());
                ds.writeByte(player.myDisciple.itemBody.length);
                for (Item item : player.myDisciple.itemBody) {
                    if (item != null) {
                        ds.writeShort(item.id);
                        ds.writeInt(item.quantity);
                        ds.writeUTF(item.info);
                        ds.writeUTF(item.content);
                        ArrayList<ItemOption> options = item.getDisplayOptions();
                        ds.writeByte(options.size());
                        for (ItemOption option : options) {
                            int[] format = option.format();
                            ds.writeShort(format[0]);
                            ds.writeInt(format[1]);
                        }
                    } else {
                        ds.writeShort(-1);
                    }
                }
                ds.writeLong(player.myDisciple.characterInfo.getHp());
                ds.writeLong(player.myDisciple.characterInfo.getFullHP());
                ds.writeLong(player.myDisciple.characterInfo.getMp());
                ds.writeLong(player.myDisciple.characterInfo.getFullMP());
                ds.writeLong(player.myDisciple.characterInfo.getFullDamage());

                String name = player.myDisciple.getName();

                ds.writeUTF("$" + name);
                ds.writeUTF(player.myDisciple.characterInfo.getStrLevel());
                ds.writeLong(player.myDisciple.characterInfo.getPower());
                ds.writeLong(player.myDisciple.characterInfo.getPotential());
                ds.writeByte(player.myDisciple.discipleStatus);
                ds.writeShort(player.myDisciple.characterInfo.getStamina());
                ds.writeShort(player.myDisciple.characterInfo.getMaxStamina());
                ds.writeByte(player.myDisciple.characterInfo.getFullCritical());
                ds.writeShort(player.myDisciple.characterInfo.getFullDefense());
                ArrayList<KeyValue> skillInfos = player.myDisciple.getInfoSkill();
                ds.writeByte(skillInfos.size());
                for (KeyValue<Short, String> keyValue : skillInfos) {
                    short skillId = keyValue.getKey();
                    String moreInfo = keyValue.getValue();
                    ds.writeShort(skillId);
                    if (skillId == -1) {
                        ds.writeUTF(moreInfo);
                    }
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void taskNext() {
        sendMessage(new Message(Cmd.TASK_UPDATE));
    }

    public void loadInfo() {
        try {
            Message mss = messageSubCommand(Cmd.ME_LOAD_INFO);
            DataOutputStream ds = mss.getWriter();
            ds.writeLong(player.getGold());
            ds.writeInt(player.getDiamond());
            ds.writeLong(player.characterInfo.getHp());
            ds.writeLong(player.characterInfo.getMp());
            ds.writeInt(player.getDiamondLock());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void setItemBag() {
        try {
            Message mss = new Message(Cmd.BAG);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(0);
            ds.writeByte(player.getItemBag().length);
            for (Item item : player.itemBag) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeInt(item.quantity);
                    ds.writeUTF(item.info);
                    ds.writeUTF(item.content);
                    ArrayList<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());
                    for (ItemOption option : options) {
                        int[] format = option.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void setItemBody() {
        try {
            Message mss = new Message(Cmd.BODY);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(0);
            ds.writeShort(player.getHead());
            ds.writeByte(player.itemBody.length);
            for (Item item : player.itemBody) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeInt(item.quantity);
                    ds.writeUTF(item.info);
                    ds.writeUTF(item.content);
                    ArrayList<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());
                    for (ItemOption option : options) {
                        int[] format = option.format();
                        ds.writeShort(format[0]);
                        ds.writeInt(format[1]);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void gameInfo() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Message mss = new Message(Cmd.GAME_INFO);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(server.gameInfos.size());
            for (GameInfo gameInfo : server.gameInfos) {
                ds.writeShort(gameInfo.getId());
                ds.writeUTF(gameInfo.getTitle());
                ds.writeUTF(gameInfo.getContent());
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void clanInfo() {
        try {
            Message ms = new Message(Cmd.CLAN_INFO);
            DataOutputStream ds = ms.getWriter();
            ds.writeInt(player.getClanID());
            if (player.getClanID() != -1) {
                Clan clan = player.clan;
                if (clan == null) {
                    return;
                }
                List<ClanMember> clanMembers = clan.getMembers();
                ds.writeUTF(clan.name);
                ds.writeUTF(clan.slogan);
                ds.writeByte(clan.imgID);
                ds.writeUTF(Utils.formatNumber(clan.powerPoint));
                ds.writeUTF(clan.leaderName);
                ds.writeByte(clan.getNumberMember());
                ds.writeByte(clan.maxMember);
                ds.writeByte(clan.getMember(player.getId()).role);
                ds.writeInt(clan.clanPoint);
                ds.writeByte(clan.level);
                for (ClanMember mem : clanMembers) {
                    ds.writeInt(mem.playerID);
                    ds.writeShort(mem.head);
                    ds.writeShort(mem.leg);
                    ds.writeShort(mem.body);
                    ds.writeUTF(mem.name);
                    ds.writeByte(mem.role);
                    ds.writeUTF(Utils.formatNumber(mem.powerPoint));
                    ds.writeInt(mem.donate);
                    ds.writeInt(mem.receiveDonate);
                    ds.writeInt(mem.clanPoint);
                    ds.writeInt(mem.currClanPoint);
                    ds.writeUTF(mem.getStrJoinTime());
                }
                ds.writeByte(clan.messages.size());
                for (ClanMessage message : clan.messages) {
                    writeClanMessage(ds, message);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void writeClanMessage(DataOutputStream ds, ClanMessage message) throws IOException{
        ds.writeByte(message.type);
        ds.writeInt(message.id);
        ds.writeInt(message.playerId);
        ds.writeUTF(message.playerName);
        ds.writeByte(message.role);
        ds.writeInt(message.time);
        if (message.type == 0) {
            ds.writeUTF(message.chat);
            ds.writeByte(message.color);
        } else if (message.type == 1) {
            ds.writeByte(message.receive);
            ds.writeByte(message.maxCap);
            ds.writeBoolean(message.isNewMessage);
        }
    }


//    @Override
//    public void sendMessage(Message message) {
//        super.sendMessage(message);
//    }
}
