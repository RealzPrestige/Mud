package dev.zprestige.mud.ui.drawables.gui.settings.impl;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.setting.impl.ModeSetting;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.gui.module.tab.ModuleTab;
import dev.zprestige.mud.ui.drawables.gui.settings.SettingDrawable;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;

import java.awt.*;

public class ModeButton extends SettingDrawable {
    private final ModeSetting setting;
    private float anim, boxWidth;
    public boolean open;

    public ModeButton(ModeSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Mud.fontManager.guiString(setting.getName(), x, y + 7.5f - Mud.fontManager.stringHeight() / 2.0f, new Color(1.0f, 1.0f, 1.0f, 0.4f));
        float longest = 0.0f;
        for (String string : setting.values) {
            float stringWidth = Mud.fontManager.stringWidth(string);
            if (stringWidth > longest) {
                longest = stringWidth;
            }
        }

        boxWidth = longest + 20.0f;
        anim = MathUtil.lerp(anim, open ? 1.0f : 0.0f, Interface.getDelta());
        height = 15.0f + (((10.0f * setting.values.size() - 1) - 2.5f) * anim);

        RenderUtil.rounded(x + width - boxWidth, y + 2.0f, x + width, y + height - 2.0f, 5.0f, ModuleTab.shade(5));
        RenderUtil.roundedOutline(x + width - boxWidth, y + 2.0f, x + width, y + height - 2.0f, 5.0f, ModuleTab.shade(-2));

        float scale = 0.9f;
        RenderUtil.invokeScale(scale);
        Mud.fontManager.guiString(setting.getValue(), (x + width - boxWidth / 2.0f - Mud.fontManager.stringWidth(setting.getValue()) * scale / 2.0f) / scale, (y + 6.5f - Mud.fontManager.stringHeight() * scale / 2.0f) / scale, Color.WHITE);
        RenderUtil.resetScale();

        RenderUtil.drawExpand(x + width - 12.5f, y + 2.5f);

        RenderUtil.prepareScissor(Math.max(guiX, Math.min(x, guiX + guiWidth)), Math.max(guiY, Math.min(guiY + guiHeight, y)), Math.max(guiX, Math.min(x + width, guiX + guiWidth)), Math.max(guiY, Math.min(guiY + guiHeight, y + height - 2.5f)));

        RenderUtil.invokeScale(scale);
        float deltaY = y + 12.5f;
        for (String value : setting.values) {
            if (setting.getValue().equals(value)) {
                continue;
            }
            Color color = new Color(1.0f, 1.0f, 1.0f, 0.4f);
            if (mouseY > deltaY && mouseY < deltaY + 11.0f) {
                color = Interface.primary();
            }
            Mud.fontManager.guiString(value, (x + width - boxWidth / 2.0f - Mud.fontManager.stringWidth(value) * scale / 2.0f) / scale, (deltaY + 5.0f - Mud.fontManager.stringHeight() * scale / 2.0f) / scale, color);
            deltaY += 11.0f;
        }
        RenderUtil.resetScale();

        RenderUtil.releaseScissor();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            if (insideBox(mouseX, mouseY)) {
                if (open) {
                    float deltaY = y + 12.5f;
                    for (String value : setting.values) {
                        if (setting.getValue().equals(value)) {
                            continue;
                        }
                        if (mouseY > deltaY && mouseY < deltaY + 11.0f) {
                            setting.invokeValue(value);
                            break;
                        }
                        deltaY += 11.0f;
                    }
                }
                open = !open;
            }
        }
    }

    private boolean insideBox(int mouseX, int mouseY) {
        return mouseX > x + width - boxWidth && mouseX < x + width && mouseY > y + 2.5f && mouseY < y + height - 2.5f;
    }
}
