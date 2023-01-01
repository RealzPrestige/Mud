package dev.zprestige.mud.ui.drawables.gui.settings;

import dev.zprestige.mud.setting.Setting;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.Drawable;
import dev.zprestige.mud.util.impl.MathUtil;

public class SettingDrawable extends Drawable {
    public float guiX, guiY, guiWidth, guiHeight;
    public float x, y, width, height;
    public final Setting<?> setting;
    public float visibleAnim;

    public SettingDrawable(Setting<?> setting) {
        this.setting = setting;
        this.visibleAnim = isVisible() ? 1.0f : 0.0f;
    }

    public float updateVisible() {
        return visibleAnim = MathUtil.lerp(visibleAnim, isVisible() ? 1.0f : 0.0f, Interface.getDelta());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    public float getHeight() {
        return height;
    }

    public Setting<?> getSetting() {
        return setting;
    }

    public boolean isVisible() {
        return setting.visible();
    }
}
