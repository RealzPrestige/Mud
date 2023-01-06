package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.render.InterpolateEvent;
import dev.zprestige.mud.util.MC;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderManager.class)
public class MixinRenderManager implements MC {
    private Entity entityIn;

    @Inject(method = "renderEntityStatic", at = @At("HEAD"))
    private void renderEntityStatic(Entity entityIn, float partialTicks, boolean p_188388_3_, CallbackInfo ci){
        this.entityIn = entityIn;
    }

    // ModifyArgs creates indexoutofbounds???

    @ModifyArg(method = "renderEntityStatic", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)V"), index = 1)
    private double renderEntityStaticX(double x){
        if (entityIn == null || mc.world == null){
            return x;
        }
        InterpolateEvent event = new InterpolateEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            return entityIn.posX - mc.getRenderManager().viewerPosX;
        }
        return x;
    }


    @ModifyArg(method = "renderEntityStatic", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)V"), index = 2)
    private double renderEntityStaticY(double y){
        if (entityIn == null || mc.world == null){
            return y;
        }
        InterpolateEvent event = new InterpolateEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            return entityIn.posY - mc.getRenderManager().viewerPosY;
        }
        return y;
    }

    @ModifyArg(method = "renderEntityStatic", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)V"), index = 3)
    private double renderEntityStaticZ(double z){
        if (entityIn == null || mc.world == null){
            return z;
        }
        InterpolateEvent event = new InterpolateEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            return entityIn.posZ - mc.getRenderManager().viewerPosZ;
        }
        return z;
    }
}
