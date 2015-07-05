package com.minehut.hub.simpleListeners;

import com.minehut.commons.common.chat.C;
import com.minehut.commons.common.player.PlayerUtil;
import com.minehut.core.Core;
import com.minehut.core.connection.event.AsyncPlayerInfoInitiatedEvent;
import com.minehut.core.player.PlayerInfo;
import com.minehut.core.player.Rank;
import com.minehut.hub.Hub;
import com.minehut.hub.HubUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by luke on 7/2/15.
 */
public class SimpleListeners implements Listener {
    private Hub hub;
    private Location spawn;

    public SimpleListeners(Hub hub) {
        this.spawn = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();

        Bukkit.getServer().getPluginManager().registerEvents(this, hub);
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
        player.teleport(HubUtils.getSpawn());
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 90, 10, false, false));

        HubUtils.setupInventory(player, true);

        event.setJoinMessage("");

        player.sendMessage(C.divider);
        player.sendMessage("");
        player.sendMessage(C.space + "Welcome to the " + C.aqua + C.bold + "MINEHUT NETWORK" + C.white + "!");
        player.sendMessage(C.space + "Create a server with " + C.aqua + "/create (name)");
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
        PlayerInfo playerInfo = Core.getInstance().getPlayerInfo(event.getPlayer());
        if (!playerInfo.getRank().has(null, Rank.Admin, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
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
        else {
            event.setFormat(playerInfo.getRank().getTag()
                    + event.getPlayer().getName()
                    + " » "
                    + C.white + "%2$s");
        }
    }

    @EventHandler
    public void onRain(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }
}
