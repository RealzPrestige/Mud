package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.render.ArmorEvent;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LayerBipedArmor.class)
public class MixinRenderArmor {

    @Redirect(method = "setModelSlotVisible(Lnet/minecraft/client/model/ModelBiped;Lnet/minecraft/inventory/EntityEquipmentSlot;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelRenderer;showModel:Z"))
    private void showModel(ModelRenderer instance, boolean value) {
        final ArmorEvent armorEvent = new ArmorEvent();
        Mud.eventBus.invoke(armorEvent);
        instance.showModel = !armorEvent.isCancelled();
    }
}
