package com.minehut.hub.daemon.commands;

import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.core.util.common.sound.S;
import com.minehut.core.command.Command;
import com.minehut.core.player.Rank;
import com.minehut.hub.Hub;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

/**
 * Created by luke on 6/12/15.
 */
public class FlyCommand extends Command {

    public FlyCommand(JavaPlugin plugin) {
        super(plugin, "fly", Rank.Mega);
    }

    @Override
    public boolean call(Player player, ArrayList<String> arrayList) {
        if(!Hub.getInstance().getPitPvPManager().isInsidePit(player.getLocation())) {
            if (player.isFlying()) {
                player.setFlying(false);
                player.setAllowFlight(false);
                S.playSound(player, Sound.FIREWORK_TWINKLE);
                F.message(player, C.aqua + "Fly Mode " + C.yellow + "has been " + C.red + "disabled");
            } else {
                player.setAllowFlight(true);
                player.setFlying(true);
                S.playSound(player, Sound.FIREWORK_TWINKLE);
                F.message(player,C.aqua + "Fly Mode " + C.yellow + "has been " + C.green + "enabled");
            }
        } else {
            F.message(player, "You can't fly in the PvP Pit");
        }

        return false;
    }
}
