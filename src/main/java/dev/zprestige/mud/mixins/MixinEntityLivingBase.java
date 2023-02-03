package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.world.LastDamageUpdateEvent;
import dev.zprestige.mud.util.MC;
import dev.zprestige.mud.util.impl.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity implements MC {

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }


    @Inject(method = "attackEntityFrom", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;lastDamage:F", ordinal = 2))
    private void attackEntityFrom2(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LastDamageUpdateEvent event = new LastDamageUpdateEvent(this, amount);
        Mud.eventBus.invoke(event);
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;lastDamage:F", ordinal = 3))
    private void attackEntityFrom3(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LastDamageUpdateEvent event = new LastDamageUpdateEvent(this, amount);
        Mud.eventBus.invoke(event);
    }
}

