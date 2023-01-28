package dev.zprestige.mud.module.misc;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.BreakBlockEvent;
import dev.zprestige.mud.events.impl.player.ClickBlockEvent;
import dev.zprestige.mud.events.impl.player.DamageBlockEvent;
import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.events.impl.render.Render2DEvent;
import dev.zprestige.mud.events.impl.render.Render3DEvent;
import dev.zprestige.mud.events.impl.system.PacketSendEvent;
import dev.zprestige.mud.mixins.interfaces.IPlayerControllerMP;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.module.combat.MineCrystal;
import dev.zprestige.mud.setting.impl.BindSetting;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.shader.impl.BufferGroup;
import dev.zprestige.mud.shader.impl.GlowShader;
import dev.zprestige.mud.util.impl.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import java.awt.*;


public class PacketMine extends Module {
    private final FloatSetting range = setting("Range", 5.0f, 0.1f, 6.0f);
    private final BooleanSetting rotate = setting("Rotate", false);
    private final BooleanSetting abort = setting("Abort", false);

    public final BooleanSetting instant = setting("Instant", false).invokeTab("Instant");
    public final BindSetting key = setting("Key", Keyboard.KEY_NONE).invokeVisibility(z -> instant.getValue()).invokeTab("Instant");
    private final BooleanSetting swap = setting("Swap", false).invokeTab("Instant");
    public final FloatSetting timing = setting("Timing", 100.0f, 0.1f, 500.0f).invokeVisibility(z -> instant.getValue()).invokeTab("Instant");

    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Render");
    private final FloatSetting opacity = setting("Opacity", 150.0f, 0.0f, 255.0f).invokeTab("Render");
    private final FloatSetting lineWidth = setting("Line Width", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Render");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Render");

    private EnumFacing face, prevFace;
    private BlockPos pos, prevPos;
    private long time, sys;
    private final BufferGroup bufferGroup = new BufferGroup(this, z -> true, lineWidth, color1, color2, step, speed, opacity,
            () -> {
                if (pos != null) {
                    float scale = Math.min(1.0f, ((System.currentTimeMillis() - time) / 1000.0f) * multiplier(pos));
                    AxisAlignedBB bb = new AxisAlignedBB(pos);
                    RenderUtil.drawBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY + scale, bb.maxZ);
                }
            }
    );

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (prevPos != null && prevFace != null && mc.currentScreen == null) {
            if (instant.getValue()) {
                if (key.getValue() != Keyboard.KEY_NONE && Keyboard.isKeyDown(key.getValue())) {
                    if (System.currentTimeMillis() - sys > timing.getValue()) {
                        int currentItem = mc.player.inventory.currentItem;
                        int slot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
                        if (swap.getValue() && slot != -1) {
                            InventoryUtil.switchToSlot(slot);
                        }
                        if (rotate.getValue()) {
                            RotationUtil.facePos(prevPos, event);
                        }
                        PacketUtil.invoke(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, prevPos, prevFace));
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        if (swap.getValue() && slot != -1) {
                            InventoryUtil.switchBack(currentItem);
                        }
                        sys = System.currentTimeMillis();
                    }
                }
            }
        }

        if (pos == null) {
            invokeAppend("");
            return;
        }
        prevPos = pos;
        prevFace = face;
        float scale = ((System.currentTimeMillis() - time) * multiplier(pos));
        invokeAppend(String.valueOf(Math.ceil(scale)).replace(".0", ""));
        if (rotate.getValue()) {
            RotationUtil.facePos(pos, event);
        }
        if (BlockUtil.is(pos, Blocks.AIR) || BlockUtil.distance(pos) > range.getValue()) {
            if (BlockUtil.is(pos, Blocks.AIR)) {
                MineCrystal.onBreakBlock(new BreakBlockEvent(pos, face));
            }
            abortBlock();
        }
    }


    @EventListener
    public void onDamageBlock(DamageBlockEvent event) {
        if (pos != null) {
            abortBlock();
        }
        if (canBreak(event.getPos())) {
            breakBlock(event.getPos(), event.getFace());
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onClickBlock(ClickBlockEvent event) {
        if (((IPlayerControllerMP) mc.playerController).getCurBlockDamageMP() > 0.0f) {
            ((IPlayerControllerMP) mc.playerController).setHittingBlock(true);
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

    @EventListener
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketHeldItemChange) {
            CPacketHeldItemChange packet = (CPacketHeldItemChange) event.getPacket();
            if (pos != null && !(mc.player.inventory.getStackInSlot(packet.getSlotId()).getItem() instanceof ItemPickaxe)) {
                BlockPos pos = this.pos;
                EnumFacing face = this.face;
                abortBlock();
                breakBlock(pos, face);
            }
        }
    }

    private void abortBlock() {
        if (abort.getValue()) {
            PacketUtil.invoke(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, pos, face));
        }
        ((IPlayerControllerMP) mc.playerController).setHittingBlock(false);
        ((IPlayerControllerMP) mc.playerController).setCurBlockDamageMP(0.0f);
        mc.world.sendBlockBreakProgress(mc.player.getEntityId(), pos, -1);
        mc.player.resetCooldown();
        pos = null;
        face = null;
    }

    private void breakBlock(BlockPos pos, EnumFacing face) {

        PacketUtil.invoke(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, face));
        PacketUtil.invoke(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, face));

        mc.player.swingArm(EnumHand.MAIN_HAND);


        this.pos = pos;
        this.face = face;
        this.time = System.currentTimeMillis();
    }

    private boolean canBreak(BlockPos pos) {
        return BlockUtil.is(pos, Blocks.OBSIDIAN) || BlockUtil.is(pos, Blocks.ENDER_CHEST) || BlockUtil.is(pos, Blocks.NETHERRACK);
    }

    private float multiplier(BlockPos pos) {
        return pos == null ? 1.0f : BlockUtil.is(pos, Blocks.OBSIDIAN) ? 0.5f : BlockUtil.is(pos, Blocks.NETHERRACK) ? 5.0f : 1.0f;
    }
}
