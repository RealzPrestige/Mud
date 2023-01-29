package dev.zprestige.mud.module.client;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.system.RPCDetailsEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.util.impl.DiscordUtil;
import dev.zprestige.mud.util.impl.MudUtil;

public class Rpc extends Module {
    private final BooleanSetting server = setting("Server", true);

    public Rpc() {
        toggle();
    }

    int opp = 0;

    @EventListener
    public void onRPCDetails(RPCDetailsEvent event){
        if (!server.getValue()) {
            event.setCancelled(true);
            event.setDetails("Smokin on " + MudUtil.getDeadOpps()[opp]);
            if (opp >= MudUtil.getDeadOpps().length - 1) {
                opp = 0;
            } else {
                opp++;
            }
        }
    }

    @Override
    public void onEnable() {
        DiscordUtil.init();
        DiscordUtil.onPre();
        DiscordUtil.onPost();
    }

    @Override
    public void onDisable() {
        DiscordUtil.onExit();
    }
}
