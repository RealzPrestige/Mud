package dev.zprestige.mud.module.visual;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.Render2DEvent;
import dev.zprestige.mud.events.impl.render.Render3DEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.shader.impl.BufferGroup;
import dev.zprestige.mud.shader.impl.GlowShader;
import dev.zprestige.mud.util.impl.BlockUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SourceESP extends Module {
    private final FloatSetting range = setting("Range", 50.0f, 0.1f, 250.0f);
    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Render");
    private final FloatSetting opacity = setting("Opacity", 150.0f, 0.0f, 255.0f).invokeTab("Render");
    private final FloatSetting lineWidth = setting("Line Width", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Render");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Render");
    private ArrayList<BlockPos> sourceBlocks = new ArrayList<>();

    private final BufferGroup bufferGroup = new BufferGroup(this, z -> true, lineWidth, color1, color2, step, speed, opacity,
            () -> sourceBlocks.stream().map(AxisAlignedBB::new).forEach(RenderUtil::drawBB)
    );

    @EventListener
    public void onRender3D(Render3DEvent event) {
        Mud.threadManager.invokeThread(() -> sourceBlocks = BlockUtil.getBlocksInRadius(range.getValue()).stream().filter(pos -> mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid && mc.world.getBlockState(pos).getBlock().getMetaFromState(mc.world.getBlockState(pos)) == 0).collect(Collectors.toCollection(ArrayList::new)));
        GlowShader.render3D(bufferGroup);
    }

    @EventListener
    public void onRender2D(Render2DEvent event) {
        GlowShader.render2D(bufferGroup);
    }
}
