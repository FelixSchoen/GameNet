package com.fschoen.gamenet.commands;

import com.fschoen.gamenet.GameNet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandExecutor {

    private GameNet main;

    public TeleportCommand(GameNet main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(GameNet.ONLY_PLAYER_EXECUTE_COMMAND);
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("GameNet.teleport")) {
            p.sendMessage(GameNet.NO_PERMISSION);
            return true;
        }

        return true;
    }
}
