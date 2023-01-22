package dev.zprestige.mud.util.impl;

import dev.zprestige.mud.util.MC;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockUtil implements MC {
    private static final Vec3i[] hole = new Vec3i[]{
            new Vec3i(-1, 0, 0),
            new Vec3i(1, 0, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1),
            new Vec3i(0, -1, 0),
    };

    public static BlockPos getPosition() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static BlockPos getPosition(EntityPlayer entityPlayer) {
        return new BlockPos(Math.floor(entityPlayer.posX), Math.floor(entityPlayer.posY), Math.floor(entityPlayer.posZ));
    }

    public static boolean isReplaceable(BlockPos pos){
        return mc.world.getBlockState(pos).getMaterial().isReplaceable();
    }


    public static boolean isSelfSafe() {
        BlockPos pos = getPosition();
        return mc.player.onGround && Arrays.stream(hole).allMatch(vec3i -> isBedrockOrObsidianOrEchest(pos.add(vec3i)));
    }

    public static float distance(BlockPos pos) {
        return (float) Math.sqrt(mc.player.getDistanceSq(pos));
    }

    public static boolean is(BlockPos pos, Block block) {
        return mc.world.getBlockState(pos).getBlock().equals(block);
    }

    public static boolean isPlayerSafe(EntityPlayer entityPlayer) {
        final BlockPos pos = entityPlayer.getPosition();
        if (isNotIntersecting(entityPlayer)) {
            return isBedrockOrObsidianOrEchest(pos.north()) && isBedrockOrObsidianOrEchest(pos.east()) && isBedrockOrObsidianOrEchest(pos.south()) && isBedrockOrObsidianOrEchest(pos.west()) && isBedrockOrObsidianOrEchest(pos.down());
        } else {
            return isIntersectingSafe(entityPlayer);
        }
    }

    public static boolean isNotIntersecting(EntityPlayer entityPlayer) {
        final BlockPos pos = entityPlayer.getPosition();
        final AxisAlignedBB bb = entityPlayer.getEntityBoundingBox();
        return (!air(pos.north()) || !bb.intersects(new AxisAlignedBB(pos.north()))) && (!air(pos.east()) || !bb.intersects(new AxisAlignedBB(pos.east()))) && (!air(pos.south()) || !bb.intersects(new AxisAlignedBB(pos.south()))) && (!air(pos.west()) || !bb.intersects(new AxisAlignedBB(pos.west())));
    }

    public static boolean isIntersectingSafe(EntityPlayer entityPlayer) {
        final BlockPos pos = entityPlayer.getPosition();
        final AxisAlignedBB bb = entityPlayer.getEntityBoundingBox();
        if (air(pos.north()) && bb.intersects(new AxisAlignedBB(pos.north()))) {
            final BlockPos pos1 = pos.north();
            if (!isBedrockOrObsidianOrEchest(pos1.north()) || !isBedrockOrObsidianOrEchest(pos1.east()) || !isBedrockOrObsidianOrEchest(pos1.west()) || !isBedrockOrObsidianOrEchest(pos1.down()))
                return false;
        }
        if (air(pos.east()) && bb.intersects(new AxisAlignedBB(pos.east()))) {
            final BlockPos pos1 = pos.east();
            if (!isBedrockOrObsidianOrEchest(pos1.north()) || !isBedrockOrObsidianOrEchest(pos1.east()) || !isBedrockOrObsidianOrEchest(pos1.south()) || !isBedrockOrObsidianOrEchest(pos1.down()))
                return false;
        }
        if (air(pos.south()) && bb.intersects(new AxisAlignedBB(pos.south()))) {
            final BlockPos pos1 = pos.south();
            if (!isBedrockOrObsidianOrEchest(pos1.east()) || !isBedrockOrObsidianOrEchest(pos1.south()) || !isBedrockOrObsidianOrEchest(pos1.west()) || !isBedrockOrObsidianOrEchest(pos1.down()))
                return false;
        }
        if (air(pos.west()) && bb.intersects(new AxisAlignedBB(pos.west()))) {
            final BlockPos pos1 = pos.west();
            return isBedrockOrObsidianOrEchest(pos1.north()) && isBedrockOrObsidianOrEchest(pos1.south()) && isBedrockOrObsidianOrEchest(pos1.west()) && isBedrockOrObsidianOrEchest(pos1.down());
        }
        return true;
    }

    public static boolean isBedrockOrObsidianOrEchest(final BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos).getBlock().equals(Blocks.ENDER_CHEST);
    }

    public static boolean air(final BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR);
    }


    public static boolean empty(BlockPos pos) {
        return mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(new BlockPos(pos.getX() + 0.5f, pos.getY() + 1.0f, pos.getZ() + 0.5f))).isEmpty();
    }

    public static boolean hasCrystal(BlockPos pos) {
        return !mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(new BlockPos(pos.getX() + 0.5f, pos.getY() + 1.5f, pos.getZ() + 0.5f))).isEmpty();
    }

    public static BlockPos center(BlockPos pos) {
        return pos.add(0.5f, 0.5f, 0.5f);
    }

    public static boolean valid(BlockPos pos, boolean updated) {
        return mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR)
                && (mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.AIR) || updated)
                && (mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN)
                || mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK));
    }

    public static float calculateEntityDamage(final EntityEnderCrystal crystal, final EntityPlayer entityPlayer) {
        return calculatePosDamage(crystal.posX, crystal.posY, crystal.posZ, entityPlayer);
    }

    public static float calculatePosDamage(final BlockPos position, final EntityPlayer entityPlayer) {
        return calculatePosDamage(position.getX() + 0.5f, position.getY() + 1.0f, position.getZ() + 0.5f, entityPlayer);
    }

    @SuppressWarnings("ConstantConditions")
    public static float calculatePosDamage(final double posX, final double posY, final double posZ, final Entity entity) {
        final float doubleSize = 12.0f;
        final double size = entity.getDistance(posX, posY, posZ) / doubleSize;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        final double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        final double value = (1.0f - size) * blockDensity;
        final float damage = (float) ((int) ((value * value + value) / 2.0f * 7.0f * doubleSize + 1.0f));
        double finalDamage = 1.0f;

        if (entity instanceof EntityLivingBase) {
            finalDamage = getBlastReduction((EntityLivingBase) entity, getMultipliedDamage(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }

        return (float) finalDamage;
    }

    private static float getMultipliedDamage(final float damage) {
        return damage * (mc.world.getDifficulty().getId() == 0 ? 0.0f : (mc.world.getDifficulty().getId() == 2 ? 1.0f : (mc.world.getDifficulty().getId() == 1 ? 0.5f : 1.5f)));
    }

    private static float getBlastReduction(final EntityLivingBase entity, final float damageI, final Explosion explosion) {
        float damage = damageI;
        final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
        damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        int k = 0;
        try {
            k = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), ds);
        } catch (Exception ignored) {
        }
        damage = damage * (1.0f - MathHelper.clamp(k, 0.0F, 20.0f) / 25.0f);

        if (entity.isPotionActive(MobEffects.RESISTANCE)) {
            damage = damage - (damage / 4);
        }

        return damage;
    }


    public static List<BlockPos> getBlocksInRadius(float range) {
        List<BlockPos> posses = new ArrayList<>();
        if (mc.player == null) {
            return posses;
        }
        for (int x = (int) -range; x < range; x++) {
            for (int y = (int) -range; y < range; y++) {
                for (int z = (int) -range; z < range; z++) {
                    BlockPos position = mc.player.getPosition().add(x, y, z);
                    if (mc.player.getDistance(position.getX() + 0.5, position.getY() + 1, position.getZ() + 0.5) <= range) {
                        posses.add(position);
                    }
                }
            }
        }
        return posses;
    }
}
