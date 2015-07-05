package com.minehut.hub.daemon;

import com.minehut.daemon.Kingdom;
import com.minehut.daemon.protocol.api.DaemonFactory;
import com.minehut.daemon.tools.mc.MCPlayer;
import com.minehut.hub.Hub;
import com.minehut.hub.commands.CreateCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luke on 7/4/15.
 */
public class DaemonManager {
    public DaemonFactory daemonFactory;
    private List<StartupMonitor> startupMonitors;

    public DaemonManager(Hub hub) {
        this.daemonFactory = new DaemonFactory("199.187.182.168", 10420);

        this.startupMonitors = new ArrayList<>();

        /* Commands */
        new CreateCommand(hub, this);
    }

    public DaemonFactory getDaemonFactory() {
        return daemonFactory;
    }

    public void handleStartupMonitor(Kingdom kingdom, Player player) {

        /* Check to see if kingdom is already being monitored */
        for (StartupMonitor startupMonitor : this.startupMonitors) {
            if (startupMonitor.getKingdom() == kingdom) {
                if (!startupMonitor.containsPlayer(player)) {
                    startupMonitor.addPlayer(player);
                    return;
                }
            }
        }

        /* Server isn't being monitored, start monitoring now */
        this.startupMonitors.add(new StartupMonitor(this, kingdom, player));
    }

    public Kingdom getPlayerKingdom(MCPlayer mcPlayer) {
        List<Kingdom> kingdoms = daemonFactory.getPlayerKingdoms(mcPlayer);

        if (kingdoms != null) {
            return kingdoms.get(0);
        }

        return null;
    }
}
