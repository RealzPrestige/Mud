package dev.zprestige.mud.module.combat;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.ModeSetting;
import dev.zprestige.mud.util.impl.BlockUtil;
import dev.zprestige.mud.util.impl.EntityUtil;
import dev.zprestige.mud.util.impl.InventoryUtil;
import dev.zprestige.mud.util.impl.PacketUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.Arrays;

public class Offhand extends Module {
    private final ModeSetting mode = setting("Mode", "Totem", Arrays.asList("Totem", "Crystal", "Gapple"));
    private final FloatSetting health = setting("Health", 14.0f, 0.1f, 36.0f).invokeVisibility(z -> !mode.getValue().equals("Totem"));
    private final FloatSetting safeHealth = setting("Safe Health", 10.0f, 0.1f, 36.0f).invokeVisibility(z -> !mode.getValue().equals("Totem"));
    private final BooleanSetting hotbar = setting("Hotbar", false);
    private final BooleanSetting swordGapple = setting("Sword Gapple", false);
    private final BooleanSetting whileHolding = setting("While Holding", false).invokeVisibility(z -> swordGapple.getValue());
    private final BooleanSetting update = setting("Update", false);

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        int slot = slot();
        if (slot != -1) {
            swap(slot);
        }
    }

    private int slot() {
        invokeAppend(mode.getValue());
        if (mc.currentScreen != null) {
            return -1;
        }

        int totem = InventoryUtil.getItemSlot(Items.TOTEM_OF_UNDYING, hotbar.getValue());
        float hp = BlockUtil.isSelfSafe() ? safeHealth.getValue() : health.getValue();

        if (EntityUtil.getHealth(mc.player) <= hp) {
            return totem;
        }

        if (swordGapple.getValue() && mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
            if (mc.gameSettings.keyBindUseItem.isKeyDown() || !whileHolding.getValue()) {
                int gapple = InventoryUtil.getItemSlot(Items.GOLDEN_APPLE, hotbar.getValue());
                if (gapple != -1) {
                    return gapple;
                }
            }
        }

        switch (mode.getValue()) {
            case "Totem":
                return totem;
            case "Crystal":
                int crystal = InventoryUtil.getItemSlot(Items.END_CRYSTAL);
                return crystal != -1 ? crystal : totem;
            case "Gapple":
                int gapple = InventoryUtil.getItemSlot(Items.GOLDEN_APPLE);
                return gapple != -1 ? gapple : totem;
        }

        return -1;
    }

    private void swap(int i) {
        Item item = mc.player.inventory.getStackInSlot(i).getItem();

        if (!mc.player.getHeldItemOffhand().getItem().equals(item)) {
            int slot = i < 9 ? i + 36 : i;
            swap(new int[]{slot, 45, slot});
            mc.playerController.updateController();
        }
    }

    private void swap(int[] slots) {
        Arrays.stream(slots).forEach(i -> mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, mc.player));

        if (update.getValue()) {
            PacketUtil.invoke(new CPacketPlayer());
        }
    }
}
