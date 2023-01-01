package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.render.CameraDistanceEvent;
import dev.zprestige.mud.events.impl.render.HurtCamEvent;
import dev.zprestige.mud.events.impl.render.Render3DPostEvent;
import dev.zprestige.mud.events.impl.world.RenderBlockOutlineEvent;
import dev.zprestige.mud.events.impl.world.RenderCloudsEvent;
import dev.zprestige.mud.events.impl.world.RenderWeatherEvent;
import dev.zprestige.mud.events.impl.world.UpdateLightMapEvent;
import dev.zprestige.mud.module.misc.FreeLook;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @ModifyArg(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 1), index = 2)
    private float modifyDistanceOne(float x){
        return handleDistance(x);
    }

    @ModifyArg(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 2), index = 2)
    private float modifyDistanceTwo(float x){
        return handleDistance(x);
    }

    private float handleDistance(float distance){
        CameraDistanceEvent event = new CameraDistanceEvent(distance);
        Mud.eventBus.invoke(event);
        return event.getDistance() ;
    }

    @Inject(method = "orientCamera", at = @At(value = "HEAD"))
    private void orientCameraPre(float partialTicks, CallbackInfo ci) {
        FreeLook.setCancelTrace(true);
    }

    @Inject(method = "orientCamera", at = @At(value = "RETURN"))
    private void orientCameraPost(float partialTicks, CallbackInfo ci) {
        FreeLook.setCancelTrace(false);
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V"))
    private void endSection(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        Render3DPostEvent event = new Render3DPostEvent(partialTicks);
        Mud.eventBus.invoke(event);
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    private void hurtCamEffect(float partialTicks, CallbackInfo ci) {
        final HurtCamEvent hurtCamEvent = new HurtCamEvent();
        Mud.eventBus.invoke(hurtCamEvent);
        if (hurtCamEvent.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderCloudsCheck", at = @At("HEAD"), cancellable = true)
    private void renderCloudsCheck(RenderGlobal renderGlobalIn, float partialTicks, int pass, double x, double y, double z, CallbackInfo ci) {
        RenderCloudsEvent event = new RenderCloudsEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderRainSnow", at = @At("HEAD"), cancellable = true)
    private void renderRainSnow(float partialTicks, CallbackInfo ci) {
        RenderWeatherEvent event = new RenderWeatherEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "addRainParticles", at = @At("HEAD"), cancellable = true)
    private void addRainParticles(CallbackInfo ci) {
        RenderWeatherEvent event = new RenderWeatherEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "updateLightmap", at = @At("HEAD"), cancellable = true)
    private void updateLightMap(float partialTicks, CallbackInfo ci) {
        UpdateLightMapEvent event = new UpdateLightMapEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "isDrawBlockOutline", at = @At("HEAD"), cancellable = true)
    private void isDrawBlockOutline(CallbackInfoReturnable<Boolean> cir) {
        RenderBlockOutlineEvent event = new RenderBlockOutlineEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }

}
