package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.render.InterpolateEvent;
import dev.zprestige.mud.util.MC;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(RenderManager.class)
public class MixinRenderManager implements MC {
    private Entity entityIn;

    @Inject(method = "renderEntityStatic", at = @At("HEAD"))
    private void renderEntityStatic(Entity entityIn, float partialTicks, boolean p_188388_3_, CallbackInfo ci){
        this.entityIn = entityIn;
    }

    @ModifyArgs(method = "renderEntityStatic", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)V"))
    private void renderEntityStatic(Args args){
        if (entityIn == null){
            return;
        }
        InterpolateEvent event = new InterpolateEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            args.set(1, entityIn.posX - mc.getRenderManager().viewerPosX);
            args.set(2, entityIn.posY - mc.getRenderManager().viewerPosY);
            args.set(3, entityIn.posZ - mc.getRenderManager().viewerPosZ);
        }
    }
}
