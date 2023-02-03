package dev.zprestige.mud.ui.drawables.gui.module;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.gui.ScrollEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.Setting;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.Drawable;
import dev.zprestige.mud.ui.drawables.gui.module.tab.ModuleTab;
import dev.zprestige.mud.ui.drawables.gui.screens.impl.DefaultScreen;
import dev.zprestige.mud.util.impl.MathUtil;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

public class ActiveModule extends Drawable {
    private final ArrayList<ModuleTab> moduleTabs = new ArrayList<>();
    private final Module module;
    public float x, y, width, height, scroll, scrollTarget;

    public ActiveModule(Module module) {
        this.module = module;
        for (Setting<?> setting : module.getSettings()) {
            if (setting.getName().equals("Enabled") || setting.getName().equals("Keybind")) {
                continue;
            }
            boolean contains = false;
            for (ModuleTab moduleTab : moduleTabs) {
                if (moduleTab.getName().equals(setting.getTab())) {
                    moduleTab.settings.add(setting);
                    contains = true;
                } else {
                    contains = false;
                }
            }
            if (!contains) {
                ModuleTab moduleTab = new ModuleTab(module, setting.getTab());
                moduleTab.settings.add(setting);
                moduleTabs.add(moduleTab);
            }
        }
        moduleTabs.forEach(ModuleTab::init);
        Mud.eventBus.registerListener(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (DefaultScreen.getActiveModule() != null && DefaultScreen.getActiveModule().equals(module)) {
            scroll = MathUtil.lerp(scroll, scrollTarget, Interface.getDelta());
        } else {
            scrollTarget = 0.0f;
        }

        float leftY = y + 10.0f, rightY = y + 10.0f;
        for (ModuleTab moduleTab : moduleTabs) {
            float x = this.x;
            boolean right = rightY < leftY;
            if (right) {
                x += width / 2.0f + 5.0f;
            }
            moduleTab.targetX = x + (width + 5.0f) * moduleTab.anim * (right ? 1.0f : -1.0f);
            moduleTab.targetY = (right ? rightY : leftY) + scroll;
            moduleTab.width = width / 2.0f - 5.0f;
            moduleTab.guiX = this.x;
            moduleTab.guiY = y;
            moduleTab.guiWidth = width;
            moduleTab.guiHeight = height;
            moduleTab.drawScreen(mouseX, mouseY, partialTicks);
            if (right) {
                rightY += moduleTab.getHeight() + 15.0f;
            } else {
                leftY += moduleTab.getHeight() + 15.0f;
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if ((DefaultScreen.getActiveModule() != null && DefaultScreen.getActiveModule() == module) || Interface.selectedScreen.equals("HudEditor")) {
            moduleTabs.stream().filter(moduleTab -> !moduleTab.ignore()).forEach(moduleTab -> moduleTab.mouseClicked(mouseX, mouseY, mouseButton));
        }
    }


    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if ((DefaultScreen.getActiveModule() != null && DefaultScreen.getActiveModule() == module) || Interface.selectedScreen.equals("HudEditor")) {
            moduleTabs.stream().filter(moduleTab -> !moduleTab.ignore()).forEach(moduleTab -> moduleTab.keyTyped(typedChar, keyCode));
        }
    }

    @EventListener
    public void onScroll(ScrollEvent event) {
        if (event.getMouseX() > x && event.getMouseX() < x + width && event.getMouseY() > y && event.getMouseY() < y + height) {
            scrollTarget +=event.getAmount() / 10.0f;
        }
    }
}
