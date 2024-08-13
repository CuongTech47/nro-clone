package com.ngocrong.backend.character;


import com.ngocrong.backend.consts.Cmd;
import com.ngocrong.backend.effect.EffectChar;
import com.ngocrong.backend.map.tzone.TMap;
import com.ngocrong.backend.map.tzone.Zone;
import com.ngocrong.backend.mob.Mob;
import com.ngocrong.backend.model.Hold;
import com.ngocrong.backend.model.PetFollow;
import com.ngocrong.backend.model.PowerInfo;
import com.ngocrong.backend.model.Waypoint;
import com.ngocrong.backend.network.Message;
import com.ngocrong.backend.network.Service;
import com.ngocrong.backend.network.Session;
import com.ngocrong.backend.skill.Skill;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CharService  extends Service {

    private static final Logger logger = Logger.getLogger(Service.class);

    public CharService(Session session) {
        super(session);
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


//    @Override
//    public void sendMessage(Message message) {
//        super.sendMessage(message);
//    }
}
