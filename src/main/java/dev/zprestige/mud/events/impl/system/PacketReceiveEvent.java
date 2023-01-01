package dev.zprestige.mud.events.impl.system;

import dev.zprestige.mud.events.bus.Event;
import net.minecraft.network.Packet;

public class PacketReceiveEvent extends Event {
    private final Packet<?> packet;

    public PacketReceiveEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}
