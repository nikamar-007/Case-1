package ru.itevents.desktop.util;

import javax.swing.ImageIcon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public final class BrandAssets {
    private static final int ICON_SIZE = 64;
    private static final int LOGO_WIDTH = 260;
    private static final int LOGO_HEIGHT = 80;

    private BrandAssets() {
    }

    public static Image createWindowIcon() {
        BufferedImage image = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            applyQualityRendering(graphics);

            Color accent = ColorPalette.ACCENT;
            Color lighter = accent.brighter();

            graphics.setPaint(new GradientPaint(0, 0, lighter, ICON_SIZE, ICON_SIZE, accent));
            graphics.fillRoundRect(0, 0, ICON_SIZE, ICON_SIZE, 20, 20);

            graphics.setColor(Color.WHITE);
            graphics.setStroke(new BasicStroke(3f));
            graphics.drawRoundRect(2, 2, ICON_SIZE - 5, ICON_SIZE - 5, 20, 20);

            String text = "IT";
            Font font = new Font("Comic Sans MS", Font.BOLD, 30);
            graphics.setFont(font);
            FontMetrics metrics = graphics.getFontMetrics();
            int textWidth = metrics.stringWidth(text);
            int textHeight = metrics.getAscent();
            int x = (ICON_SIZE - textWidth) / 2;
            int y = (ICON_SIZE + textHeight) / 2 - 6;
            graphics.drawString(text, x, y);
        } finally {
            graphics.dispose();
        }
        return image;
    }

    public static ImageIcon createLogoIcon() {
        BufferedImage image = new BufferedImage(LOGO_WIDTH, LOGO_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            applyQualityRendering(graphics);

            Color accent = ColorPalette.ACCENT;
            Color lighter = new Color(204, 255, 255);

            graphics.setColor(lighter);
            graphics.fillRoundRect(0, 0, LOGO_WIDTH, LOGO_HEIGHT, 32, 32);

            graphics.setColor(accent);
            graphics.setStroke(new BasicStroke(4f));
            graphics.drawRoundRect(2, 2, LOGO_WIDTH - 5, LOGO_HEIGHT - 5, 32, 32);

            String brand = "IT-EVENTS";
            Font brandFont = new Font("Comic Sans MS", Font.BOLD, 36);
            graphics.setFont(brandFont);
            FontMetrics brandMetrics = graphics.getFontMetrics();
            int brandX = (LOGO_WIDTH - brandMetrics.stringWidth(brand)) / 2;
            int brandY = (LOGO_HEIGHT + brandMetrics.getAscent()) / 2 - 6;
            graphics.drawString(brand, brandX, brandY);

            String tagline = "Управление мероприятиями";
            Font taglineFont = new Font("Comic Sans MS", Font.PLAIN, 18);
            graphics.setFont(taglineFont);
            FontMetrics taglineMetrics = graphics.getFontMetrics();
            int taglineX = (LOGO_WIDTH - taglineMetrics.stringWidth(tagline)) / 2;
            int taglineY = LOGO_HEIGHT - taglineMetrics.getDescent() - 8;
            graphics.drawString(tagline, taglineX, taglineY);
        } finally {
            graphics.dispose();
        }
        return new ImageIcon(image);
    }

    private static void applyQualityRendering(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}
