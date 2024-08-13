package com.ngocrong.backend.model;


import com.google.gson.annotations.SerializedName;
import com.ngocrong.backend.model.AchievementTemplate;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.util.Utils;

/**
 * Lớp `Achievement` đại diện cho một thành tựu mà người chơi có thể đạt được trong game.
 */
public class Achievement {

    private transient AchievementTemplate template;  // Template của thành tựu

    @SerializedName("id")
    private int id;  // ID của thành tựu

    @SerializedName("count")
    private long count;  // Số lần hoàn thành thành tựu

    @SerializedName("rewarded")
    private boolean rewarded;  // Đánh dấu nếu đã nhận phần thưởng

    // Constructor khởi tạo thành tựu mới với ID tương ứng
    public Achievement(int id) {
        this.id = id;
        this.count = 0;
        this.rewarded = false;
        initializeTemplate();
    }

    // Khởi tạo template dựa trên ID
    private void initializeTemplate() {
        Server server = DragonBall.getInstance().getServer();
        this.template = server.getAchievements().get(this.id);
    }

    // Cập nhật số lần hoàn thành thành tựu
    public void updateCount(long count) {
        this.count = count;
    }

    // Thêm số lượng vào thành tựu
    public void incrementCount(int amount) {
        this.count += amount;
    }

    // Đánh dấu thành tựu đã nhận thưởng
    public void setRewarded(boolean rewarded) {
        this.rewarded = rewarded;
    }

    // Kiểm tra xem thành tựu đã hoàn thành hay chưa
    public boolean isComplete() {
        return this.count >= template.getMaxCount();
    }

    // Lấy phần thưởng khi hoàn thành thành tựu
    public int getReward() {
        return template.getReward();
    }

    // Kiểm tra thành tựu đã nhận thưởng hay chưa
    public boolean isRewarded() {
        return this.rewarded;
    }

    // Lấy tên thành tựu
    public String getName() {
        return template.getName();
    }

    // Lấy nội dung mô tả thành tựu cùng với tiến độ hoàn thành
    public String getDescription() {
        return String.format("%s (%s/%s)", template.getContent(), Utils.formatNumber(count), Utils.formatNumber(template.getMaxCount()));
    }
}
