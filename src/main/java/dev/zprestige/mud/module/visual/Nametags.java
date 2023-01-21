package dev.zprestige.mud.module.visual;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.NameplateEvent;
import dev.zprestige.mud.events.impl.render.Render3DEvent;
import dev.zprestige.mud.events.impl.render.Render3DPostEvent;
import dev.zprestige.mud.events.impl.render.Render3DPreEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.shader.impl.GradientShader;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Nametags extends Module {
    private final FloatSetting scale = setting("Scale", 1.5f, 0.1f, 10.0f);

    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Render");
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Render");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Render");

    @EventListener
    public void onNameplate(NameplateEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onRender3D(Render3DPreEvent event) {
        glTranslated(0.0f, 0.0f, 0.0f);
        glRotatef(0.0f, 0.0f, 0.0f, 0.0f);
        float scaleVal = this.scale.getValue() / 1000.0f;
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer.equals(mc.player)) {
                continue;
            }
            Vec3d vec = RenderUtil.interpolateEntity(entityPlayer);
            glPushMatrix();
            glTranslated(vec.x, vec.y + entityPlayer.height * 1.25f, vec.z);
            glRotatef(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            glRotatef((mc.getRenderManager().options.thirdPersonView == 2 ? -1 : 1) * mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);

            float distance = (float) ((mc.getRenderViewEntity() == null) ? mc.player : mc.getRenderViewEntity()).getDistance(vec.x + mc.getRenderManager().viewerPosX, vec.y + mc.getRenderManager().viewerPosY, vec.z + mc.getRenderManager().viewerPosZ);
            float scale = 0.0018f + scaleVal * distance;

            glScaled(-scale, -scale, scale);
            glDisable(GL_DEPTH_TEST);

            float health = round(entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount(), 1);
            String text = entityPlayer.getName() + " " + health;

            RenderUtil.rounded(-Mud.fontManager.stringWidth(text) / 2.0f - 2.5f, -2.5f, Mud.fontManager.stringWidth(text) / 2.0f + 2.5f, 9.0f, 3.0f, new Color(0, 0, 0, 50));

            GradientShader.setup(step.getValue(), speed.getValue(), color1.getValue(), color2.getValue());
            RenderUtil.roundedOutlineTex(-Mud.fontManager.stringWidth(text) / 2.0f - 2.5f, -2.5f, Mud.fontManager.stringWidth(text) / 2.0f + 2.5f, 9.0f, 3.0f, Color.WHITE);
            GradientShader.finish();

            Mud.fontManager.string(entityPlayer.getName(), -Mud.fontManager.stringWidth(text) / 2.0f, 0, Color.WHITE);
            Mud.fontManager.string(" " + health, (-Mud.fontManager.stringWidth(text) / 2.0f) + Mud.fontManager.stringWidth(entityPlayer.getName()), 0, healthColor(health));

            ArrayList<ItemStack> stacks = new ArrayList<>();
            stacks.add(entityPlayer.getHeldItemMainhand());
            stacks.addAll(entityPlayer.inventory.armorInventory);
            stacks.add(entityPlayer.getHeldItemOffhand());
            float i = -48.0f;
            for (ItemStack itemStack : stacks) {
                glPushMatrix();
                glClear(256);
                RenderHelper.enableStandardItemLighting();
                glEnable(GL_DEPTH_TEST);
                mc.getRenderItem().zLevel = -150.0f;
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) i, -20);
                mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, (int) i, -20);
                mc.getRenderItem().zLevel = 0.0f;
                RenderHelper.disableStandardItemLighting();
                glDisable(GL_DEPTH_TEST);
                glPopMatrix();
                i += 16.0f;
            }

            glEnable(GL_BLEND);
            glEnable(GL_DEPTH_TEST);
            glPopMatrix();
        }
    }


    public Color healthColor(float health) {
        float g = (health * 7.083333333333333f) / 255.0f;
        float r = ((36 - health) * 7.083333333333333f) / 255.0f;
        return new Color(r, g, 0, 1.0f);
    }

    public static float round(float value, int places) {
        float scale = (float) Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

}
