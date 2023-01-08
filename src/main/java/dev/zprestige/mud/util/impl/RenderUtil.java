package dev.zprestige.mud.util.impl;

import dev.zprestige.mud.util.MC;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil implements MC {
    private static final ResourceLocation blank = new ResourceLocation("textures/blank.png");

    public static void drawEntityOnScreen(float x, float y, float scale, float yaw, float pitch, EntityLivingBase player) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        GlStateManager.translate(x, y, 50.0F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        player.renderYawOffset = yaw;
        player.rotationYaw = yaw;
        player.rotationPitch = pitch;
        player.rotationYawHead = yaw;
        player.prevRotationYawHead = yaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static double interpolateLastTickPos(double pos, double lastPos) {
        return lastPos + (pos - lastPos) * mc.getRenderPartialTicks();
    }

    public static Vec3d interpolateEntity(Entity entity) {
        double x;
        double y;
        double z;
        x = interpolateLastTickPos(entity.posX, entity.lastTickPosX) - mc.getRenderManager().viewerPosX;
        y = interpolateLastTickPos(entity.posY, entity.lastTickPosY) - mc.getRenderManager().viewerPosY;
        z = interpolateLastTickPos(entity.posZ, entity.lastTickPosZ) - mc.getRenderManager().viewerPosZ;
        return new Vec3d(x, y, z);
    }


    public static void drawExpand(float x, float y) {
        y += 3.0f;
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor(Color.WHITE);
        glBegin(GL_LINE_STRIP);

        glVertex2f(x + 2.5f, y);
        glVertex2f(x + 5.0f, y + 2.5f);
        glVertex2f(x + 7.5f, y);

        glEnd();
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    public static Vec3d renderOffset() {
        return new Vec3d(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
    }


    public static void renderOutline(AxisAlignedBB bb, Color color, float lineWidth) {
        bb = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ).offset(renderOffset());
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glLineWidth(lineWidth);
        glColor(color);
        glBegin(GL_LINE_STRIP);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glEnd();
        glColor(Color.WHITE);
        glLineWidth(1.0f);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void renderBox(AxisAlignedBB bb, Color color) {
        bb = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ).offset(renderOffset());
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        RenderGlobal.renderFilledBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void drawBB(AxisAlignedBB bb) {
        drawBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }

    public static void drawBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).offset(renderOffset());
        bindBlank();
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor(Color.WHITE);
        glBegin(GL_TRIANGLE_STRIP);

        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

        glEnd();
        glColor(Color.WHITE);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void drawBB(AxisAlignedBB bb, Color color) {
        bb = bb.offset(renderOffset());
        bindBlank();
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor(color);
        glBegin(GL_TRIANGLE_STRIP);

        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

        glEnd();
        glColor(Color.WHITE);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void gradient(float x, float y, float width, float height, Color topLeft, Color topRight, Color bottomLeft, Color bottomRight) {
        setupDefault(Color.WHITE);
        glShadeModel(GL_SMOOTH);
        glBegin(GL_QUADS);

        glColor(topLeft);
        glVertex2f(x, y);
        glColor(bottomLeft);
        glVertex2f(x, height);
        glColor(bottomRight);
        glVertex2f(width, height);
        glColor(topRight);
        glVertex2f(width, y);

        glEnd();
        glShadeModel(GL_FLAT);
        glColor(Color.WHITE);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void picker(float x, float y, float width, float height, Color color) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);
        glBegin(GL_POLYGON);

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glVertex2f(x, y);
        glVertex2f(x, height);

        glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
        glVertex2f(width, height);
        glVertex2f(width, y);

        glEnd();
        glDisable(GL_ALPHA_TEST);
        glBegin(GL_POLYGON);

        glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        glVertex2f(x, y);

        glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        glVertex2f(x, height);
        glVertex2f(width, height);

        glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        glVertex2f(width, y);

        glEnd();
        glEnable(GL_ALPHA_TEST);

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glShadeModel(GL_FLAT);
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    public static void circle(float x, float y, final float radius, Color color) {
        final double pi = Math.PI;
        x = x - radius / 2.0f;
        y = y - radius / 2.0f;

        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);
        glColor(color);
        glDisable(GL_CULL_FACE);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i < 360; i++) {
            glVertex2d(x + radius + Math.sin(i * pi / 180.0) * radius * -1.0, y + radius + Math.cos(i * pi / 180.0) * radius * -1.0);
        }

        glEnd();
        glEnable(GL_CULL_FACE);
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    public static void invokeScale(float scale) {
        glPushMatrix();
        glScalef(scale, scale, scale);
    }

    public static void resetScale() {
        glPopMatrix();
    }

    public static void prepareScissor(float x, float y, float width, float height) {
        scissor(x, y, width, height);
        glEnable(GL_SCISSOR_TEST);
    }

    public static void scissor(float x, float y, float width, float height) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        glScissor((int) (x * scaledResolution.getScaleFactor()), (int) ((scaledResolution.getScaledHeight() - height) * scaledResolution.getScaleFactor()), (int) ((width - x) * scaledResolution.getScaleFactor()), (int) ((height - y) * scaledResolution.getScaleFactor()));
    }

    public static void releaseScissor() {
        glDisable(GL_SCISSOR_TEST);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }

    public static void setAlphaLimit(float limit) {
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static void rounded(float x, float y, float width, float height, float radius, Color color) {
        setupDefault(color);
        glBegin(GL_TRIANGLE_FAN);
        for (int i = 1; i <= 4; i++) {
            corner(x, y, width, height, radius, i);
        }
        releaseDefault();
    }

    public static void roundedOutline(float x, float y, float width, float height, float radius, Color color) {
        setupDefault(color);
        glLineWidth(1.0f);
        glBegin(GL_LINE_STRIP);
        for (int i = 1; i <= 4; i++) {
            corner(x, y, width, height, radius, i);
        }
        glVertex2d(
                x + radius + Math.sin(0 * Math.PI / 180.0f) * radius * -1.0f,
                y + radius + Math.cos(0 * Math.PI / 180.0f) * radius * -1.0f
        );
        releaseDefault();
    }

    public static void roundedOutlineTex(float x, float y, float width, float height, float radius, Color color) {
        roundedOutlineTex(x, y, width, height, radius, color, 1.0f);
    }

    public static void roundedOutlineTex(float x, float y, float width, float height, float radius, Color color, float lineWidth) {
        bindBlank();
        setupDefault(color);
        glLineWidth(lineWidth);
        glBegin(GL_LINE_STRIP);
        for (int i = 1; i <= 4; i++) {
            glTexCoord2f(0.0f, 0.0f);
            corner(x, y, width, height, radius, i);
        }
        glVertex2d(
                x + radius + Math.sin(0 * Math.PI / 180.0f) * radius * -1.0f,
                y + radius + Math.cos(0 * Math.PI / 180.0f) * radius * -1.0f
        );
        releaseDefault();
    }

    public static void corner(float x, float y, float width, float height, float radius, int corner) {
        double pi = Math.PI;
        int i = 0;
        switch (corner) {
            case 1:
                // Top left
                while (i < 90) {
                    glVertex2d(x + radius + Math.sin(i * pi / 180.0f) * radius * -1.0f, y + radius + Math.cos(i * pi / 180.0) * radius * -1.0f);
                    i++;
                }
                break;
            case 2:
                // Bottom Left
                i = 90;
                while (i < 180) {
                    glVertex2d(
                            x + radius + Math.sin(i * pi / 180.0f) * radius * -1.0f,
                            height - radius + Math.cos(i * pi / 180.0f) * radius * -1.0f
                    );
                    i++;
                }
                break;
            case 3:
                // Bottom Right
                while (i < 90) {
                    glVertex2d(
                            width - radius + Math.sin(i * pi / 180.0) * radius,
                            height - radius + Math.cos(i * pi / 180.0) * radius
                    );
                    i++;
                }
                break;
            case 4:
                // Top Right
                i = 90;
                while (i < 180) {
                    glVertex2d(
                            width - radius + Math.sin(i * pi / 180.0f) * radius,
                            y + radius + Math.cos(i * pi / 180.0f) * radius
                    );
                    i++;
                }
                break;
        }
    }


    public static void setupDefault(Color color) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor(color);
    }

    public static void releaseDefault() {
        glEnd();
        glColor(Color.WHITE);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void texture(float x, float y, float width, float height, Color color, ResourceLocation resourceLocation) {
        bind(resourceLocation);
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor(color);
        glBegin(GL_QUADS);

        glTexCoord2f(0.0f, 0.0f);
        glVertex2f(x, y);
        glTexCoord2f(0.0f, 1.0f);
        glVertex2f(x, height);
        glTexCoord2f(1.0f, 1.0f);
        glVertex2f(width, height);
        glTexCoord2f(1.0f, 0.0f);
        glVertex2f(width, y);

        glEnd();
        glColor(Color.WHITE);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void rect(float x, float y, float width, float height, Color color) {
        setupDefault(color);
        glBegin(GL_QUADS);

        glVertex2f(x, y);
        glVertex2f(x, height);
        glVertex2f(width, height);
        glVertex2f(width, y);

        releaseDefault();
    }

    public static void bind(final ResourceLocation resourceLocation) {
        mc.getTextureManager().bindTexture(resourceLocation);
    }

    public static void bindBlank() {
        bind(blank);
    }

    public static ResourceLocation blank() {
        return blank;
    }


    public static void glColor(final Color color) {
        glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }
}
