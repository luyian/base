package com.base.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 验证码工具类
 * 用于生成图形验证码
 */
public class CaptchaUtil {

    /**
     * 验证码字符集
     */
    private static final String CHARACTERS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 4;

    /**
     * 图片宽度
     */
    private static final int WIDTH = 120;

    /**
     * 图片高度
     */
    private static final int HEIGHT = 40;

    /**
     * 干扰线数量
     */
    private static final int LINE_COUNT = 20;

    /**
     * 随机数生成器
     */
    private static final Random RANDOM = new Random();

    /**
     * 生成验证码
     *
     * @return 验证码字符串
     */
    public static String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    /**
     * 生成验证码图片（Base64 格式）
     *
     * @param code 验证码字符串
     * @return Base64 编码的图片字符串
     */
    public static String generateImageBase64(String code) {
        try {
            BufferedImage image = generateImage(code);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }

    /**
     * 生成验证码图片
     *
     * @param code 验证码字符串
     * @return BufferedImage 对象
     */
    private static BufferedImage generateImage(String code) {
        // 创建图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 填充背景色
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制干扰线
        drawInterferenceLines(g);

        // 绘制验证码字符
        drawCode(g, code);

        // 绘制干扰点
        drawInterferencePoints(g);

        g.dispose();
        return image;
    }

    /**
     * 绘制验证码字符
     *
     * @param g Graphics2D 对象
     * @param code 验证码字符串
     */
    private static void drawCode(Graphics2D g, String code) {
        int fontSize = 28;
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g.setFont(font);

        int x = 10;
        for (int i = 0; i < code.length(); i++) {
            // 随机颜色
            g.setColor(getRandomColor(50, 150));

            // 随机旋转角度
            int degree = RANDOM.nextInt(30) - 15;
            double radians = Math.toRadians(degree);

            // 计算字符位置
            int charX = x + i * (WIDTH - 20) / CODE_LENGTH;
            int charY = HEIGHT / 2 + fontSize / 3;

            // 旋转并绘制字符
            g.rotate(radians, charX, charY);
            g.drawString(String.valueOf(code.charAt(i)), charX, charY);
            g.rotate(-radians, charX, charY);
        }
    }

    /**
     * 绘制干扰线
     *
     * @param g Graphics2D 对象
     */
    private static void drawInterferenceLines(Graphics2D g) {
        for (int i = 0; i < LINE_COUNT; i++) {
            int x1 = RANDOM.nextInt(WIDTH);
            int y1 = RANDOM.nextInt(HEIGHT);
            int x2 = RANDOM.nextInt(WIDTH);
            int y2 = RANDOM.nextInt(HEIGHT);
            g.setColor(getRandomColor(180, 220));
            g.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * 绘制干扰点
     *
     * @param g Graphics2D 对象
     */
    private static void drawInterferencePoints(Graphics2D g) {
        for (int i = 0; i < WIDTH * HEIGHT / 20; i++) {
            int x = RANDOM.nextInt(WIDTH);
            int y = RANDOM.nextInt(HEIGHT);
            g.setColor(getRandomColor(150, 200));
            g.fillRect(x, y, 1, 1);
        }
    }

    /**
     * 获取随机颜色
     *
     * @param min 最小值
     * @param max 最大值
     * @return Color 对象
     */
    private static Color getRandomColor(int min, int max) {
        int r = min + RANDOM.nextInt(max - min);
        int g = min + RANDOM.nextInt(max - min);
        int b = min + RANDOM.nextInt(max - min);
        return new Color(r, g, b);
    }
}
