package com.minehut.hub.daemon.commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minehut.core.command.Command;
import com.minehut.core.player.Rank;
import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.daemon.Kingdom;
import com.minehut.hub.Hub;
import com.minehut.hub.daemon.DaemonManager;

public class RenameCommand extends Command {
    private DaemonManager daemonManager;

    public RenameCommand(JavaPlugin plugin, DaemonManager daemonManager) {
        super(plugin, "rename", Rank.regular);
        this.daemonManager = daemonManager;
    }

    @Override
    public boolean call(Player player, ArrayList<String> args) {

        /* Make sure they specify a name */
        if (args == null || args.size() < 2) { // Make sure they add 2 args
            F.message(player, "Please specify a name!");
            F.message(player, C.gray + "Example: " + C.aqua + "/rename Minehut Minehut2");
            return true;
        }

        //TODO: rename runnable
        Bukkit.getScheduler().runTaskAsynchronously(Hub.getInstance(), new RenameCommandRunnable(player.getUniqueId(), args.get(0), args.get(1)));
        return false;
    }
    
    public class RenameCommandRunnable implements Runnable {

    	private UUID playerUUID;
    	private String oldName, newName;
    	
    	public RenameCommandRunnable(UUID playerUUID, String oldName, String newName) {
    		this.playerUUID = playerUUID;
    		this.newName = newName;
    		this.oldName = oldName;
    	}
    	
    	@Override
        public void run() {
    		Player player = Bukkit.getPlayer(this.playerUUID);
            Kingdom kingdom = daemonManager.daemonFactory.getKingdom(this.oldName);
            if (kingdom == null) {
                F.message(player, this.oldName + C.red + " is not a valid kingdom");
            } else {
                if (kingdom.getOwner().playerUUID.equalsIgnoreCase(player.getUniqueId().toString())) {
                	if (!daemonManager.daemonFactory.isKingdom(this.newName)) {
                		F.message(player, "Changing kingdom name to " + C.aqua + this.newName);
                		daemonManager.daemonFactory.renameKingdom(oldName, newName);
                	} else {
                		F.message(player, C.red + "That kingdom name is already in use!");
                	}
                } else {
                	//TODO: Check if player is staff for mod or admin use
                    F.message(player, "You do not have permission to rename " + C.aqua + kingdom.getName());
                }
            }
        }
    	
    }
    
}