package dev.zprestige.mud.module.combat;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.events.impl.player.PacketMineEvent;
import dev.zprestige.mud.events.impl.render.Render2DEvent;
import dev.zprestige.mud.events.impl.render.Render3DEvent;
import dev.zprestige.mud.events.impl.world.WebExplosionEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.*;
import dev.zprestige.mud.shader.impl.BufferGroup;
import dev.zprestige.mud.shader.impl.GlowShader;
import dev.zprestige.mud.util.impl.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Arrays;
import java.util.TreeMap;

public class AutoCrystal extends Module {
    private final IntSetting placeInterval = setting("Place Interval", 50, 0, 500).invokeTab("Timing");
    private final IntSetting breakInterval = setting("Break Interval", 50, 0, 500).invokeTab("Timing");
    private final BooleanSetting simultaneously = setting("Simultaneously", false).invokeTab("Timing");

    private final ModeSetting rotate = setting("Rotate", "None", Arrays.asList("Both", "Place", "Break", "None")).invokeTab("AntiCheat");
    private final ModeSetting packet = setting("Packet", "Both", Arrays.asList("Place", "Break", "Both", "None")).invokeTab("AntiCheat");
    private final ModeSetting placements = setting("Placements", "1.12.2", Arrays.asList("1.12.2", "1.13+")).invokeTab("AntiCheat");
    private final BooleanSetting constBypass = setting("Const Bypass", false).invokeTab("AntiCheat");
    private final ModeSetting blockFace = setting("Block Face", "Closest", Arrays.asList("Closest", "Up")).invokeTab("AntiCheat");

    private final ModeSetting calculations = setting("Calculations", "Damage", Arrays.asList("Damage", "Net")).invokeTab("Calculations");
    private final BooleanSetting smartCalculations = setting("Smart Calculations", true).invokeTab("Calculations");
    private final FloatSetting minimumDamage = setting("Minimum Damage", 6.0f, 0.1f, 20.0f).invokeTab("Calculations");
    private final FloatSetting maximumSelfDamage = setting("Maximum Self Damage", 8.0f, 0.1f, 20.0f).invokeTab("Calculations");
    private final BooleanSetting placeOnMine = setting("Place On Mine", true).invokeTab("Calculations");
    private final BooleanSetting antiSuicide = setting("Anti Suicide", true).invokeTab("Calculations");
    private final FloatSetting antiSuicideSafety = setting("Anti Suicide Safety", 2.0f, 0.0f, 15.0f).invokeTab("Calculations").invokeVisibility(z -> antiSuicide.getValue());
    private final IntSetting ticksExisted = setting("Ticks Existed", 0, 0, 20).invokeTab("Calculations");

    private final FloatSetting targetRange = setting("Target Range", 10.0f, 0.1f, 15.0f).invokeTab("Ranges");
    private final FloatSetting scanRange = setting("Scan Range", 5.0f, 0.1f, 6.0f).invokeTab("Ranges");
    private final FloatSetting placeRange = setting("Place Range", 5.0f, 0.1f, 6.0f).invokeTab("Ranges");
    private final FloatSetting breakRange = setting("Break Range", 5.0f, 0.1f, 6.0f).invokeTab("Ranges");
    private final BooleanSetting strictTrace = setting("Strict Trace", false).invokeTab("Ranges");
    private final FloatSetting placeWallRange = setting("Place Wall Range", 5.0f, 0.1f, 6.0f).invokeVisibility(z -> !strictTrace.getValue()).invokeTab("Ranges");
    private final FloatSetting breakWallRange = setting("Break Wall Range", 5.0f, 0.1f, 6.0f).invokeVisibility(z -> !strictTrace.getValue()).invokeTab("Ranges");

    private final ModeSetting renderMode = setting("Render Mode", "Gradient", Arrays.asList("Gradient", "Static")).invokeTab("Render");

    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeVisibility(z -> renderMode.getValue().equals("Gradient")).invokeTab("Render");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeVisibility(z -> renderMode.getValue().equals("Gradient")).invokeTab("Render");
    private final FloatSetting opacity = setting("Opacity", 150.0f, 0.0f, 255.0f).invokeVisibility(z -> renderMode.getValue().equals("Gradient")).invokeTab("Render");
    private final FloatSetting lineWidth = setting("Line Width", 1.0f, 0.1f, 5.0f).invokeVisibility(z -> renderMode.getValue().equals("Gradient")).invokeTab("Render");
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeVisibility(z -> renderMode.getValue().equals("Gradient")).invokeTab("Render");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeVisibility(z -> renderMode.getValue().equals("Gradient")).invokeTab("Render");
    private final ColorSetting color = setting("Color", new Color(113, 93, 214)).invokeVisibility(z -> renderMode.getValue().equals("Static")).invokeTab("Render");
    private final FloatSetting outlineWidth = setting("Outline Width", 1.0f, 0.1f, 5.0f).invokeVisibility(z -> renderMode.getValue().equals("Static")).invokeTab("Render");

