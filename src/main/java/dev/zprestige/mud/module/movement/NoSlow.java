package dev.zprestige.mud.module.movement;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.InputUpdateEvent;
import dev.zprestige.mud.events.impl.player.ItemUsedEvent;
import dev.zprestige.mud.events.impl.system.PacketSendEvent;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.ModeSetting;
import dev.zprestige.mud.util.impl.EntityUtil;
import dev.zprestige.mud.util.impl.PacketUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

public class NoSlow extends Module {
    private final BooleanSetting items = setting("Items", false);
    private final BooleanSetting inventory = setting("Inventory", false);
    private final BooleanSetting inventoryStrict = setting("Inventory Strict", false).invokeVisibility(z -> inventory.getValue());
    private final ModeSetting mode = setting("Mode", "NCP", Arrays.asList("None", "NCP", "Sneak", "Swap"));

    private boolean sneaking;

    @EventListener
    public void onInputUpdate(InputUpdateEvent event) {
        if (items.getValue() && isSlowed()) {
            event.getMovementInput().moveForward *= 5.0f;
            event.getMovementInput().moveStrafe *= 5.0f;
        }
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (isNotElytraFlying()) {
            if (sneaking && !mc.player.isHandActive()) {
                PacketUtil.invoke(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                sneaking = false;
            }
            if (inventory.getValue()) {
                if (mc.currentScreen instanceof GuiChat) {
                    return;
                }
                for (KeyBinding keyBinding : EntityUtil.getMovementKeys()) {
                    if (Keyboard.isKeyDown(keyBinding.getKeyCode())) {
                        if (keyBinding.getKeyConflictContext() != KeyConflictContext.UNIVERSAL) {
                            keyBinding.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
                        }
                        KeyBinding.setKeyBindState(keyBinding.getKeyCode(), true);
                    } else {
                        KeyBinding.setKeyBindState(keyBinding.getKeyCode(), false);
                    }
                }
            }
        }
    }

    @EventListener
    public void onItemUsed(ItemUsedEvent event) {
        if (items.getValue() && isNotElytraFlying()) {
            if (mode.getValue().equals("Sneak")) {
                if (!sneaking) {
                    PacketUtil.invoke(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    sneaking = true;
                }
            }
            if (mode.getValue().equals("Swap")) {
                PacketUtil.invoke(new CPacketHeldItemChange(mc.player.inventory.currentItem));
            }
        }
    }

    @EventListener
    public void onPacketSend(PacketSendEvent event) {
        if (inventoryStrict.getValue()) {
            if (event.getPacket() instanceof CPacketClickWindow) {
                PacketUtil.invoke(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }
        }

        if (mode.getValue().equals("NCP")) {
            if (event.getPacket() instanceof CPacketPlayer) {
                PacketUtil.invoke(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, mc.player.getPosition(), EnumFacing.DOWN));
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null) {
            return;
        }
        if (sneaking) {
            PacketUtil.invoke(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            sneaking = false;
        }
    }

    private boolean isSlowed() {
        return mc.player.isHandActive() && !mc.player.isRiding() && isNotElytraFlying();
    }

    private boolean isNotElytraFlying() {
        return !mc.player.isElytraFlying();
    }

}
