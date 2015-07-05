package com.minehut.hub.daemon.commands;

import com.minehut.commons.common.chat.C;
import com.minehut.commons.common.chat.F;
import com.minehut.core.command.Command;
import com.minehut.core.player.Rank;
import com.minehut.daemon.Kingdom;
import com.minehut.daemon.SampleKingdom;
import com.minehut.daemon.protocol.status.out.StatusSampleList;
import com.minehut.daemon.tools.mc.MCPlayer;
import com.minehut.hub.Hub;
import com.minehut.hub.daemon.DaemonManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luke on 7/4/15.
 */
public class CreateCommand extends Command {
    private DaemonManager daemonManager;

    public CreateCommand(JavaPlugin plugin, DaemonManager daemonManager) {
        super(plugin, "create", Rank.regular);
        this.daemonManager = daemonManager;
    }

    @Override
    public boolean call(Player player, ArrayList<String> args) {

        /* Make sure they specify a name */
        if (args == null) {
            F.message(player, "Please specify a name!");
            F.message(player, C.gray + "Example: " + C.aqua + "/create Minehut");
        }

        /* Expect lag, time to go async boys */
        Bukkit.getServer().getScheduler().runTaskAsynchronously(Hub.getInstance(), new Runnable() {
            @Override
            public void run() {
                MCPlayer mcPlayer = new MCPlayer(player.getName(), player.getUniqueId());

                 /* Player already owns a kingdom */
                if (daemonManager.daemonFactory.hasKingdom(mcPlayer)) {
                    List<Kingdom> kingdoms = daemonManager.getDaemonFactory().getPlayerKingdoms(mcPlayer);

                    if(kingdoms.size() == 1) {
                        F.message(player, "You already have a kingdom called " + C.green + kingdoms.get(0).getName());
                        F.message(player, "Join it with " + C.aqua + "/join " + kingdoms.get(0).getName());
                    } else {
                        F.message(player, "You already have the following kingdoms:");
                        int i = 1;
                        for (Kingdom kingdom : kingdoms) {
                            F.message(player, C.gray + i + ". " + C.green + kingdom.getName());
                        }
                    }
                }

                 /* Check if name is already in use */
                //todo: check if name can be in use

                /* Didn't specify mod type, default to bukkit */
                if(args.size() == 1) {

                    StatusSampleList statusSampleList = daemonManager.getDaemonFactory().getStatusSampleList();
                    SampleKingdom sampleKingdom = statusSampleList.sampleList.get(0);

                    daemonManager.getDaemonFactory().createKingdom(mcPlayer, sampleKingdom, args.get(0));

                    F.message(player, "Give us a few moments while we assemble your free server...");


                    /* Kingdom will be created shortly after request is sent */
                    Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Hub.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            Kingdom kingdom = daemonManager.getPlayerKingdom(mcPlayer);
                            if (kingdom != null) {
                                daemonManager.handleStartupMonitor(kingdom, player);
                            } else {
                                F.log("Kingdom was null after 5 second delay :(");
                            }
                        }
                    }, 20L * 5);
                }

                else if (args.size() == 2) {
                    //TODO: mod/alternative-sample-kingdom support
                }
            }
        });

        return false;
    }
}