    private boolean calculating;
    private long placeTime, breakTime;
    private BlockPos pos;

    private final BufferGroup bufferGroup = new BufferGroup(this, z -> this.pos != null, lineWidth, color1, color2, step, speed, opacity,
            () -> {
                if (pos != null) {
                    RenderUtil.drawBB(new AxisAlignedBB(pos));
                }
            }

    );

    @EventListener
    public void onMotion(MotionUpdateEvent event) {
        EntityPlayer entityPlayer = EntityUtil.getEntityPlayer(targetRange.getValue());
        if (entityPlayer != null) {
            invokeAppend(entityPlayer.getName());
            if (constBypass.getValue()) {
                PacketUtil.invoke(new CPacketCloseWindow());
            }
            long sys = System.currentTimeMillis();
            if (sys - placeTime > placeInterval.getValue()) {

                calculating = true;
                BlockPos pos = findPos(entityPlayer);
                calculating = false;

                if (pos != null) {

                    placeCrystal(pos, event);

                    placeTime = sys;
                    if (!simultaneously.getValue()) {
                        return;
                    }
                }
                this.pos = pos;
            }
            if (sys - breakTime > breakInterval.getValue()) {
                calculating = true;
                EntityEnderCrystal crystal = crystal(entityPlayer);
                calculating = false;

                if (crystal != null) {
                    breakCrystal(crystal, event);
                    breakTime = sys;
                }
                this.pos = crystal != null ? crystal.getPosition().down() : null;
            }
        } else {
            pos = null;
        }
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (renderMode.getValue().equals("Gradient")) {
            GlowShader.render3D(bufferGroup);
        } else {
            if (pos != null) {
                RenderUtil.renderBox(new AxisAlignedBB(pos), color.getValue());
                RenderUtil.renderOutline(new AxisAlignedBB(pos), color.getValue(), outlineWidth.getValue());
            }
        }
    }

    @EventListener
    public void onRender2D(Render2DEvent event) {
        if (renderMode.getValue().equals("Gradient")) {
            GlowShader.render2D(bufferGroup);
        }
    }

    @EventListener
    public void onPacketMineEvent(PacketMineEvent event) {
        if (!placeOnMine.getValue()) {
            return;
        }
        long sys = System.currentTimeMillis();
        if (sys - placeTime > placeInterval.getValue()) {
            if (BlockUtil.distance(event.getPos()) < placeRange.getValue() && BlockUtil.valid(pos, placements.getValue().equals("1.13+"))) {
                placeCrystal(event.getPos(), null);
                placeTime = sys;
            }
        }
    }


    @EventListener
    public void onWebExplosion(WebExplosionEvent event){
        if (calculating){
            event.setCancelled(true);
        }
    }

