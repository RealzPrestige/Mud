package dev.zprestige.mud.hud.impl;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.Render2DPostEvent;
import dev.zprestige.mud.hud.HudModule;

import java.awt.*;

public class Welcomer extends HudModule {

    public Welcomer() {
        super("Welcomer", true);
        x = 1.0f;
        y = 1.0f;
    }

    @EventListener
    public void onRender2DPost(Render2DPostEvent event) {
        String text = "Welcome to " + Mud.MODNAME + " " + Mud.VERSION + " " + mc.player.getName();

        Mud.fontManager.stringNoShadowHud(text, x + 0.5f - Mud.fontManager.stringWidthHud(text) / 2.0f, y + 0.5f, new Color(0, 0, 0, 150));

        enableShader();
        Mud.fontManager.stringNoShadowHud(text, x - Mud.fontManager.stringWidthHud(text) / 2.0f, y, getRenderColor());
        disableShader();

        width = Mud.fontManager.stringWidthHud(text) / 2.0f + 5.0f;
        height = Mud.fontManager.stringHeightHud()  + 2.0f;
    }
}
