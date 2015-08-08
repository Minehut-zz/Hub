package com.minehut.hub.daemon.commands;

import com.minehut.core.command.Command;
import com.minehut.core.player.Rank;
import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.core.util.common.sound.S;
import com.minehut.daemon.Kingdom;
import com.minehut.daemon.tools.mc.MCPlayer;
import com.minehut.hub.Hub;
import com.minehut.hub.daemon.DaemonManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by luke on 6/12/15.
 */
public class MyKingdomCommand extends Command {
    private DaemonManager daemonManager;

    public MyKingdomCommand(JavaPlugin plugin, DaemonManager daemonManager) {
        super(plugin, "mykingdom", Arrays.asList("myserver"), Rank.regular);
        this.daemonManager = daemonManager;
    }

    @Override
    public boolean call(Player player, ArrayList<String> arrayList) {

        MCPlayer mcPlayer = new MCPlayer(player.getUniqueId());

        System.out.println("Checking if player has a kingdom");
             /* Player already owns a kingdom */

        F.message(player, C.purple + "Loading your Kingdoms, this may take a few moments...");
        S.pop(player);

        Bukkit.getServer().getScheduler().runTaskAsynchronously(Hub.instance, new Runnable() {
            @Override
            public void run() {
                if (daemonManager.daemonFactory.hasKingdom(mcPlayer)) {
                    List<Kingdom> kingdoms = daemonManager.getDaemonFactory().getPlayerKingdoms(mcPlayer);

                    if(kingdoms.size() == 1) {
                        F.message(player, "Your kingdom is called " + C.green + kingdoms.get(0).getName());
                        F.message(player, "Join it with " + C.aqua + "/join " + kingdoms.get(0).getName());
                    } else {
                        F.message(player, "You own the following kingdoms:");
                        int i = 1;
                        for (Kingdom kingdom : kingdoms) {
                            F.message(player, C.gray + i + ". " + C.green + kingdom.getName());
                            i++;
                        }
                    }
                } else {
                    F.warning(player, "You do not have a kingdom");
                    F.warning(player, "Create one with /create (name)");
                }
            }
        });

        return false;
    }
}
