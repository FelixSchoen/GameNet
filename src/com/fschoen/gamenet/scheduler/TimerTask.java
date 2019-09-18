package com.fschoen.gamenet.scheduler;

import com.fschoen.gamenet.GameNet;
import com.fschoen.gamenet.Utility;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class TimerTask extends BukkitRunnable {

    private GameNet plugin;
    private FileConfiguration config;
    private List<UUID> playersToConsiderForTimer;
    private static final String DATA_FILENAME = "data";

    public TimerTask(GameNet plugin, List<UUID> playersToConsiderForTimer) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.playersToConsiderForTimer = playersToConsiderForTimer;
    }

    @Override
    public void run() {
        FileConfiguration data = Utility.loadYml(plugin, DATA_FILENAME);
        for (UUID uuid : playersToConsiderForTimer) {
            int i = data.getInt(uuid + ".timer.time") - 1;
            data.set(uuid+".timer.time", i);
            Utility.saveYml(plugin, data, DATA_FILENAME);

            if (i <= 0 && plugin.getServer().getOfflinePlayer(uuid).isOnline()) ((Player)plugin.getServer().getOfflinePlayer(uuid)).kickPlayer(config.getString("strings.timer.no_time"));
        }
        playersToConsiderForTimer.clear();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            playersToConsiderForTimer.add(p.getUniqueId());
        }
    }
}
