package com.fschoen.gamenet;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class GameNet extends JavaPlugin {

    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        // On server start
    }

    @Override
    public void onDisable() {
        // On server stop
    }

    public static String NO_PERMISSION = "You are not permitted to execute this command.";
    public static String ONLY_PLAYER_EXECUTE_COMMAND = "Only Players may execute this command.";

}
