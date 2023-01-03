package dev.zprestige.mud.module.movement;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.CollideEvent;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.mixins.interfaces.IMinecraft;
import dev.zprestige.mud.mixins.interfaces.ITimer;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.setting.impl.ModeSetting;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.Arrays;

public class Step extends Module {
    private final ModeSetting mode = setting("Mode", "Vanilla", Arrays.asList("Vanilla", "Constantiam"));
    private final IntSetting height = setting("Height", 1, 1, 2).invokeVisibility(z -> mode.getValue().equals("Vanilla"));
    private final BooleanSetting timer = setting("Timer", false);
    private final FloatSetting amount = setting("Amount", 1.0f, 0.1f, 1.0f).invokeVisibility(z -> timer.getValue());

    private final float[] single = new float[]{0.42f, 0.753f, 1.0f};
    private boolean timerActive;

    @Override
    public void onDisable() {
        mc.player.stepHeight = 0.6f;
        if (timerActive) {
            ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f);
            timerActive = false;
        }
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mode.getValue().equals("Vanilla")) {
            mc.player.stepHeight = height.getValue();
        }

        if (timerActive && mc.player.onGround) {
            ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f);
            timerActive = false;
        }
    }

    @EventListener
    public void onCollide(CollideEvent event) {
        System.out.println("h");
        if (!mode.getValue().equals("NCP")) {
            return;
        }
        float height = (float) (event.getBb().minY - mc.player.posY);

        if (height <= 0 || height > this.height.getValue()) {
            return;
        }

        if (timer.getValue()) {
            ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f / amount.getValue());
            timerActive = true;
        }
        for (float offset : single) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset, mc.player.posZ, false));
        }
    }
}
