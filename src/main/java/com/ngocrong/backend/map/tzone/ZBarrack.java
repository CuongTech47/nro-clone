package com.ngocrong.backend.map.tzone;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.clan.Clan;
import com.ngocrong.backend.clan.ClanMember;
import com.ngocrong.backend.map.Barrack;
import com.ngocrong.backend.mob.Mob;
import org.apache.log4j.Logger;
import com.ngocrong.backend.server.SessionManager;
public class ZBarrack extends Zone {
    private static Logger logger = Logger.getLogger(ZBarrack.class);
    private Barrack barrack;
    public ZBarrack(Barrack barrack, TMap map, int zoneId) {
        super(map, zoneId);
        this.barrack = barrack;
    }



    public void setBarrack(Clan clan) {
        long hp = 8000;
        long dame = 300;
        for (ClanMember mem : clan.getMembers()) {
            Char _char = SessionManager.findChar(mem.playerID);
            if (_char != null) {
                if (_char.characterInfo.getFullHP() > hp && _char.characterInfo.getFullDamage() >= 300) {
                    hp = _char.characterInfo.getFullHP();
                    dame = _char.characterInfo.getFullDamage();
                }
            }
        }
        for (Mob mob : this.mobs) {
            mob.maxHp = dame * 10;
            mob.hp = mob.maxHp;
            mob.damage1 = hp / 10;
            mob.damage2 = mob.damage1 - (mob.damage1 / 10);
        }
        for (Char _char : chars) {
            if (_char.isBoss()) {
                _char.characterInfo.setFullHP(dame * 100);
                _char.characterInfo.setHp( _char.characterInfo.getFullHP());
                _char.characterInfo.setFullDamage(hp / 20);
            }
        }

    }



}
