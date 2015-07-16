package com.minehut.hub.daemon.commands;

import com.minehut.core.Core;
import com.minehut.core.command.Command;
import com.minehut.core.player.Rank;
import com.minehut.core.util.common.bungee.Bungee;
import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.daemon.Kingdom;
import com.minehut.hub.Hub;
import com.minehut.hub.daemon.DaemonManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

/**
 * Created by luke on 7/6/15.
 */
public class JoinCommand extends Command {
    private DaemonManager daemonManager;

    public JoinCommand(JavaPlugin plugin, DaemonManager daemonManager) {
        super(plugin, "join", Rank.regular);
        this.daemonManager = daemonManager;
    }

    @Override
    public boolean call(Player player, ArrayList<String> args) {

        /* Make sure they specify a name */
        if (args == null || args.size() < 1) {
            F.message(player, "Please specify a name!");
            F.message(player, C.gray + "Example: " + C.aqua + "/join Minehut");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Hub.getInstance(), new Runnable() {
            @Override
            public void run() {
                Kingdom kingdom = daemonManager.daemonFactory.getKingdom(args.get(0));
                if (kingdom == null) {
                    F.message(player, args.get(0) + C.red + " is not a valid kingdom");
                } else {
                    String startup = daemonManager.daemonFactory.getStartup(kingdom);
                    if (!startup.equalsIgnoreCase("offline")) {
                        Core.getInstance().getStatusManager().sendToKingdom(player, kingdom.getName());
                    } else {
                        daemonManager.daemonFactory.startKingdom(kingdom);
                        daemonManager.handleStartupMonitor(kingdom, player);
                    }
                }
            }
        });

        return false;
    }
}
