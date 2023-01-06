package dev.zprestige.mud.events.impl.world;

import dev.zprestige.mud.events.bus.Event;

public class FogEvent extends Event {
    private float red, green, blue;

    public FogEvent(float red, float green, float blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public void setColor(float red, float green, float blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
}
