package dev.zprestige.mud.setting.impl;


import dev.zprestige.mud.setting.Setting;
import java.util.function.Predicate;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(final String name, final Boolean value) {
        super(name, value);
    }

    @Override
    public BooleanSetting invokeVisibility(Predicate<Boolean> visible) {
        super.invokeVisibility(visible);
        return this;
    }

    @Override
    public BooleanSetting invokeTab(String tab) {
        this.tab = tab;
        return this;
    }
}
