package dev.zprestige.mud.ui;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.gui.GuiPrimaryEvent;
import dev.zprestige.mud.events.impl.gui.ScrollEvent;
import dev.zprestige.mud.events.impl.system.GuiClosedEvent;
import dev.zprestige.mud.module.Category;
import dev.zprestige.mud.shader.impl.ShadowShader;
import dev.zprestige.mud.ui.drawables.gui.screens.DrawableScreen;
import dev.zprestige.mud.ui.drawables.gui.screens.impl.ConfigScreen;
import dev.zprestige.mud.ui.drawables.gui.screens.impl.DefaultScreen;
import dev.zprestige.mud.ui.drawables.gui.screens.impl.FriendScreen;
import dev.zprestige.mud.ui.drawables.gui.screens.impl.HudEditorScreen;
import dev.zprestige.mud.ui.drawables.gui.sidebar.Sidebar;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

public class Interface extends GuiScreen {
    private final ResourceLocation
            logo = new ResourceLocation("textures/icons/other/logo.png"),
            searchIcon = new ResourceLocation("textures/icons/other/search.png");
    private final Color color = new Color(28, 31, 44);
    private static Category activeCategory = Category.Client;
    private final DrawableScreen
            defaultScreen = new DefaultScreen(),
            configScreen = new ConfigScreen(),
            hudEditorScreen = new HudEditorScreen(),
            friendScreen = new FriendScreen();

    private final Sidebar sidebar = new Sidebar();
    private final float
            guiWidth = 500.0f,
            guiHeight = 400.0f,
            sidebarWidth = 50.0f;
    private static long delta, lastFrame;
    private float x, y, searchCol;
    public static String selectedScreen = "Default";
    public static String search = "";
    private boolean searching;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        /* Setup x and y */
        x = width / 2.0f - guiWidth / 2.0f;
        y = height / 2.0f - guiHeight / 2.0f;

        /* Background */
        RenderUtil.rounded(x, y, x + guiWidth, y + guiHeight, 10.0f, color);

        /* Background Shadow */
        ShadowShader.invokeShadow();
        RenderUtil.rounded(x, y, x + guiWidth, y + guiHeight, 10.0f, color);
        ShadowShader.releaseShadow(40, 1);

        /* Icon */
        float textureSize = 20.0f;
        RenderUtil.texture(x + sidebarWidth + 15.0f, y + 5.0f, x + sidebarWidth + 15.0f + textureSize, y + 5.0f + textureSize, Color.WHITE, logo);

        /* Icon Shadow */
        ShadowShader.invokeShadow();
        RenderUtil.texture(x + sidebarWidth + 15.0f, y + 5.0f, x + sidebarWidth + 15.0f + textureSize, y + 5.0f + textureSize, Color.WHITE, logo);
        ShadowShader.releaseShadow(1, 1);

        /* Watermark */
        float scale = 1.5f;
        RenderUtil.invokeScale(scale);
        Mud.fontManager.guiString(Mud.MODNAME, (x + sidebarWidth + 20.0f + textureSize) / scale, (y + 17.5f - Mud.fontManager.stringHeight()) / scale, Color.WHITE);
        RenderUtil.resetScale();
        Mud.fontManager.guiString(Mud.VERSION, x + sidebarWidth + 21.5f + Mud.fontManager.stringWidth(Mud.MODNAME) * 1.5f + textureSize, y + 21.0f - Mud.fontManager.stringHeight(), Color.WHITE);

        /* Search bar */
        RenderUtil.rounded(x + guiWidth - 150.0f, y + 5.0f, x + guiWidth - 10.0f, y + 25.0f, 10.0f, shade(2));

        /* Search bar Shadow */
        ShadowShader.invokeShadow();
        RenderUtil.rounded(x + guiWidth - 150.0f, y + 5.0f, x + guiWidth - 10.0f, y + 25.0f, 10.0f, Color.WHITE);
        ShadowShader.releaseShadow(5, 1);

        /* Search icon */
        RenderUtil.texture(x + guiWidth - 30.0f, y + 10.0f, x + guiWidth - 20.0f, y + 20.0f, new Color(0, 0, 0, 50), searchIcon);

        /* Search Text */
        searchCol = MathUtil.lerp(searchCol, search.isEmpty() ? 0.5f : 1.0f, getDelta());
        String text = (search.isEmpty() ? "Search" : search) + (searching ? typingIcon() : "");

