package dev.zprestige.mud.module.combat;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.util.impl.BlockUtil;
import dev.zprestige.mud.util.impl.EntityUtil;
import dev.zprestige.mud.util.impl.InventoryUtil;
import dev.zprestige.mud.util.impl.RaytraceUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class AutoWeb extends Module {
    private final FloatSetting range = setting("Range", 5.0f, 0.1f, 6.0f).invokeTab("Web");
    private final IntSetting timing = setting("Timing", 50, 1, 500).invokeTab("Web");
    private final BooleanSetting packet = setting("Packet", false).invokeTab("Web");
    private final BooleanSetting rotate = setting("Rotate", false).invokeTab("Web");
    private final BooleanSetting strict = setting("Strict", false).invokeTab("Web");
    private final BooleanSetting raytrace = setting("Raytrace", false).invokeTab("Web");

    private final BooleanSetting predict = setting("Predict", false).invokeTab("Predict");
    private final IntSetting ticks = setting("Ticks", 1,1, 10).invokeTab("Predict");

    private long time;

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event){
        EntityPlayer entityPlayer = EntityUtil.getEntityPlayer(10.0f);
        if (entityPlayer == null || System.currentTimeMillis() - time < timing.getValue()){
            return;
        }
        BlockPos pos = BlockUtil.getPosition(entityPlayer);
        if (predict.getValue()){
            pos.add(entityPlayer.motionX * ticks.getValue(), entityPlayer.motionY * ticks.getValue(), entityPlayer.motionZ * ticks.getValue());
        }
        if (BlockUtil.distance(pos) > range.getValue() || !mc.world.getBlockState(pos).isTranslucent()){
            time = System.currentTimeMillis();
            return;
        }
        if (raytrace.getValue() && !RaytraceUtil.raytrace(pos)){
            return;
        }
        int slot = InventoryUtil.getBlockFromHotbar(Blocks.WEB);
        if (slot == -1){
            return;
        }

        Mud.interactionManager.placeBlock(pos, rotate.getValue(), packet.getValue(), strict.getValue(), true, slot);

        time = System.currentTimeMillis();
    }
}
