package dev.zprestige.mud.setting.impl;

import dev.zprestige.mud.setting.Setting;
import java.util.function.Predicate;

public class BindSetting extends Setting<Integer> {

    public BindSetting(final String name, final Integer value) {
        super(name, value);
    }

    @Override
    public BindSetting invokeVisibility(Predicate<Integer> visible) {
        super.invokeVisibility(visible);
        return this;
    }

    @Override
    public BindSetting invokeTab(String tab) {
        this.tab = tab;
        return this;
    }
}

