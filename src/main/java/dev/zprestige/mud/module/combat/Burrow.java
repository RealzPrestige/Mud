package dev.zprestige.mud.module.combat;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.events.impl.system.PacketSendEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.ModeSetting;
import dev.zprestige.mud.util.impl.BlockUtil;
import dev.zprestige.mud.util.impl.InventoryUtil;
import dev.zprestige.mud.util.impl.PacketUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;


public class Burrow extends Module {
    private final FloatSetting force = setting("Force", 1.0f, -5.0f, 5.0f);
    private final ModeSetting prefer = setting("Prefer", "Obsidian", Arrays.asList("Obsidian", "Ender Chests"));
    private final BooleanSetting cancelRotations = setting("Cancel Rotations", false);
    private final BooleanSetting strict = setting("Strict", false);
    private final BooleanSetting rotate = setting("Rotate", false).invokeVisibility(z -> !strict.getValue());

    private final float[] offsets = new float[]{0.41f, 0.75f, 1.00f, 1.16f};
    private BlockPos startPos;

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (!BlockUtil.is(startPos, Blocks.OBSIDIAN) && !BlockUtil.is(BlockUtil.getPosition(), Blocks.ENDER_CHEST)) {
            int slot;
            if (prefer.getValue().equals("Obsidian")) {
                int obsidian = InventoryUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
                slot = obsidian != -1 ? obsidian : InventoryUtil.getBlockFromHotbar(Blocks.ENDER_CHEST);
            } else {
                int enderChest = InventoryUtil.getBlockFromHotbar(Blocks.ENDER_CHEST);
                slot = enderChest != -1 ? enderChest : InventoryUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
            }
            if (slot == -1) {
                toggle();
                return;
            }

            for (float f : offsets) {
                PacketUtil.invoke(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + f, mc.player.posZ, true));
            }
            if (strict.getValue()) {
                event.setPitch(90);
            }

            int currentItem = mc.player.inventory.currentItem;
            InventoryUtil.switchToSlot(slot);

            Mud.interactionManager.placeBlock(startPos, rotate.getValue() && !strict.getValue(), true, strict.getValue(), true);

            InventoryUtil.switchBack(currentItem);

            PacketUtil.invoke(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + force.getValue(), mc.player.posZ, false));

        } else {
            if (!cancelRotations.getValue()) {
                toggle();
            } else {
                if (!startPos.equals(BlockUtil.getPosition())
                        || mc.player.posY > mc.player.prevPosY
                        || mc.gameSettings.keyBindJump.isKeyDown()) {
                    toggle();
                }
            }
        }
    }

    @EventListener
    public void onPacketSend(PacketSendEvent event) {
        if (cancelRotations.getValue() && (BlockUtil.is(startPos, Blocks.OBSIDIAN) || BlockUtil.is(BlockUtil.getPosition(), Blocks.ENDER_CHEST))) {
            if (event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onEnable() {
        startPos = BlockUtil.getPosition();
    }
}
