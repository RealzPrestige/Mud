package dev.zprestige.mud.ui.drawables.gui.hudmodule;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.hud.HudModule;
import dev.zprestige.mud.setting.Setting;
import dev.zprestige.mud.setting.impl.*;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.Drawable;
import dev.zprestige.mud.ui.drawables.gui.screens.impl.HudEditorScreen;
import dev.zprestige.mud.ui.drawables.gui.settings.SettingDrawable;
import dev.zprestige.mud.ui.drawables.gui.settings.impl.*;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class HudModuleButton extends Drawable {
    private final ArrayList<SettingDrawable> settingDrawables = new ArrayList<>();
    private final HudModule hudModule;
    public float guiX, guiY, guiWidth, guiHeight;
    public float x, y, targetX, targetY, width, height, anim, enabledAnim;
    private boolean open;

    public HudModuleButton(HudModule hudModule) {
        this.hudModule = hudModule;
        for (Setting<?> setting : hudModule.getSettings()) {
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
        x = MathUtil.lerp(x, targetX, Interface.getDelta());
        y = MathUtil.lerp(y, targetY, Interface.getDelta());
        RenderUtil.prepareScissor(guiX, guiY, guiX + guiWidth, guiY + guiHeight);

        GL11.glLineWidth(1.0f);
        RenderUtil.rounded(x, y, x + width, y + height, 5.0f, HudEditorScreen.shade(5));
        RenderUtil.roundedOutline(x, y, x + width, y + 20.0f, 5.0f, HudEditorScreen.shade(-3));
        RenderUtil.roundedOutline(x, y, x + width, y + height, 5.0f, HudEditorScreen.shade(-3));
        RenderUtil.roundedOutline(x, y, x + width, y + height, 5.0f, new Color(Interface.primary().getRed() / 255.0f, Interface.primary().getGreen() / 255.0f, Interface.primary().getBlue() / 255.0f, enabledAnim));


        enabledAnim = MathUtil.lerp(enabledAnim, hudModule.getEnabled() ? 1.0f : 0.0f, Interface.getDelta());

        Mud.fontManager.guiString(hudModule.getName(), x + 5.0f, y + 10.0f - Mud.fontManager.stringHeight() / 2.0f, Color.WHITE);

        RenderUtil.releaseScissor();

        anim = MathUtil.lerp(anim, open ? 1.0f : 0.0f, Interface.getDelta());

        float deltaY = y + 25.0f;
        if (anim > 0.05f) {
            for (SettingDrawable settingDrawable : settingDrawables) {
                if (settingDrawable instanceof ModeButton && (!settingDrawable.getSetting().visible() || !open)) {
                    ((ModeButton) settingDrawable).open = false;
                }
                if (settingDrawable instanceof ColorButton && (!settingDrawable.getSetting().visible() || !open)) {
                    ((ColorButton) settingDrawable).open = false;
                }
                settingDrawable.x = x + 5.0f;
                settingDrawable.y = deltaY - settingDrawable.getHeight() * (1.0f - settingDrawable.updateVisible());
                settingDrawable.width = width - 10.0f;
                settingDrawable.guiX = guiX;
                settingDrawable.guiY = guiY;
                settingDrawable.guiWidth = guiWidth;
                settingDrawable.guiHeight = guiHeight;
                RenderUtil.prepareScissor(guiX, Math.max(guiY, Math.min(guiY + guiHeight, Math.max(y + 20.0f, deltaY - 3))), guiX + guiWidth, Math.max(guiY, Math.min(guiY + guiHeight, Math.max(deltaY - 3, y + height))));
                settingDrawable.drawScreen(mouseX, mouseY, partialTicks);
                RenderUtil.releaseScissor();
                deltaY += settingDrawable.getHeight() * settingDrawable.visibleAnim;
            }
        }
        height = 20.0f + ((deltaY - y - 15.0f) * anim);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1 && inside(mouseX, mouseY)) {
            open = !open;
        }
        for (SettingDrawable settingDrawable : settingDrawables) {
            if (settingDrawable.visibleAnim > 0.9f) {
                if (settingDrawable instanceof BooleanButton && settingDrawable.getSetting().getName().equals("Enabled")) {
                    if (mouseButton == 0 && ((BooleanButton) settingDrawable).insideEnabled(mouseX, mouseY)) {
                        hudModule.toggle();
                        continue;
                    }
                }
                settingDrawable.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }

    }

    private boolean inside(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }
}
