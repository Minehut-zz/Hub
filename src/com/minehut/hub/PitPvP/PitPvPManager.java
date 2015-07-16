package com.minehut.hub.PitPvP;

import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.player.PlayerUtil;
import com.minehut.core.util.common.sound.S;
import com.minehut.hub.Hub;
import com.minehut.hub.HubUtils;
import com.minehut.hub.damage.CustomDamageEvent;
import com.minehut.hub.damage.CustomDeathEvent;
import com.minehut.hub.damage.CustomRespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by luke on 6/28/15.
 */
public class PitPvPManager implements Listener {
    private Location pitCenter;
    private int pitTopLevel = 71;

    public PitPvPManager() {
        this.pitCenter = new Location(Bukkit.getServer().getWorlds().get(0), 11, 67, -1);

        Bukkit.getServer().getPluginManager().registerEvents(this, Hub.getInstance());
    }

    @EventHandler
    public void onPitDamage(CustomDamageEvent event) {
        if (event.getDamagerPlayer() != null && event.getHurtPlayer() != null) {
            if (isInsidePit(event.getHurtPlayer().getLocation())) {
                return;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (isInsidePit(event.getTo())) {
            if (!isPreppedForPvP(event.getPlayer())) {
                this.prepareForPvP(event.getPlayer());
            }
        } else {
            if (isPreppedForPvP(event.getPlayer())) {
                this.prepareForOutside(event.getPlayer());
            }
        }
    }

    public boolean isPreppedForPvP(Player player) {
        if (player.getInventory().getChestplate() != null) {
            return true;
        }
        return false;
    }

    public void prepareForPvP(Player player) {
        player.setFlying(false);
        player.setAllowFlight(false);

        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD));

        player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));

        player.sendMessage(C.red + C.bold + "PVP ENABLED");
        S.playSound(player, Sound.NOTE_STICKS);
    }

    public void prepareForOutside(Player player) {
        player.getInventory().clear();

        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        player.sendMessage(C.green + C.bold + "PVP DISABLED");
        S.playSound(player, Sound.NOTE_STICKS);

        HubUtils.setupInventory(player, false);
    }

    @EventHandler
    public void onDeath(CustomDeathEvent event) {
        if (event.getKillerPlayer() != null && event.getDeadPlayer() != null) {
            event.getKillerPlayer().sendMessage(C.green + "You killed " + C.aqua + event.getDeadPlayer().getName());
            event.getDeadPlayer().sendMessage(C.yellow + "You were killed by " + C.red + event.getKillerPlayer().getName());

            S.playSound(event.getKillerPlayer(), Sound.LEVEL_UP);
            S.playSound(event.getKillerPlayer(), Sound.IRONGOLEM_HIT);

            S.playSound(event.getDeadPlayer(), Sound.IRONGOLEM_HIT);
            event.getDeadPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 90, 10, false, false));

            PlayerUtil.clearAll(event.getDeadPlayer());
            event.getDeadPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 90, 10, false, false));

            HubUtils.setupInventory(event.getDeadPlayer(), false);
        }
    }

    @EventHandler
    public void onRespawn(CustomRespawnEvent event) {
        event.setSpawn(Hub.getInstance().getSpawn());
    }

    public boolean isInsidePit(Location location) {
        if (location.getY() < pitTopLevel) {
            if (Math.abs(location.distance(pitCenter)) <= 13) {
                return true;
            }
        }
        return false;
    }
}
