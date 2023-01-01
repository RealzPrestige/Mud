package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.player.BreakBlockEvent;
import dev.zprestige.mud.events.impl.player.ClickBlockEvent;
import dev.zprestige.mud.events.impl.player.DamageBlockEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Inject(method = "clickBlock", at = @At("HEAD"))
    private void clickBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> cir) {
        ClickBlockEvent event = new ClickBlockEvent();
        Mud.eventBus.invoke(event);
    }

    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"), cancellable = true)
    private void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> cir) {
        DamageBlockEvent event = new DamageBlockEvent(posBlock, directionFacing);
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }
}