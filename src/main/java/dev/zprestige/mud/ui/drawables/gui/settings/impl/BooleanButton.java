package dev.zprestige.mud.ui.drawables.gui.settings.impl;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.gui.module.tab.ModuleTab;
import dev.zprestige.mud.ui.drawables.gui.settings.SettingDrawable;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;

import java.awt.*;

public class BooleanButton extends SettingDrawable {
    private final BooleanSetting setting;
    private float alpha, c;

    public BooleanButton(BooleanSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Mud.fontManager.guiString(setting.getName(), x, y + height / 2.0f - Mud.fontManager.stringHeight() / 2.0f, new Color(1.0f, 1.0f, 1.0f, alpha));

        RenderUtil.rounded(x + width - 25.0f, y + 2.5f, x + width, y + height - 2.5f, 5.0f, ModuleTab.shade(5));
        RenderUtil.roundedOutline(x + width - 25.0f, y + 2.5f, x + width, y + height - 2.5f, 5.0f, ModuleTab.shade(-2));

        alpha = MathUtil.lerp(alpha, setting.getValue() ? insideEnabled(mouseX, mouseY) ? 0.7f : 1.0f : insideEnabled(mouseX, mouseY) ? 0.7f : 0.4f, Interface.getDelta());
        c = MathUtil.lerp(c, setting.getValue() ? 1.0f : 0.0f, Interface.getDelta());
        Color p = Interface.primary();
        Color color = new Color(p.getRed() / 255.0f, p.getGreen() / 255.0f, p.getBlue() / 255.0f, alpha);
        RenderUtil.circle(x + width - 21.0f + (13.0f * c), y + height / 2.0f - 2.0f, 4.0f, color);
        height = 15.0f;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && insideEnabled(mouseX, mouseY)) {
            setting.invokeValue(!setting.getValue());
        }
    }

    public boolean insideEnabled(int mouseX, int mouseY) {
        return mouseX > x + width - 25.0f && mouseX < x + width && mouseY > y + 2.5f && mouseY < y + height - 2.5f;
    }
}
