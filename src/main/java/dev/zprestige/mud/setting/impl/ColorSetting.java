package dev.zprestige.mud.setting.impl;

import dev.zprestige.mud.setting.Setting;

import java.awt.*;
import java.util.function.Predicate;

public class ColorSetting extends Setting<Color> {

    public ColorSetting(final String name, final Color value) {
        super(name, value);
    }

    @Override
    public ColorSetting invokeVisibility(Predicate<Color> visible) {
        super.invokeVisibility(visible);
        return this;
    }

    @Override
    public ColorSetting invokeTab(String tab) {
        this.tab = tab;
        return this;
    }
}
