package com.ngocrong.backend.item;

import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.util.Utils;

public class ItemOption {
    public transient byte active;
    public transient byte activeCard;
    public transient ItemOptionTemplate optionTemplate;
    public int param;
    public int id;


    public ItemOption(int optionTemplateId, int param) {
       this.param = param;
       this.id = optionTemplateId;

        Server server = DragonBall.getInstance().getServer();

        this.optionTemplate = server.iOptionTemplates.get(optionTemplateId);
    }

    public String getOptionString() {
        return Utils.replace(this.optionTemplate.name, "#", this.param + "");
    }

    public int[] format() {
        int id = optionTemplate.id;
        int param = this.param;
        if (param > 65535) {
            switch (id) {
                case 6:
                    id = 22;
                    param /= 1000;
                    break;

                case 7:
                    id = 23;
                    param /= 1000;
                    break;

                case 31:
                    id = 171;
                    param /= 1000;
                    break;

                case 48:
                    id = 2;
                    param /= 1000;
                    break;
            }
        }
        return new int[]{id, param};
    }
}
