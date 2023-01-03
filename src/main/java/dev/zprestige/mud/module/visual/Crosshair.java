package dev.zprestige.mud.module.visual;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.RenderOverlayEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.shader.impl.GradientShader;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.awt.*;

public class Crosshair extends Module {
    private final FloatSetting length = setting("Length", 0.3f, 0.3f, 10.0f).invokeTab("Crosshair");
    private final FloatSetting thickness = setting("Thickness", 1.0f, 0.5f, 5.0f).invokeTab("Crosshair");
    private final FloatSetting gap = setting("Gap", 1.0f, 0.1f, 5.0f).invokeTab("Crosshair");

    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Shader");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Shader");
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Shader");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Shader");

    @EventListener
    public void onRenderOverlay(RenderOverlayEvent event) {
        if (!event.getElementType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS)) {
            return;
        }
        float x = event.getScaledResolution().getScaledWidth() / 2.0f;
        float y = event.getScaledResolution().getScaledHeight() / 2.0f;
        float gap = this.gap.getValue() / 2.0f;
        float length = this.length.getValue();
        float thickness = this.thickness.getValue() / 2.0f;

        GradientShader.setup(step.getValue(), speed.getValue(), color1.getValue(), color2.getValue());

        RenderUtil.texture(x - gap - length, y - thickness, x - gap, y + thickness, Color.WHITE, RenderUtil.blank());
        RenderUtil.texture(x + gap, y - thickness, x + gap + length, y + thickness, Color.WHITE, RenderUtil.blank());

        RenderUtil.texture(x - thickness, y - gap - length, x + thickness, y - gap, Color.WHITE, RenderUtil.blank());
        RenderUtil.texture(x - thickness, y + gap, x + thickness, y + gap + length, Color.WHITE, RenderUtil.blank());

        GradientShader.finish();

        event.setCancelled(true);
    }
}
