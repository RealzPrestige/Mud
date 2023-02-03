package dev.zprestige.mud.ui.drawables.gui.category;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.gui.ScrollEvent;
import dev.zprestige.mud.module.Category;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.shader.impl.BlurShader;
import dev.zprestige.mud.shader.impl.ShadowShader;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.Drawable;
import dev.zprestige.mud.ui.drawables.gui.module.ModuleButton;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;

public class CategoryBar extends Drawable {
    private final ArrayList<ModuleButton> moduleButtons = new ArrayList<>();
    private final ResourceLocation
            left = new ResourceLocation("textures/icons/other/left.png"),
            right = new ResourceLocation("textures/icons/other/right.png");
    private final Category category;
    public float x, y, width, height, lAnim, rAnim;
    private float anim, target, shift;

    public CategoryBar(Category category) {
        this.category = category;

        float delta = 1.0f;
        float deltaX = 1.0f;
        boolean neg = true;
        for (Module module : Mud.moduleManager.getModules()) {
            if (module.getCategory() == category) {
                moduleButtons.add(new ModuleButton(module, deltaX * 120.0f));
                deltaX -= neg ? delta : -delta;
                neg = !neg;
                delta += 1.0f;
            }
        }
        Mud.eventBus.registerListener(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float y = this.y - (110.0f * (1.0f - anim));
        /* Animation */
        anim = MathUtil.lerp(anim, Interface.getActiveCategory() != null && Interface.getActiveCategory() == category ? 1.0f : 0.0f, Interface.getDelta());
        if (anim < 0.1f) {
            return;
        }

        /* Background */
        RenderUtil.rounded(x, y, x + width, y + height, 10.0f, barColor());

        /* Shadows */
        ShadowShader.invokeShadow();
        /* -> Background */
        RenderUtil.rounded(x, y, x + width, y + height, 10.0f, Color.WHITE);
        /* -> Left Circle */
        RenderUtil.circle(x + 15.0f, y + height / 2.0f - 5.0f, 10.0f, Color.WHITE);
        /* -> Right Circle */
        RenderUtil.circle(x + width - 25.0f, y + height / 2.0f - 5.0f, 10.0f, Color.WHITE);
        /* Release Shadow */
        ShadowShader.releaseShadow(5, 1);

        /* Left Circle */
        RenderUtil.circle(x + 15.0f, y + height / 2.0f - 5.0f, 10.0f, Interface.shade(5));

        /* Right Circle */
        RenderUtil.circle(x + width - 25.0f, y + height / 2.0f - 5.0f, 10.0f, Interface.shade(5));

        /* Arrow Left */
        RenderUtil.texture(x + 15.0f, y + height / 2.0f - 5f, x + 25.0f, y + height / 2.0f + 5.0f, new Color(1.0f, 1.0f, 1.0f, lAnim), left);
        lAnim = MathUtil.lerp(lAnim, insideLeft(mouseX, mouseY, y) ? 1.0f : 0.5f, Interface.getDelta());

        /* Arrow Right */
        RenderUtil.texture(x + width - 25.0f, y + height / 2.0f - 5f, x + width - 15.0f, y + height / 2.0f + 5.0f, new Color(1.0f, 1.0f, 1.0f, rAnim), right);
        rAnim = MathUtil.lerp(rAnim, insideRight(mouseX, mouseY, y) ? 1.0f : 0.5f, Interface.getDelta());

        /* Shift */
        shift = MathUtil.lerp(shift, target, Interface.getDelta());

        if (Interface.getActiveCategory() == null || Interface.getActiveCategory() != category) {
            target = 0.0f;
        }

        shift = MathUtil.lerp(shift, target, Interface.getDelta());

            float delta = 1.0f;
            float deltaX = 1.0f;
            boolean neg = true;
            for (ModuleButton moduleButton : moduleButtons) {
                if (!Interface.search.isEmpty() && !moduleButton.getModule().getName().toLowerCase().contains(Interface.search.toLowerCase())) {
                    continue;
                }
                moduleButton.deltaXTarget = deltaX * 120.0f;
                deltaX -= neg ? delta : -delta;
                neg = !neg;
                delta += 1.0f;
            }


        /* Setup Module Buttons */
        for (ModuleButton moduleButton : moduleButtons) {
            if (!Interface.search.isEmpty() && !moduleButton.getModule().getName().toLowerCase().contains(Interface.search.toLowerCase())) {
                continue;
            }
            moduleButton.x = x + 37.5f + moduleButton.deltaX + shift;
            moduleButton.y = y + 5.0f;
            moduleButton.height = height - 10.0f;
            moduleButton.width = 110.0f;
            moduleButton.guiY = this.y - 30.0f;
            moduleButton.guiX = x + 32.5f;
            moduleButton.guiWidth = x + width - 32.5f;
        }

        /* Blur background */
        BlurShader.invokeBlur();
        for (ModuleButton moduleButton : moduleButtons) {
            if (!Interface.search.isEmpty() && !moduleButton.getModule().getName().toLowerCase().contains(Interface.search.toLowerCase())) {
                continue;
            }
            /* Scissor Module Buttons */
            RenderUtil.prepareScissor(x + 32.5f, this.y - 30.0f, x + width - 32.5f, this.y + height);
            moduleButton.background();
            /* Release Scissor */
            RenderUtil.releaseScissor();
        }
        BlurShader.releaseBlur(10.0f);

        /* Shadow around */
        ShadowShader.invokeShadow();
        for (ModuleButton moduleButton : moduleButtons) {
            if (!Interface.search.isEmpty() && !moduleButton.getModule().getName().toLowerCase().contains(Interface.search.toLowerCase())) {
                continue;
            }
            /* Scissor Module Buttons */
            RenderUtil.prepareScissor(x + 32.5f, this.y - 30.0f, x + width - 32.5f, this.y + height);
            moduleButton.background();
            /* Release Scissor */
            RenderUtil.releaseScissor();
        }
        ShadowShader.releaseShadow(10, 1);

        /* Render each after blur & setup */
        for (ModuleButton moduleButton : moduleButtons) {
            if (!Interface.search.isEmpty() && !moduleButton.getModule().getName().toLowerCase().contains(Interface.search.toLowerCase())) {
                continue;
            }
            /* Scissor Module Buttons */
            RenderUtil.prepareScissor(x + 32.5f, this.y - 30.0f, x + width - 32.5f, this.y + height);
            moduleButton.drawScreen(mouseX, mouseY, partialTicks);
            /* Release Scissor */
            RenderUtil.releaseScissor();
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float y = this.y - (110.0f * (1.0f - anim));
        if (mouseButton == 0) {
            if (insideLeft(mouseX, mouseY, y)) {
                target += 120.0f;
            }
            if (insideRight(mouseX, mouseY, y)) {
                target -= 120.0f;
            }
        }
        for (ModuleButton moduleButton : moduleButtons) {
            if (moduleButton.x + moduleButton.width > x + 32.5f && moduleButton.x < x + width - 32.5f) {
                moduleButton.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (ModuleButton moduleButton : moduleButtons) {
            moduleButton.keyTyped(typedChar, keyCode);
        }
    }

    @EventListener
    public void onScroll(ScrollEvent event){
        if (event.getMouseX() > x && event.getMouseX() < x + width && event.getMouseY() > y && event.getMouseY() < y + height) {
            target += event.getAmount() / 5.0f;
        }
    }

    private boolean insideLeft(int mouseX, int mouseY, float y) {
        return mouseX > x + 15.0f && mouseX < x + 25.0f && mouseY > y + height / 2.0f - 5.0f && mouseY < y + height / 2.0f + 5.0f;
    }

    private boolean insideRight(int mouseX, int mouseY, float y) {
        return mouseX > x + width - 25.0f && mouseX < x + width - 15.0f && mouseY > y + height / 2.0f - 5.0f && mouseY < y + height / 2.0f + 5.0f;
    }

    public Color barColor() {
        return new Color(48, 51, 71);
    }
}
