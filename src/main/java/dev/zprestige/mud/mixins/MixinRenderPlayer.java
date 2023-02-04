package dev.zprestige.mud.mixins;


import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.render.RenderRotationsEvent;
import dev.zprestige.mud.util.MC;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer implements MC {
    private float renderPitch, renderHeadYaw, prevRenderHeadYaw, lastRenderHeadYaw, prevRenderPitch, lastRenderPitch;

    @Inject(method = "doRender*", at = @At("HEAD"))
    public void doRenderPre(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (entity.equals(mc.player) && mc.currentScreen == null) {
            RenderRotationsEvent event = new RenderRotationsEvent();
            Mud.eventBus.invoke(event);
            if (event.isCancelled()) {
                prevRenderHeadYaw = entity.prevRotationYawHead;
                prevRenderPitch = entity.prevRotationPitch;
                renderPitch = entity.rotationPitch;
                renderHeadYaw = entity.rotationYawHead;

                entity.rotationPitch = event.getPitch();
                entity.prevRotationPitch = lastRenderPitch;
                entity.rotationYawHead = event.getYaw();
                entity.prevRotationYaw = lastRenderHeadYaw;
                entity.prevRotationYawHead = lastRenderHeadYaw;
            }
        }
    }

    @Inject(method = "doRender*", at = @At("RETURN"))
    public void rotateEnd(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (entity.equals(mc.player) && mc.currentScreen == null) {
            RenderRotationsEvent event = new RenderRotationsEvent();
            Mud.eventBus.invoke(event);
            if (event.isCancelled()) {
                if (partialTicks > 0.9f){
                    lastRenderHeadYaw = entity.rotationYawHead;
                    lastRenderPitch = entity.rotationPitch;
                }
                entity.rotationPitch = renderPitch;
                entity.rotationYawHead = renderHeadYaw;
                entity.prevRotationYaw = prevRenderHeadYaw;
                entity.prevRotationYawHead = prevRenderHeadYaw;
                entity.prevRotationPitch = prevRenderPitch;
            }
        }
    }
}