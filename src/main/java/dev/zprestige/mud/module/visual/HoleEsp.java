package dev.zprestige.mud.module.visual;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.Render2DEvent;
import dev.zprestige.mud.events.impl.render.Render3DEvent;
import dev.zprestige.mud.manager.EventManager;
import dev.zprestige.mud.manager.HoleManager;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.shader.impl.BufferGroup;
import dev.zprestige.mud.shader.impl.GlowShader;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class HoleEsp extends Module {
    private final IntSetting range = setting("Range", 5, 1, 20);
    private final FloatSetting animationSpeed = setting("Animation Speed", 50.0f, 1.0f, 200.0f);
    private final BooleanSetting checkFrustum = setting("Check Frustum", true);

    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Shader");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Shader");
    private final FloatSetting opacity = setting("Opacity", 150.0f, 0.0f, 255.0f).invokeTab("Shader");
    private final FloatSetting lineWidth = setting("Line Width", 1.0f, 0.1f, 5.0f).invokeTab("Shader");

    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Coloring");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Coloring");

    private final ArrayList<Hole> renderHoles = new ArrayList<>();
    private final ICamera camera = new Frustum();

    private final BufferGroup bufferGroup = new BufferGroup(this, z -> true, lineWidth, color1, color2, step, speed, opacity,
            () -> new ArrayList<>(renderHoles).forEach(hole -> {
                if (!Mud.holeManager.holeManagerContains(hole.holePos.getPos()) || differentRenderType(hole.holePos)) {
                    hole.out = true;
                    if (hole.size <= 0.1f) {
                        renderHoles.remove(hole);
                        return;
                    }
                }
                hole.updateSize();
                if (!checkFrustum.getValue() || Mud.frustumManager.isInsideFrustum(new AxisAlignedBB(hole.holePos.getPos()))){
                    hole.render();
                }
            })
    );


    private boolean differentRenderType(final HoleManager.HolePos pos) {
        return Mud.holeManager.getHoles().stream().filter(holePos -> holePos.getPos().equals(pos.getPos())).anyMatch(holePos -> !holePos.getHoleType().equals(pos.getHoleType()));
    }

    private boolean holesContains(final HoleManager.HolePos pos) {
        return renderHoles.stream().anyMatch(renderHole -> renderHole.holePos.getPos().equals(pos.getPos()));
    }

    @EventListener
    public void onRender3D(final Render3DEvent event) {
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);

        Mud.threadManager.invokeThread(() -> Mud.holeManager.loadHoles(range.getValue()));

        for (HoleManager.HolePos holePos : Mud.holeManager.getHoles()) {
            final boolean diff = differentRenderType(holePos);
            if (!holesContains(holePos) || diff) {
                Hole hole1 = new Hole(holePos);
                renderHoles.add(hole1);
            }
        }

        GlowShader.render3D(bufferGroup);
    }

    @EventListener
    public void onRender2D(Render2DEvent event) {
        GlowShader.render2D(bufferGroup);
    }

    public class Hole {
        public final HoleManager.HolePos holePos;
        public boolean out;
        public long sys;
        public float size;

        public Hole(final HoleManager.HolePos holePos) {
            this.holePos = holePos;
            this.out = false;
            this.sys = System.currentTimeMillis();
            this.size = 0.0f;
        }

        public void updateSize(){
            size = MathUtil.lerp(size, out ? 0.0f : 1.0f, 0.02f * EventManager.deltaTime * animationSpeed.getValue() / 100.0f);
        }

        public void render() {
            final AxisAlignedBB bb = new AxisAlignedBB(holePos.getPos());
            if (!camera.isBoundingBoxInFrustum(bb.grow(2.0))) {
                return;
            }
            if (holePos.isDouble()) {
                if (holePos.isWestDouble()) {
                    RenderUtil.drawBB(bb.minX - 1, bb.minY, bb.minZ, bb.minX * (1.0f - size) + bb.maxX * size, bb.minY + 0.05f, bb.minZ * (1.0f - size) + bb.maxZ * size);
                } else {
                    RenderUtil.drawBB(bb.minX, bb.minY, bb.minZ - 1, bb.minX * (1.0f - size) + bb.maxX * size, bb.minY + 0.05f, bb.minZ * (1.0f - size) + bb.maxZ * size);
                }
            } else {
                RenderUtil.drawBB(bb.minX, bb.minY, bb.minZ, bb.minX * (1.0f - size) + bb.maxX * size, bb.minY + 0.05f, bb.minZ * (1.0f - size) + bb.maxZ * size);
            }
        }
    }
}