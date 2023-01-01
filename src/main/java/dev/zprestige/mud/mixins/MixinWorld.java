package dev.zprestige.mud.mixins;

import dev.zprestige.mud.module.misc.FreeLook;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {

    @Inject(method = "rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;", at = @At("HEAD"), cancellable = true)
    private void rayTraceBlocks(Vec3d start, Vec3d end, CallbackInfoReturnable<RayTraceResult> cir) {
        if (FreeLook.isCancelTrace()) {
            cir.setReturnValue(null);
        }
    }
}
