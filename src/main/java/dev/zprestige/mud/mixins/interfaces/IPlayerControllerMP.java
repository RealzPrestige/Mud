package dev.zprestige.mud.mixins.interfaces;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerControllerMP.class)
public interface IPlayerControllerMP {

    @Accessor("isHittingBlock")
    void setHittingBlock(boolean hittingBlock);

    @Accessor("curBlockDamageMP")
    void setCurBlockDamageMP(float curBlockDamageMP);

    @Accessor("curBlockDamageMP")
    float getCurBlockDamageMP();
}
