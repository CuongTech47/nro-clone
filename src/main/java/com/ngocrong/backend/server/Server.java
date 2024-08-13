package com.ngocrong.backend.server;

import com.ngocrong.backend.item.ItemOptionTemplate;
import com.ngocrong.backend.item.ItemTemplate;
import com.ngocrong.backend.model.AchievementTemplate;
import com.ngocrong.backend.task.TaskTemplate;
import lombok.Getter;
import lombok.Setter;
//import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {

    protected boolean start;


    private boolean isMaintained;

    public static final int COUNT_SESSION_ON_IP = 3;
    public ArrayList<ItemOptionTemplate> iOptionTemplates;
    public HashMap<Integer, ItemTemplate> iTemplates;

    @Getter
    private List<AchievementTemplate> achievements;
    public ArrayList<TaskTemplate> taskTemplates;

    public byte[][] smallVersion, backgroundVersion;

    @Getter
    private final Config config;

    public Server() {
        this.config = new Config();
        this.config.load();
        this.achievements = new ArrayList<>();
    }

    public boolean isMaintained() {
        return isMaintained;
    }

    public void setMaintained(boolean maintained) {
        this.isMaintained = maintained;
    }

    public void saveData() {
        // Implementation for saving data
    }

    public static int getMaxQuantityItem() {
        return DragonBall.getInstance().getServer().getConfig().getMaxQuantity();
    }

}
