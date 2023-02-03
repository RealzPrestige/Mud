package dev.zprestige.mud.mixins;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketUseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer {

    @Inject(method = "processUseEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetHandlerPlayServer;disconnect(Lnet/minecraft/util/text/ITextComponent;)V"), cancellable = true)
    private void processUseEntity(CPacketUseEntity packetIn, CallbackInfo ci){
        ci.cancel();
    }
}
