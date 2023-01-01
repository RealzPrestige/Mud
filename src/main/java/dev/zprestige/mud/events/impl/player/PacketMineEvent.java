package dev.zprestige.mud.events.impl.player;

import dev.zprestige.mud.events.bus.Event;
import net.minecraft.util.math.BlockPos;

public class PacketMineEvent extends Event {
    private final BlockPos pos;

    public PacketMineEvent(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }
}
