package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.gui.DrawContainerEvent;
import dev.zprestige.mud.events.impl.gui.GuiContainerItemStackEvent;
import dev.zprestige.mud.events.impl.gui.GuiContainerMouseClickedEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class MixinGuiContainer {

    @Shadow
    protected int guiLeft;

    @Shadow
    protected int guiTop;

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci){
        DrawContainerEvent event = new DrawContainerEvent(mouseX, mouseY, partialTicks, guiLeft, guiTop);
        Mud.eventBus.invoke(event);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        GuiContainerMouseClickedEvent event = new GuiContainerMouseClickedEvent(mouseX, mouseY, mouseButton);
        Mud.eventBus.invoke(event);
    }

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void drawItemStack(Slot slotIn, CallbackInfo ci){
        GuiContainerItemStackEvent event = new GuiContainerItemStackEvent(slotIn.getStack(), slotIn.getSlotIndex());
        Mud.eventBus.invoke(event);
    }
}
