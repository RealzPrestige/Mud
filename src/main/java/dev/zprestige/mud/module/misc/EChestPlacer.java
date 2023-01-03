package dev.zprestige.mud.module.misc;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.Render2DEvent;
import dev.zprestige.mud.events.impl.render.Render3DEvent;
import dev.zprestige.mud.events.impl.system.KeyEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BindSetting;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.shader.impl.BufferGroup;
import dev.zprestige.mud.shader.impl.GlowShader;
import dev.zprestige.mud.util.impl.InventoryUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class EChestPlacer extends Module {
    private final BindSetting triggerKey = setting("Trigger Key", Keyboard.KEY_NONE).invokeTab("Placing");
    private final BooleanSetting packet = setting("Packet", false).invokeTab("Placing");
    private final BooleanSetting rotate = setting("Rotate", false).invokeTab("Placing");
    private final BooleanSetting strict = setting("Strict", false).invokeTab("Placing");

    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Render");
    private final FloatSetting opacity = setting("Opacity", 150.0f, 0.0f, 255.0f).invokeTab("Render");
    private final FloatSetting lineWidth = setting("Line Width", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Render");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Render");

    private BlockPos pos;
    private EnumFacing face;
    private long time;
    private final BufferGroup bufferGroup = new BufferGroup(this, z -> pos != null && face != null, lineWidth, color1, color2, step, speed, opacity,
            () -> {
                float scale = (System.currentTimeMillis() - time) / 500.0f;
                if (scale >= 1.0f) {
                    pos = null;
                    face = null;
                    return;
                }
                AxisAlignedBB bb = new AxisAlignedBB(pos.add(face.getDirectionVec()));
                RenderUtil.drawBB(bb.minX + 0.5f * scale, bb.minY + 0.5f * scale, bb.minZ + 0.5f * scale, bb.maxX - 0.5f * scale, bb.maxY - 0.5f * scale, bb.maxZ - 0.5f * scale);
            }
    );


    @EventListener
    public void onKey(KeyEvent event) {
        if (mc.currentScreen != null) {
            return;
        }
        if (triggerKey.getValue() != Keyboard.KEY_NONE) {
            int slot = InventoryUtil.getBlockFromHotbar(Blocks.ENDER_CHEST);
            if (slot == -1) {
                return;
            }
            if (event.getKey() == triggerKey.getValue()) {
                RayTraceResult result = mc.objectMouseOver;
                if (result.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                    BlockPos pos = result.getBlockPos();
                    EnumFacing enumFacing = result.sideHit;
                    BlockPos pos1 = pos.add(enumFacing.getDirectionVec());
                    int currentItem = mc.player.inventory.currentItem;
                    InventoryUtil.switchToSlot(slot);
                    Mud.interactionManager.placeBlock(pos1, rotate.getValue(), packet.getValue(), strict.getValue(), false);
                    InventoryUtil.switchBack(currentItem);
                    this.pos = pos;
                    this.face = enumFacing;
                    this.time = System.currentTimeMillis();
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
