package dev.zprestige.mud.module.visual;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.chat.GuiChatTypeEvent;
import dev.zprestige.mud.events.impl.render.Render2DPostEvent;
import dev.zprestige.mud.events.impl.render.RenderChatEvent;
import dev.zprestige.mud.events.impl.render.RenderTextBoxEvent;
import dev.zprestige.mud.events.impl.system.CustomChatEvent;
import dev.zprestige.mud.events.impl.system.DisconnectEvent;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.manager.EventManager;
import dev.zprestige.mud.mixins.interfaces.IGuiNewChat;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.shader.impl.GradientShader;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class CustomChat extends Module {
    private final FloatSetting y = setting("Y", 0.0f, 0.0f, 500.0f);
    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Render");
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Render");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Render");
    private ArrayList<Message> messages = new ArrayList<>();
    private ArrayList<String> strings = new ArrayList<>();
    private float scroll, scrollTarget;

    @EventListener
    public void onDisconnect(DisconnectEvent event) {
        messages.clear();
    }

    @EventListener
    public void onRender2DPost(Render2DPostEvent event) {
        y.max = new ScaledResolution(mc).getScaledHeight();

        float stringY = 0.0f;
        for (String ignored : strings) {
            stringY += 10.0f;
        }
        RenderUtil.rounded(2.5f, 102.5f + y.getValue(), 252.5f, 122.5f + (stringY - 10.0f) + y.getValue(), 3.0f, new Color(0, 0, 0, 100));

        RenderUtil.prepareScissor(0.0f, y.getValue(), mc.displayWidth, y.getValue() + 100.0f);

        ArrayList<Runnable> gradients = new ArrayList<>();

        float deltaY = 80.0f + scroll + y.getValue();
        for (Message message : new ArrayList<>(messages)) {
            if (deltaY < y.getValue()) {
                continue;
            }
            String text = message.getText();
            float time = message.getFactor();
            float x = 2.5f - (Mud.fontManager.stringWidth(text) * (1.0f - time));

            float deltaS = 7.0f;
            for (String ignored : message.getStrings()) {
                deltaS += 10.0f;
            }
            deltaY -= deltaS - 17.0f;

            RenderUtil.rounded(x, deltaY + 2.5f + 10.0f * (1.0f - time), x + 250.0f, deltaY + deltaS + 10.0f * (1.0f - time), 3.0f, new Color(0, 0, 0, 100));

            float finalDeltaY = deltaY, finalDeltaS = deltaS;
            gradients.add(() -> RenderUtil.roundedOutlineTex(x, finalDeltaY + 2.5f + 10.0f * (1.0f - time), x + 250.0f, finalDeltaY + finalDeltaS + 10.0f * (1.0f - time), 3.0f, Color.WHITE));

            deltaS = 7.0f;
            for (String string : message.getStrings()) {
                float diff = 0.0f;
                float y1 = deltaY + deltaS - 1.0f + 10.0f * (1.0f - time);
                if (string.startsWith("[Mud]")) {
                    Mud.fontManager.stringNoShadow("[Mud]", x + 5.5f, y1 + 0.5f, new Color(0, 0, 0, 150));
                    GradientShader.setup(step.getValue(), speed.getValue(), color1.getValue(), color2.getValue());
                    Mud.fontManager.stringNoShadow("[Mud]", x + 5.0f, y1, Color.WHITE);
                    GradientShader.finish();
                    diff += Mud.fontManager.stringWidth("[Mud] ");
                }
                if (string.contains("\uDE82")) {
                    float stringWidth = Mud.fontManager.stringWidth(string.replace("[Mud]", "").replace("\uDE82", ""));
                    RenderUtil.invokeScale(0.8f);
                    mc.fontRenderer.drawString("d", -43, 1, -1);
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Items.TOTEM_OF_UNDYING), (int) ((x - 2.5f + stringWidth) / 0.8f), (int) ((y1 - 2.5f) / 0.8f));
                    RenderHelper.disableStandardItemLighting();
                    RenderUtil.resetScale();
                }
                Mud.fontManager.string(string.replace("[Mud]", "").replace("\uDE82", ""), x + 5.0f + diff, y1, Color.WHITE);
                deltaS += 10.0f;
            }
            deltaY -= 20.0f * time;
        }

        GradientShader.setup(step.getValue(), speed.getValue(), color1.getValue(), color2.getValue());

        gradients.forEach(Runnable::run);

        RenderUtil.releaseScissor();

        RenderUtil.roundedOutlineTex(2.5f, y.getValue() + 102.5f, 252.5f, y.getValue() + 122.5f + (stringY - 10.0f), 3.0f, Color.WHITE);
        GradientShader.finish();

        stringY = 0.0f;
        for (String string : strings) {
            Mud.fontManager.string(string + ((mc.currentScreen instanceof GuiChat) ? typingIcon() : ""), 5.0f, y.getValue() + 107.5f + stringY, Color.WHITE);
            stringY += 10.0f;
        }

        scroll = MathUtil.lerp(scroll, scrollTarget, EventManager.getDeltaTime());
        if (mc.currentScreen instanceof GuiChat) {
            scrollTarget += Mouse.getDWheel() / 10.0f;
        } else {
            scrollTarget = 0.0f;
            strings = new ArrayList<>();
            strings.add("");
        }
    }

    @EventListener
    public void onTick(TickEvent event) {
        Mud.threadManager.invokeThread(() -> {
            for (ChatLine chatLine : new ArrayList<>(((IGuiNewChat) mc.ingameGUI.getChatGUI()).getDrawnChatLines())) {
                String text = chatLine.getChatComponent().getFormattedText();
                if (messages.stream().anyMatch(message -> text.equals(message.getText()))) {
                    continue;
                }
                messages.add(new Message(text));
                messages = messages.stream().sorted(Comparator.comparing(Message::getTime)).collect(Collectors.toCollection(ArrayList::new));
            }
        });
    }

    @EventListener
    public void onChatTyped(GuiChatTypeEvent event) {
        ArrayList<String> strings = new ArrayList<>();

        StringBuilder current = new StringBuilder();
        for (Character chr : event.getText().toCharArray()) {
            if (Mud.fontManager.stringWidth(current.toString()) >= 235.0f) {
                strings.add(current.toString());
                current = new StringBuilder();
            }

            current.append(chr);
        }
        strings.add(current.toString());
        this.strings = strings;
    }

    @EventListener
    public void onRenderTextBox(RenderTextBoxEvent event) {
        event.setCancelled(true);
    }


    @EventListener
    public void onRenderChat(RenderChatEvent event) {
        event.setCancelled(true);
    }

    @EventListener
    public void onCustomChat(CustomChatEvent event) {
        event.setCancelled(true);
    }

    private long s = System.currentTimeMillis();

    private String typingIcon() {
        if (System.currentTimeMillis() - s < 500) {
            return "";
        }
        if (System.currentTimeMillis() - s < 1000) {
            return "_";
        }
        s = System.currentTimeMillis();
        return "";
    }

    private static class Message {
        private final ArrayList<String> strings;
        private final String text;
        private final long sys;

        public Message(String text) {
            this.text = text;
            this.sys = System.currentTimeMillis();
            this.strings = new ArrayList<>();

            StringBuilder current = new StringBuilder();
            for (Character chr : text.toCharArray()) {
                if (Mud.fontManager.stringWidth(current.toString()) >= 240.0f) {
                    strings.add(current.toString());
                    current = new StringBuilder();
                }

                current.append(chr);
            }
            strings.add(current.toString());
        }

        public String getText() {
            return text;
        }

        public float getFactor() {
            return Math.min(1.0f, (System.currentTimeMillis() - sys) / 250.0f);
        }

        public float getTime() {
            return System.currentTimeMillis() - sys;
        }

        public ArrayList<String> getStrings() {
            return strings;
        }

    }
}
