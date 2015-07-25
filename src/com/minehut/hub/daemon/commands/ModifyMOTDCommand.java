package com.minehut.hub.daemon.commands;

import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minehut.core.command.Command;
import com.minehut.core.player.Rank;
import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.daemon.Kingdom;
import com.minehut.hub.Hub;
import com.minehut.hub.daemon.DaemonManager;

public class ModifyMOTDCommand extends Command {
    private DaemonManager daemonManager;

    public ModifyMOTDCommand(JavaPlugin plugin, DaemonManager daemonManager) {
        super(plugin, "set-motd", Rank.regular);
        this.daemonManager = daemonManager;
    }

    @Override
    public boolean call(Player player, ArrayList<String> args) {

        /* Make sure they specify a name */
        if (args == null || args.size() < 2) { // Make sure they add 2 args
            F.message(player, "Please specify a name!");
            F.message(player, C.gray + "Example: " + C.aqua + "/set-motd Minehut This is my message of the day!");
            return true;
        }

        //TODO: rename runnable
        Bukkit.getScheduler().runTaskAsynchronously(Hub.getInstance(), new ModifyMOTDCommandRunnable(player.getUniqueId(), args.get(0), StringUtils.join(args.subList(1, args.size()), " ")));
        return false;
    }
    
    public class ModifyMOTDCommandRunnable implements Runnable {

    	private UUID playerUUID;
    	private String kingdomName, motd;
    	
    	public ModifyMOTDCommandRunnable(UUID playerUUID, String kingdomName, String motd) {
    		this.playerUUID = playerUUID;
    		this.kingdomName = kingdomName;
    		this.motd = motd;
    	}
    	
    	@Override
        public void run() {
    		Player player = Bukkit.getPlayer(this.playerUUID);
            Kingdom kingdom = daemonManager.daemonFactory.getKingdom(this.kingdomName);
            if (kingdom == null) {
                F.message(player, this.kingdomName + C.red + " is not a valid kingdom");
            } else {
                if (kingdom.getOwner().playerUUID.equalsIgnoreCase(player.getUniqueId().toString())) {
                	String motdOut = this.motd, rank = kingdom.getOwner().rank.toLowerCase();
                	if (rank.equals("admin")||rank.equals("owner")||rank.equals("dev")||rank.equals("champ") || rank.equals("mod")) {
            			motdOut = replaceSpecial(replaceColors(motdOut));
            		} else //NEW: Champs+ can use bold/etc, legend can use just colors.
            		if (rank.equals("legend")) {
            			motdOut = replaceColors(motdOut);
            		}
                	F.message(player, "Changing kingdom motd to " + C.aqua + motdOut);
                	daemonManager.daemonFactory.setKingdomMOTD(kingdom, motdOut);
                } else {
                	//TODO: Check if player is staff for mod or admin use
                    F.message(player, "You do not have permission to set motd for " + C.aqua + kingdom.getName());
                }
            }
        }
    }
    
    public static boolean isSpecial(ChatColor cc) {
    	return (cc == ChatColor.BOLD||
    			cc == ChatColor.UNDERLINE||
    			cc == ChatColor.STRIKETHROUGH||
    			cc == ChatColor.ITALIC||
    			cc == ChatColor.MAGIC||
    			cc == ChatColor.RESET);
    }
    
    public static String replaceSpecial(String s) {
    	String temp = s;
    	for (ChatColor cc : ChatColor.values()) {
    		if (isSpecial(cc)&&cc!=ChatColor.MAGIC) {
    			temp = temp.replace("&" + cc.getChar(), cc.toString());
    		}
    	}
    	return temp;
    }
    
    public static String replaceColors(String s) { //TODO: move to util class
    	String temp = s;
    	for (ChatColor cc : ChatColor.values()) {
    		if (!isSpecial(cc)) {
    			temp = temp.replace("&" + cc.getChar(), cc.toString());
    		}
    	}
    	return temp;
    }
}
