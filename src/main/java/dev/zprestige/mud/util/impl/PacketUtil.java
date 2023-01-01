package dev.zprestige.mud.util.impl;

import dev.zprestige.mud.util.MC;
import net.minecraft.network.Packet;

public class PacketUtil implements MC {
    public static boolean noEvent;

    public static void invoke(Packet<?> packet) {
        if (mc.getConnection() != null) {
            mc.getConnection().getNetworkManager().channel().writeAndFlush(packet);
        }
    }

    public static void invokeNoEvent(Packet<?> packet) {
        if (mc.getConnection() != null) {
            noEvent = true;
            mc.getConnection().getNetworkManager().channel().writeAndFlush(packet);
            noEvent = false;
        }
    }


}
