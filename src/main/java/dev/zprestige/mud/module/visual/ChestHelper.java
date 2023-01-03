package dev.zprestige.mud.module.visual;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.gui.DrawContainerEvent;
import dev.zprestige.mud.events.impl.gui.GuiContainerItemStackEvent;
import dev.zprestige.mud.events.impl.gui.GuiContainerMouseClickedEvent;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.shader.impl.BlurShader;
import dev.zprestige.mud.shader.impl.ShadowGradientShader;
import dev.zprestige.mud.util.impl.InventoryUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import java.awt.*;
import java.util.ArrayList;

public class ChestHelper extends Module {
    private final IntSetting radius = setting("Radius", 5, 2, 10).invokeTab("Glow");
    private final FloatSetting intensity = setting("Intensity", 1.0f, 1.0f, 2.0f).invokeTab("Glow");
    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Glow");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Glow");
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Glow");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Glow");

    private final ArrayList<ItemShulker> itemShulkers = new ArrayList<>();

    private final ItemStack[] itemStacks = new ItemStack[]{
            new ItemStack(Items.END_CRYSTAL),
            new ItemStack(Items.EXPERIENCE_BOTTLE),
            new ItemStack(Items.GOLDEN_APPLE),
            new ItemStack(Items.TOTEM_OF_UNDYING)
    };

    private final int width = 176, height = 166;
    private int x, y;

    @EventListener
    public void onTick(TickEvent event) {
        if (mc.currentScreen == null) {
            if (!itemShulkers.isEmpty()) {
                itemShulkers.clear();
            }
        }
    }

    @EventListener
    public void onDrawContainer(DrawContainerEvent event) {
        if (!(mc.currentScreen instanceof GuiChest)){
            return;
        }

        x = event.getX();
        y = event.getY();

        float x = this.x + width + 2.5f, y = this.y + height - 80.0f, w = this.x + width + 23.5f, h = this.y + height;

        // Background
        RenderUtil.rounded(x, y, w, h, 5.0f, new Color(48, 51, 71, 150));

        // blur
        BlurShader.invokeBlur();
        RenderUtil.rounded(x, y, w, h, 5.0f, Color.WHITE);
        BlurShader.releaseBlur(10.0f);

        // Glow
        ShadowGradientShader.invokeShadow();
        RenderUtil.rounded(x, y, w, h, 5.0f, Color.WHITE);
        ShadowGradientShader.releaseShadow(radius.getValue(), 1, intensity.getValue(), step.getValue(), speed.getValue(), color1.getValue(), color2.getValue());

        float deltaY = y + 2.5f;
        for (ItemStack itemStack : itemStacks) {
            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) (x + 2.5f), (int) deltaY);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, (int) (x + 2.5f), (int) deltaY, null);
            deltaY += 18.75f;
        }
    }

    @EventListener
    public void onGuiContainerMouseClicked(GuiContainerMouseClickedEvent event) {
        if (!(mc.currentScreen instanceof GuiChest)){
            return;
        }
        if (event.getMouseButton() == 0) {
            float x = this.x + width + 2.5f, y = this.y + height - 80.0f, w = this.x + width + 23.5f, h = this.y + height;
            if (event.getMouseX() > x && event.getMouseX() < w && event.getMouseY() > y && event.getMouseY() < h) {
                float deltaY = y + 2.5f;
                for (ItemStack itemStack : itemStacks) {
                    if (event.getMouseY() > deltaY && event.getMouseY() < deltaY + 18.75f) {
                        handleClick(itemStack);
                    }
                    deltaY += 18.75f;
                }
            }
        }
        itemShulkers.clear();
    }

    @EventListener
    public void onGuiContainerItemStack(GuiContainerItemStackEvent event) {
        if (!(mc.currentScreen instanceof GuiChest)){
            return;
        }
        if (contains(event.getItemStack())) {
            return;
        }
        if (event.getItemStack().getItem() instanceof ItemShulkerBox) {
            NBTTagCompound tagCompound = event.getItemStack().getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10)) {
                NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
                if (!blockEntityTag.hasKey("Items", 9)) {
                    return;
                }
                NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(blockEntityTag, items);

                itemShulkers.add(new ItemShulker(event.getItemStack(), items, event.getIndex()));
            }
        }
    }

    private void handleClick(ItemStack itemStack) {
        Item item = itemStack.getItem();
        ItemShulker highest = null;
        if (item.equals(Items.END_CRYSTAL)) {
            for (ItemShulker itemShulker : itemShulkers) {
                if (itemShulker.getCrystals() > 0 && (highest == null || itemShulker.getCrystals() > highest.getCrystals())) {
                    highest = itemShulker;
                }
            }
        }
        if (item.equals(Items.EXPERIENCE_BOTTLE)) {
            for (ItemShulker itemShulker : itemShulkers) {
                if (itemShulker.getExp() > 0 && (highest == null || itemShulker.getExp() > highest.getExp())) {
                    highest = itemShulker;
                }
            }
        }
        if (item.equals(Items.GOLDEN_APPLE)) {
            for (ItemShulker itemShulker : itemShulkers) {
                if (itemShulker.getGapples() > 0 && (highest == null || itemShulker.getGapples() > highest.getGapples())) {
                    highest = itemShulker;
                }
            }
        }
        if (item.equals(Items.TOTEM_OF_UNDYING)) {
            for (ItemShulker itemShulker : itemShulkers) {
                if (itemShulker.getTotems() > 0 && (highest == null || itemShulker.getTotems() > highest.getTotems())) {
                    highest = itemShulker;
                }
            }
        }
        if (highest != null) {
            clickHighest(highest);
        }
    }

    private void clickHighest(ItemShulker itemShulker){
        if (itemShulker != null) {
            Container guiContainer = mc.player.openContainer;
            NonNullList<ItemStack> inventory = guiContainer.getInventory();
            for (int i = 0, inventorySize = inventory.size(); i < inventorySize; i++) {
                ItemStack itemStack1 = inventory.get(i);
                if (itemStack1.equals(itemShulker.getShulker())) {
                    int air = getAir();
                    if (air == -1){
                        return;
                    }
                    mc.playerController.windowClick(guiContainer.windowId, i, 0, ClickType.PICKUP, mc.player);

                    itemShulkers.clear();
                }
            }
        }
    }

    private int getAir(){
        return InventoryUtil.getItemSlot(Items.AIR);
    }

    private boolean contains(ItemStack itemStack) {
        return itemShulkers.stream().anyMatch(itemShulker -> itemShulker.getShulker().equals(itemStack));
    }

    private static int getAmount(Item item, NonNullList<ItemStack> itemStacks) {
        return itemStacks.stream().filter(itemStack -> itemStack.getItem().equals(item)).mapToInt(ItemStack::getCount).sum();
    }

    private static class ItemShulker {
        private final ItemStack shulker;
        private final int crystals, exp, gapples, totems, index;

        public ItemShulker(ItemStack shulker, NonNullList<ItemStack> itemStacks, int index) {
            this.shulker = shulker;
            this.crystals = getAmount(Items.END_CRYSTAL, itemStacks);
            this.exp = getAmount(Items.EXPERIENCE_BOTTLE, itemStacks);
            this.gapples = getAmount(Items.GOLDEN_APPLE, itemStacks);
            this.totems = getAmount(Items.TOTEM_OF_UNDYING, itemStacks);
            this.index = index;
        }

        public ItemStack getShulker() {
            return shulker;
        }

        public int getCrystals() {
            return crystals;
        }

        public int getExp() {
            return exp;
        }

        public int getGapples() {
            return gapples;
        }

        public int getTotems() {
            return totems;
        }

        public int getIndex() {
            return index;
        }
    }
}
