package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.world.UpdateAnimationEvent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureAtlasSprite.class)
public class MixinTextureAtlasSprite {

    @Inject(method = "updateAnimation", at = @At("HEAD"), cancellable = true)
    private void updateAnimation(CallbackInfo ci){
        UpdateAnimationEvent event = new UpdateAnimationEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
