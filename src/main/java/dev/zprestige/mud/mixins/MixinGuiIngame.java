package dev.zprestige.mud.mixins;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    private void renderPotionEffects(final ScaledResolution resolution, final CallbackInfo ci) {
        ci.cancel();
    }
}
