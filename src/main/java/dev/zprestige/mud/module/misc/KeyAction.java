package dev.zprestige.mud.module.misc;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BindSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.util.impl.InventoryUtil;
import dev.zprestige.mud.util.impl.PacketUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;

public class KeyAction extends Module {
    private final BindSetting key = setting("Key", Keyboard.KEY_NONE);
    private final IntSetting expPackets = setting("Exp Packets", 1, 1, 10);
    private int pearlTicks;

    @EventListener
    public void onTick(TickEvent event) {
        if (key.getValue().equals(Keyboard.KEY_NONE) || !Keyboard.isKeyDown(key.getValue()) || mc.currentScreen != null) {
            return;
        }

        if (pearlTicks > 0) {
            pearlTicks--;
        }

        RayTraceResult result = mc.objectMouseOver;

        switch (result.typeOfHit) {
            case BLOCK:
            case MISS:
                boolean block = result.typeOfHit.equals(RayTraceResult.Type.BLOCK);
                if (!block && pearlTicks > 0) {
                    return;
                }
                int slot = block ? InventoryUtil.getItemFromHotbar(Items.EXPERIENCE_BOTTLE) : InventoryUtil.getItemFromHotbar(Items.ENDER_PEARL);
                if (slot == -1) {
                    return;
                }

                int currentItem = mc.player.inventory.currentItem;

                InventoryUtil.switchToSlot(slot);

                for (int i = 0; i < (block ? expPackets.getValue() : 1); i++) {
                    PacketUtil.invoke(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    if (!block) {
                        pearlTicks = 10;
                    }
                }

                InventoryUtil.switchBack(currentItem);
                break;
        }
    }
}
