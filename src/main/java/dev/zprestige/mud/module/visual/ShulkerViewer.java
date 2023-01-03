package dev.zprestige.mud.module.visual;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.RenderToolTipEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.shader.impl.BlurShader;
import dev.zprestige.mud.shader.impl.ShadowGradientShader;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;

public class ShulkerViewer extends Module {
    private final IntSetting radius = setting("Radius", 5, 2, 10).invokeTab("Glow");
    private final FloatSetting intensity = setting("Intensity", 1.0f, 1.0f, 2.0f).invokeTab("Glow");
    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Glow");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Glow");
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Glow");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Glow");

    @EventListener
    public void onRenderToolTip(RenderToolTipEvent event) {
        if (event.getItemStack().getItem() instanceof ItemShulkerBox) {
            NBTTagCompound tagCompound = event.getItemStack().getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10)) {
                NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
                if (!blockEntityTag.hasKey("Items", 9)) {
                    return;
                }
                event.setX(event.getX() + 10);
                event.setY(event.getY() + 10);
                float width = 162.5f, height = 57.5f;

                NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(blockEntityTag, items);

                GlStateManager.disableDepth();
                RenderUtil.rounded(event.getX(), event.getY(), event.getX() + width, event.getY() + height, 5.0f, new Color(48, 51, 71, 150));

                BlurShader.invokeBlur();
                RenderUtil.rounded(event.getX(), event.getY(), event.getX() + width, event.getY() + height, 5.0f, Color.WHITE);
                BlurShader.releaseBlur(10.0f);

                ShadowGradientShader.invokeShadow();
                RenderUtil.rounded(event.getX(), event.getY(), event.getX() + width, event.getY() + height, 5.0f, Color.WHITE);
                ShadowGradientShader.releaseShadow(radius.getValue(), 1, intensity.getValue(), step.getValue(), speed.getValue(), color1.getValue(), color2.getValue());

                int item = 0;
                float deltaX = event.getX() + 2.5f, deltaY = event.getY() + 2.5f;
                for (ItemStack itemStack : items) {
                    if (item == 9) {
                        deltaX = event.getX() + 2.5f;
                        deltaY += 17.5f;
                        item = 0;
                    }
                    String s = itemStack.isEmpty() ?TextFormatting.YELLOW + "0" : null;
                    mc.getRenderItem().zLevel = 500.0f;
                    GlStateManager.enableDepth();
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) deltaX, (int) deltaY);
                    mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, (int) deltaX, (int) deltaY, s);
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.disableDepth();
                    mc.getRenderItem().zLevel = 0.0f;
                    deltaX += 17.5f;
                    item++;
                }
                GlStateManager.enableDepth();

                event.setCancelled(true);
            }
        }
    }

}
