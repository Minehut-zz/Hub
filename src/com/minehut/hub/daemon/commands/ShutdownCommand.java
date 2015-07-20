package com.minehut.hub.daemon.commands;

import com.minehut.core.Core;
import com.minehut.core.command.Command;
import com.minehut.core.player.Rank;
import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.daemon.Kingdom;
import com.minehut.hub.Hub;
import com.minehut.hub.daemon.DaemonManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by luke on 7/6/15.
 */
public class ShutdownCommand extends Command {
    private DaemonManager daemonManager;

    public ShutdownCommand(JavaPlugin plugin, DaemonManager daemonManager) {
        super(plugin, "shutdown", Rank.regular);
        this.daemonManager = daemonManager;
    }

    @Override
    public boolean call(Player player, ArrayList<String> args) {

        /* Make sure they specify a name */
        if (args == null || args.size() < 1) {
            F.message(player, "Please specify a name!");
            F.message(player, C.gray + "Example: " + C.aqua + "/shutdown Minehut");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Hub.getInstance(), new ShutdownCommandRunnable(player.getUniqueId(), args.get(0)));

        return false;
    }
    
    public class ShutdownCommandRunnable implements Runnable {

    	private UUID playerUUID;
    	private String kingdomName;
    	
    	public ShutdownCommandRunnable(UUID playerUUID, String kingdomName) {
    		this.playerUUID = playerUUID;
    		this.kingdomName = kingdomName;
    	}
    	
    	@Override
        public void run() {
    		Player player = Bukkit.getPlayer(this.playerUUID);
            Kingdom kingdom = daemonManager.daemonFactory.getKingdom(this.kingdomName);
            if (kingdom == null) {
                F.message(player, this.kingdomName + C.red + " is not a valid kingdom");
            } else {
                if (kingdom.getOwner().playerUUID.equalsIgnoreCase(player.getUniqueId().toString())) {
                    String startup = daemonManager.daemonFactory.getStartup(kingdom);
                    if (!startup.equalsIgnoreCase("offline")) {
                            /* Server is online */
                        daemonManager.getDaemonFactory().stopKingdom(kingdom.getName());
                        F.message(player, C.aqua + kingdom.getName() + C.yellow + " was shutdown");
                    } else {
                        F.message(player, C.aqua + kingdom.getName() + C.yellow + "is already offline!");
                    }
                } else {
                    F.message(player, "You do not have permission to shutdown " + C.aqua + kingdom.getName());
                }
            }
        }
    	
    }
    
}
