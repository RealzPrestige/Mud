package dev.zprestige.mud.mixins;

import com.mojang.authlib.GameProfile;
import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.player.BlockPushEvent;
import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.events.impl.player.MoveEvent;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends AbstractClientPlayer {
    protected MotionUpdateEvent motionUpdateEvent;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @ParametersAreNonnullByDefault
    public void move(MoverType type, double x, double y, double z) {
        MoveEvent event = new MoveEvent(type, x, y, z);
        Mud.eventBus.invoke(event);
        super.move(type, event.getMotionX(), event.getMotionY(), event.getMotionZ());
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void move(MoverType type, double x, double y, double z, CallbackInfo ci) {
        MoveEvent event = new MoveEvent(type, x, y, z);
        Mud.eventBus.invoke(event);
        if (event.getMotionX() != x || event.getMotionY() != y || event.getMotionZ() != z) {
            super.move(type, event.getMotionX(), event.getMotionY(), event.getMotionZ());
            ci.cancel();
        }
    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;onUpdateWalkingPlayer()V", shift = At.Shift.BEFORE))
    private void onUpdate(CallbackInfo callbackInfo) {
        motionUpdateEvent = new MotionUpdateEvent(posX, getEntityBoundingBox().minY, posZ, rotationYaw, rotationPitch, onGround);
        Mud.eventBus.invoke(motionUpdateEvent);
        posX = motionUpdateEvent.getX();
        posY = motionUpdateEvent.getY();
        posZ = motionUpdateEvent.getZ();
        rotationYaw = motionUpdateEvent.getRotationYaw();
        rotationPitch = motionUpdateEvent.getRotationPitch();
        onGround = motionUpdateEvent.isOnGround();

    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;onUpdateWalkingPlayer()V", shift = At.Shift.AFTER))
    private void onUpdateWalkingPlayerPost(CallbackInfo callbackInfo) {
        if (posX == motionUpdateEvent.getX()) {
            posX = motionUpdateEvent.getPrevX();
        }
        if (posY == motionUpdateEvent.getY()) {
            posY = motionUpdateEvent.getPrevY();
        }
        if (posZ == motionUpdateEvent.getZ()) {
            posZ = motionUpdateEvent.getPrevZ();
        }
        if (rotationYaw == motionUpdateEvent.getRotationYaw()) {
            rotationYaw = motionUpdateEvent.getPrevYaw();
        }
        if (rotationPitch == motionUpdateEvent.getRotationPitch()) {
            rotationPitch = motionUpdateEvent.getPrevPitch();
        }
        if (onGround == motionUpdateEvent.isOnGround()) {
            onGround = motionUpdateEvent.isPrevOnGround();
        }
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "net/minecraft/client/entity/EntityPlayerSP.posX:D"))
    private double posX(EntityPlayerSP entityPlayerSP) {
        return motionUpdateEvent.getX();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "net/minecraft/util/math/AxisAlignedBB.minY:D"))
    private double minY(AxisAlignedBB axisAlignedBB) {
        return motionUpdateEvent.getY();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "net/minecraft/client/entity/EntityPlayerSP.posZ:D"))
    private double posZ(EntityPlayerSP entityPlayerSP) {
        return motionUpdateEvent.getZ();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "net/minecraft/client/entity/EntityPlayerSP.rotationYaw:F"))
    private float rotationYaw(EntityPlayerSP entityPlayerSP) {
        return motionUpdateEvent.getYaw();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "net/minecraft/client/entity/EntityPlayerSP.rotationPitch:F"))
    private float rotationPitch(EntityPlayerSP entityPlayerSP) {
        return motionUpdateEvent.getPitch();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "net/minecraft/client/entity/EntityPlayerSP.onGround:Z"))
    private boolean onGround(EntityPlayerSP entityPlayerSP) {
        return motionUpdateEvent.isOnGround();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At(value = "RETURN"))
    private void onUpdateWalkingPlayerReturn(CallbackInfo callbackInfo) {
        MotionUpdateEvent event = new MotionUpdateEvent(motionUpdateEvent);
        event.setCancelled(true);
        Mud.eventBus.invoke(event);
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void pushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        BlockPushEvent event = new BlockPushEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }

}