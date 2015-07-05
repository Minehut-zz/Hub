package com.minehut.hub.commands;

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

    private int previousPercentage;


    public StartupMonitor(DaemonManager daemonManager, Kingdom kingdom, Player player) {
        this.daemonManager = daemonManager;
        this.kingdom = kingdom;

        this.players = new ArrayList<>();
        this.players.add(player);

        this.previousPercentage = -1;

        this.runnableID = monitorRunnable();

        Bukkit.getServer().getPluginManager().registerEvents(this, Hub.getInstance());
    }

    private int monitorRunnable() {
        return Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Hub.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (players != null && !players.isEmpty()) {

                    int percentage; //TODO: retrieve percentage

                    for (Player player : players) {

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
    }

    public void addPlayer(Player player) {
        this.players.add(player);
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

    public int getPreviousPercentage() {
        return previousPercentage;
    }
}
