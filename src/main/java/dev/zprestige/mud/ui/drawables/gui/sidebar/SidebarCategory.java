package dev.zprestige.mud.ui.drawables.gui.sidebar;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.module.Category;
import dev.zprestige.mud.shader.impl.ShadowShader;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.Drawable;
import dev.zprestige.mud.ui.drawables.gui.screens.impl.DefaultScreen;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class SidebarCategory extends Drawable {
    private final ResourceLocation resourceLocation;
    private final float textureSize = 20.0f;
    private final Category category;
    public float x, y, sidebarWidth;
    private float hoverAnim;

    public SidebarCategory(Category category) {
        resourceLocation = new ResourceLocation("textures/icons/categories/" + category.toString().toLowerCase() + ".png");
        this.category = category;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        /* Render icon */
        Color color = new Color(255 - ((int) ((255 - Interface.primary().getRed()) * hoverAnim)), 255 - ((int) ((255 - Interface.primary().getGreen()) * hoverAnim)), 255 - ((int) ((255 - Interface.primary().getBlue()) * hoverAnim)));
        RenderUtil.texture(x, y, x + textureSize, y + textureSize, color, resourceLocation);

        /* Icon shadow */
        ShadowShader.invokeShadow();
        RenderUtil.texture(x, y, x + textureSize, y + textureSize, Color.WHITE, resourceLocation);
        ShadowShader.releaseShadow(1, 1);

        /* Color for Menu icon */
        hoverAnim = MathUtil.lerp(hoverAnim, Interface.getActiveCategory() != null && Interface.getActiveCategory() == category ? inside(mouseX, mouseY) ? 0.7f : 1.0f : inside(mouseX, mouseY) ? 0.7f : 0.0f, Interface.getDelta());

        /* Text */
        Mud.fontManager.guiString(category.toString(), x + textureSize + 20.0f - (30.0f * (sidebarWidth / 100.0f - 0.5f)), y + textureSize / 2.0f - Mud.fontManager.stringHeight() / 2.0f, Color.WHITE);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && inside(mouseX, mouseY) && (Interface.getActiveCategory() == null || Interface.getActiveCategory() != category)) {
            Interface.setActiveCategory(category);
            DefaultScreen.setActiveModule(null);
            Interface.selectedScreen = "Default";
        }
    }

    public boolean inside(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + textureSize && mouseY > y && mouseY < y + textureSize;
    }
}
