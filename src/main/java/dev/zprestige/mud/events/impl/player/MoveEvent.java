package dev.zprestige.mud.events.impl.player;

import dev.zprestige.mud.events.bus.Event;
import net.minecraft.entity.MoverType;

public class MoveEvent extends Event {
    private double motionX, motionY, motionZ;
    private final MoverType type;

    public MoveEvent(MoverType type, double x, double y, double z) {
        this.type = type;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    public void setMotion(double x, double y, double z) {
        motionX = x;
        motionY = y;
        motionZ = z;
    }

    public double getMotionX() {
        return motionX;
    }

    public double getMotionY() {
        return motionY;
    }

    public double getMotionZ() {
        return motionZ;
    }

    public MoverType getType() {
        return type;
    }

    public void setMotionX(double motionX) {
        this.motionX = motionX;
    }

    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    public void setMotionZ(double motionZ) {
        this.motionZ = motionZ;
    }
}