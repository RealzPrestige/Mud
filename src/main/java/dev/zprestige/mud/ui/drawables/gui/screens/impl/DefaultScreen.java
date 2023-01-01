package dev.zprestige.mud.ui.drawables.gui.screens.impl;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.module.Category;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.gui.category.CategoryBar;
import dev.zprestige.mud.ui.drawables.gui.module.ActiveModule;
import dev.zprestige.mud.ui.drawables.gui.screens.DrawableScreen;
import dev.zprestige.mud.util.impl.RenderUtil;

import java.util.ArrayList;

public class DefaultScreen extends DrawableScreen {
    private final ArrayList<CategoryBar> categoryBars = new ArrayList<>();
    private final ArrayList<ActiveModule> activeModules = new ArrayList<>();
    private long sys;
    public static Module activeModule = null;

    public DefaultScreen() {
        for (Category category : Category.values()) {
            categoryBars.add(new CategoryBar(category));
        }
        for (Module module : Mud.moduleManager.getModules()) {
            activeModules.add(new ActiveModule(module));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Interface.selectedScreen.equals("Default")) {
            sys = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - sys > 500) {
            return;
        }

        /* Category bars */
        for (CategoryBar categoryBar : categoryBars) {
            categoryBar.x = x + sidebarWidth + 15.0f;
            categoryBar.y = y + categoryBarY;
            categoryBar.width = guiWidth - sidebarWidth - 25.0f;
            categoryBar.height = 70.0f;

            /* Category bar scissor */
            RenderUtil.prepareScissor(x, y, x + guiWidth, y + guiHeight);

            categoryBar.drawScreen(mouseX, mouseY, partialTicks);

            /* Release scissor */
            RenderUtil.releaseScissor();
        }
        for (ActiveModule activeModule : activeModules) {
            activeModule.x = x + sidebarWidth + 15.0f;
            activeModule.y = y + 105.0f;
            activeModule.width = guiWidth - sidebarWidth - 25.0f;
            activeModule.height = guiHeight - y - 40.0f;
            activeModule.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (System.currentTimeMillis() - sys > 500) {
            return;
        }
        for (CategoryBar categoryBar : categoryBars) {
            categoryBar.mouseClicked(mouseX, mouseY, mouseButton);
        }
        for (ActiveModule activeModule : activeModules) {
            activeModule.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (CategoryBar categoryBar : categoryBars) {
            categoryBar.keyTyped(typedChar, keyCode);
        }
        for (ActiveModule activeModule : activeModules) {
            activeModule.keyTyped(typedChar, keyCode);
        }
    }

    public static Module getActiveModule() {
        return activeModule;
    }

    public static void setActiveModule(Module activeModule) {
        DefaultScreen.activeModule = activeModule;
    }
}
