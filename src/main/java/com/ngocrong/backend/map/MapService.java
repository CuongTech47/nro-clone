package com.ngocrong.backend.map;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.consts.Cmd;
import com.ngocrong.backend.consts.ItemName;
import com.ngocrong.backend.disciple.Disciple;
import com.ngocrong.backend.item.ItemMap;
import com.ngocrong.backend.mob.Mob;
import com.ngocrong.backend.model.Hold;
import com.ngocrong.backend.network.Message;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.map.tzone.Zone;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MapService {
    private static Logger logger = Logger.getLogger(MapService.class);
    public Zone zone;

    public MapService(Zone zone) {
        this.zone = zone;
    }


    public void throwItem(Char _char , ItemMap itemMap) {
        try {
            Message ms = new Message(Cmd.ME_THROW);
            DataOutputStream ds = ms.getWriter();
            ds.writeByte(itemMap.item.indexUI);
            ds.writeShort(itemMap.id);
            ds.writeShort(itemMap.x);
            ds.writeShort(itemMap.y);
            ds.flush();
            _char.service.sendMessage(ms);
            ms.cleanup();


            ms = new Message(Cmd.PLAYER_THROW);
            ds = ms.getWriter();
            ds.writeInt(_char.getId());
            ds.writeShort(itemMap.id);
            ds.writeShort(itemMap.item.id);
            ds.writeShort(itemMap.x);
            ds.writeShort(itemMap.y);
            ds.flush();

            sendMessage(ms,_char);
            ms.cleanup();
        }catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }
    public void removeItemMap(ItemMap item) {
        try {
            Message ms = new Message(Cmd.ITEMMAP_REMOVE);
            DataOutputStream ds = ms.getWriter();
            ds.writeShort(item.id);
            ds.flush();
            sendMessage(ms, null);
            ms.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void attackPlayer(Char _char, long dameHit, boolean isCrit, byte eff) {
        try {
            Message msg = new Message(Cmd.HAVE_ATTACK_PLAYER);
            DataOutputStream ds = msg.getWriter();
            ds.writeInt(_char.getId());
            ds.writeLong(_char.getCharacterInfo().getHp());
            ds.writeLong(dameHit);
            ds.writeBoolean(isCrit);
            ds.writeByte(eff);
            ds.flush();
            sendMessage(msg, null);
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void playerPickItem(ItemMap item, Char _char, String notification) {
        try {
            Message msg = new Message(Cmd.ITEMMAP_MYPICK);
            DataOutputStream ds = msg.getWriter();
            ds.writeShort(item.id);
            ds.writeUTF(notification);
            ds.writeShort(item.item.quantity);
            ds.writeShort(item.item.quantity);
            ds.flush();
            _char.service.sendMessage(msg);
            msg.cleanup();
            if (item.item.id != ItemName.DUA_BE) {
                msg = new Message(Cmd.ITEMMAP_PLAYERPICK);
                ds = msg.getWriter();
                ds.writeShort(item.id);
                ds.writeInt(_char.getId());
                ds.flush();
                sendMessage(msg, _char);
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void addItemMap(ItemMap item) {
        try {
            Message msg = new Message(Cmd.ITEMMAP_ADD);
            DataOutputStream ds = msg.getWriter();
            ds.writeShort(item.id);
            ds.writeShort(item.item.id);
            ds.writeShort(item.x);
            ds.writeShort(item.y);
            ds.writeInt(item.playerID);
            if (item.playerID == -2) {
                ds.writeShort(item.r);
            }
            ds.flush();
            sendMessage(msg, null);
            msg.cleanup();
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
            sendMessage(mss, null);
            mss.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }


    public void skillNotFocus(Char _char, byte type, ArrayList<Mob> mobs, ArrayList<Char> chars) {
        try {
            Skill skill = _char.select;
            Message message = new Message(Cmd.SKILL_NOT_FOCUS);
            DataOutputStream ds = message.getWriter();
            ds.writeByte(type);
            ds.writeInt(_char.getId());
            ds.writeShort(skill.id);
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
                ds.writeShort(_char.getSeconds());
            }
            ds.flush();
            sendMessage(message, null);
        }catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }







    private void sendMessage(Message message , Char _char) {
        zone.lockChar.readLock();
        try {
            for (Char _c : zone.chars) {
                if (_c != _char) {
                    _c.service.sendMessage(message);
                }
            }
        }finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void playerLoadBody(Char _char) {
        zone.lockChar.readLock().lock();
        try {
            for (Char _c : zone.chars) {
                if (_c != _char) {
                    _c.service.playerLoadBody(_char);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void mobMeUpdate(Char _char, Object target, long dame, byte skillId, byte type) {
        zone.lockChar.readLock().lock();
        try {
            for (Char _c : zone.chars) {
                _c.service.mobMeUpdate(_char, target, dame, skillId, type);
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void move(Char _char) {
        zone.lockChar.readLock().lock();
        try {
            for (Char _c : zone.chars) {
                _c.service.move(_char);
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void playerRemove(Char aChar) {
        zone.lockChar.readLock().lock();
        try {
            for (Char _c : zone.chars) {
                if (_c != aChar) {
                    _c.service.playerRemove(aChar);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void updateBody(byte type, Char _char) {
        try {
            Message mss = new Message(Cmd.UPDATE_BODY);
            DataOutputStream ds = mss.getWriter();
            ds.writeByte(type);
            ds.writeInt(_char.getId());
            if (type != -1) {
                ds.writeShort(_char.getHead());
                ds.writeShort(_char.getBody());
                ds.writeShort(_char.getLeg());
                ds.writeBoolean(_char.isMonkey());
            }
            ds.flush();
            if (zone.map.isMapSingle()) {
                _char.service.sendMessage(mss);
            } else {
                sendMessage(mss, null);
            }
            mss.cleanup();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    public void updateBag(Char aChar) {
        zone.lockChar.readLock().lock();
        try {
            for (Char _c : zone.chars) {
                _c.service.updateBag(aChar);
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void flag(Char _char) {
        try {
            Message ms = new Message(Cmd.CHANGE_FLAG);
            DataOutputStream ds = ms.getWriter();
            ds.writeByte(1);
            ds.writeInt(_char.getId());
            ds.writeByte(_char.getFlag());
            ds.flush();
            sendMessage(ms, null);
            ms.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    public void playerLoadLive(Char _char) {
        try {
            Message mss = new Message(Cmd.RETURN_POINT_MAP);
            DataOutputStream ds = mss.getWriter();
            ds.writeInt(_char.getId());
            ds.writeShort(_char.getX());
            ds.writeShort(_char.getY());
            ds.flush();
            sendMessage(mss, _char);
            mss.cleanup();
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
    }

    public void playerAdd(Char _char) {
        zone.lockChar.readLock().lock();
        try {
            for (Char _c : zone.chars) {
                if (_c != _char) {
                    _c.service.playerAdd(_char);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }

    public void playerLoadAll(Char _char) {
        zone.lockChar.readLock().lock();
        try {
            for (Char _c : zone.chars) {
                if (_c != _char) {
                    _c.service.playerLoadAll(_char);
                }
            }
        } finally {
            zone.lockChar.readLock().unlock();
        }
    }
}
