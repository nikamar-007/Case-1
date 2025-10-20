package ru.itevents.desktop.util;

import javax.swing.ImageIcon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public final class BrandAssets {
    private static final int ICON_SIZE = 96;
    private static final int LOGO_WIDTH = 420;
    private static final int LOGO_HEIGHT = 120;

    private static volatile Image windowIcon;
    private static volatile ImageIcon logoIcon;

    private BrandAssets() {
    }

    public static Image getWindowIcon() {
        if (windowIcon == null) {
            synchronized (BrandAssets.class) {
                if (windowIcon == null) {
                    windowIcon = createWindowIcon();
                }
            }
        }
        return windowIcon;
    }

    public static ImageIcon getLogoIcon() {
        if (logoIcon == null) {
            synchronized (BrandAssets.class) {
                if (logoIcon == null) {
                    logoIcon = new ImageIcon(createLogoImage());
                }
            }
        }
        return logoIcon;
    }

    private static Image createWindowIcon() {
        BufferedImage image = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        try {
            applyQualityHints(g2);
            GradientPaint outer = new GradientPaint(0, 0, new Color(4, 123, 245), ICON_SIZE, ICON_SIZE, new Color(0, 60, 140));
            g2.setPaint(outer);
            g2.fillRoundRect(0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE / 3, ICON_SIZE / 3);

            GradientPaint inner = new GradientPaint(0, ICON_SIZE, new Color(255, 255, 255, 110), ICON_SIZE, 0, new Color(255, 255, 255, 0));
            g2.setPaint(inner);
            int margin = ICON_SIZE / 10;
            g2.fillRoundRect(margin, margin, ICON_SIZE - margin * 2, ICON_SIZE - margin * 2, ICON_SIZE / 4, ICON_SIZE / 4);

            g2.setColor(new Color(255, 255, 255, 220));
            g2.setStroke(new BasicStroke(Math.max(2f, ICON_SIZE / 24f)));
            g2.drawRoundRect(margin / 2, margin / 2, ICON_SIZE - margin, ICON_SIZE - margin, ICON_SIZE / 3, ICON_SIZE / 3);

            Font font = new Font("Comic Sans MS", Font.BOLD, ICON_SIZE / 2);
            g2.setFont(font);
            g2.setColor(Color.WHITE);
            String text = "IT";
            int textWidth = g2.getFontMetrics().stringWidth(text);
            int textHeight = g2.getFontMetrics().getAscent();
            int x = (ICON_SIZE - textWidth) / 2;
            int y = (ICON_SIZE + textHeight) / 2 - ICON_SIZE / 12;
            g2.drawString(text, x, y);
        } finally {
            g2.dispose();
        }
        return image;
    }

    private static Image createLogoImage() {
        BufferedImage image = new BufferedImage(LOGO_WIDTH, LOGO_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        try {
            applyQualityHints(g2);
            GradientPaint background = new GradientPaint(0, 0, new Color(1, 105, 235), LOGO_WIDTH, LOGO_HEIGHT, new Color(0, 65, 160));
            g2.setPaint(background);
            g2.fillRoundRect(0, 0, LOGO_WIDTH, LOGO_HEIGHT, LOGO_HEIGHT / 2, LOGO_HEIGHT / 2);

            g2.setColor(new Color(255, 255, 255, 230));
            g2.setStroke(new BasicStroke(6f));
            int inset = 6;
            g2.drawRoundRect(inset, inset, LOGO_WIDTH - inset * 2, LOGO_HEIGHT - inset * 2, LOGO_HEIGHT / 2, LOGO_HEIGHT / 2);

            Font brandFont = new Font("Comic Sans MS", Font.BOLD, LOGO_HEIGHT / 2);
            g2.setFont(brandFont);
            g2.setColor(Color.WHITE);
            String headline = "IT-EVENTS";
            int headlineWidth = g2.getFontMetrics().stringWidth(headline);
            int headlineX = (LOGO_WIDTH - headlineWidth) / 2;
            int baseline = LOGO_HEIGHT / 2 + g2.getFontMetrics().getAscent() / 2 - 6;
            g2.drawString(headline, headlineX, baseline);

            Font subFont = new Font("Comic Sans MS", Font.PLAIN, LOGO_HEIGHT / 6);
            g2.setFont(subFont);
            String subline = "Календарь мероприятий";
            int sublineWidth = g2.getFontMetrics().stringWidth(subline);
            int sublineX = (LOGO_WIDTH - sublineWidth) / 2;
            int sublineY = baseline + g2.getFontMetrics().getAscent();
            g2.drawString(subline, sublineX, sublineY);
        } finally {
            g2.dispose();
        }
        return image;
    }

    private static void applyQualityHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }
}
