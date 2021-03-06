package com.minehut.hub.daemon.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minehut.core.command.Command;
import com.minehut.core.player.Rank;
import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.daemon.Kingdom;
import com.minehut.daemon.tools.mc.MCPlayer;
import com.minehut.hub.Hub;
import com.minehut.hub.daemon.DaemonManager;

public class ResetCommand extends Command {
	
    private DaemonManager daemonManager;

    public ResetCommand(JavaPlugin plugin, DaemonManager daemonManager) {
        super(plugin, "reset", Rank.regular);
        this.daemonManager = daemonManager;
    }

    @Override
    public boolean call(Player player, ArrayList<String> args) {
        /* Make sure they specify a name */
        if (args == null || args.isEmpty()) {
            F.warning(player, "Please specify a name!");
            F.warning(player, C.gray + "Example: " + C.aqua + "/reset Minehut");
            return true;
        }

        Bukkit.getServer().getScheduler().runTaskAsynchronously(Hub.getInstance(), new ResetCommandRunnable(player, args, player.getUniqueId(), args.get(0), daemonManager.getDefaultSampleKingdom().getType()));

//        else if (args.size() == 2) {
//        	if (this.daemonManager.getDaemonFactory().isSampleKingdom(args.get(1))) {
//        		Bukkit.getServer().getScheduler().runTaskAsynchronously(Hub.getInstance(), new ResetCommandRunnable(player.getUniqueId(), args.get(0), args.get(1)));
//        	} else {
//        		F.warning(player, "That is not a proper Kingdom type!");
//        	}
//        }
        return false;
    }
    
    public class ResetCommandRunnable implements Runnable {

    	private UUID playerUUID;
    	private String kingdomName, sample;
        private ArrayList<String> args;
        private Player player;
    	
    	public ResetCommandRunnable(Player player, ArrayList<String> args, UUID uuid, String kingdomName, String sample) {
    		this.playerUUID = uuid;
    		this.kingdomName = kingdomName;
    		this.sample = sample;
            this.player = player;
            this.args = args;
    	}
    	
		@Override
        public void run() {


            if (args.size() == 1) {
                Player player = Bukkit.getPlayer(this.playerUUID);
                Kingdom kingdom = daemonManager.daemonFactory.getKingdom(this.kingdomName);
                if (kingdom == null) {
                    F.warning(player, this.kingdomName + C.red + " is not a valid kingdom");
                } else {
                    if (kingdom.getOwner().playerUUID.equalsIgnoreCase(player.getUniqueId().toString())) {
                        kingdom.setSampleKingdom(daemonManager.getDaemonFactory().getSampleKingdom(sample));
                        daemonManager.getDaemonFactory().resetKingdom(kingdom);
                        F.message(player, "Your kingdom has been reset!");
                    } else {
                        F.message(player, "You do not have permission to reset " + C.aqua + kingdom.getName());
                    }
                }
            } else {
                F.warning(player, "Please specify which kingdom to reset");
                F.warning(player, "Example: " + C.green + "/reset Minehut");
            }

        }
    	
    }
    
}

