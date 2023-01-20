package dev.zprestige.mud.module.misc;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.util.impl.EntityUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

import java.util.stream.IntStream;

public class Refill extends Module {
    public final BooleanSetting strict = setting("Strict", false);
    public final IntSetting delay = setting("Delay", 50, 1, 500);
    public final IntSetting fillAt = setting("Fill At", 60, 1, 64);
    private long time;

    @EventListener
    public void onTick(TickEvent event){
        if (mc.currentScreen == null && (!strict.getValue() || !EntityUtil.isMoving()) && System.currentTimeMillis() - time > delay.getValue() && IntStream.range(0, 9).anyMatch(this::refillSlot)) {
            time = System.currentTimeMillis();
        }
    }

    private boolean refillSlot(final int slot) {
        final ItemStack stack = mc.player.inventory.getStackInSlot(slot);
        if ((!stack.isEmpty() && stack.getItem() != Items.AIR) && stack.isStackable() && stack.getCount() < stack.getMaxStackSize() && stack.getCount() <= fillAt.getValue()) {
            for (int i = 9; i < 36; ++i) {
                final ItemStack item = mc.player.inventory.getStackInSlot(i);
                if (!item.isEmpty() && stack.getItem() == item.getItem() && stack.getDisplayName().equals(item.getDisplayName())) {
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
                    mc.playerController.updateController();
                    return true;
                }
            }
        }
        return false;
    }
}
