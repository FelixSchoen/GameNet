package com.fschoen.gamenet;

import com.fschoen.gamenet.commands.WarpCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class GameNet extends JavaPlugin {

    @Override
    public void onEnable() {
        FileConfiguration config = this.getConfig();
        this.setConfigDefaults(config);
        this.registerCommands();
        this.registerListeners();
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
        //TODO
    }

}
