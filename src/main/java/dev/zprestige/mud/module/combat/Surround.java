package dev.zprestige.mud.module.combat;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.setting.impl.ModeSetting;
import dev.zprestige.mud.util.impl.BlockUtil;
import dev.zprestige.mud.util.impl.InventoryUtil;
import dev.zprestige.mud.util.impl.RaytraceUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Arrays;

public class Surround extends Module {
    private final IntSetting blocksPerTick = setting("Blocks Per Tick", 4, 1, 12).invokeTab("Placing");
    private final BooleanSetting multiTask = setting("Multi Task", true).invokeTab("Placing");

    private final BooleanSetting allowEnderchests = setting("Allow Enderchests", false).invokeTab("Placing");
    private final ModeSetting disable = setting("Disable", "Complete", Arrays.asList("Complete", "Conditional", "Y Check", "Moving")).invokeTab("Disabling");
    private final FloatSetting distance = setting("Distance", 1.0f, 1.0f, 5.0f).invokeVisibility(z -> disable.getValue().equals("Conditional")).invokeTab("Disabling");

    private final BooleanSetting extend = setting("Extend", true).invokeTab("AntiCheat");
    private final BooleanSetting packet = setting("Packet", true).invokeTab("AntiCheat");
    private final BooleanSetting rotate = setting("Rotate", false).invokeTab("AntiCheat");
    private final BooleanSetting strict = setting("Strict", false).invokeTab("AntiCheat");
    private final BooleanSetting raytrace = setting("Raytrace", false).invokeTab("AntiCheat");

    private BlockPos startPos;
    private final Vec3i[] surround = new Vec3i[]{
            new Vec3i(0, -1, 0),
            new Vec3i(1, -1, 0),
            new Vec3i(-1, -1, 0),
            new Vec3i(0, -1, 1),
            new Vec3i(0, -1, -1),
            new Vec3i(1, 0, 0),
            new Vec3i(-1, 0, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1),
    };

    @Override
    public void onEnable() {
        startPos = BlockUtil.getPosition();
    }

    @EventListener
    public void onTick(TickEvent event) {
        invokeAppend(BlockUtil.isSelfSafe() ? "Safe" : "Unsafe");
        if (shouldToggle()) {
            toggle();
            return;
        }

        if (mc.player.isHandActive() && !multiTask.getValue()) {
            return;
        }

        int block = block();
        if (block == -1) {
            toggle();
            return;
        }


        int placed = 0;

        BlockPos player = BlockUtil.getPosition();

        for (Vec3i vec3i : surround) {
            BlockPos pos = player.add(vec3i);
            boolean see = RaytraceUtil.raytrace(pos);
            if (raytrace.getValue() && !see){
                continue;
            }
            if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
                continue;
            }
            if (!mc.player.getEntityBoundingBox().intersects(new AxisAlignedBB(pos))) {
                if (placed >= blocksPerTick.getValue()) {
                    System.out.println(placed);
                    return;
                }
                place(pos, block);
                placed++;
            } else if (extend.getValue()) {
                ArrayList<BlockPos> intersecting = getIntersectingPositions(pos);
                for (BlockPos iPos : intersecting) {
                    if (mc.world.getBlockState(iPos).getMaterial().isReplaceable()) {
                        if (placed >= blocksPerTick.getValue()) {
                            System.out.println(placed);
                            return;
                        }

                        place(iPos, block);

                        placed++;
                    }
                }
            }
        }

        if (placed == 0 && disable.getValue().equals("Complete")) {
            toggle();
        }
    }

    private void place(BlockPos pos, int block) {
        Mud.interactionManager.placeBlock(pos, rotate.getValue(), packet.getValue(), strict.getValue(), false, block);
    }

    private int block() {
        int obsidian = InventoryUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
        return obsidian == -1 && allowEnderchests.getValue() ? InventoryUtil.getBlockFromHotbar(Blocks.ENDER_CHEST) : obsidian;
    }

    private boolean shouldToggle() {
        switch (disable.getValue()) {
            case "Complete":
                return false;
            case "Conditional":
                return mc.player.stepHeight > 0.6f || !mc.player.onGround || (startPos != null && BlockUtil.distance(startPos) > distance.getValue()) || (startPos != null && mc.player.posY > startPos.getY());
            case "Y Check":
                return startPos != null && mc.player.posY > startPos.getY();
            case "Moving":
                return isMoving();
        }

        return false;
    }

    private ArrayList<BlockPos> getIntersectingPositions(BlockPos pos) {
        ArrayList<BlockPos> posses = new ArrayList<>();
        AxisAlignedBB bound = mc.player.getEntityBoundingBox();

        Arrays.stream(surround).map(pos::add).forEach(pos1 -> {
            if (mc.world.getBlockState(pos1).getMaterial().isReplaceable()) {
                boolean see = mc.world.rayTraceBlocks(mc.player.getPositionVector().add(0, mc.player.eyeHeight, 0), new Vec3d(pos1.getX() + 0.5f, pos1.getY() + 0.5f, pos1.getZ() + 0.5f), false, true, false) != null;
                if (raytrace.getValue() && see){
                    return;
                }
                AxisAlignedBB bb = new AxisAlignedBB(pos1);
                if (!bound.intersects(bb)) {
                    posses.add(pos1);
                }
            }
        });
        return posses;
    }

    private boolean isMoving() {
        return mc.player.movementInput.moveForward != 0.0f && mc.player.movementInput.moveStrafe != 0.0f;
    }
}
