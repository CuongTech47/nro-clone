package com.ngocrong.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Caption {

    // Danh sách chứa các chú thích cho từng hành tinh
    private static final List<String> TRAI_DAT = new ArrayList<>();
    private static final List<String> NAMEC = new ArrayList<>();
    private static final List<String> XAYDA = new ArrayList<>();

    /**
     * Thêm chú thích vào danh sách dựa trên hành tinh.
     *
     * @param planet  Hành tinh được chỉ định (0: Trái Đất, 1: Namec, 2: Xayda).
     * @param caption Chú thích cần thêm.
     */
    public static void addCaption(byte planet, String caption) {
        switch (planet) {
            case 0 -> TRAI_DAT.add(caption);
            case 1 -> NAMEC.add(caption);
            case 2 -> XAYDA.add(caption);
            default -> throw new IllegalArgumentException("Hành tinh không hợp lệ: " + planet);
        }
    }

    /**
     * Lấy danh sách chú thích của hành tinh.
     *
     * @param planet Hành tinh được chỉ định (0: Trái Đất, 1: Namec, 2: Xayda).
     * @return Danh sách chú thích tương ứng với hành tinh.
     */
    public static List<String> getCaption(byte planet) {
        return switch (planet) {
            case 0 -> TRAI_DAT;
            case 1 -> NAMEC;
            case 2 -> XAYDA;
            default -> throw new IllegalArgumentException("Hành tinh không hợp lệ: " + planet);
        };
    }
}
