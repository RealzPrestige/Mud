package dev.zprestige.mud.hud.impl;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.Render2DPostEvent;
import dev.zprestige.mud.hud.HudModule;
import dev.zprestige.mud.manager.EventManager;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.util.impl.MathUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class ModuleList extends HudModule {
    private final ArrayList<Module> modules = new ArrayList<>();

    public ModuleList() {
        super("ModuleList", true);
        x = 100;
        y = 70;
    }

    @EventListener
    public void onRender2DPost(Render2DPostEvent event) {
        Mud.moduleManager.getModules().stream().filter(module -> module.getEnabled().getValue() && !modules.contains(module)).forEach(modules::add);
       modules.sort(Comparator.comparing(Module::getStringWidthFull));

        ArrayList<Runnable> names = new ArrayList<>(), appends = new ArrayList<>(), shadows = new ArrayList<>();

        float deltaY = y;
        for (Module module : new ArrayList<>(modules)) {
            module.anim = MathUtil.lerp(module.anim, module.getEnabled().getValue() ? 1.0f : 0.0f, 0.005f * EventManager.deltaTime);
            if (!module.getEnabled().getValue() && module.anim < 0.05f) {
                modules.remove(module);
                continue;
            }
            float x = this.x + width + module.getStringWidthFull() * module.anim;

            float finalDeltaY = deltaY;
            shadows.add(() -> Mud.fontManager.stringNoShadowHud(module.getName(), x + 0.5f, finalDeltaY + 0.5f, new Color(0, 0, 0, 150)));
            names.add(() -> Mud.fontManager.stringNoShadowHud(module.getName(), x, finalDeltaY, Color.WHITE));
            appends.add(() -> Mud.fontManager.stringHud(module.getAppend(), x + module.getStringWidth(), finalDeltaY, Color.GRAY));

            deltaY += (Mud.fontManager.stringHeightHud() + 4.0f) * module.anim;
        }

        shadows.forEach(Runnable::run);
        enableShader();
        names.forEach(Runnable::run);
        disableShader();
        appends.forEach(Runnable::run);

        width = 200.0f;
        height = deltaY - y + 1.0f;
    }
}
