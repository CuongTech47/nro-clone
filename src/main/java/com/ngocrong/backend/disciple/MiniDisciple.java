package com.ngocrong.backend.disciple;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.util.Utils;

public class MiniDisciple extends Char {
    private short pHead, pBody, pLeg;
    public Item item;
    private final Char owner;

    public MiniDisciple(Item item, Char _owner, Char owner) {
        super();
        this.setId(-(((Utils.nextInt(100) * 1000) + Utils.nextInt(100) * 100)) + Utils.nextInt(100));
        this.owner = owner;
        this.setName("");
        setPart();
    }

    private void setPart() {
        this.pHead = item.template.head;
        this.pBody = item.template.body;
        this.pLeg = item.template.leg;
    }

    public void move () {
        short x = (short) (owner.getX() + Utils.nextInt(-50, 50));
        short y = owner.getY();
        moveTo(x, y);
    }

    private void moveTo(short x, short y) {
        setX(x);
        setY(y);
        if (zone != null) {
//            zone.service.move(this);
        }
    }
}
