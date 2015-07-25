package com.minehut.hub;

import com.minehut.hub.PitPvP.PitPvPManager;
import com.minehut.hub.daemon.DaemonManager;
import com.minehut.hub.daemon.commands.*;
import com.minehut.hub.damage.DamageManagerModule;
import com.minehut.hub.simpleListeners.SimpleListeners;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by luke on 7/2/15.
 */
public class Hub extends JavaPlugin {
    private static Hub hub;
    private PitPvPManager pitPvPManager;
    public DaemonManager daemonManager;

    @EventHandler
    public void onEnable() {
        this.hub = this;

        Bukkit.getServer().getWorlds().get(0).getSpawnLocation().setX(-4.5);
        Bukkit.getServer().getWorlds().get(0).getSpawnLocation().setY(75);
        Bukkit.getServer().getWorlds().get(0).getSpawnLocation().setZ(-22.5);
        Bukkit.getServer().getWorlds().get(0).getSpawnLocation().setYaw(180);

        new DamageManagerModule();
        new SimpleListeners(this);
        this.pitPvPManager = new PitPvPManager();

        /* Commands */
        new FlyCommand(this);
        new HelpCommand(this);

        /* Kingdoms Related */
        this.daemonManager = new DaemonManager(this);

        new CreateCommand(this, this.daemonManager);
        new JoinCommand(this, this.daemonManager);
        new ShutdownCommand(this, this.daemonManager);
        new ResetCommand(this, this.daemonManager);
        new RenameCommand(this, this.daemonManager);
        new ModifyMOTDCommand(this, this.daemonManager);
    }

    public static Hub getInstance() {
        return hub;
    }

    public PitPvPManager getPitPvPManager() {
        return pitPvPManager;
    }

    public Location getSpawn() {
        return this.getServer().getWorlds().get(0).getSpawnLocation();
    }
}
