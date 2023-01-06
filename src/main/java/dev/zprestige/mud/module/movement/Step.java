package dev.zprestige.mud.module.movement;


import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MoveEvent;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.mixins.interfaces.IMinecraft;
import dev.zprestige.mud.mixins.interfaces.ITimer;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.ModeSetting;
import dev.zprestige.mud.util.impl.EntityUtil;
import dev.zprestige.mud.util.impl.PacketUtil;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.Arrays;

public class Step extends Module {
    private final ModeSetting mode = setting("Mode", "Vanilla", Arrays.asList("Vanilla", "NCP"));
    private final BooleanSetting doubles = setting("Doubles", false);
    private final BooleanSetting useTimer = setting("Timer", false);
    private final FloatSetting amount = setting("Amount", 1.0f, 0.1f, 1.0f).invokeVisibility(z -> useTimer.getValue());

    private final float[] singleOffsets = new float[]{
            0.42f,
            0.753f,
            1.0f
    }, doubleOffsets = new float[]{
            0.42f,
            0.78f,
            0.63f,
            0.51f,
            0.90f,
            1.21f,
            1.45f,
            1.43f
    };
    private boolean timer;

    @Override
    public void onDisable() {
        mc.player.stepHeight = 0.6f;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mode.getValue().equals("Vanilla")) {
            if (mc.player.isInWater() || mc.player.isInLava() || mc.player.isRiding()) {
                mc.player.stepHeight = 0.6f;
            } else {
                mc.player.stepHeight = doubles.getValue() ? 2.0f : 1.0f;
            }
        }
        if (mode.getValue().equals("NCP")) {
            if (timer && mc.player.onGround) {
                ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f);
                timer = false;
            }
        }

    }

    @EventListener
    public void onMove(MoveEvent event) {
        if (!mode.getValue().equals("NCP")) {
            return;
        }
        if (canStep()) {
            final int height = doubles.getValue() ? 2 : 1;
            final float[] i = EntityUtil.forward(0.1f);
            if (checkEmpty(height, i)) {
                if (useTimer.getValue()) {
                    ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f / amount.getValue());
                    timer = true;
                }
                performStep(height, i);
            }
        }
    }


    protected boolean checkEmpty(final int amount, final float[] i) {
        return amount == 1 ? checkFirstHeight(i) : (checkFirstHeight(i)) || (isBoundingEmpty(i, 2.1f) && !isBoundingEmpty(i, 1.9f));
    }

    protected boolean checkFirstHeight(final float[] i) {
        return isBoundingEmpty(i, 1.1f) && !isBoundingEmpty(i, 0.9f);
    }

    protected boolean canStep() {
        return mc.player.collidedHorizontally && mc.player.onGround;
    }

    protected void performStep(final int amount, final float[] i) {
        sendOffsets(amount, i);
    }

    protected void sendOffsets(final int amount, final float[] i) {
        for (float j : amount == 1 ? singleOffsets : checkFirstHeight(i) ? singleOffsets : doubleOffsets) {
            PacketUtil.invoke(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + j, mc.player.posZ, mc.player.onGround));
        }
        mc.player.setPosition(mc.player.posX, mc.player.posY + (amount == 2 ? checkFirstHeight(i) ? 1 : 2 : 1), mc.player.posZ);
    }

    protected boolean isBoundingEmpty(final float[] i, final float y) {
        return mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(i[0], y, i[1])).isEmpty();
    }
}
