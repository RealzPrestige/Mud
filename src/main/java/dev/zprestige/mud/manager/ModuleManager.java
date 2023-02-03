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
                new ClickGui().invoke("ClickGui"),
                new ColorSync().invoke("ColorSync"),
                new Notifications().invoke("Notifications"),
                new Rpc().invoke("Rpc").invokeSection(Category.Combat),
                new Aura().invoke("Aura"),
                new AutoAnchor().invoke("AutoAnchor"),
                new AutoBed().invoke("AutoBed"),
                new AutoCrystal().invoke("AutoCrystal"),
                new AutoWeb().invoke("AutoWeb"),
                new Burrow().invoke("Burrow"),
                new Criticals().invoke("Criticals"),
                new MineCrystal().invoke("MineCrystal"),
                new Offhand().invoke("Offhand"),
                new Surround().invoke("Surround").invokeSection(Category.Misc),
                new AnchorWaster().invoke("AnchorWaster"),
                new ChorusManipulator().invoke("ChorusManipulator"),
                new EChestPlacer().invoke("EChestPlacer"),
                new FakePlayer().invoke("FakePlayer"),
                new FreeLook().invoke("FreeLook"),
                new KeyAction().invoke("KeyAction"),
                new MultiTask().invoke("MultiTask"),
                new NoRotate().invoke("NoRotate"),
                new PacketMine().invoke("PacketMine"),
                new PlaceConfirm().invoke("PlaceConfirm"),
                new Quiver().invoke("Quiver"),
                new Refill().invoke("Refill"),
                new RemoveInterpolation().invoke("RemoveInterpolation").invokeSection(Category.Movement),
                new Blink().invoke("Blink"),
                new FastSwim().invoke("FastSwim"),
                new Ground().invoke("Ground"),
                new HoleSnap().invoke("HoleSnap"),
                new NoSlow().invoke("NoSlow"),
                new PacketFly().invoke("PacketFly"),
                new ReverseStep().invoke("ReverseStep"),
                new Speed().invoke("Speed"),
                new Step().invoke("Step"),
                new TickShift().invoke("TickShift"),
                new Velocity().invoke("Velocity"),
                new WallClip().invoke("WallClip").invokeSection(Category.Visual),
                new ChestHelper().invoke("ChestHelper"),
                new ChorusEsp().invoke("ChorusEsp"),
                new Crosshair().invoke("Crosshair"),
                new CustomChat().invoke("CustomChat"),
                new HandShader().invoke("HandShader"),
                new HoleEsp().invoke("HoleEsp"),
                new NoRender().invoke("NoRender"),
                new ShaderEsp().invoke("ShaderEsp"),
                new ShulkerViewer().invoke("ShulkerViewer"),
                new ViewModel().invoke("ViewModel")
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
