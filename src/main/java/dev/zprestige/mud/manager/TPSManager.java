package dev.zprestige.mud.manager;


import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.system.PacketReceiveEvent;
import dev.zprestige.mud.util.MC;
import dev.zprestige.mud.util.impl.MathUtil;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;

public class TPSManager implements MC {
    private final float[] TPS = new float[20];
    private long prevTime;
    private int currentTick;

    public TPSManager() {
        prevTime = -1;
        for (int i = 0, len = TPS.length; i < len; i++) {
            TPS[i] = 0;
        }
        Mud.eventBus.registerListener(this);
    }

    public float getTPS() {
        int tickCount = 0;
        float tickRate = 0;
        for (float tick : TPS) {
            if (tick > 0) {
                tickRate += tick;
                tickCount++;
            }
        }

        return mc.isSingleplayer() ? 20.0f : MathUtil.roundNumber(MathHelper.clamp((tickRate / tickCount), 0, 20), 2);
    }

    @EventListener
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            if (prevTime != -1) {
                TPS[currentTick % TPS.length] = MathHelper.clamp((20 / ((System.currentTimeMillis() - prevTime) / 1000.0f)), 0, 20);
                currentTick++;
            }
            prevTime = System.currentTimeMillis();
        }
    }

}

