package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.player.CollideEvent;
import dev.zprestige.mud.events.impl.player.TurnEvent;
import dev.zprestige.mud.util.MC;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity implements MC {


    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", shift = At.Shift.BEFORE, ordinal = 0))
    public void move(MoverType type, double x, double y, double z, CallbackInfo ci) {
        if (this.equals(mc.player)) {
            CollideEvent event = new CollideEvent(getEntityBoundingBox());
            Mud.eventBus.invoke(event);
        }
    }

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void onTurn(float yaw, float pitch, CallbackInfo ci) {
        TurnEvent event = new TurnEvent(yaw, pitch);
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
