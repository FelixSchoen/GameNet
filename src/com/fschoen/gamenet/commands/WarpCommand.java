package com.fschoen.gamenet.commands;

import com.fschoen.gamenet.GameNet;
import com.fschoen.gamenet.Utility;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WarpCommand implements CommandExecutor {

    private GameNet plugin;
    private FileConfiguration config;
    private static final String WARP_FILENAME = "warps";

    public WarpCommand(GameNet plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(config.getString("strings.general.only_player_execute_command"));
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("GameNet.warp")) {
            p.sendMessage(config.getString("strings.general.no_permission"));
            return true;
        }

        FileConfiguration warps = loadYml(plugin, WARP_FILENAME);

        if (args.length == 0) {
            return false;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                Utility.sendHeader(p, config.getString("strings.WarpCommand.your_warps"));

                for (String s : config.getConfigurationSection(p.getUniqueId().toString()).getKeys(true)) {
                    p.sendMessage(s);
                }

                return true;
            }
            return false;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                String name = args[1];

                warps.set(p.getUniqueId().toString() + "." + name + "." + "x", p.getLocation().getX());
                warps.set(p.getUniqueId().toString() + "." + name + "." + "y", p.getLocation().getY());
                warps.set(p.getUniqueId().toString() + "." + name + "." + "z", p.getLocation().getZ());
                warps.set(p.getUniqueId().toString() + "." + name + "." + "pitch", p.getLocation().getPitch());
                warps.set(p.getUniqueId().toString() + "." + name + "." + "yaw", p.getLocation().getYaw());
                warps.set(p.getUniqueId().toString() + "." + name + "." + "world", p.getLocation().getWorld().getName());
                saveYml(warps, WARP_FILENAME);

                p.sendMessage(config.getString("strings.WarpCommand.warp_set").replace("<warp>", name));
                return true;
            } else if (args[0].equalsIgnoreCase("delete")) {
                //TODO
            } else if (args[0].equalsIgnoreCase("goto") || args[0].equalsIgnoreCase("to") || args[0].equalsIgnoreCase("go")) {
                String name = args[1];

                if (warps.get(p.getUniqueId().toString() + "." + name) == null)
                    p.sendMessage(config.getString("strings.WarpCommand.warp_not_exist"));

                Location location = new Location(plugin.getServer().getWorld(warps.getString(p.getUniqueId().toString() + "." + name + ".world")),
                        warps.getDouble(p.getUniqueId().toString() + "." + name + ".x"),
                        warps.getDouble(p.getUniqueId().toString() + "." + name + ".y"),
                        warps.getDouble(p.getUniqueId().toString() + "." + name + ".z"),
                        (float) warps.getDouble(p.getUniqueId().toString() + "." + name + ".yaw"),
                        (float) warps.getDouble(p.getUniqueId().toString() + "." + name + ".pitch"));
                p.teleport(location);

                p.sendMessage(config.getString("strings.WarpCommand.warp_goto").replace("<warp>", name));
                return true;
            }
        }

        return false;
    }

    /**
     * Loads a yml from a given name.
     *
     * @param plugin Main class of the plugin
     * @param name   Filename
     * @return The yml configuration found.
     */
    private FileConfiguration loadYml(GameNet plugin, String name) {
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
    private boolean saveYml(FileConfiguration config, String name) {
        File file = new File(plugin.getDataFolder() + "/" + name + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
