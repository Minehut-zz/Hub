package com.minehut.hub.miniplugins.Archery.command;

import com.minehut.core.command.Command;
import com.minehut.core.player.Rank;
import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.core.util.common.sound.S;
import com.minehut.hub.Hub;
import com.minehut.hub.miniplugins.Archery.ArcheryManager;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

/**
 * Created by luke on 6/12/15.
 */
public class ArcheryCommand extends Command {
    public ArcheryManager archeryManager;

    public ArcheryCommand(JavaPlugin plugin, ArcheryManager archeryManager) {
        super(plugin, "archery", Rank.Admin);
        this.archeryManager = archeryManager;
    }

    @Override
    public boolean call(Player player, ArrayList<String> args) {

        if (args == null || args.size() != 1) {
            F.warning(player, "/archery add/remove");
            return true;
        }

        if (args.get(0).equalsIgnoreCase("add")) {
            archeryManager.spawnMob();
            F.message(player, C.green + "Successfully spawned archery mob");
            S.pop(player);
        } else if (args.get(0).equalsIgnoreCase("remove")) {
            LivingEntity livingEntity = archeryManager.mobs.get(0);
            archeryManager.mobs.remove(0);
            livingEntity.remove();
            F.message(player, C.red + "Successfully removed archery mob");
            S.pop(player);
        } else {
            F.warning(player, "/archery add/remove");
        }

        return false;
    }
}
