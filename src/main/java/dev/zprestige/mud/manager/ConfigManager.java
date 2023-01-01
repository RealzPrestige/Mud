package dev.zprestige.mud.manager;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.hud.HudModule;
import dev.zprestige.mud.module.Category;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.Setting;
import dev.zprestige.mud.setting.impl.*;
import dev.zprestige.mud.util.MC;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigManager implements MC {
    public final File
            gameDir = mc.gameDir,
            folder = new File(gameDir + "/Mud");


    public ConfigManager() {
        folder.mkdir();
        try {
            File active = new File(folder + "/active.txt");
            active.createNewFile();
            if (active.exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(active));
                String activeConfig = bufferedReader.readLine();
                bufferedReader.close();
                load(activeConfig, new ArrayList<>(Arrays.asList(Category.values())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(String f, ArrayList<Category> categories) {
        try {
            File friends = new File(folder + "/friends.txt");
            if (friends.exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(friends));
                bufferedReader.lines().forEach(Mud.friendManager::add);
                bufferedReader.close();
            }
            for (HudModule hudModule : Mud.hudModuleManager.getHudModules()) {
                File file = new File(folder + "/" + f + "/hud/" + hudModule.getName() + ".txt");
                if (!file.exists()) {
                    continue;
                }
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                bufferedReader.lines().forEach(line -> {
                    String[] split = line.split(":");
                    String name = split[0];
                    if (name.equals("x")) {
                        hudModule.x = Float.parseFloat(split[1]);
                    }
                    if (name.equals("y")) {
                        hudModule.y = Float.parseFloat(split[1]);
                    }
                    Setting<?> setting = getSetting(hudModule, name);
                    if (setting == null) {
                        return;
                    }
                    String value = split[1];
                    if (setting instanceof BindSetting) {
                        ((BindSetting) setting).invokeValue(Integer.parseInt(value));
                        return;
                    }
                    if (setting instanceof BooleanSetting) {
                        if (setting.getName().equals("Enabled")) {
                            if (Boolean.parseBoolean(value)) {
                                hudModule.toggle();
                            }
                        } else {
                            ((BooleanSetting) setting).invokeValue(Boolean.parseBoolean(value));
                        }
                        return;
                    }
                    if (setting instanceof ColorSetting) {
                        int red = Integer.parseInt(split[1]),
                                green = Integer.parseInt(split[2]),
                                blue = Integer.parseInt(split[3]),
                                alpha = Integer.parseInt(split[4]);
                        ((ColorSetting) setting).invokeValue(new Color(red, green, blue, alpha));
                        return;
                    }
                    if (setting instanceof FloatSetting) {
                        ((FloatSetting) setting).invokeValue(Float.parseFloat(value));
                        return;
                    }
                    if (setting instanceof IntSetting) {
                        ((IntSetting) setting).invokeValue(Integer.parseInt(value));
                        return;
                    }
                    if (setting instanceof ModeSetting) {
                        ((ModeSetting) setting).invokeValue(value);
                    }
                });
                bufferedReader.close();
            }
            for (Module module : Mud.moduleManager.getModules()) {
                if (categories.stream().noneMatch(category -> module.getCategory().equals(category))) {
                    continue;
                }
                File file = new File(folder + "/" + f + "/" + module.getCategory().toString() + "/" + module.getName() + ".txt");
                if (!file.exists()) {
                    continue;
                }
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                bufferedReader.lines().forEach(line -> {
                    String[] split = line.split(":");
                    String name = split[0];
                    Setting<?> setting = getSetting(module, name);
                    if (setting == null) {
                        return;
                    }
                    String value = split[1];
                    if (setting instanceof BindSetting) {
                        ((BindSetting) setting).invokeValue(Integer.parseInt(value));
                        return;
                    }
                    if (setting instanceof BooleanSetting) {
                        if (setting.getName().equals("Enabled")) {
                            if (Boolean.parseBoolean(value)) {
                                module.toggle();
                            }
                        } else {
                            ((BooleanSetting) setting).invokeValue(Boolean.parseBoolean(value));
                        }
                        return;
                    }
                    if (setting instanceof ColorSetting) {
                        int red = Integer.parseInt(split[1]),
                                green = Integer.parseInt(split[2]),
                                blue = Integer.parseInt(split[3]),
                                alpha = Integer.parseInt(split[4]);
                        ((ColorSetting) setting).invokeValue(new Color(red, green, blue, alpha));
                        return;
                    }
                    if (setting instanceof FloatSetting) {
                        ((FloatSetting) setting).invokeValue(Float.parseFloat(value));
                        return;
                    }
                    if (setting instanceof IntSetting) {
                        ((IntSetting) setting).invokeValue(Integer.parseInt(value));
                        return;
                    }
                    if (setting instanceof ModeSetting) {
                        ((ModeSetting) setting).invokeValue(value);
                    }
                });
                bufferedReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(String f, boolean saveActive, ArrayList<Category> categories) {
        try {
            if (saveActive) {
                File active = new File(folder + "/active.txt");
                active.createNewFile();

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(active));
                bufferedWriter.write(f);
                bufferedWriter.close();
            }

            File friends = new File(folder + "/friends.txt");
            friends.createNewFile();
            BufferedWriter friendsWriter = new BufferedWriter(new FileWriter(friends));
            for (String name : Mud.friendManager.getFriendList()) {
                friendsWriter.write(name + "\n");
            }
            friendsWriter.close();

            for (Category category : Category.values()) {
                File file = new File(folder + "/" + f + "/" + category.toString());
                file.mkdirs();
            }
            File hud = new File(folder + "/" + f + "/hud/");
            hud.mkdirs();

            for (HudModule module : Mud.hudModuleManager.getHudModules()) {
                File file = new File(folder + "/" + f + "/hud/" + module.getName() + ".txt");
                file.createNewFile();

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write("x:" + module.x + "\n");
                bufferedWriter.write("y:" + module.y + "\n");

                for (Setting<?> setting : module.getSettings()) {
                    String value = String.valueOf(setting.getValue());
                    if (setting instanceof ColorSetting) {
                        Color color = ((ColorSetting) setting).getValue();
                        value = color.getRed() + ":" + color.getGreen() + ":" + color.getBlue() + ":" + color.getAlpha();
                    }
                    bufferedWriter.write(setting.getName() + ":" + value + "\n");
                }

                bufferedWriter.close();
            }


            for (Module module : Mud.moduleManager.getModules()) {
                if (categories.stream().noneMatch(category -> module.getCategory().equals(category))) {
                    continue;
                }
                File file = new File(folder + "/" + f + "/" + module.getCategory().toString() + "/" + module.getName() + ".txt");
                file.createNewFile();

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

                for (Setting<?> setting : module.getSettings()) {
                    String value = String.valueOf(setting.getValue());
                    if (setting instanceof ColorSetting) {
                        Color color = ((ColorSetting) setting).getValue();
                        value = color.getRed() + ":" + color.getGreen() + ":" + color.getBlue() + ":" + color.getAlpha();
                    }
                    bufferedWriter.write(setting.getName() + ":" + value + "\n");
                }

                bufferedWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Setting<?> getSetting(Module module, String name) {
        return module.getSettings().stream().filter(setting -> setting.getName().equals(name)).findFirst().orElse(null);
    }

    private Setting<?> getSetting(HudModule module, String name) {
        return module.getSettings().stream().filter(setting -> setting.getName().equals(name)).findFirst().orElse(null);
    }
}
