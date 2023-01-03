package dev.zprestige.mud.module.client;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.hud.HudModule;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.Setting;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.ColorSetting;

import java.awt.*;

public class ColorSync extends Module {
    private final BooleanSetting sync = setting("Sync", false);
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214));
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214));

    public ColorSync() {
        if (!isEnabled()) {
            toggle();
        }
    }

    @Override
    public void onDisable() {
        toggle();
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (sync.getValue()) {
            for (Module module : Mud.moduleManager.getModules()) {
                for (Setting<?> setting : module.getSettings()) {
                    if (setting instanceof ColorSetting) {
                        if (setting.getName().equals("Color 1")) {
                            ((ColorSetting) setting).invokeValue(color1.getValue());
                        }
                        if (setting.getName().equals("Color 2")) {
                            ((ColorSetting) setting).invokeValue(color2.getValue());
                        }
                    }
                }
            }
            for (HudModule hudModule : Mud.hudModuleManager.getHudModules()) {
                for (Setting<?> setting : hudModule.getSettings()) {
                    if (setting instanceof ColorSetting) {
                        if (setting.getName().equals("Color 1")) {
                            ((ColorSetting) setting).invokeValue(color1.getValue());
                        }
                        if (setting.getName().equals("Color 2")) {
                            ((ColorSetting) setting).invokeValue(color2.getValue());
                        }
                    }
                }
            }
            sync.invokeValue(false);
        }
    }
}
