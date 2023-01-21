package dev.zprestige.mud.hud;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.setting.Setting;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.ModeSetting;
import dev.zprestige.mud.shader.impl.GradientShader;
import dev.zprestige.mud.util.MC;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class HudModule implements MC {
    private final String name;
    private final ArrayList<Setting<?>> settings;

    private final BooleanSetting enabled = new BooleanSetting("Enabled", false);

    private final ModeSetting mode = new ModeSetting("Color Mode", "Gradient", Arrays.asList("Gradient", "Static"));
    private final ColorSetting color = new ColorSetting("Color", new Color(113, 93, 214)).invokeVisibility(z -> mode.getValue().equals("Static"));
    private final ColorSetting color1 = new ColorSetting("Color 1", new Color(113, 93, 214)).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    private final ColorSetting color2 = new ColorSetting("Color 2", new Color(113, 220, 214)).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    private final FloatSetting speed = new FloatSetting("Speed", 1.0f, 0.1f, 5.0f).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    private final FloatSetting step = new FloatSetting("Step", 0.2f, 0.1f, 2.0f).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    public float x, y, width, height, dragX, dragY;
    public boolean dragging;

    public HudModule(String name, boolean invokeSettings) {
        this.name = name;
        this.settings = new ArrayList<>();
        if (invokeSettings) {
            this.settings.addAll(Arrays.asList(enabled, mode, color, color1, color2, speed, step));
        }
    }


    public void enableShader() {
        if (isGradient()) {
            GradientShader.setup(getStep(), getSpeed(), getGradient()[0], getGradient()[1]);
        }
    }

    public void disableShader() {
        if (isGradient()) {
            GradientShader.finish();
            GL11.glLineWidth(1.0f);
        }
    }
    private void enable() {
        Mud.eventBus.registerListener(this);
    }

    private void disable() {
        Mud.eventBus.unregisterListener(this);
    }

    public void toggle() {
        enabled.invokeValue(!enabled.getValue());
        if (enabled.getValue()) {
            enable();
        } else {
            disable();
        }
    }


    public String getName() {
        return name;
    }

    public ArrayList<Setting<?>> getSettings() {
        return settings;
    }


    public BooleanSetting getSetting() {
        return enabled;
    }

    public boolean getEnabled() {
        return enabled.getValue();
    }

    public boolean isGradient() {
        return mode.getValue().equals("Gradient");
    }

    public Color getRenderColor() {
        return mode.getValue().equals("Static") ? getStatic() : Color.WHITE;
    }

    public Color getStatic() {
        return color.getValue();
    }

    public Color[] getGradient() {
        return new Color[]{color1.getValue(), color2.getValue()};
    }

    public float getSpeed() {
        return speed.getValue();
    }

    public float getStep() {
        return step.getValue();
    }

}
