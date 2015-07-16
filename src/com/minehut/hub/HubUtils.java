package com.minehut.hub;

import com.minehut.core.rules.RuleManager;
import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.items.ItemStackFactory;
import com.minehut.core.status.menu.ServerMenuManager;
import com.minehut.hub.daemon.DaemonManager;
import com.minehut.kingdomhost.KingdomHost;
import com.minehut.kingdomhost.menu.MyKingdom;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by luke on 7/2/15.
 */
public class HubUtils {

    public static void setupInventory(Player player, boolean setHeldSlot) {
        if (setHeldSlot) {
            player.getInventory().setHeldItemSlot(1);
        }

        player.getInventory().setItem(1, ServerMenuManager.getServerSelectorItem());

        player.getInventory().setItem(4, MyKingdom.getItem());

        player.getInventory().setItem(7, RuleManager.getRulesItem());
    }
}
