package dev.zprestige.mud.module.combat;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.events.impl.render.Render3DPreEvent;
import dev.zprestige.mud.manager.EventManager;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.ModeSetting;
import dev.zprestige.mud.util.impl.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Arrays;

import static dev.zprestige.mud.util.impl.RenderUtil.glColor;
import static org.lwjgl.opengl.GL11.*;

public class Aura extends Module {
    private final FloatSetting range = setting("Range", 5.0f, 0.1f, 6.0f);
    private final FloatSetting wallRange = setting("Wall Range", 4.5f, 0.1f, 6.0f);
    private final ModeSetting weapon = setting("Weapon", "Require", Arrays.asList("Require", "Swap", "Ignore"));
    private final ModeSetting rotations = setting("Rotations", "None", Arrays.asList("None", "Always", "Hit"));
    private final BooleanSetting delay = setting("Delay", true);
    private final BooleanSetting disable = setting("Disable", false);

    private final BooleanSetting strictTrace = setting("Strict Trace", false).invokeTab("Server");
    private final BooleanSetting tpsSync = setting("TPS Sync", false).invokeTab("Server");

    private final BooleanSetting render = setting("Render", false).invokeTab("Render");
    private final FloatSetting speed = setting("Speed", 1.0f, 0.5f, 1.5f).invokeVisibility(z -> render.getValue()).invokeTab("Render");
    private final ColorSetting color = setting("Color", new Color(113, 93, 214)).invokeVisibility(z -> render.getValue()).invokeTab("Render");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeVisibility(z -> render.getValue()).invokeTab("Render");

    private EntityPlayer entityPlayer;
    private long sys;
    private float i;

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (mc.player == null) {
            return;
        }
        switch (weapon.getValue()) {
            case "Require":
                if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) {
                    this.entityPlayer = null;
                    return;
                }
                break;
            case "Swap":
                if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) {
                    int sword = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
                    if (sword != -1) {
                        InventoryUtil.switchToSlot(sword);
                    }
                }
                break;
            case "Ignore":
                break;
        }
        EntityPlayer entityPlayer = EntityUtil.getEntityPlayer(6.0f);
        this.entityPlayer = entityPlayer;
        if (entityPlayer != null) {
            boolean raytrace = RaytraceUtil.raytrace(entityPlayer);
            float distance = raytrace ? range.getValue() : (strictTrace.getValue() ? 0.0f : wallRange.getValue());
            if (mc.player.getDistance(entityPlayer) > distance) {
                return;
            }
            if (rotations.getValue().equals("Always")) {
                RotationUtil.faceEntity(entityPlayer, event);
            }
            float multiplier = mc.player.getCooledAttackStrength(20 - Mud.tpsManager.getTPS());
            if (System.currentTimeMillis() - sys > (delay.getValue() ? (650 * (tpsSync.getValue() ? multiplier : 1.0f)) : 50)) {
                if (rotations.getValue().equals("Hit")) {
                    RotationUtil.faceEntity(entityPlayer, event);
                }
                PacketUtil.invoke(new CPacketUseEntity(entityPlayer));
                mc.player.swingArm(EnumHand.MAIN_HAND);
                sys = System.currentTimeMillis();
            }
        } else if (disable.getValue()) {
            toggle();
        }
    }

    @EventListener
    public void onRender3DPre(Render3DPreEvent event) {
        if (entityPlayer == null) {
            return;
        }
        if (render.getValue()) {
            render(entityPlayer);
        }
    }

    private void render(EntityPlayer entityPlayer) {
        Vec3d vec = RenderUtil.interpolateEntity(entityPlayer);
        final float sin = ((float) Math.sin(i / 25.0f) / 2.0f);
        final float sin2 = ((float) Math.sin(i / 25.0f + 0.5) / 2.0f);
        i = i + 10 * speed.getValue() * EventManager.getDeltaTime();
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glShadeModel(GL_SMOOTH);
        glDisable(GL_CULL_FACE);
        glBegin(GL_QUAD_STRIP);

        for (int i = 0; i <= 360; i++) {
            double x = ((Math.cos(i * Math.PI / 180F) * entityPlayer.width) + vec.x);
            double y = (vec.y + (entityPlayer.height / 2.0f)) + 0.1f;
            double z = ((Math.sin(i * Math.PI / 180F) * entityPlayer.width) + vec.z);
            glColor(color.getValue());
            glVertex3d(x, y + (sin2 * entityPlayer.height), z);
            glColor(new Color(color2.getValue().getRed(), color2.getValue().getGreen(), color2.getValue().getBlue(), 0));
            glVertex3d(x, y + (sin * entityPlayer.height), z);
        }

        glEnd();
        glEnable(GL_CULL_FACE);
        glShadeModel(GL_FLAT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }
}