        /* Search Scissor */
        RenderUtil.prepareScissor(x, y, x + guiWidth - 30.0f, y + height);

        Mud.fontManager.string(text, x + guiWidth - 140.0f, y + 17.5f - Mud.fontManager.stringHeight(), new Color(searchCol, searchCol, searchCol, searchCol));

        /* Release Search scissor */
        RenderUtil.releaseScissor();

        /* ConfigScreen */
        configScreen.x = x;
        configScreen.y = y;
        configScreen.drawScreen(mouseX, mouseY, partialTicks);


        /* Default Screen */
        defaultScreen.x = x;
        defaultScreen.y = y;
        defaultScreen.drawScreen(mouseX, mouseY, partialTicks);

        /* Default Screen */
        hudEditorScreen.x = x;
        hudEditorScreen.y = y;
        hudEditorScreen.drawScreen(mouseX, mouseY, partialTicks);

        /* Default Screen */
        hudEditorScreen.x = x;
        hudEditorScreen.y = y;
        hudEditorScreen.drawScreen(mouseX, mouseY, partialTicks);

        /* Friends */
        friendScreen.x = x + 5.0f;
        friendScreen.y = y + 5.0f;
        friendScreen.drawScreen(mouseX, mouseY, partialTicks);


        /* Sidebar */
        sidebar.x = x + 5.0f;
        sidebar.y = y + 5.0f;
        sidebar.height = guiHeight - 10.0f;
        sidebar.drawScreen(mouseX, mouseY, partialTicks);

        /* Delta time */
        delta = System.currentTimeMillis() - lastFrame;
        lastFrame = System.currentTimeMillis();

        int wheel = Mouse.getDWheel();
        if (wheel != 0){
            ScrollEvent event = new ScrollEvent(mouseX, mouseY, wheel);
            Mud.eventBus.invoke(event);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        sidebar.mouseClicked(mouseX, mouseY, mouseButton);

        defaultScreen.mouseClicked(mouseX, mouseY, mouseButton);
        configScreen.mouseClicked(mouseX, mouseY, mouseButton);
        hudEditorScreen.mouseClicked(mouseX, mouseY, mouseButton);
        friendScreen.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            if (insideSearch(mouseX, mouseY)) {
                searching = !searching;
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (searching) {
            search = type(search, typedChar, keyCode);
        }

        defaultScreen.keyTyped(typedChar, keyCode);
        configScreen.keyTyped(typedChar, keyCode);
        friendScreen.keyTyped(typedChar, keyCode);

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        GuiClosedEvent event = new GuiClosedEvent();
        Mud.eventBus.invoke(event);
    }

    public String type(String string, char typedChar, int keyCode) {
        String newString = string;
        switch (keyCode) {
            case Keyboard.KEY_BACK:
                if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                    newString = "";
                }
                if (newString.length() > 0) {
                    newString = newString.substring(0, newString.length() - 1);
                }
                break;
            case Keyboard.KEY_RETURN:
                searching = false;
                break;
            default:
                if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    newString = newString + typedChar;
                    break;
                }
        }
        return newString;
    }

    private boolean insideSearch(int mouseX, int mouseY) {
        return mouseX > x + guiWidth - 150.0f && mouseX < x + guiWidth - 10.0f && mouseY > y + 5.0f && mouseY < y + 25.0f;
    }

    private long sys = 0L;

    private String typingIcon() {
        if (System.currentTimeMillis() - sys > 1000) {
            sys = System.currentTimeMillis();
            return "";
        }
        if (System.currentTimeMillis() - sys > 500) {
            return "_";
        }
        return "";
    }

    public static Color primary() {
        GuiPrimaryEvent event = new GuiPrimaryEvent(Color.WHITE);
        Mud.eventBus.invoke(event);
        return event.getColor();
    }

    public static Color shade(int i) {
        return new Color(28 + i, 31 + i, 44 + i);
    }

    public static Color shade(int i, int alpha) {
        return new Color(28 + i, 31 + i, 44 + i, alpha);
    }

    public static Category getActiveCategory() {
        return activeCategory;
    }

    public static void setActiveCategory(Category activeCategory) {
        Interface.activeCategory = activeCategory;
    }

    public static float getDelta() {
        return delta * 0.01f;
    }
}
