package dev.zprestige.mud.mixins;

import dev.zprestige.mud.util.MC;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractClientPlayer.class)
public class MixinAbstractClientPlayer implements MC {

    @Inject(method = "isSpectator", at = @At("HEAD"), cancellable = true)
    private void  isSpectator(CallbackInfoReturnable<Boolean> cir){
        if (mc.getConnection() == null){
            cir.setReturnValue(false);
        }
    }

}
