package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.world.WebExplosionEvent;
import dev.zprestige.mud.module.misc.FreeLook;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld {

    @Shadow
    public abstract boolean isOutsideBuildHeight(BlockPos pos);

    @Shadow
    public abstract Chunk getChunk(BlockPos pos);


    @Inject(method = "rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;", at = @At("HEAD"), cancellable = true)
    private void rayTraceBlocks(Vec3d start, Vec3d end, CallbackInfoReturnable<RayTraceResult> cir) {
        if (FreeLook.isCancelTrace()) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true)
    private void getBlockState(BlockPos pos, CallbackInfoReturnable<IBlockState> cir) {
        if (isOutsideBuildHeight(pos)) {
            cir.setReturnValue(Blocks.AIR.getDefaultState());
        } else {
            Chunk chunk = getChunk(pos);
            IBlockState blockState = chunk.getBlockState(pos);
            if (blockState.getBlock().equals(Blocks.WEB)) {
                WebExplosionEvent event = new WebExplosionEvent();
                Mud.eventBus.invoke(event);
                if (event.isCancelled()) {
                    cir.setReturnValue(Blocks.AIR.getDefaultState());
                }
            } else {
                cir.setReturnValue(blockState);
            }
        }
    }
}
