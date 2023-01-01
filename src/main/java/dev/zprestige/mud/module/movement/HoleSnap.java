package dev.zprestige.mud.module.movement;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.system.PacketSendEvent;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.manager.HoleManager;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.util.impl.BlockUtil;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

public class HoleSnap extends Module {
    private final FloatSetting boundingBoxSize = setting("Bounding Box Size", 1.0f, 0.1f, 10.0f);
    private final BooleanSetting timeoutOnBlockPlacement = setting("Timeout On Block Placement", true);
    private final BooleanSetting down = setting("Down", true);
    private final FloatSetting motionY = setting("Motion Y", 1.0f, 0.1f, 5.0f);
    private long sys;

    @EventListener
    public void onTick(TickEvent event) {
        if (BlockUtil.isPlayerSafe(mc.player) || PacketFly.isActive()) {
            sys = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - sys < 1000) {
            return;
        }
        if (!mc.player.onGround) {
            return;
        }
        HoleManager.HolePos closest = Mud.holeManager.getHoles().stream().min(Comparator.comparingDouble(hole -> Math.sqrt(mc.player.getDistanceSq(BlockUtil.center(hole.getPos()))))).orElse(null);
        if (closest == null) {
            return;
        }
        BlockPos pos = BlockUtil.center(closest.getPos()).add(0.0f, 1.0f, 0.0f);
        AxisAlignedBB bb = new AxisAlignedBB(pos).shrink(0.1f * (boundingBoxSize.max - (boundingBoxSize.getValue() + 5.0f)));
        if (mc.player.getEntityBoundingBox().intersects(bb)) {
            mc.player.setPosition(pos.getX() + 0.5f, mc.player.posY, pos.getZ() + 0.5f);
            if (down.getValue()) {
                mc.player.motionY = -motionY.getValue();
            }
            sys = System.currentTimeMillis();
        }
    }

    @EventListener
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && timeoutOnBlockPlacement.getValue()) {
            sys = System.currentTimeMillis();
        }
    }
}
