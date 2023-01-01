package dev.zprestige.mud.module.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.module.client.Notifications;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.UUID;

public class FakePlayer extends Module {
    private EntityOtherPlayerMP fakePlayer;

    @Override
    public void onEnable() {
        if (mc.world == null || mc.player == null){
            toggle();
            return;
        }
        fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("12cbdfad-33b7-4c07-aeac-01766e609482"), "zPrestige_"));
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        fakePlayer.inventory.copyInventory(mc.player.inventory);
        fakePlayer.setHealth(36);
        mc.world.addEntityToWorld(-1, fakePlayer);
        Notifications.post("[Mud] " + ChatFormatting.GRAY + "Your Fake Player has been added to the world.");

    }

    @Override
    public void onDisable(){
        if (mc.world == null || mc.player == null){
            return;
        }
        mc.world.removeEntity(fakePlayer);
        Notifications.post("[Mud] " + ChatFormatting.GRAY + "Your Fake Player has been removed from the world.");
    }
}
