package dev.zprestige.mud.mixins.interfaces;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderer.class)
public interface IItemRenderer {

    @Accessor("prevEquippedProgressMainHand")
    float getPrevEquippedProgressMainHand();

    @Accessor("prevEquippedProgressOffHand")
    float getPrevEquippedProgressOffHand();

    @Accessor("equippedProgressMainHand")
    void setEquippedProgressMainHand(float equippedProgressMainHand);

    @Accessor("equippedProgressOffHand")
    void setEquippedProgressOffHand(float equippedProgressOffHand);

    @Accessor("itemStackMainHand")
    void setItemStackMainHand(ItemStack itemStackMainHand);

    @Accessor("itemStackOffHand")
    void setItemStackOffHand(ItemStack itemStackMainHand);

}
