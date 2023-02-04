package dev.zprestige.mud.mixins;

import dev.zprestige.mud.util.MC;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase implements MC {

    @Inject(method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z", at=@At(value="HEAD"), cancellable=true)
    public void canRenderName(CallbackInfoReturnable<Boolean> info) {
        if (mc.player == null) {
            info.setReturnValue(false);
        }
    }
}