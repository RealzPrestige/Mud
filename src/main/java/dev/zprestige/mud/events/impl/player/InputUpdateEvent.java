package dev.zprestige.mud.events.impl.player;

import dev.zprestige.mud.events.bus.Event;
import net.minecraft.util.MovementInput;

public class InputUpdateEvent extends Event {
    private final MovementInput movementInput;

    public InputUpdateEvent(MovementInput movementInput) {
        this.movementInput = movementInput;
    }

    public MovementInput getMovementInput() {
        return movementInput;
    }
}