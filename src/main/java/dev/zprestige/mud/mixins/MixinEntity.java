package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.player.TurnEvent;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    public void onTurn(float yaw, float pitch, CallbackInfo ci) {
        TurnEvent event = new TurnEvent(yaw, pitch);
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
