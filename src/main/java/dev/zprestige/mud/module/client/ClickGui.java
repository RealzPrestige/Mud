package dev.zprestige.mud.module.client;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.gui.GuiPrimaryEvent;
import dev.zprestige.mud.events.impl.system.GuiClosedEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.ColorSetting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ClickGui extends Module {
    public final ColorSetting color = setting("Color", new Color(113, 93, 214)).invokeTab("Coloring");

    public ClickGui() {
        getKeybind().invokeValue(Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        if (mc.world != null && mc.player != null) {
            mc.displayGuiScreen(Mud.clickGui);
        }
    }

    @Override
    public void onDisable() {
        if (mc.currentScreen != null) {
            mc.displayGuiScreen(null);
        }
    }

    @EventListener
    public void onGuiClosed(GuiClosedEvent event) {
        toggle();
    }

    @EventListener
    public void onGuiPrimary(GuiPrimaryEvent event) {
        event.setColor(color.getValue());
    }
}
