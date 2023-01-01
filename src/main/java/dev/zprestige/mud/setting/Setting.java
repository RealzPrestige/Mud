package dev.zprestige.mud.setting;

import java.util.function.Predicate;

public abstract class Setting<T> {
    public final String name;
    public T value;
    public Predicate<T> visible;
    public String tab = "Main";

    public Setting(final String name, final T value) {
        this.name = name;
        this.value = value;
    }

    public String getName(){
        return name;
    }

    public T getValue(){
        return value;
    }

    public void invokeValue(final T value) {
        this.value = value;
    }

    public Setting<T> invokeVisibility(final Predicate<T> visible) {
        this.visible = visible;
        return this;
    }

    public Setting<T> invokeTab(final String tab) {
        this.tab = tab;
        return this;
    }

    public boolean visible() {
        return visible == null || visible.test(this.value);
    }

    public String getTab() {
        return tab;
    }
}