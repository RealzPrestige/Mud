package dev.zprestige.mud.manager;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.system.KeyEvent;
import dev.zprestige.mud.module.Category;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.module.client.ClickGui;
import dev.zprestige.mud.module.client.ColorSync;
import dev.zprestige.mud.module.client.Notifications;
import dev.zprestige.mud.module.client.Rpc;
import dev.zprestige.mud.module.combat.*;
import dev.zprestige.mud.module.misc.*;
import dev.zprestige.mud.module.movement.*;
import dev.zprestige.mud.module.visual.*;
import dev.zprestige.mud.util.MC;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;

public class ModuleManager implements MC {
    private final ArrayList<Module> modules;
    private Category category;

    public ModuleManager() {
        Mud.eventBus.registerListener(this);
        modules = new ArrayList<>();
    }

    public void init(){
        category = Category.Client;
        modules.addAll(Arrays.asList(
                new ClickGui().invoke("Click Gui"),
                new ColorSync().invoke("Color Sync"),
                new Notifications().invoke("Notifications"),
                new Rpc().invoke("Rpc").invokeSection(Category.Combat),
                new Aura().invoke("Aura"),
                new AutoAnchor().invoke("Auto Anchor"),
                new AutoBed().invoke("AutoBed"),
                new AutoCrystal().invoke("Auto Crystal"),
                new AutoWeb().invoke("AutoWeb"),
                new Burrow().invoke("Burrow"),
                new Criticals().invoke("Criticals"),
                new MineCrystal().invoke("Mine Crystal"),
                new Offhand().invoke("Offhand"),
                new Surround().invoke("Surround").invokeSection(Category.Misc),
                new AnchorWaster().invoke("Anchor Waster"),
                new ChorusManipulator().invoke("Chorus Manipulator"),
                new EChestPlacer().invoke("E Chest Placer"),
                new FakePlayer().invoke("FakePlayer"),
                new FreeLook().invoke("FreeLook"),
                new KeyAction().invoke("Key Action"),
                new MultiTask().invoke("Multi Task"),
                new NoRotate().invoke("NoRotate"),
                new PacketMine().invoke("Packet Mine"),
                new PlaceConfirm().invoke("Place Confirm"),
                new Quiver().invoke("Quiver"),
                new Refill().invoke("Refill"),
                new RemoveInterpolation().invoke("Remove Interpolation").invokeSection(Category.Movement),
                new Blink().invoke("Blink"),
                new FastSwim().invoke("Fast Swim"),
                new Ground().invoke("Ground"),
                new HoleSnap().invoke("Hole Snap"),
                new NoSlow().invoke("No Slow"),
                new PacketFly().invoke("Packet Fly"),
                new ReverseStep().invoke("Reverse Step"),
                new Speed().invoke("Speed"),
                new Step().invoke("Step"),
                new TickShift().invoke("Tick Shift"),
                new Velocity().invoke("Velocity"),
                new WallClip().invoke("Wall Clip").invokeSection(Category.Visual),
                new ChestHelper().invoke("Chest Helper"),
                new ChorusEsp().invoke("Chorus Esp"),
                new Crosshair().invoke("Crosshair"),
                new CustomChat().invoke("Custom Chat"),
                new HandShader().invoke("Hand Shader"),
                new HoleEsp().invoke("Hole Esp"),
                new NoRender().invoke("No Render"),
                new ShaderEsp().invoke("Shader Esp"),
                new ShulkerViewer().invoke("Shulker Viewer"),
                new ViewModel().invoke("View Model")
        ));
    }

    @EventListener
    public void onKey(KeyEvent event) {
        if (mc.currentScreen != null) {
            return;
        }
        for (Module module : getModules()) {
            if (module.getKeybind().getValue() != Keyboard.KEY_NONE && module.getKeybind().getValue() == event.getKey()) {
                module.toggle();
            }
        }
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public void startSection(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
}
