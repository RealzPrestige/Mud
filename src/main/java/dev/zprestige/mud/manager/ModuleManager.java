package dev.zprestige.mud.manager;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.system.KeyEvent;
import dev.zprestige.mud.module.Category;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.util.MC;
import dev.zprestige.mud.util.impl.ClassFinder;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class ModuleManager implements MC {
    private final ArrayList<Module> modules;

    public ModuleManager() {
        Mud.eventBus.registerListener(this);
        modules = new ArrayList<>();
        try {
            for (Category category : Category.values()) {
                ArrayList<Class<?>> classes = ClassFinder.classesExtending(category.toString().toLowerCase(), Module.class);

                for (Class<?> c : classes) {

                    String moduleName = c.getName().split("\\.")[5];
                    StringBuilder name = new StringBuilder();

                    int uppercase = 0;

                    for (char ch : moduleName.toCharArray()) {
                        String appendix = "";
                        if (Character.isUpperCase(ch)) {
                            if (uppercase > 0) {
                                appendix += " ";
                            }
                            uppercase++;
                        }
                        appendix += String.valueOf(ch);
                        name.append(appendix);
                    }

                    Module module = ((Module) c.getConstructor().newInstance()).invoke(name.toString(), category);

                    modules.add(module);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public Module getModuleByClass(Class<?> c) {
        return modules.stream().filter(module -> module.getClass().equals(c)).findFirst().orElse(null);
    }
}
