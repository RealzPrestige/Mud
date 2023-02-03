package dev.zprestige.mud.ui.drawables.gui.screens.impl;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.gui.ScrollEvent;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.Drawable;
import dev.zprestige.mud.ui.drawables.gui.screens.DrawableScreen;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

public class FriendScreen extends DrawableScreen {
    private float anim, scroll, scrollTarget;
    private String search = "";
    private boolean searching;
    private long sys;

    public FriendScreen(){
        Mud.eventBus.registerListener(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Interface.selectedScreen.equals("Friends")) {
            sys = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - sys > 500) {
            return;
        }
        anim = MathUtil.lerp(anim, Interface.selectedScreen.equals("Friends") ? 0.0f : 1.0f, Interface.getDelta());

        float width = guiWidth - sidebarWidth - 25.0f;
        float x = this.x + sidebarWidth + 15.0f;
        float exit = width / 1.5f * anim;

        float leftX = x - exit,
                rightX = x + width / 2.0f + 5.0f + exit,
                leftWidth = x + width / 2.0f - 5.0f - exit,
                rightWidth = this.x + guiWidth - 10.0f + exit,
                boxY = y + categoryBarY + 30.0f,
                boxHeight = y + 30.0f + guiHeight - 100.0f;

        if (!Interface.selectedScreen.equals("Friends")) {
            scrollTarget = 0.0f;
        }

        scroll = MathUtil.lerp(scroll, scrollTarget, Interface.getDelta());

        GL11.glLineWidth(1.0f);
        RenderUtil.prepareScissor(x, y, this.x + guiWidth - 5.0f, boxHeight);

        RenderUtil.rounded(rightX - 105.0f + exit, boxY - 30.0f, rightX + 95.0f + exit, boxY - 10.0f, 5.0f, shade(5));
        RenderUtil.roundedOutline(rightX - 105.0f + exit, boxY - 30.0f, rightX + 95.0f + exit, boxY - 10.0f, 5.0f, shade(-3));

        RenderUtil.releaseScissor();

        RenderUtil.prepareScissor(x, y, Math.min(this.x + guiWidth - 5.0f, rightX + 95.0f), boxHeight);
        String text = (search.isEmpty() ? "Search" : search) + (searching ? typingIcon() : ""),
                textW = (search.isEmpty() ? "Search" : search);
        Mud.fontManager.guiString(text, rightX - 5.0f - Mud.fontManager.stringWidth(textW) / 2.0f, boxY - 20.0f - Mud.fontManager.stringHeight() / 2.0f, Color.WHITE);
        RenderUtil.releaseScissor();


        RenderUtil.prepareScissor(x, boxY, this.x + guiWidth - 5.0f, boxHeight);

        RenderUtil.rounded(leftX, boxY, leftWidth, boxHeight, 5.0f, shade(5));
        RenderUtil.rounded(rightX, boxY, rightWidth, boxHeight, 5.0f, shade(5));
        RenderUtil.roundedOutline(leftX, boxY, leftWidth, boxHeight, 5.0f, shade(-3));
        RenderUtil.roundedOutline(rightX, boxY, rightWidth, boxHeight, 5.0f, shade(-3));

        float leftY = boxY + 10.0f + scroll, rightY = boxY + 10.0f + scroll;
        for (NetworkPlayerInfo networkPlayerInfo : mc.player.connection.getPlayerInfoMap()) {
            if (networkPlayerInfo.getGameProfile().getName().equals(mc.player.getName())) {
                continue;
            }
            Person person = new Person(networkPlayerInfo);
            if (!networkPlayerInfo.getGameProfile().getName().toLowerCase().contains(search.toLowerCase())) {
                continue;
            }
            person.x = (person.friend ? rightX : leftX) + 5.0f;
            person.y = (person.friend ? rightY : leftY);
            person.width = (person.friend ? rightWidth - rightX : leftWidth - leftX) - 10.0f;
            person.height = 20.0f;
            person.drawScreen(mouseX, mouseY, partialTicks);
            if (person.friend) {
                rightY += 25.0f;
            } else {
                leftY += 25.0f;
            }
        }

        RenderUtil.releaseScissor();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (System.currentTimeMillis() - sys > 500) {
            return;
        }
        float width = guiWidth - sidebarWidth - 25.0f,
                x = this.x + sidebarWidth + 15.0f,
                exit = width / 1.5f * anim,
                leftX = x - exit,
                rightX = x + width / 2.0f + 5.0f + exit,
                leftWidth = x + width / 2.0f - 5.0f - exit,
                rightWidth = this.x + guiWidth - 10.0f + exit,
                boxY = y + categoryBarY + 30.0f,
                boxHeight = y + 30.0f + guiHeight - 100.0f;
        if (mouseButton == 0) {
            if (mouseX > rightX - 105.0f && mouseX < rightX + 95.0f && mouseY > boxY - 30.0f && mouseY < boxY - 10.0f) {
                searching = !searching;
            }
        }
        float leftY = boxY + 10.0f + scroll, rightY = boxY + 10.0f + scroll;
        for (NetworkPlayerInfo networkPlayerInfo : mc.player.connection.getPlayerInfoMap()) {
            if (networkPlayerInfo.getGameProfile().getName().equals(mc.player.getName())) {
                continue;
            }
            Person person = new Person(networkPlayerInfo);
            if (!networkPlayerInfo.getGameProfile().getName().toLowerCase().contains(search.toLowerCase())) {
                continue;
            }
            person.x = (person.friend ? rightX : leftX) + 5.0f;
            person.y = (person.friend ? rightY : leftY);
            person.width = (person.friend ? rightWidth - rightX : leftWidth - leftX) - 10.0f;
            person.height = 20.0f;
            if (person.y > boxY && mouseY < boxHeight) {
                person.mouseClicked(mouseX, mouseY, mouseButton);
            }
            if (person.friend) {
                rightY += 25.0f;
            } else {
                leftY += 25.0f;
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (System.currentTimeMillis() - sys > 500) {
            return;
        }
        if (searching) {
            search = type(search, typedChar, keyCode);
        }
    }

    @EventListener
    public void onScroll(ScrollEvent event){
        if (event.getMouseX() > x && event.getMouseX() < this.x + guiWidth && event.getMouseY() > y && event.getMouseY() < y + guiHeight) {
            if (Interface.selectedScreen.equals("Friends")) {
                scrollTarget += event.getAmount() / 10.0f;
            }
        }
    }
    private long sys1 = 0L;

    private String typingIcon() {
        if (System.currentTimeMillis() - sys1 > 1000) {
            sys1 = System.currentTimeMillis();
            return "";
        }
        if (System.currentTimeMillis() - sys1 > 500) {
            return "_";
        }
        return "";
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

    private void renderPlayerHead(NetworkPlayerInfo networkPlayerInfo, int x, int y) {
        mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(x, y, 16.0f, 17.0f, 15, 15, 15, 15, 128.0f, 128.0f);
    }


    public class Person extends Drawable {
        private final NetworkPlayerInfo networkPlayerInfo;
        private float x, y, width, height;
        private final boolean friend;

        public Person(NetworkPlayerInfo networkPlayerInfo) {
            this.networkPlayerInfo = networkPlayerInfo;
            friend = Mud.friendManager.contains(networkPlayerInfo.getGameProfile().getName());
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            if (networkPlayerInfo != null && mc.getConnection() != null) {
                RenderUtil.rounded(x, y, x + width, y + height, 5.0f, shade(5));
                RenderUtil.roundedOutline(x, y, x + width, y + height, 5.0f, friend ? Color.CYAN : shade(-3));
                renderPlayerHead(Objects.requireNonNull(Objects.requireNonNull(mc.getConnection()).getPlayerInfo(networkPlayerInfo.getGameProfile().getName())), (int) (x + 5), (int) (y + 2.5f));
                Mud.fontManager.guiString(networkPlayerInfo.getGameProfile().getName(), x + 25.0f, y + height / 2.0f - Mud.fontManager.stringHeight() / 2.0f, Color.WHITE);
            }
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            if (mouseButton == 0) {
                if (inside(mouseX, mouseY)) {
                    if (friend) {
                        Mud.friendManager.remove(networkPlayerInfo.getGameProfile().getName());
                    } else {
                        Mud.friendManager.add(networkPlayerInfo.getGameProfile().getName());
                    }
                }
            }
        }

        private boolean inside(int mouseX, int mouseY) {
            return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
        }
    }

    public Color color() {
        return new Color(43, 46, 66);
    }

    public static Color shade(int i) {
        return new Color(43 + i, 46 + i, 66 + i);
    }
}
