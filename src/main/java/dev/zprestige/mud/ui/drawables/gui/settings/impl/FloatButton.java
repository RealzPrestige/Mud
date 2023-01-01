package dev.zprestige.mud.ui.drawables.gui.settings.impl;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.gui.module.tab.ModuleTab;
import dev.zprestige.mud.ui.drawables.gui.screens.impl.DefaultScreen;
import dev.zprestige.mud.ui.drawables.gui.settings.SettingDrawable;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class FloatButton extends SettingDrawable {
    private final FloatSetting setting;
    private float sliderX;

    public FloatButton(FloatSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Mud.fontManager.guiString(setting.getName(), x, y + height / 2.0f - Mud.fontManager.stringHeight() / 2.0f, new Color(1.0f, 1.0f, 1.0f, 0.4f));

        RenderUtil.rect(x + width / 2.0f - 10.0f, y + 6.5f, x + width - 10.0f, y + height - 6.5f, ModuleTab.shade(5));

        float w = width / 2.0f,
                v = setting.value - setting.min,
                m = setting.max - setting.min,
                targetX = x + width / 2.0f - 10.0f + w * (v / m);
        sliderX = MathUtil.lerp(sliderX, targetX, Interface.getDelta());

        RenderUtil.rect(x + width / 2.0f - 10.0f, y + 6.5f, sliderX, y + height - 6.5f, Interface.primary());
        RenderUtil.roundedOutline(x + width / 2.0f - 10.0f, y + 6.5f, x + width - 10.0f, y + height - 6.5f, 0.0f, ModuleTab.shade(-3));

        RenderUtil.circle(sliderX - 1.75f, y + height / 2.0f - 1.75f, 3.5f, ModuleTab.shade(-3));
        RenderUtil.circle(sliderX - 1.5f, y + height / 2.0f - 1.5f, 3.0f, Interface.primary());

        float scale = 0.7f;
        RenderUtil.invokeScale(scale);

        String value = setting.getValue().toString();
        Mud.fontManager.guiString(value, (sliderX - Mud.fontManager.stringWidth(value) * scale / 2.0f) / scale, (y - 2.0f) / scale, new Color(1.0f, 1.0f, 1.0f, 0.4f));

        value = String.valueOf(setting.min);
        Mud.fontManager.guiString(value, (x + width / 2.0f - 12.5f - Mud.fontManager.stringWidth(value) * scale) / scale, (y + height / 2.0f - Mud.fontManager.stringHeight() * scale / 2.0f) / scale, new Color(1.0f, 1.0f, 1.0f, 0.4f));

        value = String.valueOf(setting.max);
        Mud.fontManager.guiString(value, (x + width - 7.5f) / scale, (y + height / 2.0f - Mud.fontManager.stringHeight() * scale / 2.0f) / scale, new Color(1.0f, 1.0f, 1.0f, 0.4f));

        RenderUtil.resetScale();

        if ((DefaultScreen.getActiveModule() != null || Interface.selectedScreen.equals("HudEditor")) && Mouse.isButtonDown(0) && visibleAnim > 0.9f) {
            if (insideSlider(mouseX, mouseY)) {
                float diff = mouseX - (x + width / 2.0f - 10.0f);
                float multiplier = diff / w;
                setting.invokeValue(roundNumber(Math.max(setting.min, Math.min(setting.max, (multiplier * (setting.max - setting.min)) + setting.min))));
            }
        }

        height = 15.0f;
    }

    private boolean insideSlider(int mouseX, int mouseY) {
        return mouseX > x + width / 2.0f - 15.0f && mouseX < x + width - 5.0f && mouseY > y + 5.0f && mouseY < y + height - 5.0f;
    }

    private float roundNumber(float value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.FLOOR).floatValue();
    }

}
