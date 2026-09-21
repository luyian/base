package com.base.barcode.util;

import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * 条码渲染工具类
 *
 * @author base
 */
public final class BarcodeGeneratorUtil {

    /**
     * 渲染 BitMatrix 为 PNG 字节数组
     *
     * @param matrix 条码位图矩阵
     * @return PNG 图片字节数组
     */
    public static byte[] renderPng(BitMatrix matrix) throws Exception {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        }
    }

    private BarcodeGeneratorUtil() {
    }
}