package com.minehut.hub.daemon;

import com.minehut.core.Core;
import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.daemon.Kingdom;
import com.minehut.daemon.tools.mc.MCPlayer;
import com.minehut.hub.Hub;
import com.minehut.hub.daemon.DaemonManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by luke on 7/4/15.
 */
public class StartupMonitor implements Listener {
    DaemonManager daemonManager;

    Kingdom kingdom;
    List<Player> players;

    private int runnableID = -1;

    private String previousPercentage;


    public StartupMonitor(DaemonManager daemonManager, Kingdom kingdom, Player player) {
        this.daemonManager = daemonManager;
        this.kingdom = kingdom;

        this.players = new ArrayList<>();
        this.players.add(player);

        F.message(player, "Starting up your kingdom...", F.BroadcastType.MINIMAL_BORDER);

        this.previousPercentage = "";

        this.runnableID = monitorRunnable();

        Bukkit.getServer().getPluginManager().registerEvents(this, Hub.instance);
    }

    private int monitorRunnable() {
        return Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Hub.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (players != null && !players.isEmpty()) {

                    String startup = daemonManager.getDaemonFactory().getStartup(kingdom);

                    if (startup.equalsIgnoreCase("100%")) {
                        for (Player player : players) {
                            if(player != null) {
                                F.log("DEBUG -> Kingdom Name: " + kingdom.getName());
                                Core.getInstance().getStatusManager().sendToKingdom(player, kingdom.getName());
                            }
                        }
                        destroy();
                    } else if (startup.equalsIgnoreCase("offline")) {
                        for (Player player : players) {
                            if(player != null) {
                                F.message(player, C.yellow + kingdom.getName() + C.red + " went offline during startup.");
                            }
                        }
                        destroy();
                    } else if (!previousPercentage.equalsIgnoreCase(startup)) {
                        previousPercentage = startup;

                        for (Player player : players) {
                            if(player != null) {
                                F.message(player, kingdom.getName() + C.gray + " Startup Status: " + C.aqua + startup);
                            }
                        }
                    }

                } else {
                    destroy();
                }
            }
        }, 20L, 20L);
    }

    public void destroy() {
        if(this.runnableID != -1) {
            Bukkit.getServer().getScheduler().cancelTask(this.runnableID);
        }
        HandlerList.unregisterAll(this);
        daemonManager.removeStartupMonitor(this);
    }

    public void addPlayer(Player player) {
        this.players.add(player);

        F.message(player, "Starting up your kingdom...", F.BroadcastType.MINIMAL_BORDER);
    }

    public boolean containsPlayer(Player player) {
        return this.players.contains(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (this.players.contains(event.getPlayer())) {
            this.players.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerKickEvent event) {
        if (this.players.contains(event.getPlayer())) {
            this.players.remove(event.getPlayer());
        }
    }

    public DaemonManager getDaemonManager() {
        return daemonManager;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getRunnableID() {
        return runnableID;
    }
}
