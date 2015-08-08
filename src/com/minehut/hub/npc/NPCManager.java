package com.minehut.hub.npc;

import com.minehut.core.util.common.chat.C;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

/**
 * Created by luke on 8/7/15.
 */
public class NPCManager {
    public NPCManager(World world) {

        new CommandNPC("/gs-kingdom", EntityType.VILLAGER, C.green + C.bold + "KINGDOMS", new Location(world, -4.5, 72.5, -32.5), true);
        new CommandNPC("/gs-warzone", EntityType.SKELETON, C.yellow + C.bold + "WARZONE", new Location(world, -8.5, 72.5, -32.5), true);
        new CommandNPC("/gs-tnt", EntityType.CREEPER, C.blue + C.bold + "TNT WARS", new Location(world, -11.5, 72.5, -31.5), true);
        new CommandNPC("/gs-vanilla", EntityType.SHEEP, C.purple + C.bold + "VANILLA", new Location(world, 2.5, 72.5, -31.5), true);

        new CommandNPC("/mykingdom", EntityType.VILLAGER, C.yellow + "What is my Kingdom?", new Location(world, -9.5, 73, -17.5), true);
        new CommandNPC("/help", EntityType.VILLAGER, C.yellow + "Need Help?", new Location(world, 0, 73, -17.5), true);

    }
}
