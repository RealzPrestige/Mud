package dev.zprestige.mud.ui.drawables.gui.module.tab;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.Setting;
import dev.zprestige.mud.setting.impl.*;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.Drawable;
import dev.zprestige.mud.ui.drawables.gui.screens.impl.DefaultScreen;
import dev.zprestige.mud.ui.drawables.gui.settings.SettingDrawable;
import dev.zprestige.mud.ui.drawables.gui.settings.impl.*;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;

import java.awt.*;
import java.util.ArrayList;

public class ModuleTab extends Drawable {
    public final ArrayList<Setting<?>> settings = new ArrayList<>();
    public final ArrayList<SettingDrawable> settingDrawables = new ArrayList<>();
    public float x = -0.26969f, y = -0.26969f, width, height;
    public float guiX, guiY, guiWidth, guiHeight;
    public float targetX, targetY, anim;
    public final Module module;
    public String name;

    public ModuleTab(Module module, String name) {
        this.module = module;
        this.name = name;
    }

    public void init() {
        for (Setting<?> setting : settings) {
            if (setting instanceof BooleanSetting) {
                settingDrawables.add(new BooleanButton((BooleanSetting) setting));
                continue;
            }
            if (setting instanceof BindSetting) {
                settingDrawables.add(new BindButton((BindSetting) setting));
                continue;
            }
            if (setting instanceof FloatSetting) {
                settingDrawables.add(new FloatButton((FloatSetting) setting));
                continue;
            }
            if (setting instanceof IntSetting) {
                settingDrawables.add(new IntButton((IntSetting) setting));
                continue;
            }
            if (setting instanceof ModeSetting) {
                settingDrawables.add(new ModeButton((ModeSetting) setting));
                continue;
            }
            settingDrawables.add(new ColorButton((ColorSetting) setting));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        anim = MathUtil.lerp(anim, (DefaultScreen.getActiveModule() != null && DefaultScreen.getActiveModule() == module) ? 0.0f : 1.0f, Interface.getDelta());
        if (ignore()) {
            return;
        }

        x = MathUtil.lerp(x, targetX, Interface.getDelta());
        y = MathUtil.lerp(y, targetY, Interface.getDelta());

        RenderUtil.prepareScissor(guiX, guiY, guiX + guiWidth, guiY + guiHeight);

        Mud.fontManager.guiString(name, x + 5.0f, y - 7.5f, Color.GRAY);
        RenderUtil.rounded(x, y, x + width, y + height, 7.0f, color());
        RenderUtil.roundedOutline(x, y, x + width, y + height, 7.0f, shade(-3));

        RenderUtil.releaseScissor();

        float deltaY = y + 5.0f;
        for (SettingDrawable settingDrawable : settingDrawables) {
            settingDrawable.x = x + 5.0f;
            settingDrawable.y = deltaY - settingDrawable.getHeight() * (1.0f - settingDrawable.updateVisible());
            settingDrawable.width = width - 10.0f;
            settingDrawable.guiX = guiX;
            settingDrawable.guiY = guiY;
            settingDrawable.guiWidth = guiWidth;
            settingDrawable.guiHeight = guiHeight;
            RenderUtil.prepareScissor(guiX, Math.max(guiY, Math.min(guiY + guiHeight, deltaY - 3)), guiX + guiWidth, Math.max(guiY, Math.min(guiY + guiHeight, Math.max(deltaY - 3, y + height))));
            settingDrawable.drawScreen(mouseX, mouseY, partialTicks);
            RenderUtil.releaseScissor();
            deltaY += settingDrawable.getHeight() * settingDrawable.updateVisible();
        }
        height = deltaY - y + 5.0f;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (ignore() || mouseX < guiX || mouseX > guiX + guiWidth || mouseY < guiY || mouseY > guiY + guiHeight) {
            return;
        }
        settingDrawables.stream().filter(settingDrawable -> settingDrawable.visibleAnim > 0.9f).forEach(settingDrawable -> settingDrawable.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (ignore()) {
            return;
        }
        settingDrawables.stream().filter(SettingDrawable::isVisible).forEach(settingDrawable -> settingDrawable.keyTyped(typedChar, keyCode));
    }

    public float getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public Color color() {
        return new Color(43, 46, 66);
    }

    public static Color shade(int i) {
        return new Color(43 + i, 46 + i, 66 + i);
    }

    public boolean ignore() {
        return anim > 0.9f;
    }
}
