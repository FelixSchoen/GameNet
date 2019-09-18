package com.fschoen.gamenet;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class Utility {

    /**
     * Loads a yml from a given name.
     *
     * @param plugin Main class of the plugin
     * @param name   Filename
     * @return The yml configuration found.
     */
    public static FileConfiguration loadYml(GameNet plugin, String name) {
        File file = new File(plugin.getDataFolder() + "/" + name + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config;
    }

    /**
     * Saves a given yml configuration to a given file.
     *
     * @param config The yml configuration
     * @param name   Filename
     * @return True if the operation succeeds, false otherwise.
     */
    public static boolean saveYml(GameNet plugin, FileConfiguration config, String name) {
        File file = new File(plugin.getDataFolder() + "/" + name + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void sendHeader(Player p, String title) {
        p.sendMessage("--- " + ChatColor.BOLD + title + ChatColor.BOLD + " ---");
    }

    public static long differenceHours(long later, long earlier) {
        long difference = later - earlier;
        difference /= (1000*60*60);
        return difference;
    }

}
