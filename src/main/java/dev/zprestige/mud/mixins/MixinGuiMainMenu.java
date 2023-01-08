package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu {

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        Mud.altManagerScreen.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci){
        Mud.altManagerScreen.setClicked(mouseButton);
        if (Mud.altManagerScreen.rendering) {
            ci.cancel();
        }
    }

    @Inject(method = "keyTyped", at = @At("HEAD"))
    private void keyTyped(char typedChar, int keyCode, CallbackInfo ci){
        Mud.altManagerScreen.keyTyped(typedChar, keyCode);
    }
}
