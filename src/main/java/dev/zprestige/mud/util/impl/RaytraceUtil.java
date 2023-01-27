package dev.zprestige.mud.util.impl;

import dev.zprestige.mud.util.MC;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RaytraceUtil implements MC {

    public static boolean raytrace(Entity entity) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(entity.posX, entity.posY + entity.getEyeHeight() + entity.height / 2.0f, entity.posZ)) == null;
    }


    public static boolean raytrace(Vec3d vec) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), vec, false, true, false) == null;
    }

    public static boolean raytrace(BlockPos pos) {
        return raytrace(new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
    }
}
