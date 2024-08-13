package com.ngocrong.backend.task;

import com.ngocrong.backend.util.Utils;

public class Task {
    public int id;
    public int index;
    public int count;

    public transient String name;
    public transient String detail;
    public transient int[] tasks;
    public transient int[] mapTasks;
    public transient short[] counts;
    public transient String[] subNames;
    public transient String[] contents;
    public transient int rewardPower, rewardPotential, rewardGold, rewardGem, rewardGemLock;

    public void initTask(int gender) {
        TaskTemplate task = TaskTemplate.getTaskTemplate(this.id);
        this.name = task.name;
        this.rewardPotential = task.rewardPotential;
        this.rewardPower = task.rewardPower;
        this.rewardGold = task.rewardGold;
        this.rewardGem = task.rewardGem;
        this.rewardGemLock = task.rewardGemLock;
        this.detail = task.details[gender];
        this.tasks = task.tasks[gender];
        this.mapTasks = task.mapTasks[gender];
        this.counts = task.counts;
        this.subNames = task.subNames[gender];
        this.contents = task.contents[gender];
        addDetailReward();
    }


    private void addDetailReward() {
        if (rewardPower > 0) {
            this.detail += String.format("\nThưởng %s sức mạnh", Utils.formatNumber(rewardPower));
        }
        if (rewardPotential > 0) {
            this.detail += String.format("\nThưởng %s tiềm năng", Utils.formatNumber(rewardPotential));
        }
        if (rewardGold > 0) {
            this.detail += String.format("\nThưởng %s vàng", Utils.formatNumber(rewardGold));
        }
        if (rewardGem > 0) {
            this.detail += String.format("\nThưởng %s ngọc", Utils.formatNumber(rewardGem));
        }
        if (rewardGemLock > 0) {
            this.detail += String.format("\nThưởng %s hồng ngọc", Utils.formatNumber(rewardGemLock));
        }
    }
}
