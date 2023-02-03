package dev.zprestige.mud.module;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.system.ToggleEvent;
import dev.zprestige.mud.setting.Setting;
import dev.zprestige.mud.setting.impl.*;
import dev.zprestige.mud.util.MC;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module implements MC {
    private String name;
    private Category category;
    private final ArrayList<Setting<?>> settings;
    private final BooleanSetting enabled;
    private final BindSetting keybind;
    private boolean alwaysListening = false;
    private String append = "";
    public float anim = 0.0f;

    public Module() {
        this.settings = new ArrayList<>();
        this.enabled = new BooleanSetting("Enabled", false);
        this.keybind = new BindSetting("Keybind", Keyboard.KEY_NONE);
        this.settings.addAll(Arrays.asList(enabled, keybind));
    }

    public Module invoke(String name) {
        this.name = name;
        this.category = Mud.moduleManager.getCategory();
        return this;
    }

    public Module invokeSection(Category category){
        Mud.moduleManager.startSection(category);
        return this;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    private void enable() {
        if (!isAlwaysListening()) {
            Mud.eventBus.registerListener(this);
        }
        onEnable();
        Mud.eventBus.invoke(new ToggleEvent(this));
    }

    private void disable() {
        if (!isAlwaysListening()) {
            Mud.eventBus.unregisterListener(this);
        }
        onDisable();
        Mud.eventBus.invoke(new ToggleEvent(this));
    }

    public void toggle() {
        enabled.invokeValue(!enabled.getValue());
        if (enabled.getValue()) {
            enable();
        } else {
            disable();
        }
    }

    public BindSetting setting(String name, int key) {
        BindSetting setting = new BindSetting(name, key);
        settings.add(setting);
        return setting;
    }

    public BooleanSetting setting(String name, boolean value) {
        BooleanSetting setting = new BooleanSetting(name, value);
        settings.add(setting);
        return setting;
    }

    public FloatSetting setting(String name, float value, float min, float max) {
        FloatSetting setting = new FloatSetting(name, value, min, max);
        settings.add(setting);
        return setting;
    }

    public IntSetting setting(String name, int value, int min, int max) {
        IntSetting setting = new IntSetting(name, value, min, max);
        settings.add(setting);
        return setting;
    }

    public ModeSetting setting(String name, String value, List<String> values) {
        ModeSetting setting = new ModeSetting(name, value, values);
        settings.add(setting);
        return setting;
    }

    public ColorSetting setting(String name, Color value) {
        ColorSetting setting = new ColorSetting(name, value);
        settings.add(setting);
        return setting;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public ArrayList<Setting<?>> getSettings() {
        return settings;
    }

    public BooleanSetting getEnabled() {
        return enabled;
    }

    public BindSetting getKeybind() {
        return keybind;
    }

    public float getStringWidthFull() {
        return -(Mud.fontManager.stringWidthHud(name) + Mud.fontManager.stringWidthHud(getAppend()));
    }

    public float getStringWidth() {
        return Mud.fontManager.stringWidthHud(name);
    }

    public String getAppend() {
        return append;
    }

    public void invokeAppend(String append) {
        this.append = " " + append;
    }

    public void setAlwaysListening(boolean alwaysListening) {
        this.alwaysListening = alwaysListening;
    }

    public boolean isAlwaysListening() {
        return alwaysListening;
    }

    public boolean isEnabled(){
        return getEnabled().getValue();
    }
}