    private void breakCrystal(EntityEnderCrystal entity, MotionUpdateEvent event) {
        EnumHand enumHand =
                mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND
                        : mc.player.getHeldItem(EnumHand.OFF_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND
                        : null;
        if (enumHand == null) {
            return;
        }

        if (rotate.getValue().equals("Both") || rotate.getValue().equals("Break")) {
            RotationUtil.faceEntity(entity, event);

        }
        int handleWeakness = handleWeakness();
        if (packet.getValue().equals("Both") || packet.getValue().equals("Break")) {
            PacketUtil.invoke(new CPacketUseEntity(entity));
        } else {
            mc.playerController.attackEntity(mc.player, entity);
        }
        if (handleWeakness == -1) {
            InventoryUtil.switchBack(handleWeakness);
        }

        mc.player.swingArm(enumHand);
    }

    private void placeCrystal(BlockPos pos, MotionUpdateEvent event) {
        EnumHand enumHand =
                mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND
                        : mc.player.getHeldItem(EnumHand.OFF_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND
                        : null;
        if (enumHand == null) {
            return;
        }
        if (event != null && (rotate.getValue().equals("Both") || rotate.getValue().equals("Place"))) {
            RotationUtil.facePos(pos, event);
        }

        EnumFacing enumFacing = blockFace.getValue().equals("Closest") ? Mud.interactionManager.getClosestEnumFacing(pos) : EnumFacing.UP;
        if (enumFacing == null) {
            enumFacing = EnumFacing.UP;
        }

        if (packet.getValue().equals("Both") || packet.getValue().equals("Place")) {
            PacketUtil.invoke(new CPacketPlayerTryUseItemOnBlock(pos, enumFacing, enumHand, enumFacing.getDirectionVec().getX(), enumFacing.getDirectionVec().getY(), enumFacing.getDirectionVec().getZ()));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, enumFacing, new Vec3d(mc.player.posX, -mc.player.posY, -mc.player.posZ), enumHand);
        }

        mc.player.swingArm(enumHand);

    }

    private EntityEnderCrystal crystal(EntityPlayer entityPlayer) {
        final TreeMap<Float, EntityEnderCrystal> posses = new TreeMap<>();
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) {
                continue;
            }
            if (entity.ticksExisted < ticksExisted.getValue()) {
                continue;
            }
            boolean raytrace = RaytraceUtil.raytrace(entity);
            float range = raytrace ? breakRange.getValue() : (strictTrace.getValue() ? 0.0f : breakWallRange.getValue());
            if (mc.player.getDistance(entity) > range) {
                continue;
            }
            float selfDamage = BlockUtil.calculateEntityDamage((EntityEnderCrystal) entity, mc.player);
            float damage = BlockUtil.calculateEntityDamage((EntityEnderCrystal) entity, entityPlayer);
            if (smartCalculations.getValue() && damage - selfDamage < 0) {
                continue;
            }
            if (selfDamage > maximumSelfDamage.getValue()) {
                continue;
            }
            if (antiSuicide.getValue() && selfDamage > mc.player.getHealth() + mc.player.getAbsorptionAmount() - antiSuicideSafety.getValue()) {
                continue;
            }
            if (damage < minimumDamage.getValue()) {
                continue;
            }
            posses.put(calculations.getValue().equals("Damage") ? damage : damage - selfDamage, (EntityEnderCrystal) entity);
        }
        if (!posses.isEmpty()) {
            return posses.lastEntry().getValue();
        }
        return null;
    }

    private BlockPos findPos(EntityPlayer entityPlayer) {
        final TreeMap<Float, BlockPos> posses = new TreeMap<>();
        for (BlockPos pos : BlockUtil.getBlocksInRadius(scanRange.getValue())) {
            if (!BlockUtil.valid(pos, placements.getValue().equals("1.13+"))) {
                continue;
            }
            boolean raytrace = RaytraceUtil.raytrace(pos.up());
            float range = raytrace ? placeRange.getValue() : (strictTrace.getValue() ? 0.0f : placeWallRange.getValue());
            if (Math.sqrt(mc.player.getDistanceSq(BlockUtil.center(pos))) > range) {
                continue;
            }
            float selfDamage = BlockUtil.calculatePosDamage(pos, mc.player);
            float damage = BlockUtil.calculatePosDamage(pos, entityPlayer);
            if (smartCalculations.getValue() && damage - selfDamage < 0) {
                continue;
            }
            if (!BlockUtil.empty(pos)) {
                continue;
            }
            if (selfDamage > maximumSelfDamage.getValue()) {
                continue;
            }
            if (antiSuicide.getValue() && selfDamage > mc.player.getHealth() + mc.player.getAbsorptionAmount() - antiSuicideSafety.getValue()) {
                continue;
            }
            if (damage < minimumDamage.getValue()) {
                continue;
            }
            posses.put(calculations.getValue().equals("Damage") ? damage : damage - selfDamage, pos);
        }
        if (!posses.isEmpty()) {
            return posses.lastEntry().getValue();
        }
        return null;
    }

    private int handleWeakness() {
        int currentItem = -1;
        PotionEffect weakness = mc.player.getActivePotionEffect(MobEffects.WEAKNESS);
        if (weakness != null && !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
            int swordSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
            if (swordSlot != -1) {
                currentItem = mc.player.inventory.currentItem;
                InventoryUtil.switchToSlot(swordSlot);
            }
        }
        return currentItem;
    }
}
