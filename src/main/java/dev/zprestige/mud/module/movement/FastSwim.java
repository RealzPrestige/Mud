package dev.zprestige.mud.module.movement;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MoveEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BindSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.ModeSetting;
import dev.zprestige.mud.util.impl.BlockUtil;
import dev.zprestige.mud.util.impl.EntityUtil;
import net.minecraft.init.Blocks;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

public class FastSwim extends Module {
    private final ModeSetting mode = setting("Mode", "Motion", Arrays.asList("Motion", "Factor", "Position"));
    private final FloatSetting horizontalSpeed = setting("Horizontal Speed", 1.0f, 0.1f, 10.0f);
    private final FloatSetting verticalSpeedUp = setting("Vertical Speed Up", 1.0f, 0.1f, 10.0f);
    private final FloatSetting verticalSpeedDown = setting("Vertical Speed Down", 1.0f, 0.1f, 10.0f);
    private final ModeSetting down = setting("Down", "Vanilla", Arrays.asList("Vanilla", "NCP", "Remove"));
    private final BindSetting boost = setting("Boost", Keyboard.KEY_NONE);

    @EventListener
    public void onMove(MoveEvent event) {
        if ((!mc.player.isInLava()
                && !mc.player.isInWater())
                || BlockUtil.is(BlockUtil.getPosition(), Blocks.AIR)
                || BlockUtil.is(BlockUtil.getPosition().up(), Blocks.AIR)
        ) {
            return;
        }
        boolean boost = this.boost.getValue() != Keyboard.KEY_NONE && Keyboard.isKeyDown(this.boost.getValue());
        float boostFactor = boost ? 2.0f : 1.0f;
        float[] forward = EntityUtil.forward(horizontalSpeed.getValue() / 10.0f * boostFactor);
        switch (mode.getValue()) {
            case "Motion":
                mc.player.motionX *= horizontalSpeed.getValue() * boostFactor;
                mc.player.motionZ *= horizontalSpeed.getValue() * boostFactor;
                break;
            case "Factor":
                mc.player.motionX = forward[0];
                mc.player.motionZ = forward[1];
                break;
            case "Position":
                mc.player.setPosition(mc.player.posX + forward[0], mc.player.posY, mc.player.posZ + forward[1]);
                break;
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY -= verticalSpeedDown.getValue() / 100.0f;
        } else if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY += verticalSpeedUp.getValue() / 100.0f;
        }
        if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
            switch (down.getValue()) {
                case "Vanilla":
                    break;
                case "NCP":
                    mc.player.motionY = -0.005f;
                    break;
                case "Remove":
                    mc.player.motionY = 0.0f;
                    break;
            }
        }
    }
}
