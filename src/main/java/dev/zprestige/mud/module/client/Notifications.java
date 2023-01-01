package dev.zprestige.mud.module.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.chat.CustomChatMessageEvent;
import dev.zprestige.mud.events.impl.system.PacketReceiveEvent;
import dev.zprestige.mud.events.impl.system.ToggleEvent;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.text.TextComponentString;

import java.util.HashMap;

public class Notifications extends Module {
    private final HashMap<String, Integer> popMap = new HashMap<>();

    private void onPop(EntityPlayer entityPlayer) {
        if (entityPlayer.equals(mc.player)) {
            return;
        }
        int pops = 1;
        if (popMap.containsKey(entityPlayer.getName())) {
            pops = popMap.get(entityPlayer.getName());
            popMap.put(entityPlayer.getName(), ++pops);
        } else {
            popMap.put(entityPlayer.getName(), pops);
        }
        if (popMap.containsKey(entityPlayer.getName())) {
            String text = "[Mud] " + entityPlayer.getName() + ChatFormatting.GRAY + " has popped " + ChatFormatting.WHITE + pops + ChatFormatting.GRAY + (pops == 1 ? " totem." : " totems.");
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(text), 1);
            Mud.eventBus.invoke(new CustomChatMessageEvent(text));
        }
    }

    private void onDeath(EntityPlayer entityPlayer) {
        if (entityPlayer.equals(mc.player)) {
            return;
        }
        if (popMap.containsKey(entityPlayer.getName())) {
            int pops = popMap.get(entityPlayer.getName());

            popMap.remove(entityPlayer.getName());

            String text = "[Mud] " + entityPlayer.getName() + ChatFormatting.GRAY + " has died after popping " + ChatFormatting.WHITE + pops + ChatFormatting.GRAY + (pops == 1 ? " totem." : " totems.");
            post(text);
        }
    }

    @EventListener
    public void onToggle(ToggleEvent event) {
        String text;
        if (event.isEnable()) {
            text = "[Mud] " + event.getModule().getName() + ChatFormatting.RESET + " has been" + ChatFormatting.GREEN + " Enabled";
        } else {
            text = "[Mud] " + event.getModule().getName() + ChatFormatting.RESET + " has been" + ChatFormatting.RED + " Disabled";
        }
        post(text);
    }

    @EventListener
    public void onTick(TickEvent event) {
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer.equals(mc.player) || entityPlayer.getHealth() > 0.0f) {
                continue;
            }
            onDeath(entityPlayer);
        }

    }

    @EventListener
    public void onPacketReceive(PacketReceiveEvent event) {
        if (mc.world != null && mc.player != null && event.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            final Entity entity = packet.getEntity(mc.world);
            if (entity instanceof EntityPlayer && packet.getOpCode() == 35) {
                onPop((EntityPlayer) entity);
            }
        }
    }

    public static void post(String text) {
        if (mc.player == null && mc.world == null){
            return;
        }
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(text), 1);
        Mud.eventBus.invoke(new CustomChatMessageEvent(text));
    }
}
