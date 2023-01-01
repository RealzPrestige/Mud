package dev.zprestige.mud.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.mud.module.client.Notifications;
import dev.zprestige.mud.util.MC;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public class FriendManager implements MC {
    private final ArrayList<String> friendList = new ArrayList<>();

    public void add(String name){
        if (!friendList.contains(name)) {
            friendList.add(name);
            Notifications.post("[Mud] " + ChatFormatting.WHITE + name + ChatFormatting.GRAY + " has been added to your friends list.");
        }
    }

    public void remove(String name){
        friendList.remove(name);
        Notifications.post("[Mud] " + ChatFormatting.WHITE + name + ChatFormatting.GRAY + " has been removed from your friends list.");
    }

    public boolean contains(EntityPlayer entityPlayer){
        return getFriendList().contains(entityPlayer.getName());
    }

    public boolean contains(String name){
        return getFriendList().contains(name);
    }


    public ArrayList<String> getFriendList() {
        return friendList;
    }
}