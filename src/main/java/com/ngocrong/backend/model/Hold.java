package com.ngocrong.backend.model;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.map.tzone.Zone;
import com.ngocrong.backend.mob.Mob;
import com.ngocrong.backend.skill.Skill;
import lombok.Getter;

@Getter
public class Hold extends Thread{
    private final Zone zone;
    private final Char holder;
    private final Object detainee;
    private int seconds;
    private volatile boolean isClosed;


    public Hold(Zone zone, Char holder, Object detainee, int seconds) {
        this.zone = zone;
        this.holder = holder;
        this.detainee = detainee;
        this.seconds = seconds;
    }

    public void update() {
        this.seconds--;
        if (this.seconds <= 0) {
            close();
        }
    }

    public void run() {
        while (!isClosed) {
            try {
                update();
                Thread.sleep(1000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        super.start();
        holder.setHold(this);
        holder.setHeld(true);
        if (detainee instanceof Mob) {
            Mob mob = (Mob) detainee;
            mob.hold = this;
            mob.isHeld = true;
            zone.mapService.setEffect(this, mob.mobId, Skill.ADD_EFFECT, Skill.MONSTER, (byte) 32);
        } else {
            Char _char = (Char) detainee;
            _char.setHold(this);
            _char.setHeld(true);
            zone.mapService.setEffect(this, _char.getId(), Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 32);
        }
    }


    public void close() {
        this.isClosed = true;
        holder.setHeld(false);
        zone.mapService.setEffect(null, holder.getId(), Skill.REMOVE_EFFECT, Skill.CHARACTER , (byte) 32);

        if (this.detainee instanceof Mob) {
            Mob mob  = (Mob) this.detainee;
            mob.isHeld = false;
            mob.hold = null;
            zone.mapService.setEffect(null,mob.mobId,Skill.REMOVE_EFFECT,Skill.MONSTER,(byte) 32);
        }else {
            Char _char = (Char) this.detainee;
            _char.setHeld(false);
            zone.mapService.setEffect(null, _char.getId(), Skill.REMOVE_EFFECT, Skill.CHARACTER, (byte) 32);
            _char.setHold(null);
        }
        holder.setHold(null);

    }
}
