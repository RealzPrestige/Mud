package dev.zprestige.mud.ui.drawables.gui.settings.impl;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.setting.impl.BindSetting;
import dev.zprestige.mud.ui.drawables.gui.module.tab.ModuleTab;
import dev.zprestige.mud.ui.drawables.gui.settings.SettingDrawable;
import dev.zprestige.mud.util.impl.RenderUtil;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class BindButton extends SettingDrawable {
    private final BindSetting setting;
    private boolean typing;

    public BindButton(BindSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Mud.fontManager.guiString(setting.getName(), x, y + height / 2.0f - Mud.fontManager.stringHeight() / 2.0f, new Color(1.0f, 1.0f, 1.0f, 0.4f));

        RenderUtil.rounded(x + width - 50.0f, y + 2.0f, x + width, y + height - 2.0f, 5.0f, ModuleTab.shade(5));
        RenderUtil.roundedOutline(x + width - 50.0f, y + 2.0f, x + width, y + height - 2.0f, 5.0f, ModuleTab.shade(-2));

        float scale = 0.9f;
        RenderUtil.invokeScale(scale);

        String text = typing ? dots() : setting.getValue() == Keyboard.KEY_NONE ? "None" : Keyboard.getKeyName(setting.getValue());
        Mud.fontManager.guiString(text, (x + width - 25.0f - Mud.fontManager.stringWidth(text) * scale / 2.0f) / scale, (y + height / 2.0f - Mud.fontManager.stringHeight() * scale / 2.0f) / scale, Color.WHITE);

        RenderUtil.resetScale();

        height = 15.0f;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && insideKey(mouseX, mouseY)) {
            typing = !typing;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (typing) {
            if (keyCode == Keyboard.KEY_DELETE || keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_ESCAPE) {
                setting.invokeValue(Keyboard.KEY_NONE);
                typing = false;
            } else if (keyCode == Keyboard.KEY_RETURN) {
                typing = false;
            } else {
                setting.invokeValue(keyCode);
                typing = false;
            }
        }
    }

    private boolean insideKey(int mouseX, int mouseY) {
        return mouseX > x + width - 40.0f && mouseX < x + width && mouseY > y + 2.5f && mouseY < y + height - 2.5f;
    }

    private long sys = 0L;

    private String dots() {
        float diff = System.currentTimeMillis() - sys;
        if (diff > 1333) {
            sys = System.currentTimeMillis();
            return "...";
        }
        if (diff > 999) {
            return "...";
        }
        if (diff > 666) {
            return "..";
        }
        if (diff > 333) {
            return ".";
        }
        return "";
    }
}
