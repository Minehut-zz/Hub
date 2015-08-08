package com.minehut.hub.daemon.commands;

import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.core.util.common.sound.S;
import com.minehut.core.command.Command;
import com.minehut.core.player.Rank;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luke on 6/12/15.
 */
public class HelpCommand extends Command {

    public HelpCommand(JavaPlugin plugin) {
        super(plugin, "help", Arrays.asList("?"), Rank.regular);
    }

    @Override
    public boolean call(Player player, ArrayList<String> arrayList) {

        F.message(player, "/create (name)");
        F.message(player, "/join (name)");
        F.message(player, "/reset");
        F.message(player, "/set-motd (motd)");
        F.message(player, "/shutdown (name)");
        F.message(player, "/rename (old-name) (new-name)");

        S.pop(player);

        return false;
    }
}
