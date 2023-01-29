package dev.zprestige.mud.ui.drawables.gui.module;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.Drawable;
import dev.zprestige.mud.ui.drawables.gui.screens.impl.DefaultScreen;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class ModuleButton extends Drawable {
    private final Module module;
    private final ResourceLocation icon;
    public float deltaX;
    public float deltaXTarget;
    public float x, y, height, width,
            hover, guiY, alpha, c,
            guiX, guiWidth;
    private boolean binding = false;

    public ModuleButton(Module module, float deltaX) {
        this.module = module;
        this.icon = new ResourceLocation(
                "textures/icons/categories/"
                        + module.getCategory().toString().toLowerCase()
                        + "/" + module.getName().toLowerCase().replace(" ", "")
                        + ".png");
        this.deltaX = deltaX;
        this.deltaXTarget = deltaX;
    }

    public void background() {
        /* Blur & Shadow */
        RenderUtil.rounded(x, y, x + width, y + height, 7.0f, Color.WHITE);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        deltaX = MathUtil.lerp(deltaX, deltaXTarget, Interface.getDelta());
        float barY = y + height - 15.0f;

        /* Bottom bar */
        RenderUtil.setupDefault(Interface.shade(3));
        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(x, barY);
        RenderUtil.corner(x, barY, x + width, y + height, 7.0f, 2);
        RenderUtil.corner(x, barY, x + width, y + height, 7.0f, 3);
        glVertex2f(x + width, barY);
        RenderUtil.releaseDefault();


        /* Name */
        String text = module.getName();
        Mud.fontManager.guiString(text, x + width / 2.0f - Mud.fontManager.stringWidth(text) / 2.0f, y + 5.0f, Color.WHITE);

        /* Icon */
        RenderUtil.texture(x + width / 2.0f - 14.0f, y + 16.0f, x + width / 2.0f + 16.0f, y + 46.0f, new Color(0, 0, 0, 50), icon);
        RenderUtil.texture(x + width / 2.0f - 15.0f, y + 15.0f, x + width / 2.0f + 15.0f, y + 45.0f, Color.WHITE, icon);

        hover = MathUtil.lerp(hover, insideKey(mouseX, mouseY) ? 0.2f : 0.0f, Interface.getDelta());

        /* Key background */
        RenderUtil.setupDefault(Interface.shade(5));
        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(x, barY);
        RenderUtil.corner(x, barY, x + 40.0f, y + height, 7.0f, 2);
        glVertex2f(x + 40.0f, y + height);
        glVertex2f(x + 40.0f, barY);
        RenderUtil.releaseDefault();

        /* Hover key */
        RenderUtil.setupDefault(new Color(0.0f, 0.0f, 0.0f, hover));
        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(x, barY);
        RenderUtil.corner(x, barY, x + 40.0f, y + height, 7.0f, 2);
        glVertex2f(x + 40.0f, y + height);
        glVertex2f(x + 40.0f, barY);
        RenderUtil.releaseDefault();

        /* Enabled background */
        RenderUtil.rounded(x + width - 30.0f, barY + 2.5f, x + width - 5.0f, y + height - 2.5f, 5.0f, Interface.shade(5));
        RenderUtil.roundedOutline(x + width - 30.0f, barY + 2.5f, x + width - 5.0f, y + height - 2.5f, 5.0f, Interface.shade(-2));

        /* Circle */
        alpha = MathUtil.lerp(alpha, module.getEnabled().getValue() ? insideEnabled(mouseX, mouseY) ? 0.7f : 1.0f : insideEnabled(mouseX, mouseY) ? 0.7f : 0.4f, Interface.getDelta());
        c = MathUtil.lerp(c, module.getEnabled().getValue() ? 1.0f : 0.0f, Interface.getDelta());
        Color p = Interface.primary();
        Color color = new Color(p.getRed() / 255.0f, p.getGreen() / 255.0f, p.getBlue() / 255.0f, alpha);
        RenderUtil.circle(x + width - 26.0f + (15.0f * c), barY + 5.5f, 4.0f, color);

        /* Prepare Scissor */
        RenderUtil.prepareScissor(Math.min(guiWidth, Math.max(x, guiX)), guiY, Math.min(guiWidth, Math.max(x + 40.0f, guiX)), Math.max(guiY, y + height));

        /* Scale */
        float scale = 0.9f;
        RenderUtil.invokeScale(scale);

        /* Key Text */
        String keyText = binding ? dots() : module.getKeybind().getValue() == Keyboard.KEY_NONE ? "None" : Keyboard.getKeyName(module.getKeybind().getValue());
        Mud.fontManager.guiString(keyText, (x + 20.0f - Mud.fontManager.stringWidth(keyText) * scale / 2.0f) / scale, (barY + Mud.fontManager.stringHeight()) / scale, Color.WHITE);

        /* Reset Scale */
        RenderUtil.resetScale();

        /* Release Scissor */
        RenderUtil.releaseScissor();

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            if (insideKey(mouseX, mouseY)) {
                binding = !binding;
            }
            if (insideEnabled(mouseX, mouseY)) {
                module.toggle();
            }
            if (outsideBar(mouseX, mouseY)) {
                DefaultScreen.setActiveModule(module);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (binding) {
            if (keyCode == Keyboard.KEY_DELETE || keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_ESCAPE) {
                module.getKeybind().invokeValue(Keyboard.KEY_NONE);
                binding = false;
            } else if (keyCode == Keyboard.KEY_RETURN) {
                binding = false;
            } else {
                module.getKeybind().invokeValue(keyCode);
                binding = false;
            }
        }
    }

    private boolean outsideBar(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + width & mouseY > y && mouseY < y + height - 15.0f;

    }

    private boolean insideEnabled(int mouseX, int mouseY) {
        return mouseX > x + width - 30.0f && mouseX < x + width - 5.0f & mouseY > y + height - 12.5f && mouseY < y + height - 2.5f;
    }

    private boolean insideKey(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + 40.0f & mouseY > y + height - 15.0f && mouseY < y + height;
    }

    private long sys = 0L;

    private String dots() {
        float diff = System.currentTimeMillis() - sys;
        if (diff > 1333) {
            sys = System.currentTimeMillis();
            return "...";
        }
        if (diff > 999) {
            return "...";
        }
        if (diff > 666) {
            return "..";
        }
        if (diff > 333) {
            return ".";
        }
        return "";
    }

    public Module getModule() {
        return module;
    }
}
