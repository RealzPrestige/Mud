package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.player.DoubleInteractEvent;
import dev.zprestige.mud.events.impl.system.KeyEvent;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.util.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft implements MC {

    @Inject(method = "dispatchKeypresses", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal = 0))
    private void dispatchKeypresses(CallbackInfo ci) {
        int key = Keyboard.getEventKey() == Keyboard.KEY_NONE ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
        if (Keyboard.getEventKeyState()) {
            KeyEvent event = new KeyEvent(key);
            Mud.eventBus.invoke(event);
        }
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    private void runTick(CallbackInfo ci) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        TickEvent event = new TickEvent();
        Mud.eventBus.invoke(event);
    }

    @Inject(method = "getLimitFramerate", at = @At("RETURN"), cancellable = true)
    public void getLimitFramerate(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(mc.gameSettings.limitFramerate);
    }

    @Redirect(method = "sendClickBlockToController", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
    public boolean handActiveRedirect(EntityPlayerSP entityPlayerSP) {
        DoubleInteractEvent event = new DoubleInteractEvent();
        return event.isCancelled();
    }
}
