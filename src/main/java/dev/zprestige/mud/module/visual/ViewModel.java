package dev.zprestige.mud.module.visual;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.RenderItemEvent;
import dev.zprestige.mud.events.impl.render.RotateArmEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

import static org.lwjgl.opengl.GL11.*;

public class ViewModel extends Module {
    private final BooleanSetting staticHands = setting("Static Hands", false);

    private final FloatSetting mainHandX = setting("Mainhand X", 0.0f, -10.0f, 10.0f).invokeTab("Mainhand");
    private final FloatSetting mainHandY = setting("Mainhand Y", 0.0f, -10.0f, 10.0f).invokeTab("Mainhand");
    private final FloatSetting mainHandZ = setting("Mainhand Z", 0.0f, -10.0f, 10.0f).invokeTab("Mainhand");
    private final FloatSetting mainHandScaleX = setting("Mainhand Scale X", 0.0f, -10.0f, 10.0f).invokeTab("Mainhand");
    private final FloatSetting mainHandScaleY = setting("Mainhand Scale Y", 0.0f, -10.0f, 10.0f).invokeTab("Mainhand");
    private final FloatSetting mainHandScaleZ = setting("Mainhand Scale Z", 0.0f, -10.0f, 10.0f).invokeTab("Mainhand");
    private final FloatSetting mainHandRotationX = setting("Mainhand Rotation X", 0.0f, 0.0f, 10.0f).invokeTab("Mainhand");
    private final FloatSetting mainHandRotationY = setting("Mainhand Rotation Y", 0.0f, 0.0f, 10.0f).invokeTab("Mainhand");
    private final FloatSetting mainHandRotationZ = setting("Mainhand Rotation Z", 0.0f, 0.0f, 10.0f).invokeTab("Mainhand");

    private final FloatSetting offHandX = setting("OffHand X", 0.0f, -10.0f, 10.0f).invokeTab("OffHand");
    private final FloatSetting offHandY = setting("OffHand Y", 0.0f, -10.0f, 10.0f).invokeTab("OffHand");
    private final FloatSetting offHandZ = setting("OffHand Z", 0.0f, -10.0f, 10.0f).invokeTab("OffHand");
    private final FloatSetting offHandScaleX = setting("OffHand Scale X", 0.0f, -10.0f, 10.0f).invokeTab("OffHand");
    private final FloatSetting offHandScaleY = setting("OffHand Scale Y", 0.0f, -10.0f, 10.0f).invokeTab("OffHand");
    private final FloatSetting offHandScaleZ = setting("OffHand Scale Z", 0.0f, -10.0f, 10.0f).invokeTab("OffHand");
    private final FloatSetting offHandRotationX = setting("OffHand Rotation X", 0.0f, 0.0f, 10.0f).invokeTab("OffHand");
    private final FloatSetting offHandRotationY = setting("OffHand Rotation Y", 0.0f, 0.0f, 10.0f).invokeTab("OffHand");
    private final FloatSetting offHandRotationZ = setting("OffHand Rotation Z", 0.0f, 0.0f, 10.0f).invokeTab("OffHand");

    @EventListener
    public void onRenderItem(RenderItemEvent event) {
        if (event.getTransform().equals(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND)) {
            glTranslatef(mainHandX.getValue() / 40.0f, mainHandY.getValue() / 40.0f, mainHandZ.getValue() / 40.0f);
            glScalef(mainHandScaleX.getValue() / 10.0f + 1.0f, mainHandScaleY.getValue() / 10.0f + 1.0f, mainHandScaleZ.getValue() / 10.0f + 1.0f);
            glRotatef(mainHandRotationX.getValue() * 36.0f, 1.0f, 0.0f, 0.0f);
            glRotatef(mainHandRotationY.getValue() * 36.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(mainHandRotationZ.getValue() * 36.0f, 0.0f, 0.0f, 1.0f);
        } else {
            glTranslatef(offHandX.getValue() / 40.0f, offHandY.getValue() / 40.0f, offHandZ.getValue() / 40.0f);
            glScalef(offHandScaleX.getValue() / 10.0f + 1.0f, offHandScaleY.getValue() / 10.0f + 1.0f, offHandScaleZ.getValue() / 10.0f + 1.0f);
            glRotatef(offHandRotationX.getValue() * 36.0f, 1.0f, 0.0f, 0.0f);
            glRotatef(offHandRotationY.getValue() * 36.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(offHandRotationZ.getValue() * 36.0f, 0.0f, 0.0f, 1.0f);
        }
    }

    @EventListener
    public void onRotateArm(RotateArmEvent event){
        if (staticHands.getValue()){
            event.setCancelled(true);
        }
    }

}
