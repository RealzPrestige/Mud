package dev.zprestige.mud.util.impl;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import dev.zprestige.mud.Mud;
import dev.zprestige.mud.util.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.ServerData;

public class DiscordUtil implements MC {

    public static final String APP_ID = "1059823008270778369";
    public static final String STEAM_ID = "";

    private static DiscordRPC discord;
    private static DiscordRichPresence presence;

    private static Thread thread;

    public static void init() {
        discord = DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence();
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        discord.Discord_Initialize(
                APP_ID,
                handlers,
                true,
                STEAM_ID);
    }

    public static void onPre() {
        presence.startTimestamp = System.currentTimeMillis() / 1000L;

        presence.state = Mud.MODNAME + " " + Mud.VERSION;
        presence.details = "";

        presence.largeImageKey = Mud.MODID;
        presence.largeImageText = Mud.MODNAME + " " + Mud.VERSION;

        discord.Discord_UpdatePresence(presence);
    }

    @SuppressWarnings("BusyWait")
    public static void onPost() {
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                discord.Discord_RunCallbacks();

                presence.state = generateDetails(Minecraft.getMinecraft());
                presence.details = Mud.MODNAME + " " + Mud.VERSION;

                discord.Discord_UpdatePresence(presence);

                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException ignored) {
                }
            }
        }, "DiscordUpdate");

        thread.start();
    }

    public static void onExit() {
        if (thread != null && !thread.isInterrupted())
            thread.interrupt();
        discord.Discord_Shutdown();
    }

    private static String generateDetails(Minecraft mc) {
        if (mc.currentScreen instanceof GuiMainMenu)
            return "Main Screen";

        ServerData serverData = mc.getCurrentServerData();

        if (serverData != null)
            return "Playing on " + serverData.serverIP;
        else
            return "Playing alone";
    }

}
