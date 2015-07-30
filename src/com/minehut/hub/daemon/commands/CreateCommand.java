package com.minehut.hub.daemon.commands;

import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.core.command.Command;
import com.minehut.core.player.Rank;
import com.minehut.daemon.Kingdom;
import com.minehut.daemon.tools.mc.MCPlayer;
import com.minehut.hub.Hub;
import com.minehut.hub.daemon.DaemonManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        /* Expect lag, time to go async boys
         * Moved to it's own class to clean things up
         * and my jdk was freaking saying it needed final vars */
        if (args.size() == 1) {
        	Bukkit.getServer().getScheduler().runTaskAsynchronously(Hub.getInstance(), new CreateCommandRunnable(player.getUniqueId(), args.get(0), daemonManager.getDefaultSampleKingdom().getType()));
        } else
        if (args.size() == 2) {
        	if (this.daemonManager.getDaemonFactory().isSampleKingdom(args.get(1))) {
        		Bukkit.getServer().getScheduler().runTaskAsynchronously(Hub.getInstance(), new CreateCommandRunnable(player.getUniqueId(), args.get(0), args.get(1)));
        	} else {
        		F.message(player, "That is not a proper Kingdom type!");
        	}
        }
        return false;
    }
    
    public class CreateCommandRunnable implements Runnable {

    	private UUID playerUUID;
    	private String kingdomName, sample;
    	
    	public CreateCommandRunnable(UUID uuid, String kingdomName, String sample) {
    		System.out.println("CreateCommandRunnable created playerUUID=" + uuid + ",kingdomName=" + kingdomName +",sample=" + sample);
    		this.playerUUID = uuid;
    		this.kingdomName = kingdomName;
    		this.sample = sample;
    	}
    	
		@Override
        public void run() {
			Player player = Bukkit.getPlayer(this.playerUUID);
			if (player == null) {
				System.out.println("NULL PLAYER OBJECT FOUND");
				return;
			}
            MCPlayer mcPlayer = new MCPlayer(player.getUniqueId());

            System.out.println("Checking if player has a kingdom");
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
                        i++;
                    }
                }

                return;
            }
            System.out.println("Checking if kingdom name is in use");
             /* Check if name is already in use */
            //todo: check if name can be in use
            if (daemonManager.getDaemonFactory().isKingdom(this.kingdomName)) {
            	F.message(player, "There is already a Kingdom with that name!");
            	return;
            }
            
            System.out.println("Checking if sample text is not null and not blank");
            /* Didn't specify mod type, default to bukkit */
            if(this.sample!=null||this.sample.equals("")) {
                F.message(player, "Give us a few moments while we assemble your free server...");

                daemonManager.getDaemonFactory().createKingdom(mcPlayer, daemonManager.getDaemonFactory().getSampleKingdom(this.sample), this.kingdomName);

                Kingdom kingdom = daemonManager.getPlayerKingdom(mcPlayer);
                if (kingdom != null) {
                    F.log("Starting kingdom " + kingdom.getName());
                    daemonManager.getDaemonFactory().startKingdom(kingdom);
                    daemonManager.handleStartupMonitor(kingdom, player);
                } else {
                    F.log("Kingdom was null after 5 second delay :(");
                }
            }
        }
    	
    }
    
}
