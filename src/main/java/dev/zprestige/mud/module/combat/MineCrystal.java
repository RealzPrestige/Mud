package dev.zprestige.mud.module.combat;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.BreakBlockEvent;
import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.events.impl.system.PacketReceiveEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BindSetting;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.util.impl.BlockUtil;
import dev.zprestige.mud.util.impl.InventoryUtil;
import dev.zprestige.mud.util.impl.PacketUtil;
import dev.zprestige.mud.util.impl.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class MineCrystal extends Module {
    private final BindSetting key = setting("Key", Keyboard.KEY_NONE);
    private final FloatSetting timing = setting("Timing", 100.0f, 0.1f, 500.0f);
    private final FloatSetting range = setting("Range", 5.0f, 0.1f, 6.0f);
    private final BooleanSetting packet = setting("Packet", false);
    private final BooleanSetting rotate = setting("Rotate", false);
    private final BooleanSetting strict = setting("Strict", false);

    private long time;
    private static BlockPos pos;
    private static EnumFacing face;

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (key.getValue() == Keyboard.KEY_NONE || !Keyboard.isKeyDown(key.getValue()) || !mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL)) {
            return;
        }
        if (pos == null || face == null) {
            return;
        }
        if (BlockUtil.distance(pos) > range.getValue()) {
            pos = null;
            face = null;
            return;
        }
        if (System.currentTimeMillis() - time < timing.getValue()) {
            return;
        }
        if (BlockUtil.is(pos, Blocks.OBSIDIAN)) {
            if (BlockUtil.hasCrystal(pos)) {
                int slot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
                if (slot == -1) {
                    return;
                }
                if (rotate.getValue()) {
                    RotationUtil.facePos(pos, event);
                }
                PacketUtil.invoke(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, face), slot);
                mc.player.swingArm(EnumHand.MAIN_HAND);
            } else {
                EnumHand enumHand =
                        mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND
                                : mc.player.getHeldItem(EnumHand.OFF_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND
                                : null;
                if (enumHand == null) {
                    return;
                }

                if (rotate.getValue()) {
                    RotationUtil.facePos(pos, event);
                }

                EnumFacing face = EnumFacing.UP;
                PacketUtil.invoke(new CPacketPlayerTryUseItemOnBlock(pos, face, enumHand, face.getDirectionVec().getX(), face.getDirectionVec().getY(), face.getDirectionVec().getZ()));

            }
        } else {
            if (BlockUtil.hasCrystal(pos)) {
                EnumHand enumHand =
                        mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND
                                : mc.player.getHeldItem(EnumHand.OFF_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND
                                : null;
                if (enumHand == null) {
                    return;
                }

                EntityEnderCrystal entity = null;
                for (Entity e : new ArrayList<>(mc.world.loadedEntityList)) {
                    if (e instanceof EntityEnderCrystal && Math.sqrt(e.getDistanceSq(pos)) < 1.5f) {
                        entity = (EntityEnderCrystal) e;
                    }
                }
                if (entity == null) {
                    return;
                }
                if (rotate.getValue()) {
                    RotationUtil.faceEntity(entity, event);
                }
                PacketUtil.invoke(new CPacketUseEntity(entity));

                mc.player.swingArm(enumHand);
            } else {
                int currentItem = mc.player.inventory.currentItem;
                int slot = InventoryUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
                if (slot != -1) {
                    InventoryUtil.switchToSlot(slot);
                }
                Mud.interactionManager.placeBlock(pos, rotate.getValue(), packet.getValue(), strict.getValue(), false);
                if (slot != -1) {
                    InventoryUtil.switchBack(currentItem);
                }
            }
            time = System.currentTimeMillis();
        }
    }

    public static void onBreakBlock(BreakBlockEvent event) {
        MineCrystal.pos = event.getPos();
        MineCrystal.face = event.getEnumFacing();
    }
}
