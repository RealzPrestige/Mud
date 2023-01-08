package dev.zprestige.mud.module.combat;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.events.impl.render.Render2DEvent;
import dev.zprestige.mud.events.impl.render.Render3DEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.shader.impl.BufferGroup;
import dev.zprestige.mud.shader.impl.GlowShader;
import dev.zprestige.mud.util.impl.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.awt.*;
import java.util.ArrayList;

public class AutoAnchor extends Module {
    private final IntSetting timing = setting("Timing", 50, 1, 500);
    private final FloatSetting range = setting("Range", 5.0f, 0.1f, 10.0f);
    private final BooleanSetting packet = setting("Packet", true);
    private final BooleanSetting rotate = setting("Rotate", false);
    private final BooleanSetting strict = setting("Strict", false);

    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Render");
    private final FloatSetting opacity = setting("Opacity", 150.0f, 0.0f, 255.0f).invokeTab("Render");
    private final FloatSetting lineWidth = setting("Line Width", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Render");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Render");

    private final ArrayList<BlockPos> anchors = new ArrayList<>(), glowStoned = new ArrayList<>();
    private long time;
    private final Vec3i[] offsets = new Vec3i[]{
            new Vec3i(1, 0, 0),
            new Vec3i(-1, 0, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1),
    };
    private final BufferGroup bufferGroup = new BufferGroup(this, z -> true, lineWidth, color1, color2, step, speed, opacity,
            () -> anchors.stream().map(AxisAlignedBB::new).forEach(RenderUtil::drawBB));

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (System.currentTimeMillis() - AutoCrystal.time < 100){
            return;
        }
        new ArrayList<>(anchors).stream().filter(pos -> mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || BlockUtil.distance(pos) > range.getValue()).forEach(anchors::remove);
        new ArrayList<>(glowStoned).stream().filter(pos -> mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || BlockUtil.distance(pos) > range.getValue()).forEach(glowStoned::remove);
        EntityPlayer entityPlayer = EntityUtil.getEntityPlayer(range.getValue());
        if (entityPlayer == null || EntityUtil.isMoving()) {
            return;
        }
        if (System.currentTimeMillis() - time < timing.getValue()) {
            return;
        }
        BlockPos pos = BlockUtil.getPosition(entityPlayer).up().up();
        boolean canPlace = false;
        for (Vec3i vec3i : offsets) {
            BlockPos vec = pos.add(vec3i);
            if (!mc.world.getBlockState(vec).getMaterial().isReplaceable()) {
                canPlace = true;
            }
        }
        if (canPlace) {
            if (!anchors.contains(pos)) {
                int slot = InventoryUtil.getBlockSlotByName("anchor");
                if (slot == -1) {
                    return;
                }
                Mud.interactionManager.placeBlock(pos, rotate.getValue(), packet.getValue(), strict.getValue(), false, slot);
                anchors.add(pos);
                time = System.currentTimeMillis();
            } else if (!glowStoned.contains(pos)) {
                int slot = InventoryUtil.getBlockSlot(Blocks.GLOWSTONE);
                if (slot == -1) {
                    return;
                }
                int currentItem = mc.player.inventory.currentItem;
                InventoryUtil.switchToSlot(slot);
                PacketUtil.invoke(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                InventoryUtil.switchBack(currentItem);
                glowStoned.add(pos);
                time = System.currentTimeMillis();
            } else {
                PacketUtil.invoke(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                time = System.currentTimeMillis();
            }
        } else {
            BlockPos placeable = null;
            BlockPos pos1 = pos.down();
            for (Vec3i vec3i : offsets) {
                BlockPos vec = pos1.add(vec3i);
                if (!mc.world.getBlockState(vec).getMaterial().isReplaceable()) {
                    placeable = vec.up();
                }
            }
            if (placeable != null) {
                int slot = InventoryUtil.getBlockSlot(Blocks.OBSIDIAN);
                if (slot == -1) {
                    return;
                }
                Mud.interactionManager.placeBlock(placeable, rotate.getValue(), packet.getValue(), strict.getValue(), false, slot);
                anchors.add(pos);
                time = System.currentTimeMillis();
            } else {
                pos1 = pos1.down();
                for (Vec3i vec3i : offsets) {
                    BlockPos vec = pos1.add(vec3i);
                    if (!mc.world.getBlockState(vec).getMaterial().isReplaceable()) {
                        placeable = vec.up();
                    }
                }
                if (placeable != null) {
                    int slot = InventoryUtil.getBlockSlot(Blocks.OBSIDIAN);
                    if (slot == -1) {
                        return;
                    }
                    Mud.interactionManager.placeBlock(placeable, rotate.getValue(), packet.getValue(), strict.getValue(), false, slot);
                    anchors.add(pos);
                    time = System.currentTimeMillis();
                }
            }
        }
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        GlowShader.render3D(bufferGroup);
    }

    @EventListener
    public void onRender2D(Render2DEvent event) {
        GlowShader.render2D(bufferGroup);
    }
}
