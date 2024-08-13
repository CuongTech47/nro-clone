package com.ngocrong.backend.model;


import com.ngocrong.backend.lib.CaptchaGenerate;
import com.ngocrong.backend.util.Utils;
import lombok.Data;

@Data
public class Captcha {

    private byte zoomLevel;
    private byte[] imageData;
    private String captchaText;
    private String keyString;

    /**
     * Tạo ra mã khóa, văn bản captcha, và hình ảnh captcha.
     */
    public void generate() {
        generateKeyString();
        generateCaptchaText();
        generateCaptchaImage();
    }

    /**
     * Tạo ra chuỗi khóa ngẫu nhiên được sử dụng cho captcha.
     */
    private void generateKeyString() {
        byte[] array = new byte[5];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) Utils.nextInt(97, 122); // Giá trị ASCII cho chữ cái thường
        }
        keyString = new String(array);
    }

    /**
     * Tạo ra văn bản captcha ngẫu nhiên dựa trên chuỗi khóa.
     */
    private void generateCaptchaText() {
        byte[] array = new byte[5];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) keyString.charAt(Utils.nextInt(keyString.length()));
        }
        captchaText = new String(array);
    }

    /**
     * Tạo hình ảnh cho văn bản captcha.
     */
    private void generateCaptchaImage() {
        imageData = CaptchaGenerate.generateImage(captchaText, zoomLevel);
    }

    /**
     * Xử lý ký tự đầu vào để xác thực captcha.
     * @param ch ký tự do người dùng nhập vào.
     */
    public void input(char ch) {
        // Thêm phần xử lý ký tự nhập vào tại đây.
    }
}
