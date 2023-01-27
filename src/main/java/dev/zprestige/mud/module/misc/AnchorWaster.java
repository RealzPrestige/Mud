package dev.zprestige.mud.module.misc;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.util.impl.BlockUtil;
import dev.zprestige.mud.util.impl.EntityUtil;
import dev.zprestige.mud.util.impl.PacketUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class AnchorWaster extends Module {

    @EventListener
    public void onTick(TickEvent event){
        if (mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell")){
            return;
        }
        EntityPlayer entityPlayer = EntityUtil.getEntityPlayer(10.0f);
        if (entityPlayer == null){
            return;
        }
        BlockPos pos = BlockUtil.getPosition().up().up();
        if (BlockUtil.is(pos, Blocks.OBSIDIAN)){
            PacketUtil.invoke(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
        }
    }
}
