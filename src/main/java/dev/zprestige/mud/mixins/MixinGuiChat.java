package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.chat.GuiChatTypeEvent;
import dev.zprestige.mud.events.impl.render.RenderTextBoxEvent;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
public class MixinGuiChat {

    @Shadow
    protected GuiTextField inputField;

    @Inject(method = "keyTyped", at = @At("RETURN"))
    private void keyTyped(char typedChar, int keyCode, CallbackInfo ci){
        GuiChatTypeEvent event = new GuiChatTypeEvent(inputField.getText());
        Mud.eventBus.invoke(event);
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci){
        RenderTextBoxEvent event = new RenderTextBoxEvent();
        Mud.eventBus.invoke(event);
        if (event.isCancelled()){
            ci.cancel();
        }
    }
}
