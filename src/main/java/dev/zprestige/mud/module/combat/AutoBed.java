package dev.zprestige.mud.module.combat;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.util.impl.*;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;

import java.util.stream.IntStream;

public class AutoBed extends Module {
    private final IntSetting timing = setting("Timing", 50, 0, 500);
    private final BooleanSetting packet = setting("Packet", true);
    private final BooleanSetting bedRotate = setting("Bed Rotate", false);
    private final BooleanSetting rotate = setting("Rotate", false);
    private final BooleanSetting strict = setting("Strict", false);

    private final Vec3i[] vec3is = new Vec3i[]{new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(0, 0, -1),};
    private AxisAlignedBB bb;
    private long time;

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (System.currentTimeMillis() - time < timing.getValue()) {
            return;
        }

        EntityPlayer entityPlayer = EntityUtil.getEntityPlayer(10.0f);
        if (entityPlayer == null) {
            return;
        }


        if (mc.currentScreen instanceof GuiCrafting) {
            if (EntityUtil.isMoving() || !mc.player.onGround) {
                return;
            }
            BlockPos pos = BlockUtil.getPosition(entityPlayer).up();

            if (BlockUtil.is(pos, Blocks.BED)) {
                if (rotate.getValue()) {
                    RotationUtil.facePos(pos, event);
                }
                PacketUtil.invoke(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, EnumHand.MAIN_HAND, 0.5f, 0.0f, 0.5f));
                time = System.currentTimeMillis();
                return;
            }

            int bed = InventoryUtil.getItemSlot(Items.BED);
            if (bed == -1) {
                GuiContainer guiContainer = (GuiCrafting) mc.currentScreen;
                NonNullList<ItemStack> inventory = guiContainer.inventorySlots.getInventory();

                try {
                    if (fillSlots(inventory, guiContainer.inventorySlots.windowId, 1, 4, Blocks.WOOL)) {
                        time = System.currentTimeMillis();
                        return;
                    }

                    if (fillSlots(inventory, guiContainer.inventorySlots.windowId, 4, 7, Blocks.PLANKS)) {
                        time = System.currentTimeMillis();
                        return;
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }

                ItemStack crafted = inventory.get(0);
                if (!crafted.isEmpty()) {
                    mc.playerController.windowClick(guiContainer.inventorySlots.windowId, 0, 0, ClickType.QUICK_MOVE, mc.player);
                    time = System.currentTimeMillis();
                    return;
                }

                return;
            }

            if (!BlockUtil.isReplaceable(pos)) {
                return;
            }

            float distance = Float.MAX_VALUE;
            BlockPos added = null;
            Vec3i vec = null;

            for (Vec3i vec3i : vec3is) {
                BlockPos add = pos.add(vec3i);
                if (BlockUtil.isReplaceable(add)) {
                    float dist = BlockUtil.distance(add);
                    if (dist < distance) {
                        added = add;
                        vec = vec3i;
                    }
                }
            }

            if (added == null) {
                return;
            }

            int slot = InventoryUtil.getItemFromHotbar(Items.BED);
            if (slot == -1) {
                return;
            }
            float rotate = rotate(vec);
            float[] prev = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
            PacketUtil.invoke(new CPacketPlayer.Rotation(rotate, 0.0f, mc.player.onGround));

            Mud.interactionManager.placeBlock(added, bedRotate.getValue(), packet.getValue(), strict.getValue(), false, slot);

            PacketUtil.invoke(new CPacketPlayer.Rotation(prev[0], prev[1], mc.player.onGround));

            time = System.currentTimeMillis();
            this.bb = new AxisAlignedBB(added).offset(vec.getX(), vec.getY(), vec.getZ()).setMaxY(bb.maxY - 0.25f);
        } else {
            if (!mc.player.onGround || EntityUtil.isMoving()) {
                return;
            }
            if (mc.objectMouseOver != null) {
                if (mc.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                    BlockPos pos = mc.objectMouseOver.getBlockPos();
                    if (BlockUtil.is(pos, Blocks.CRAFTING_TABLE)) {
                        if (rotate.getValue()) {
                            RotationUtil.facePos(pos, event);
                        }
                        PacketUtil.invoke(new CPacketPlayerTryUseItemOnBlock(pos, mc.objectMouseOver.sideHit, EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                        time = System.currentTimeMillis();
                    }
                }
            }
        }
    }

    private float rotate(Vec3i vec3i) {
        if (vec3i.getZ() == -1) {
            return 0.0f;
        }
        if (vec3i.getX() == 1) {
            return 90.0f;
        }
        if (vec3i.getZ() == 1) {
            return 180.0f;
        }
        return 270.0f;
    }

    private boolean fillSlots(NonNullList<ItemStack> inventory, int windowId, int start, int end, Block block) {
        boolean required = IntStream.range(start, end).anyMatch(i1 -> inventory.get(i1).isEmpty());

        if (required) {
            int item = -1;

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack itemStack = inventory.get(i);
                if (itemStack.getItem().equals(Item.getItemFromBlock(block))) {
                    item = i;
                }
            }

            if (item == -1) {
                return false;
            }

            mc.playerController.windowClick(windowId, item, 0, ClickType.PICKUP, mc.player);

            for (int i = start; i < end; i++) {
                ItemStack itemStack = inventory.get(i);
                if (itemStack.isEmpty()) {
                    mc.playerController.windowClick(windowId, i, 1, ClickType.PICKUP, mc.player);
                }
            }

            mc.playerController.windowClick(windowId, item, 0, ClickType.PICKUP, mc.player);

            return true;
        }
        return false;
    }

}
