package dev.zprestige.mud.setting.impl;

import dev.zprestige.mud.setting.Setting;

import java.util.function.Predicate;

public class IntSetting extends Setting<Integer> {
    public int min, max;

    public IntSetting(final String name, final Integer value, int min, int max) {
        super(name, value);
        this.min = min;
        this.max = max;
    }

    @Override
    public IntSetting invokeVisibility(Predicate<Integer> visible) {
        super.invokeVisibility(visible);
        return this;
    }

    @Override
    public IntSetting invokeTab(String tab) {
        this.tab = tab;
        return this;
    }
}
