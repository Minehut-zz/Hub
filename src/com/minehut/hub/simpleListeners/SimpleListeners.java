package com.minehut.hub.simpleListeners;

import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.player.PlayerUtil;
import com.minehut.core.Core;
import com.minehut.core.connection.event.AsyncPlayerInfoInitiatedEvent;
import com.minehut.core.player.PlayerInfo;
import com.minehut.core.player.Rank;
import com.minehut.hub.Hub;
import com.minehut.hub.HubUtils;
import com.minehut.hub.simpleListeners.command.BuildCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

/**
 * Created by luke on 7/2/15.
 */
public class SimpleListeners implements Listener {

    public ArrayList<Player> builders;

    private Hub hub;
    public Location spawn;

    public SimpleListeners(Hub hub) {
        this.hub = hub;
        this.spawn = new Location(Bukkit.getServer().getWorlds().get(0), -4.5, 75, -13.5);
        this.spawn.setYaw(180);

        this.builders = new ArrayList<>();
        new BuildCommand(hub, this);

        Bukkit.getServer().getPluginManager().registerEvents(this, hub);
    }

    public boolean canBuild(Player player) {
        return this.builders.contains(player);
    }

    @EventHandler
    public void noUproot(PlayerInteractEvent event) {
        if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerinfoEstablished(AsyncPlayerInfoInitiatedEvent event) {

        if(!event.playerInfo.getRank().has(null, Rank.Mod, false)) {
            if (event.playerInfo.getRank().has(null, Rank.Mega, false)) {
                Bukkit.getServer().broadcastMessage(event.playerInfo.getRank().getTag() + event.playerInfo.getName() + C.yellow + " joined the server.");
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        PlayerUtil.clearAll(event.getPlayer());
        player.teleport(this.spawn);
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 90, 10, false, false));

        HubUtils.setupInventory(player, true);

        event.setJoinMessage("");

        player.sendMessage(C.divider);
        player.sendMessage("");
        player.sendMessage(C.space + "Welcome to the " + C.purple + C.bold + "MINEHUT NETWORK");
        player.sendMessage(C.space + "Create a server with " + C.green + "/create (name)");
        player.sendMessage(C.space + "For additional commands, type " + C.green + "/help");
        player.sendMessage("");
        player.sendMessage(C.divider);

//		/* Fly Check */
//		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
//			@Override
//			public void run() {
//				if(player != null) {
//					if (API.getAPI().getGamePlayer(player).getRank().has(null, Rank.Mega, false)) {
//						player.setAllowFlight(true);
//						player.setFlying(true);
//					}
//				}
//			}
//		}, 20 * 1);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");

        if (this.builders.contains(event.getPlayer())) {
            this.builders.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        event.setLeaveMessage("");

        if (this.builders.contains(event.getPlayer())) {
            this.builders.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (!Core.getInstance().getPlayerInfo(event.getPlayer()).getRank().has(null, Rank.Admin, false)) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        /* Fall below spawn */
        if (event.getPlayer().getLocation().getY() <= 20) {
            event.setTo(this.spawn);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if(this.canBuild(event.getPlayer())) return;

        PlayerInfo playerInfo = Core.getInstance().getPlayerInfo(event.getPlayer());

        if (!playerInfo.getRank().has(null, Rank.Admin, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if(this.canBuild(event.getPlayer())) return;

        PlayerInfo playerInfo = Core.getInstance().getPlayerInfo(event.getPlayer());
        if (!playerInfo.getRank().has(null, Rank.Admin, false)) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onMoveInventory(InventoryMoveItemEvent event) {

        event.setCancelled(true);
    }

    @EventHandler
    public void hangingBreak(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player) {

            if(this.canBuild((Player) event.getRemover())) return;

            PlayerInfo playerInfo = Core.getInstance().getPlayerInfo((Player) event.getRemover());
            if (playerInfo.getRank().has(null, Rank.Admin, false)) {
                return;
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();
        PlayerInfo playerInfo = Core.getInstance().getPlayerInfo(player);

        if(playerInfo.getRank().has(null, Rank.Admin, false)) {
            event.setFormat(playerInfo.getRank().getTag()
                    + event.getPlayer().getName()
                    + " » "
                    + C.green + "%2$s");
        }
        else if(playerInfo.getRank().has(null, Rank.Mod, false)) {
            event.setFormat(playerInfo.getRank().getTag()
                    + event.getPlayer().getName()
                    + " » "
                    + C.yellow + "%2$s");
        }
        else if(playerInfo.getRank().has(null, Rank.Mega, false)) {
            event.setFormat(playerInfo.getRank().getTag()
                    + event.getPlayer().getName()
                    + " » "
                    + C.white + "%2$s");
        }
        else {
            event.setFormat(playerInfo.getRank().getTag()
                    + event.getPlayer().getName()
                    + " » "
                    + C.gray + "%2$s");
        }
    }

    @EventHandler
    public void onRain(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }
}
