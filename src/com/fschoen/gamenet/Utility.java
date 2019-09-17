package com.fschoen.gamenet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utility {

    public static void sendHeader(Player p, String title) {
        p.sendMessage("--- " + ChatColor.BOLD + title + ChatColor.BOLD + " ---");
    }

}
