package dev.zprestige.mud.mixins;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.system.PacketReceiveEvent;
import dev.zprestige.mud.events.impl.system.PacketSendEvent;
import dev.zprestige.mud.util.impl.PacketUtil;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void sendPacket(Packet<?> packet, CallbackInfo ci) {
        if (PacketUtil.noEvent) {
            return;
        }
        PacketSendEvent event = new PacketSendEvent(packet);
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "channelRead0*", at = @At("HEAD"), cancellable = true)
    private void receivePacket(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        PacketReceiveEvent event = new PacketReceiveEvent(packet);
        Mud.eventBus.invoke(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
