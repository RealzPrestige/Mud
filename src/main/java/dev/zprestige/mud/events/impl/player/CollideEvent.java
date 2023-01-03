package dev.zprestige.mud.events.impl.player;

import dev.zprestige.mud.events.bus.Event;
import net.minecraft.util.math.AxisAlignedBB;

public class CollideEvent extends Event {
    private final AxisAlignedBB bb;

    public CollideEvent(AxisAlignedBB bb){
        this.bb = bb;
    }

    public AxisAlignedBB getBb() {
        return bb;
    }
}
