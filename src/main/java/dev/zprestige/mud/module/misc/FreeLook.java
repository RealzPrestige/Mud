package dev.zprestige.mud.module.misc;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.CameraSetupEvent;
import dev.zprestige.mud.events.impl.player.TurnEvent;
import dev.zprestige.mud.events.impl.render.CameraDistanceEvent;
import dev.zprestige.mud.events.impl.render.Render3DEvent;
import dev.zprestige.mud.events.impl.render.RenderRotationsEvent;
import dev.zprestige.mud.events.impl.system.PacketSendEvent;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.manager.EventManager;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BindSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.util.impl.MathUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

public class FreeLook extends Module {
    private final BindSetting key = setting("Key", Keyboard.KEY_NONE);
    private final FloatSetting distance = setting("Distance", 4.0f, 0.1f, 8.0f);
    private final FloatSetting speed = setting("Speed", 1.0f, 0.5f, 3.0f);
    private float yaw = 0.0f, pitch = 0.0f, dist;
    private boolean active, disabled;
    private static boolean cancelTrace;
    private float rotationYaw, rotationPitch;

    public static void setCancelTrace(boolean cancelTrace) {
        FreeLook.cancelTrace = cancelTrace;
    }

    public static boolean isCancelTrace() {
        return cancelTrace;
    }

    @EventListener
    public void onRenderRotations(RenderRotationsEvent event){
        event.setYaw(rotationYaw);
        event.setPitch(rotationPitch);
        event.setCancelled(true);
    }

    @EventListener
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
            rotationYaw = (((CPacketPlayer) event.getPacket()).getYaw(0.0f));
            rotationPitch = (((CPacketPlayer) event.getPacket()).getPitch(0.0f));

        }
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        dist = MathUtil.lerp(dist, active ? distance.getValue() : 0.0f, EventManager.getDeltaTime() * speed.getValue());
    }

    @EventListener
    public void onCameraDistance(CameraDistanceEvent event) {
        event.setDistance(-dist);
    }

    @EventListener
    public void onCameraSetup(CameraSetupEvent event) {
        if (!active && dist < 0.05f) {
            return;
        }
        event.setYaw(event.getYaw() + yaw);
        event.setPitch(event.getPitch() + pitch);
    }

    @EventListener
    public void onTurn(TurnEvent event) {
        if (!active && dist < 0.05f) {
            return;
        }
        yaw = yaw + event.getYaw() * 0.15f;
        pitch = pitch - event.getPitch() * 0.15f;
        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
        event.setCancelled(true);
    }

    @EventListener
    public void onTick(TickEvent event) {
        active = mc.currentScreen == null && key.getValue() != Keyboard.KEY_NONE && Keyboard.isKeyDown(key.getValue());
        invokeAppend(active ? "Active" : "");
        if (active) {
            mc.gameSettings.thirdPersonView = 1;
            disabled = false;
        } else {
            if (!disabled && dist < 0.05f) {
                mc.gameSettings.thirdPersonView = 0;
                yaw = 0.0f;
                pitch = 0.0f;
                disabled = true;
            }
        }
    }
}
