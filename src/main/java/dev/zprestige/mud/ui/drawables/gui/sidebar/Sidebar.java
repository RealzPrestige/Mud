package dev.zprestige.mud.ui.drawables.gui.sidebar;

import dev.zprestige.mud.module.Category;
import dev.zprestige.mud.shader.impl.ShadowShader;
import dev.zprestige.mud.ui.Interface;
import dev.zprestige.mud.ui.drawables.Drawable;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;

public class Sidebar extends Drawable {
    private final ResourceLocation resourceLocation = new ResourceLocation("textures/icons/other/menu.png");
    private final ArrayList<SidebarCategory> sidebarCategories = new ArrayList<>();
    private final ArrayList<SidebarItem> sidebarItems = new ArrayList<>();
    private float sidebarWidth = 50.0f;
    public float x, y, height;
    private float hoverAnim;
    private boolean open;

    public Sidebar() {
        for (Category category : Category.values()) {
            sidebarCategories.add(new SidebarCategory(category));
        }
        sidebarItems.add(new SidebarItem("Configs"));
        sidebarItems.add(new SidebarItem("HudEditor"));
        sidebarItems.add(new SidebarItem("Friends"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        /* Sidebar */
        RenderUtil.rounded(x, y, x + sidebarWidth, y + height, 10.0f, sidebarColor());

        /* Sidebar Shadow */
        ShadowShader.invokeShadow();
        RenderUtil.rounded(x, y, x + sidebarWidth, y + height, 10.0f, Color.WHITE);
        ShadowShader.releaseShadow(5, 1);

        /* Menu icon */
        float textureSize = 21.0f, textureOffset = 5.0f;
        Color color = new Color(255 - ((int) ((255 - Interface.primary().getRed()) * hoverAnim)), 255 - ((int) ((255 - Interface.primary().getGreen()) * hoverAnim)), 255 - ((int) ((255 - Interface.primary().getBlue()) * hoverAnim)));
        RenderUtil.texture(x + 15.5f, y + textureOffset, x + 15.5f + textureSize, y + textureOffset + textureSize, color, resourceLocation);

        /* Icon shadow */
        ShadowShader.invokeShadow();
        RenderUtil.texture(x + 15.5f, y + textureOffset, x + 15.5f + textureSize, y + textureOffset + textureSize, Color.WHITE, resourceLocation);
        ShadowShader.releaseShadow(1, 1);

        /* Color for Menu icon */
        hoverAnim = MathUtil.lerp(hoverAnim, insideMenu(mouseX, mouseY) ? 1.0f : 0.0f, Interface.getDelta());

        /* Setup & render SidebarCategories */
        float deltaY = 45.0f;
        for (SidebarCategory sidebarCategory : sidebarCategories) {
            sidebarCategory.x = x + 15.0f;
            sidebarCategory.y = y + deltaY;
            sidebarCategory.sidebarWidth = sidebarWidth;

            /* Scissor SidebarCategories */
            RenderUtil.prepareScissor(x, y, x + sidebarWidth, y + height);

            sidebarCategory.drawScreen(mouseX, mouseY, partialTicks);

            /* Release Scissor */
            RenderUtil.releaseScissor();
            deltaY += 30.0f;
        }

        deltaY += 10.0f;
        for (SidebarItem sidebarItem : sidebarItems) {
            sidebarItem.x = x + 15.0f;
            sidebarItem.y = y + deltaY;
            sidebarItem.sidebarWidth = sidebarWidth;
            /* Scissor SidebarCategories */
            RenderUtil.prepareScissor(x, y, x + sidebarWidth, y + height);

            sidebarItem.drawScreen(mouseX, mouseY, partialTicks);

            /* Release Scissor */
            RenderUtil.releaseScissor();
            deltaY += 30.0f;
        }


        /* Set width */
        sidebarWidth = MathUtil.lerp(sidebarWidth, open ? 100.0f : 50.0f, Interface.getDelta());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && insideMenu(mouseX, mouseY)) {
            open = !open;
        }
        for (SidebarCategory sidebarCategory : sidebarCategories) {
            sidebarCategory.mouseClicked(mouseX, mouseY, mouseButton);
        }
        for (SidebarItem sidebarItem : sidebarItems) {
            sidebarItem.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    /* Inside Menu icon */
    private boolean insideMenu(int mouseX, int mouseY) {
        float textureSize = 21.0f, textureOffset = 5.0f;
        return mouseX > x + 15.0f && mouseX < x + sidebarWidth - 15.0f && mouseY > y + textureOffset && mouseY < y + textureOffset + textureSize;
    }

    public Color sidebarColor() {
        return new Color(48, 51, 71);
    }
}
