package dev.zprestige.mud.manager;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.font.FontRenderer;
import dev.zprestige.mud.util.MC;
import dev.zprestige.mud.util.impl.RenderUtil;

import java.awt.*;
import java.io.InputStream;

public class FontManager implements MC {
    private final FontRenderer font, hudFont;

    public FontManager() {
        font = new FontRenderer(getFont(35));
        hudFont = new FontRenderer(getFont(37.5f));
    }


    public float stringHeight() {
        return font.getHeight() / 2.0f;
    }

    public float stringHeightHud() {
        return hudFont.getHeight() / 2.0f;
    }

    public float stringWidth(String text) {
        return font.getStringWidth(text);
    }

    public float stringWidthHud(String text) {
        return hudFont.getStringWidth(text);
    }

    public void string(String text, float x, float y, Color color) {
        font.drawStringWithShadow(text, x, y, color.getRGB());
    }

    public void stringHud(String text, float x, float y, Color color) {
        hudFont.drawStringWithShadow(text, x, y, color.getRGB());
    }

    public void stringNoShadow(String text, float x, float y, Color color) {
        font.drawString(text, x, y, color.getRGB(), false);
    }

    public void stringNoShadowHud(String text, float x, float y, Color color) {
        hudFont.drawString(text, x, y, color.getRGB(), false);
    }

    public void guiString(String text, float x, float y, Color color) {
        font.drawStringWithShadow(text, x, y, color.getRGB());
        if (!Mud.clickGui.search.isEmpty() && text.toLowerCase().contains(Mud.clickGui.search.toLowerCase())){
            RenderUtil.rect(x, y , x + stringWidth(text), y + stringHeight() + 2.5f, new Color(Interface.primary().getRed(), Interface.primary().getGreen(), Interface.primary().getBlue(), 100));
        }
    }


    private Font getFont(float size) {
        final Font plain = new Font("default", Font.PLAIN, (int) size);
        try {
            InputStream inputStream = FontManager.class.getResourceAsStream("textures/font/Font.ttf");
            if (inputStream != null) {
                Font font = Font.createFont(0, inputStream);
                font = font.deriveFont(Font.PLAIN, size);
                inputStream.close();
                return font;
            }
            return plain;
        } catch (Exception exception) {
            return plain;
        }
    }
}