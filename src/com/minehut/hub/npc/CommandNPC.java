package com.minehut.hub.npc;

import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.sound.S;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Luke on 11/26/14.
 */
public class CommandNPC extends Npc implements Listener {
    String name;
    String command;

    public CommandNPC(String command, EntityType entityType, String name, Location location, boolean freeze) {
        super(entityType, name, location, freeze);
        this.name = name;
        this.command = command;
    }

	public CommandNPC(String command, EntityType entityType, String name, Location location, boolean freeze, ArrayList<String> messages, boolean normalNametag) {
		super(entityType, name, location, freeze, normalNametag);
		this.name = name;
        this.command = command;
	}

    public CommandNPC(String command, Horse.Style style, String name, Location location, boolean freeze) {
        super(style, name, location, freeze);
        this.name = name;
        this.command = command;
    }

    public CommandNPC(String command, Skeleton.SkeletonType skeletonType, String name, Location location, boolean freeze) {
        super(skeletonType, name, location, freeze);
        this.name = name;
        this.command = command;
    }

    @Override
    public void onClick(Player player) {
        if (this.command != null) {
            Bukkit.getServer().getPluginManager().callEvent(new PlayerCommandPreprocessEvent(player, this.command));
        }
    }

    @EventHandler
    public void onMount(VehicleEnterEvent event) {
        if (event.getVehicle() == this.getMob()) {
            event.setCancelled(true);
        }
    }
}
