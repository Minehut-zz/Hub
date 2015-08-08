package com.minehut.hub.daemon;

import com.minehut.core.util.EventUtils;
import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.core.util.common.items.ItemStackFactory;
import com.minehut.daemon.Kingdom;
import com.minehut.daemon.SampleKingdom;
import com.minehut.daemon.protocol.api.DaemonFactory;
import com.minehut.daemon.protocol.status.out.StatusSampleList;
import com.minehut.daemon.tools.mc.MCPlayer;
import com.minehut.hub.Hub;
import com.minehut.hub.daemon.commands.CreateCommand;
import com.minehut.hub.daemon.commands.JoinCommand;
import com.minehut.hub.daemon.commands.ShutdownCommand;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by luke on 7/4/15.
 */
public class DaemonManager implements Listener {
    public DaemonFactory daemonFactory;
    private List<StartupMonitor> startupMonitors;
    private ItemStack ownedServersItem;

    public DaemonManager(Hub hub) {
        this.ownedServersItem = getOwnedServersItem();
        this.daemonFactory = new DaemonFactory("mc.minehut.com", 10420);

        this.startupMonitors = new ArrayList<>();

        /* Commands */
        //new CreateCommand(hub, this);
        //new JoinCommand(hub, this);
       // new ShutdownCommand(hub, this);
    }

    public DaemonFactory getDaemonFactory() {
        return daemonFactory;
    }


    public boolean checkForStartupMonitor(Kingdom kingdom, Player player) {

        /* Check to see if kingdom is already being monitored */
        for (StartupMonitor startupMonitor : this.startupMonitors) {
            if (startupMonitor.getKingdom().getName().equals(kingdom.getName())) {
                if (!startupMonitor.containsPlayer(player)) {
                    startupMonitor.addPlayer(player);
                }
                return true;
            }
        }

        return false;
    }

    public void addStartupMonitor(Kingdom kingdom, Player player) {
        F.log("CREATING NEW STARTUP MONITOR! Player: " + player.getName() + " Kingdom: " + kingdom.getName());

        this.startupMonitors.add(new StartupMonitor(this, kingdom, player));
    }

    public Kingdom getPlayerKingdom(MCPlayer mcPlayer) {
        List<Kingdom> kingdoms = daemonFactory.getPlayerKingdoms(mcPlayer);

        if (kingdoms != null) {
            return kingdoms.get(0);
        }

        return null;
    }

    public void removeStartupMonitor(StartupMonitor startupMonitor) {
        StartupMonitor toRemove = null;

        for (StartupMonitor startupMonitor1 : this.startupMonitors) {
            if (startupMonitor.kingdom.getName().equals(startupMonitor1.kingdom.getName())) {
                toRemove = startupMonitor1;
            }
        }

        if (toRemove != null) {
            int index = this.startupMonitors.indexOf(toRemove);
            this.startupMonitors.remove(index);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (EventUtils.isItemClickWithDisplayName(event)) {
            if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.ownedServersItem.getItemMeta().getDisplayName())) {
                MCPlayer mcPlayer = new MCPlayer(event.getPlayer().getUniqueId());
                if (this.daemonFactory.hasKingdom(mcPlayer)) {
                    Bukkit.getServer().getScheduler().runTaskAsynchronously(Hub.getInstance(), new MessagePlayerKingdomsRunnable(event.getPlayer().getUniqueId()));
                } else {
                    F.message(event.getPlayer(), "You do not have a Kingdom.");
                    F.message(event.getPlayer(), "Create one with " + C.aqua + "/create (name)");
                }
            }
        }
    }

    public class MessagePlayerKingdomsRunnable implements Runnable { //My java was forcing me to make crap final, this avoids that and should work for both of us

    	private UUID playerUUID;
    	
    	public MessagePlayerKingdomsRunnable(UUID uuid) {
    		this.playerUUID = uuid;
    	}
    	
		@Override
		public void run() {
			Player player = Bukkit.getPlayer(this.playerUUID);
			messagePlayerKingdoms(player, new MCPlayer(player.getUniqueId()));
		}
		
		public void messagePlayerKingdoms(Player player, MCPlayer mcPlayer) {

	        List<Kingdom> kingdoms = daemonFactory.getPlayerKingdoms(mcPlayer);

	        if(kingdoms.size() == 1) {
	            F.message(player, "You own the kingdom called " + C.green + kingdoms.get(0).getName());
	            F.message(player, "Join it with " + C.aqua + "/join " + kingdoms.get(0).getName());
	        } else {
	            F.message(player, "You own the following kingdoms:");
	            int i = 1;
	            for (Kingdom kingdom : kingdoms) {
	                F.message(player, C.gray + i + ". " + C.green + kingdom.getName());
	                i++;
	            }
	        }
	    }
    }
    

    public SampleKingdom getDefaultSampleKingdom() {
        StatusSampleList statusSampleList = daemonFactory.getStatusSampleList();

        for (SampleKingdom sampleKingdom : statusSampleList.sampleList) {
            if (sampleKingdom.getName().equalsIgnoreCase("default")) {
                return sampleKingdom;
            }
        }
        F.log("Couldn't find Default Sample Kingdom");
        return null;
    }

    public static ItemStack getOwnedServersItem() {
        return ItemStackFactory.createItem(Material.SIGN_POST, C.yellow + C.bold + "Your Kingdom Info");
    }
}
