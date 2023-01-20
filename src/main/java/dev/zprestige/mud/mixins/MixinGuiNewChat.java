package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.render.RenderChatEvent;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat {

    @Inject(method = "drawChat", at = @At("HEAD"), cancellable = true)
    private void drawScreen(int updateCounter, CallbackInfo ci) {
        RenderChatEvent event = new RenderChatEvent(updateCounter);
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
