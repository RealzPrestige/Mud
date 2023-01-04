package dev.zprestige.mud.hud.impl;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.Render2DPostEvent;
import dev.zprestige.mud.hud.HudModule;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Armor extends HudModule {
    private final BooleanSetting reversed = new BooleanSetting("Reversed", false);

    public Armor() {
        super("Armor", false);
        x = 35;
        y = 10;
        getSettings().addAll(Arrays.asList(reversed, getSetting()));
    }


    @EventListener
    public void onRender2DPost(Render2DPostEvent event) {
        if (mc.currentScreen != null) {
            width = 70.0f;
            height = 20.0f;
            return;
        }
        float deltaX = 0.0f;
        NonNullList<ItemStack> stacks = mc.player.inventory.armorInventory;

        if (reversed.getValue()) {
            Collections.reverse(stacks);
        }

        float scale = 0.7f;
        ArrayList<Runnable> post = new ArrayList<>();

        for (ItemStack itemStack : stacks) {
            if (!itemStack.isEmpty()) {
                Item item = itemStack.getItem();
                if (item instanceof ItemArmor) {
                    int percentage = (int) Math.ceil(getPercentage(itemStack));
                    String text = percentage + "%";
                    float finalDeltaX = deltaX;
                    post.add(() -> Mud.fontManager.stringHud(text, (x + finalDeltaX + 10.0f - Mud.fontManager.stringWidthHud(text) * scale / 2.0f) / scale, (y - 5.0f) / scale, new Color(getRed(percentage), getGreen(percentage), 0.0f)));
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) (x + deltaX), (int) y);
                    RenderHelper.disableStandardItemLighting();
                }
            }
            deltaX += 17.5f;
        }
        if (mc.currentScreen == null) {
            RenderUtil.invokeScale(scale);
            post.forEach(Runnable::run);
            RenderUtil.resetScale();
        }

        if (reversed.getValue()) {
            Collections.reverse(stacks);
        }
        width = 70.0f;
        height = 20.0f;
    }

    private float getPercentage(ItemStack itemStack) {
        float durability = itemStack.getMaxDamage() - itemStack.getItemDamage();
        return (durability / itemStack.getMaxDamage()) * 100.0f;
    }

    private float getGreen(float percentage) {
        return percentage / 100.0f;
    }

    private float getRed(float percentage) {
        return 1.0f - percentage / 100.0f;
    }
}
