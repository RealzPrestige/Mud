package dev.zprestige.mud.events.impl.gui;

import dev.zprestige.mud.events.bus.Event;
import net.minecraft.item.ItemStack;

public class GuiContainerItemStackEvent extends Event {
    private final ItemStack stack;
    private final int index;

    public GuiContainerItemStackEvent(ItemStack stack, int index) {
        this.stack = stack;
        this.index = index;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public int getIndex() {
        return index;
    }
}
