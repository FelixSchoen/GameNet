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
import java.util.ArrayList;
import java.util.Collections;
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

        FileConfiguration warps = Utility.loadYml(plugin, WARP_FILENAME);

        if (args.length == 0) {
            return false;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                Utility.sendHeader(p, config.getString("strings.WarpCommand.your_warps"));

                List<String> list = new ArrayList<>();

                for (String s : warps.getConfigurationSection(p.getUniqueId().toString()).getKeys(false)) {
                    list.add(s);
                }

                if (list.size() == 0)
                    p.sendMessage(config.getString("strings.WarpCommand.no_warps"));
                else {
                    Collections.sort(list);
                    int i = 1;
                    for (String s : list) {
                        p.sendMessage(i + ". " + s);
                        i++;
                    }
                }

                return true;
            }
            else this.gotoWarp(args[0], warps, p);
            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                String name = args[1];

                if (name.equalsIgnoreCase("goto") || name.equalsIgnoreCase("go")
                        || name.equalsIgnoreCase("to") || name.equalsIgnoreCase("set")
                        || name.equalsIgnoreCase("delete") || name.equalsIgnoreCase("list")) {
                    p.sendMessage(config.getString("strings.general.name_reserved"));
                    return true;
                }

                warps.set(p.getUniqueId().toString() + "." + name + "." + "x", p.getLocation().getX());
                warps.set(p.getUniqueId().toString() + "." + name + "." + "y", p.getLocation().getY());
                warps.set(p.getUniqueId().toString() + "." + name + "." + "z", p.getLocation().getZ());
                warps.set(p.getUniqueId().toString() + "." + name + "." + "pitch", p.getLocation().getPitch());
                warps.set(p.getUniqueId().toString() + "." + name + "." + "yaw", p.getLocation().getYaw());
                warps.set(p.getUniqueId().toString() + "." + name + "." + "world", p.getLocation().getWorld().getName());
                Utility.saveYml(plugin, warps, WARP_FILENAME);

                p.sendMessage(config.getString("strings.WarpCommand.warp_set").replace("<warp>", name));
                return true;
            } else if (args[0].equalsIgnoreCase("delete")) {
                String name = args[1];

                if (warps.get(p.getUniqueId().toString() + "." + name) == null) {
                    p.sendMessage(config.getString("strings.WarpCommand.warp_not_exist").replace("<warp>", name));
                    return true;
                }

                warps.set(p.getUniqueId().toString() + "." + name, null);
                Utility.saveYml(plugin, warps, WARP_FILENAME);

                p.sendMessage(config.getString("strings.WarpCommand.warp_delete").replace("<warp>", name));
                return true;
            } else if (args[0].equalsIgnoreCase("goto") || args[0].equalsIgnoreCase("to") || args[0].equalsIgnoreCase("go")) {
                String name = args[1];

                this.gotoWarp(name, warps, p);

                return true;
            }
        }

        return false;
    }

    private void gotoWarp(String name, FileConfiguration warps, Player p) {
        if (warps.get(p.getUniqueId().toString() + "." + name) == null) {
            p.sendMessage(config.getString("strings.WarpCommand.warp_not_exist").replace("<warp>", name));
            return;
        }

        Location location = new Location(plugin.getServer().getWorld(warps.getString(p.getUniqueId().toString() + "." + name + ".world")),
                warps.getDouble(p.getUniqueId().toString() + "." + name + ".x"),
                warps.getDouble(p.getUniqueId().toString() + "." + name + ".y"),
                warps.getDouble(p.getUniqueId().toString() + "." + name + ".z"),
                (float) warps.getDouble(p.getUniqueId().toString() + "." + name + ".yaw"),
                (float) warps.getDouble(p.getUniqueId().toString() + "." + name + ".pitch"));
        p.teleport(location);

        p.sendMessage(config.getString("strings.WarpCommand.warp_goto").replace("<warp>", name));
    }

}
