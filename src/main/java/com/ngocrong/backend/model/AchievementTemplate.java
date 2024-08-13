package com.ngocrong.backend.model;

/**
 * Lớp `AchievementTemplate` mô tả các thuộc tính của một mẫu thành tựu.
 */
public class AchievementTemplate {
    private int id;  // ID của mẫu thành tựu
    private String name;  // Tên của mẫu thành tựu
    private String content;  // Nội dung mô tả mẫu thành tựu
    private int maxCount;  // Số lượng tối đa cần đạt để hoàn thành thành tựu
    private int reward;  // Phần thưởng nhận được khi hoàn thành thành tựu

    // Getter và Setter cho các thuộc tính
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }
}
