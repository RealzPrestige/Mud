package dev.zprestige.mud.setting.impl;

import dev.zprestige.mud.setting.Setting;

import java.util.List;
import java.util.function.Predicate;

public class ModeSetting extends Setting<String> {
    public final List<String> values;

    public ModeSetting(final String name, final String value, final List<String> values) {
        super(name, value);
        this.values = values;
    }

    @Override
    public ModeSetting invokeVisibility(Predicate<String> visible) {
        super.invokeVisibility(visible);
        return this;
    }

    @Override
    public ModeSetting invokeTab(String tab) {
        this.tab = tab;
        return this;
    }
}
