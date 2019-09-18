package com.fschoen.gamenet.listener;

import com.fschoen.gamenet.GameNet;
import com.fschoen.gamenet.Utility;
import com.fschoen.gamenet.scheduler.TimerTask;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

public class JoinListener implements Listener {

    private GameNet plugin;
    List<UUID> playersToConsider;
    private FileConfiguration config;
    private static final String DATA_FILENAME = "data";

    public JoinListener(GameNet plugin, List<UUID> playersToConsider) {
        this.plugin = plugin;
        this.playersToConsider = playersToConsider;
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        FileConfiguration data = Utility.loadYml(plugin, DATA_FILENAME);

        if (!p.hasPlayedBefore() || data.get(p.getUniqueId().toString()) == null) {
            if (config.getBoolean("general.timer.enabled")) {
                data.set(p.getUniqueId().toString() + ".timer.time", config.get("general.timer.time"));
                data.set(p.getUniqueId().toString() + ".timer.timestamp", System.currentTimeMillis());
            }
            Utility.saveYml(plugin, data, DATA_FILENAME);
        }

        if (config.getBoolean("general.timer.enabled")) {
            // Check if last login was refill_time hours ago
            if (Utility.differenceHours(System.currentTimeMillis(), data.getLong(p.getUniqueId().toString() + ".timer.timestamp")) >= config.getInt("general.timer.refill_time")) {
                // Check if user has amassed retroactive days
                long timestamp = data.getLong(p.getUniqueId().toString() + ".timer.timestamp");
                timestamp += (1000 * 60 * 60 * config.getInt("general.timer.refill_time"));

                long difference = Utility.differenceHours(System.currentTimeMillis(), timestamp);
                int retrocativeDays = (int) (difference / 24);
                retrocativeDays = Math.min(retrocativeDays, config.getInt("general.timer.max_retroactive_days"));

                int time = data.getInt(p.getUniqueId().toString() + ".timer.time") + config.getInt("general.timer.time") * (retrocativeDays + 1);
                time = Math.min(time, config.getInt("general.timer.max_time"));

                data.set(p.getUniqueId().toString() + ".timer.time", time);
                data.set(p.getUniqueId().toString() + ".timer.timestamp", System.currentTimeMillis());
                Utility.saveYml(plugin, data, DATA_FILENAME);
            }

            // Check if player has no time remaining
            if (data.getInt(p.getUniqueId().toString() + ".timer.time") <= 0) {
                long hours, minutes;
                minutes = Utility.differenceMinutes(data.getLong(p.getUniqueId().toString()+".timer.timestamp")+1000*60*60*config.getInt("general.timer.refill_time"), System.currentTimeMillis());
                hours = minutes / 60;
                minutes -= hours*60;

                p.kickPlayer(config.getString("strings.timer.no_time").replace("<time>", hours + " hours, " + minutes + " minutes"));
                return;
            }

            // Check if player has logged in more than one time during the last time interval
            if (!playersToConsider.contains(p.getUniqueId())) {
                playersToConsider.add(p.getUniqueId());
            }
        }
    }
}
