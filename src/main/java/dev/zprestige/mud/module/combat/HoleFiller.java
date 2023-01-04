package dev.zprestige.mud.module.combat;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.manager.HoleManager;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.setting.impl.ModeSetting;
import dev.zprestige.mud.util.impl.BlockUtil;
import dev.zprestige.mud.util.impl.EntityUtil;
import dev.zprestige.mud.util.impl.InventoryUtil;
import dev.zprestige.mud.util.impl.RaytraceUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Arrays;
import java.util.TreeMap;

public class HoleFiller extends Module {
    private final ModeSetting block = setting("Block", "Obsidian", Arrays.asList("Obsidian", "Ender Chest", "Web"));
    private final IntSetting timing = setting("Timing", 50, 1, 500);
    private final FloatSetting range = setting("Range", 5.0f, 0.1f, 6.0f);
    private final BooleanSetting whenMoving = setting("When Moving", true);
    private final BooleanSetting inHole = setting("In Hole", true);
    private final BooleanSetting doubles = setting("Doubles", false);
    private final BooleanSetting packet = setting("Packet", false);
    private final BooleanSetting rotate = setting("Rotate", false);
    private final BooleanSetting strict = setting("Strict", false);
    private final BooleanSetting raytrace = setting("Raytrace", false);

    private final Vec3i[] offsets = new Vec3i[]{
            new Vec3i(1, 0, 0),
            new Vec3i(-1, 0, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1)
    };
    private long time;

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (System.currentTimeMillis() - time < timing.getValue()) {
            return;
        }
        if ((!whenMoving.getValue() && EntityUtil.isMoving()) || (inHole.getValue() && !BlockUtil.isSelfSafe()) || !mc.player.onGround){
            return;
        }
        int slot = getBlock();
        if (slot == -1) {
            return;
        }
        EntityPlayer entityPlayer = EntityUtil.getEntityPlayer(10.0f);
        if (entityPlayer == null) {
            return;
        }
        TreeMap<Float, BlockPos> posses = new TreeMap<>();
        for (HoleManager.HolePos holePos : Mud.holeManager.getHoles()) {
            BlockPos pos = holePos.getPos();
            if (BlockUtil.distance(pos) > range.getValue() || (!doubles.getValue() && holePos.isDouble())) {
                continue;
            }
            if (raytrace.getValue()){
                boolean see = false;
                for (Vec3i vec3i : offsets){
                    if (!RaytraceUtil.raytrace(pos.add(vec3i))){
                        see = true;
                    }
                }
                if (!see){
                    continue;
                }
            }
            posses.put((float) entityPlayer.getDistanceSq(pos), pos);
        }
        if (!posses.isEmpty()) {
            BlockPos pos = posses.firstEntry().getValue();
            Mud.interactionManager.placeBlock(pos, rotate.getValue(), packet.getValue(), strict.getValue(), false, slot);
            time = System.currentTimeMillis();
        }
    }

    private int getBlock() {
        switch (block.getValue()) {
            case "Obsidian":
                return InventoryUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
            case "Ender Chest":
                return InventoryUtil.getBlockFromHotbar(Blocks.ENDER_CHEST);
            case "Web":
                return InventoryUtil.getBlockFromHotbar(Blocks.WEB);
        }
        return -1;
    }
}
