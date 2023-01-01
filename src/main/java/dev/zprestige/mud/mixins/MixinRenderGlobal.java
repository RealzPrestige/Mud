package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.world.RenderSkyEvent;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {

    @Inject(method = "renderSky(FI)V", at = @At("HEAD"), cancellable = true)
    private void renderSky(float partialTicks, int pass, CallbackInfo ci){
        RenderSkyEvent event = new RenderSkyEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()){
            ci.cancel();
        }
    }
}
