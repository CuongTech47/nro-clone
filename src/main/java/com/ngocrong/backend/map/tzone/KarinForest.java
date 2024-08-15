package com.ngocrong.backend.map.tzone;

import com.ngocrong.backend.bot.boss.karin.TaoPaiPai;
import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.task.Task;

public class KarinForest extends MapSingle{
    public KarinForest(TMap map, int zoneId , Char _c) {
        super(map, zoneId);
        Task task = _c.getTaskMain();

        if ((task.id == 9 && (task.index == 2 || task.index == 3)) || (task.id == 10 && (task.index == 0 || task.index == 1))) {
            if (task.id == 10 && task.index == 1) {
                TaoPaiPai taoPaiPai = new TaoPaiPai();
                taoPaiPai.setInfo(1000, 100000, _c.characterInfo.getFullHP() / 50, 0, 5);
                taoPaiPai.setLocation(this);
            } else {
                appearTaoPaiPai();
            }
        }
    }

    private void appearTaoPaiPai() {
        TaoPaiPai taoPaiPai = new TaoPaiPai();
        taoPaiPai.setInfo(50000, 100000, 100, 5, 10);
        taoPaiPai.setLocation(this);
    }
}
