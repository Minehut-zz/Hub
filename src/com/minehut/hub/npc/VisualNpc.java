package com.minehut.hub.npc;

import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.sound.S;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Luke on 11/26/14.
 */
public class VisualNpc extends Npc implements Listener {
    String name;
    ArrayList<String> messages;
    Random random = new Random();

    public VisualNpc(EntityType entityType, String name, Location location, boolean freeze, ArrayList<String> messages) {
        super(entityType, name, location, freeze);
        this.name = name;
        this.messages = messages;
    }

	public VisualNpc(EntityType entityType, String name, Location location, boolean freeze, ArrayList<String> messages, boolean normalNametag) {
		super(entityType, name, location, freeze, normalNametag);
		this.name = name;
		this.messages = messages;
	}

    public VisualNpc(Horse.Style style, String name, Location location, boolean freeze, ArrayList<String> messages) {
        super(style, name, location, freeze);
        this.name = name;
        this.messages = messages;
    }

    public VisualNpc(Skeleton.SkeletonType skeletonType, String name, Location location, boolean freeze, ArrayList<String> messages) {
        super(skeletonType, name, location, freeze);
        this.name = name;
        this.messages = messages;
    }

    @Override
    public void onClick(Player player) {
        if (this.messages == null)
            return;

        int index = random.nextInt(this.messages.size());
        String msg = this.messages.get(index);

        S.pop(player);
        player.sendMessage(C.yellow + name + "> " + C.mBody + msg);
    }

    @EventHandler
    public void onMount(VehicleEnterEvent event) {
        if (event.getVehicle() == this.getMob()) {
            event.setCancelled(true);
        }
    }
}
