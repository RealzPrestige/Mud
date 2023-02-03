package dev.zprestige.mud.ui.drawables.gui.screens.impl;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.gui.ScrollEvent;
import dev.zprestige.mud.hud.HudModule;
import dev.zprestige.mud.ui.HudEditorInterface;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.gui.hudmodule.HudModuleButton;
import dev.zprestige.mud.ui.drawables.gui.screens.DrawableScreen;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;

public class HudEditorScreen extends DrawableScreen {
    private final ArrayList<HudModuleButton> hudModuleButtons = new ArrayList<>();
    private float anim, scroll, scrollTarget;
    private long sys;

    public HudEditorScreen() {
        for (HudModule hudModule : Mud.hudModuleManager.getHudModules()) {
            hudModuleButtons.add(new HudModuleButton(hudModule));
        }
        Mud.eventBus.registerListener(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Interface.selectedScreen.equals("HudEditor")) {
            sys = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - sys > 500) {
            return;
        }
        anim = MathUtil.lerp(anim, Interface.selectedScreen.equals("HudEditor") ? 0.0f : 1.0f, Interface.getDelta());

        float width = guiWidth - sidebarWidth - 25.0f;
        float x = this.x + sidebarWidth + 15.0f;
        float exit = width / 1.5f * anim;

        RenderUtil.prepareScissor(x, y, this.x + guiWidth, y + guiHeight);

        float leftX = x - exit,
                rightX = x + width / 2.0f + 5.0f + exit,
                leftWidth = x + width / 2.0f - 5.0f - exit,
                rightWidth = this.x + guiWidth - 10.0f + exit,
                boxY = y + categoryBarY,
                boxHeight = y + guiHeight - 100.0f;

        RenderUtil.rounded(leftX, boxY, leftWidth, boxHeight, 5.0f, shade(5));
        RenderUtil.rounded(rightX, boxY, rightWidth, boxHeight, 5.0f, shade(5));
        RenderUtil.roundedOutline(leftX, boxY, leftWidth, boxHeight, 5.0f, shade(-3));
        RenderUtil.roundedOutline(rightX, boxY, rightWidth, boxHeight, 5.0f, shade(-3));

        RenderUtil.releaseScissor();

        scroll = MathUtil.lerp(scroll, scrollTarget, Interface.getDelta());
        if (!Interface.selectedScreen.equals("HudEditor")) {
            scrollTarget = 0.0f;
        }
        float leftY = boxY + 10.0f + scroll, rightY = boxY + 10.0f + scroll;
        for (HudModuleButton hudModuleButton : hudModuleButtons) {
            hudModuleButton.guiX = x;
            hudModuleButton.guiY = boxY;
            hudModuleButton.guiWidth = guiWidth - sidebarWidth - 25.0f;
            hudModuleButton.guiHeight = boxHeight - boxY;
            if (rightY < leftY) {
                hudModuleButton.targetX = rightX + 5.0f;
                hudModuleButton.targetY = rightY;
                hudModuleButton.width = rightWidth - rightX - 10.0f;
                rightY += hudModuleButton.height + 5.0f;
            } else {
                hudModuleButton.targetX = leftX + 5.0f;
                hudModuleButton.targetY = leftY;
                hudModuleButton.width = leftWidth - leftX - 10.0f;
                leftY += hudModuleButton.height + 5.0f;
            }
            hudModuleButton.drawScreen(mouseX, mouseY, partialTicks);
        }

        RenderUtil.prepareScissor(x, y, this.x + guiWidth, y + guiHeight);
        RenderUtil.rounded(x + width / 2.0f - 5.0f, boxY, x + width / 2.0f + 5.0f, Math.max(boxY, boxHeight * (1.0f - anim)), 0.0f, Interface.shade(0));


        RenderUtil.rounded(x + width / 2.0f - 75.0f, y + guiHeight - 50.0f + 60.0f * anim, x + width / 2.0f + 75.0f, y + guiHeight - 30.0f + 60.0f * anim, 5.0f, shade(5));
        RenderUtil.roundedOutline(x + width / 2.0f - 75.0f, y + guiHeight - 50.0f + 60.0f * anim, x + width / 2.0f + 75.0f, y + guiHeight - 30.0f + 60.0f * anim, 5.0f, shade(-3));
        String text = "Open Hud Editor";

        Mud.fontManager.guiString(text, x + width / 2.0f - Mud.fontManager.stringWidth(text) / 2.0f, y + guiHeight - 40.0f + 60.0f * anim - Mud.fontManager.stringHeight() / 2.0f, Color.WHITE);
        RenderUtil.releaseScissor();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (System.currentTimeMillis() - sys > 500) {
            return;
        }
        for (HudModuleButton hudModuleButton : hudModuleButtons) {
            hudModuleButton.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (mouseButton == 0) {
            float width = guiWidth - sidebarWidth - 25.0f;
            float x = this.x + sidebarWidth + 15.0f;
            if (mouseX > x + width / 2.0f - 75.0f && mouseX < x + width / 2.0f + 75.0f && mouseY > y + guiHeight - 50.0f + 60.0f * anim && mouseY < y + guiHeight - 30.0f + 60.0f * anim) {
                mc.displayGuiScreen(new HudEditorInterface());
            }
        }
    }

    @EventListener
    public void onScroll(ScrollEvent event) {
        if (event.getMouseX() > x && event.getMouseX() < this.x + guiWidth && event.getMouseY() > y && event.getMouseY() < y + guiHeight) {
            if (Interface.selectedScreen.equals("HudEditor")) {
                scrollTarget += event.getAmount() / 10.0f;
            }
        }
    }

    public Color color() {
        return new Color(43, 46, 66);
    }

    public static Color shade(int i) {
        return new Color(43 + i, 46 + i, 66 + i);
    }
}
