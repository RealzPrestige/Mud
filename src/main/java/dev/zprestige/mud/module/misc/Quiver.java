package dev.zprestige.mud.module.misc;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.mixins.interfaces.IKeybinding;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.util.impl.BlockUtil;
import dev.zprestige.mud.util.impl.EntityUtil;
import dev.zprestige.mud.util.impl.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemBow;
import net.minecraft.potion.PotionEffect;

public class Quiver extends Module {
    private final BooleanSetting safeOnly = setting("Safe Only", true);
    private final BooleanSetting switchBack = setting("Switch Back", true);
    private final BooleanSetting checkEnemies = setting("Check Enemies", false);
    private final FloatSetting enemyRange = setting("Enemy Range", 5.0f, 0.1f, 20.0f).invokeVisibility(z -> checkEnemies.getValue());
    private final IntSetting effectThreshold = setting("Effect Threshold (S)", 10, 0, 60);

    private int prevItem;
    private boolean pressed;
    private int shot;

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (shot > 0) {
            shot--;
            return;
        }
        if (!(mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && mc.player.isHandActive()) {
            return;
        }
        boolean isThresholdReached = isThresholdReached();
        if (!isThresholdReached) {
            return;
        }
        if (checkEnemies.getValue()) {
            EntityPlayer entityPlayer = EntityUtil.getEntityPlayer(enemyRange.getValue());
            if (entityPlayer != null) {
                return;
            } else {
                if (pressed) {
                    ((IKeybinding) mc.gameSettings.keyBindUseItem).setPressed(false);
                    pressed = false;
                }
            }
        }
        if (safeOnly.getValue() && !BlockUtil.isSelfSafe()) {
            return;
        }
        int slot = InventoryUtil.getItemFromHotbar(Items.BOW);
        if (slot != -1) {
            event.setPitch(-90.0f);
            if (!mc.player.getHeldItemMainhand().getItem().equals(Items.BOW)) {
                prevItem = mc.player.inventory.currentItem;
                InventoryUtil.switchToSlot(slot);
            }
            ((IKeybinding) mc.gameSettings.keyBindUseItem).setPressed(true);
            pressed = true;
            if (mc.player.getItemInUseMaxCount() >= 3) {
                ((IKeybinding) mc.gameSettings.keyBindUseItem).setPressed(false);
                if (switchBack.getValue()) {
                    InventoryUtil.switchBack(prevItem);
                }
                shot = 40;
                pressed = false;
            }
        }
    }

    private boolean isThresholdReached() {
        PotionEffect speed = mc.player.getActivePotionEffect(MobEffects.SPEED);
        if (speed != null) {
            if (speed.getDuration() / 20.0f > effectThreshold.getValue()) {
                return false;
            }
        }
        PotionEffect strength = mc.player.getActivePotionEffect(MobEffects.STRENGTH);
        if (strength != null) {
            return strength.getDuration() / 20.0f <= effectThreshold.getValue();
        }
        return true;
    }
}
