package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.render.RotateArmEvent;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

    @Inject(method = "rotateArm", at = @At("HEAD"), cancellable = true)
    private void rotateArm(float p_187458_1_, CallbackInfo ci){
        RotateArmEvent event = new RotateArmEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()){
            ci.cancel();
        }
    }
}
