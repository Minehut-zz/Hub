package com.minehut.hub;

import com.minehut.commons.common.chat.C;
import com.minehut.commons.common.items.ItemStackFactory;
import com.minehut.core.status.menu.ServerMenuManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by luke on 7/2/15.
 */
public class HubUtils {
    public static Location getSpawn() {
        return Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
    }

    public static void setupInventory(Player player, boolean setHeldSlot) {
        if (setHeldSlot) {
            player.getInventory().setHeldItemSlot(1);
        }

        player.getInventory().setItem(1, ServerMenuManager.getServerSelectorItem());

        player.getInventory().setItem(3, getOwnedServersItem());
    }

    public static ItemStack getOwnedServersItem() {
        return ItemStackFactory.createItem(Material.SIGN_POST, C.yellow + C.bold + "Your Kingdom Info");
    }
}
