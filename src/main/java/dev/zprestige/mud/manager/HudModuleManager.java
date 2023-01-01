package dev.zprestige.mud.manager;

import dev.zprestige.mud.hud.HudModule;
import dev.zprestige.mud.hud.impl.Armor;
import dev.zprestige.mud.hud.impl.ModuleList;
import dev.zprestige.mud.hud.impl.Watermark;
import dev.zprestige.mud.hud.impl.Welcomer;
import dev.zprestige.mud.util.MC;

import java.util.ArrayList;
import java.util.Arrays;

public class HudModuleManager implements MC {
    private final ArrayList<HudModule> hudModules;

    public HudModuleManager() {
        hudModules = new ArrayList<>();
        hudModules.addAll(Arrays.asList(
                new Watermark(),
                new Welcomer(),
                new Armor(),
                new ModuleList()
        ));
    }

    public ArrayList<HudModule> getHudModules() {
        return hudModules;
    }
}
