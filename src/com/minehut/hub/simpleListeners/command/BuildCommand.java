package com.minehut.hub.simpleListeners.command;

import com.minehut.core.command.Command;
import com.minehut.core.player.Rank;
import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.core.util.common.sound.S;
import com.minehut.hub.Hub;
import com.minehut.hub.simpleListeners.SimpleListeners;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

/**
 * Created by luke on 6/12/15.
 */
public class BuildCommand extends Command {
    public SimpleListeners simpleListeners;

    public BuildCommand(JavaPlugin plugin, SimpleListeners simpleListeners) {
        super(plugin, "build", Rank.Admin);
        this.simpleListeners = simpleListeners;
    }

    @Override
    public boolean call(Player player, ArrayList<String> args) {

        if (args == null || args.size() != 2) {
            F.warning(player, "/build toggle (player)");
            return true;
        }

        Player target = Bukkit.getServer().getPlayer(args.get(1));
        if (target != null) {
            if (simpleListeners.canBuild(target)) {
                /* turn off */

                simpleListeners.builders.remove(target);
                target.setGameMode(GameMode.SURVIVAL);
                F.warning(target, "Your build perms have been " + C.red + "removed");

                F.message(player, "You have" + C.red + " removed " + C.yellow + "build perms for " + C.purple + target.getName());
                S.pop(player);

            } else {
                /* turn on */

                simpleListeners.builders.add(target);
                target.setGameMode(GameMode.CREATIVE);
                F.warning(target, "You have been " + C.green + "granted" + C.gray + " build perms");

                F.message(player, "You have" + C.green + " granted " + C.yellow + "build perms for " + C.purple + target.getName());
                S.pop(player);

            }
        } else {
            F.warning(player, "Could not find " + C.purple + args.get(1));
        }


        return false;
    }
}
