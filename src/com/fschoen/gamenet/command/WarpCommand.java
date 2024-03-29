package com.fschoen.gamenet.command;

import com.fschoen.gamenet.GameNet;
import com.fschoen.gamenet.Utility;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WarpCommand implements CommandExecutor, TabCompleter {

    private GameNet plugin;
    private FileConfiguration config;
    private static final String WARP_FILENAME = "data";

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

                List<String> list = this.getWarpNames(warps, p);

                if (list.size() == 0)
                    p.sendMessage(config.getString("strings.WarpCommand.no_warps"));
                else {
                    int i = 1;
                    for (String s : list) {
                        p.sendMessage(i + ". " + s);
                        i++;
                    }
                }

                return true;
            } else this.gotoWarp(args[0], warps, p);
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

                warps.set(p.getUniqueId().toString() + ".warps." + name + "." + "x", p.getLocation().getX());
                warps.set(p.getUniqueId().toString() + ".warps." + name + "." + "y", p.getLocation().getY());
                warps.set(p.getUniqueId().toString() + ".warps." + name + "." + "z", p.getLocation().getZ());
                warps.set(p.getUniqueId().toString() + ".warps." + name + "." + "pitch", p.getLocation().getPitch());
                warps.set(p.getUniqueId().toString() + ".warps." + name + "." + "yaw", p.getLocation().getYaw());
                warps.set(p.getUniqueId().toString() + ".warps." + name + "." + "world", p.getLocation().getWorld().getName());
                Utility.saveYml(plugin, warps, WARP_FILENAME);

                p.sendMessage(config.getString("strings.WarpCommand.warp_set").replace("<warp>", name));
                return true;
            } else if (args[0].equalsIgnoreCase("delete")) {
                String name = args[1];

                if (warps.get(p.getUniqueId().toString() + ".warps." + name) == null) {
                    p.sendMessage(config.getString("strings.WarpCommand.warp_not_exist").replace("<warp>", name));
                    return true;
                }

                warps.set(p.getUniqueId().toString() + ".warps." + name, null);
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player p = (Player) sender;
        List<String> completions = new ArrayList<>();
        List<String> list = new ArrayList<>();
        FileConfiguration warps = Utility.loadYml(plugin, WARP_FILENAME);

        if (args.length == 1) {
            list.add("set");
            list.add("delete");
            list.add("goto");
            list.add("list");
            StringUtil.copyPartialMatches(args[0], list, completions);
            return completions;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("goto")
                    || args[0].equalsIgnoreCase("go") || args[0].equalsIgnoreCase("to")) {
                list.addAll(this.getWarpNames(warps, p));
                StringUtil.copyPartialMatches(args[1], list, completions);
                return completions;
            }
        }

        return null;
    }

    private void gotoWarp(String name, FileConfiguration warps, Player p) {
        if (warps.get(p.getUniqueId().toString() + ".warps." + name) == null) {
            p.sendMessage(config.getString("strings.WarpCommand.warp_not_exist").replace("<warp>", name));
            return;
        }

        Location location = new Location(plugin.getServer().getWorld(warps.getString(p.getUniqueId().toString() + "." + name + ".world")),
                warps.getDouble(p.getUniqueId().toString() + ".warps." + name + ".x"),
                warps.getDouble(p.getUniqueId().toString() + ".warps." + name + ".y"),
                warps.getDouble(p.getUniqueId().toString() + ".warps." + name + ".z"),
                (float) warps.getDouble(p.getUniqueId().toString() + ".warps." + name + ".yaw"),
                (float) warps.getDouble(p.getUniqueId().toString() + ".warps." + name + ".pitch"));
        p.teleport(location);

        p.sendMessage(config.getString("strings.WarpCommand.warp_goto").replace("<warp>", name));
    }

    private List<String> getWarpNames(FileConfiguration warps, Player p) {

        List<String> list;

        if (warps.getConfigurationSection(p.getUniqueId().toString() + ".warps") == null) list = new ArrayList<>();
        else list = new ArrayList<>(warps.getConfigurationSection(p.getUniqueId().toString() + ".warps").getKeys(false));

        Collections.sort(list);

        return list;
    }

}
