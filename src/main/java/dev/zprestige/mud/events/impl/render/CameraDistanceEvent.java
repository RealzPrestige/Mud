package dev.zprestige.mud.events.impl.render;

import dev.zprestige.mud.events.bus.Event;

public class CameraDistanceEvent extends Event {
    private float distance;

    public CameraDistanceEvent(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
