package com.ngocrong.backend.lib;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;
public class CaptchaGenerate {
    private CaptchaGenerate() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generates a random alphanumeric string of eight characters.
     *
     * @return A random alphanumeric string of eight characters.
     */
    public static String generateText() {
        return new StringTokenizer(UUID.randomUUID().toString(), "-").nextToken();
    }

    /**
     * Generates a PNG image with the given text displayed.
     *
     * @param text      The text to be displayed in the image.
     * @param zoomLevel The zoom level for the image, affecting size and font.
     * @return A byte array representing the generated PNG image.
     */
    public static byte[] generateImage(String text, byte zoomLevel) {
        int width = 90 * zoomLevel;
        int height = 30 * zoomLevel;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();
        setRenderingHints(graphics);
        drawBackground(graphics, width, height);
        drawText(graphics, text, zoomLevel);
        drawNoise(graphics, zoomLevel, width, height);

        graphics.dispose();

        return createImageByteArray(image);
    }

    private static void setRenderingHints(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private static void drawBackground(Graphics2D graphics, int width, int height) {
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, width, height);
    }

    private static void drawText(Graphics2D graphics, String text, byte zoomLevel) {
        graphics.setFont(new Font("Serif", Font.PLAIN, 20 * zoomLevel));
        byte[] textBytes = text.getBytes();
        Random random = new Random();

        for (int i = 0; i < textBytes.length; i++) {
            graphics.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            graphics.drawString(new String(new byte[]{textBytes[i]}), (10 + (i * 15)) * zoomLevel, (int) ((random.nextDouble() * 15 + 15) * zoomLevel));
        }
    }

    private static void drawNoise(Graphics2D graphics, byte zoomLevel, int width, int height) {
        graphics.setColor(Color.white);
        graphics.setStroke(new BasicStroke(zoomLevel));
        int ovalSize = 30 * zoomLevel;

        for (int i = 0; i < 8; i++) {
            graphics.drawOval((int) (Math.random() * width), (int) (Math.random() * height / 3), ovalSize, ovalSize);
        }
    }

    private static byte[] createImageByteArray(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create captcha image", e);
        }
    }
}
