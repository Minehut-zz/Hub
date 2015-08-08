package com.minehut.hub.npc;

import com.minehut.core.util.common.creature.Creature;
import com.minehut.core.util.common.sound.S;
import com.minehut.hub.Hub;
import com.minehut.hub.damage.CustomDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Luke on 11/24/14.
 */
public abstract class Npc implements Listener {
    public LivingEntity mob;
    public LivingEntity slime;
    public Location location;
    public boolean freeze;
    public double yaw = 0;

	public Npc(EntityType entityType, String name, Location location, boolean freeze) {
        this.location = location;
        Bukkit.getPluginManager().registerEvents(this, Hub.instance);

        location.getChunk().load();
		this.yaw = location.getY();
        this.mob = Creature.spawnCreature(entityType, location);
        this.mob.teleport(location);
		this.mob.setRemoveWhenFarAway(false);
        this.freeze = freeze;
        this.checkLocation();

        //Nametag Magic
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		armorStand.setCustomName(name);
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
		armorStand.setRemoveWhenFarAway(false);

        Slime slime = (Slime) location.getWorld().spawnEntity(location, EntityType.SLIME);
		slime.setRemoveWhenFarAway(false);
        slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10));
        slime.setSize(-3);
        this.slime = slime;

        slime.setPassenger(armorStand);
        this.mob.setPassenger(slime);

    }

	public Npc(EntityType entityType, String name, Location location, boolean freeze, boolean normalNametag) {
		this.location = location;
		Bukkit.getPluginManager().registerEvents(this, Hub.instance);

		Creature.loadChunk(location);
		this.mob = Creature.spawnCreature(entityType, location);
		this.mob.setCustomName(name);
		this.mob.setCustomNameVisible(true);
		this.mob.teleport(location);
		this.mob.setRemoveWhenFarAway(false);
		this.freeze = freeze;
		this.checkLocation();
	}

    public Npc(Horse.Style style, String name, Location location, boolean freeze) {
        this.location = location;
        Bukkit.getPluginManager().registerEvents(this, Hub.instance);

        Creature.loadChunk(location);
        this.mob = Creature.spawnAdultHorse(style, location, name);
        this.mob.teleport(location);
        this.freeze = freeze;
        this.checkLocation();
    }



    public Npc(Skeleton.SkeletonType skeletonType, String name, Location location, boolean freeze) {
        this.location = location;
        Bukkit.getPluginManager().registerEvents(this, Hub.instance);

        Creature.loadChunk(location);
        if (skeletonType == Skeleton.SkeletonType.WITHER) {
            this.mob = Creature.spawnNetherSkelleton(location, name);
        } else {
            this.mob = Creature.spawnCreature(EntityType.SKELETON, location, name);
        }
        this.mob.teleport(location);
        this.freeze = freeze;
        this.checkLocation();
    }

    public abstract void onClick(Player player);

    @EventHandler
    public void interact(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() == this.slime) {



            // Get the Vector from you to the entity. You basically 'translate' from the global coords to
            // local coords with you as the center and take the Vector to the Entity's location
            Vector toEntity = this.slime.getLocation().toVector().subtract(event.getPlayer().getLocation().toVector());
            // Now we need a direction vector... simple, isn't it?
            Vector direction = event.getPlayer().getLocation().getDirection();
            // Now, for the Vector math:
            // The dot product between Vector a and b normally returns cos(angle) * length of a * length of b.
            // The direction Vector's length is already 1 (thanks to Bukkit), but the toEntity Vector isn't
            // normalize() will divide the x, y and z of the Vector with the Vector's length, which
            // means that the resulting Vector will be of length 'Vector length / Vector length'... O right, that's x / x,
            // otherwise known as 1 ;3.
            double dot = toEntity.normalize().dot(direction);
            // If dot == 1, then the player is looking right at the entity (angle of 0, we are talking about cosine after all)
            // If dot == 0, then the player is looking to somewhere perpendicular - 90 degrees rotated away - from the entity
            // Thus, the closer to 1, the closer we are looking at the entity.


            if (dot > .2) {
                this.onClick(event.getPlayer());
                event.setCancelled(true);
            }
        } else if (event.getRightClicked() == this.mob) {
            this.onClick(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCustomDamage(CustomDamageEvent event) {
        if(event.getHurtEntity() == this.mob || event.getHurtEntity() == this.slime) {
            if (event.getDamagerPlayer() != null) {
                event.setCancelled(true);
                this.onClick(event.getDamagerPlayer());
            }
        }
    }

    public void checkLocation() {
        if (freeze)
            mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 6, false, false));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Hub.instance, new Runnable() {
            @Override
            public void run() {
                if (freeze && mob.getLocation() != location) {
                    mob.teleport(location);
                }
                if (mob.getLocation().getY() <= 0) {
                    mob.teleport(location);
					mob.getLocation().setYaw((float) yaw);
                }
            }
        }, 0, 20);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getEntity() == this.mob) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() == this.mob || event.getEntity() == this.slime) {
            event.setCancelled(true);
        }
    }

    public LivingEntity getMob() {
        return mob;
    }

    public Location getLocation() {
        return location;
    }
}
