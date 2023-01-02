package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.gui.GuiBackgroundEvent;
import dev.zprestige.mud.events.impl.render.RenderToolTipEvent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {

    @Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
    private void renderToolTip(ItemStack stack, int x, int y, CallbackInfo ci) {
        RenderToolTipEvent event = new RenderToolTipEvent(stack, x, y);
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "drawDefaultBackground", at = @At("HEAD"), cancellable = true)
    private void drawBackground(CallbackInfo ci) {
        GuiBackgroundEvent event = new GuiBackgroundEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
