package dev.zprestige.mud;

import dev.zprestige.mud.events.bus.EventBus;
import dev.zprestige.mud.manager.*;
import dev.zprestige.mud.module.Category;
import dev.zprestige.mud.shader.impl.GlowShader;
import dev.zprestige.mud.ui.Interface;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.util.ArrayList;
import java.util.Arrays;

@Mod(modid = Mud.MODID, version = Mud.VERSION)
public class Mud {
    public static final String
            MODID = "mud",
            MODNAME = "Mud",
            VERSION = "1.0";
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static final EventBus eventBus = new EventBus();
    public static final ModuleManager moduleManager = new ModuleManager();
    public static final EventManager eventManager = new EventManager();
    public static final FontManager fontManager = new FontManager();
    public static final FriendManager friendManager = new FriendManager();
    public static final ThreadManager threadManager = new ThreadManager();
    public static final HudModuleManager hudModuleManager = new HudModuleManager();
    public static final ConfigManager configManager = new ConfigManager();
    public static final HoleManager holeManager = new HoleManager();
    public static final InteractionManager interactionManager = new InteractionManager();
    public static final FrustumManager frustumManager = new FrustumManager();
    public static final Interface clickGui = new Interface();
    public static final TPSManager tpsManager = new TPSManager();
    public static final Thread thread = new Thread(() -> configManager.save("AutoSave", false, new ArrayList<>(Arrays.asList(Category.values()))));

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Runtime.getRuntime().addShutdownHook(thread);
    }
}
