package com.fschoen.gamenet;

import com.fschoen.gamenet.command.WarpCommand;
import com.fschoen.gamenet.listener.JoinListener;
import com.fschoen.gamenet.scheduler.TimerTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameNet extends JavaPlugin {

    private List<UUID> playersToConsiderForTimer = new ArrayList<>();

    @Override
    public void onEnable() {
        FileConfiguration config = this.getConfig();
        this.setConfigDefaults(config);
        this.registerCommands();
        this.registerListeners();
        BukkitTask task = new TimerTask(this, playersToConsiderForTimer).runTaskTimer(this, 0, 20*15);
    }

    @Override
    public void onDisable() {
        // On server stop
    }

    /**
     * Copies the given defaults into the configuration file and saves the file. Does not overwrite changed attributes.
     * @param config Configuration file of the plugin
     */
    private void setConfigDefaults(FileConfiguration config) {
        this.saveDefaultConfig();
        config.options().copyDefaults(true);
        saveConfig();
    }

    /**
     * Registers all commands.
     */
    private void registerCommands() {
        WarpCommand warpCommand = new WarpCommand(this);
        this.getCommand("warp").setExecutor(warpCommand);
        this.getCommand("warp").setTabCompleter(warpCommand);
    }

    /**
     * Registers all listeners.
     */
    private void registerListeners() {
        JoinListener joinListener = new JoinListener(this, playersToConsiderForTimer);
        this.getServer().getPluginManager().registerEvents(joinListener, this);
    }

}
