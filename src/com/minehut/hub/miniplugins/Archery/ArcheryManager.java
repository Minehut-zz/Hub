package com.minehut.hub.miniplugins.Archery;

import com.minehut.core.util.common.chat.F;
import com.minehut.core.util.common.creature.Creature;
import com.minehut.core.util.common.items.ItemStackFactory;
import com.minehut.core.util.common.particles.ParticleEffect;
import com.minehut.core.util.common.region.Region;
import com.minehut.core.util.common.sound.S;
import com.minehut.hub.Hub;
import com.minehut.hub.damage.CustomDamageEvent;
import com.minehut.hub.miniplugins.Archery.command.ArcheryCommand;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by luke on 8/9/15.
 */
public class ArcheryManager implements Listener {
    public Region gates;
    public ArrayList<LivingEntity> mobs;
    public ArrayList<Location> spawns;
    public World world;

    public ArcheryManager() {
        this.world = Bukkit.getServer().getWorlds().get(0);

//        this.spawns = new Region(
//                "spawns",
//                new Location(world, 44.5, 72, -83.5),
//                new Location(world, 36.5, 72, -90.5)
//        );

        this.spawns = new ArrayList<>();
        this.spawns.add(new Location(world, 43.5, 79, -90.5));
        this.spawns.add(new Location(world, 40.5, 79, -89.5));
        this.spawns.add(new Location(world, 37.5, 79, -88.5));
        this.spawns.add(new Location(world, 38.5, 79, -90.5));
        this.spawns.add(new Location(world, 40.5, 79, -87.5));
        this.spawns.add(new Location(world, 37.5, 79, -90.5));
        this.spawns.add(new Location(world, 43.5, 79, -86.5));
        this.spawns.add(new Location(world, 40.5, 79, -86.5));
        this.spawns.add(new Location(world, 39.5, 79, -89.5));

        this.gates = new Region(
                "gates",
                new Location(world, 35.5, 74.5, -77.5),
                new Location(world, 45.5, 72, -75.5)
        );

        this.mobs = new ArrayList<>();
        this.spawnMob();
        this.spawnMob();
        this.spawnMob();

        new ArcheryCommand(Hub.instance, this);

        Bukkit.getServer().getPluginManager().registerEvents(this, Hub.instance);
    }

    @EventHandler
    public void onCustomDamage(CustomDamageEvent event) {
        if (this.mobs.contains(event.getHurtEntity())) {
            if (event.getProjectile() != null) {
                if (event.getDamagerPlayer() != null) {
                    Location center = event.getHurtEntity().getEyeLocation().add(0, -.5, 0);
                    ParticleEffect.SMOKE_LARGE.display(.5f, .5f, .5f, 0.05f, 40, center, 20);
                    ParticleEffect.LAVA.display(.5f, .5f, .5f, 0.05f, 40, center, 20);
                    ParticleEffect.CRIT.display(.5f, .5f, .5f, 0.05f, 40, center, 20);

                    event.getHurtEntity().remove();

                    S.playSound(event.getDamagerPlayer(), Sound.IRONGOLEM_HIT);

                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Hub.instance, new Runnable() {
                        @Override
                        public void run() {
                            spawnMob();
                        }
                    }, 10L);
                }
            }
        }
    }

    public void spawnMob() {
//        int maxX = Math.max(this.spawns.getLoc1().getBlockX(), this.spawns.getLoc2().getBlockX());
//        int minX = Math.min(this.spawns.getLoc1().getBlockX(), this.spawns.getLoc2().getBlockX());
//
//        int maxZ = Math.max(this.spawns.getLoc1().getBlockZ(), this.spawns.getLoc2().getBlockZ());
//        int minZ = Math.max(this.spawns.getLoc1().getBlockZ(), this.spawns.getLoc2().getBlockZ());
//
//        Random r = new Random();
//        int x = r.nextInt(maxX - minX) + minX;
//        int z = r.nextInt(maxZ - minZ) + minZ;
//
//        double x = minX + (double)(Math.random()*maxX);
//        F.debug(Double.toString(x));
//        double z = minZ + (double)(Math.random()*maxZ);
//        F.debug(Double.toString(z));


        Location spawn = this.spawns.get(new Random().nextInt(this.spawns.size()));

        this.mobs.add(Creature.spawnCreature(EntityType.CHICKEN, spawn));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        /* Entering */
        if (this.gates.contains(event.getTo()) && !this.gates.contains(event.getFrom())) {
            event.getPlayer().getInventory().setItemInHand(ItemStackFactory.createItem(Material.BOW, Enchantment.ARROW_INFINITE, 1));
            event.getPlayer().getInventory().setItem(24, ItemStackFactory.createItem(Material.ARROW));
            S.pop(event.getPlayer());
        }

        /* Leaving */
        else if (this.gates.contains(event.getFrom()) && !this.gates.contains(event.getTo())) {
            event.getPlayer().getInventory().remove(Material.BOW);
            event.getPlayer().getInventory().remove(Material.ARROW);
            S.pop(event.getPlayer());
        }
    }
}
