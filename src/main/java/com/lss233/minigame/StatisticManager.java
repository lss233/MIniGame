package com.lss233.minigame;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class StatisticManager {
    private final Plugin plugin;
    private final File statsDir;;
    public StatisticManager(Plugin plugin) {
        this.plugin = plugin;
        this.statsDir = new File(plugin.getJavaPlugin().getDataFolder(), "playerstats");
        if(!statsDir.exists())
            statsDir.mkdirs();
    }

    public ConfigurationSection getPlayerStats(Player player) {
        File file = new File(statsDir, player.getUniqueId() + ".yml");
        if(file.exists()){
            YamlConfiguration config =YamlConfiguration.loadConfiguration(file);
            return config.getConfigurationSection("stats");
        } else {
            return new MemoryConfiguration();
        }
    }
    public void writePlayerStats(Player player, ConfigurationSection section) throws IOException {
        File file = new File(statsDir, player.getUniqueId() + ".yml");
        if(file.exists())
            file.delete();
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("stats", section);
        configuration.save(file);
    }
    public void savePlayerStats(Player player, Map<String, Object> statsMap) throws IOException {
        if(statsMap.isEmpty()) return;
        ConfigurationSection stats = getPlayerStats(player);
        ConfigurationSection total = stats.getConfigurationSection("total");
        stats.set("last", statsMap);
        statsMap.keySet().forEach(i -> {
            Object currentValue;
            if(total == null) currentValue = null;
            else currentValue  = total.get(i);
            if(currentValue == null){
                stats.set("total." + i, statsMap.get(i));
            } else if(currentValue instanceof Integer){
                stats.set("total." + i, stats.getInt("total." + i, 0) + (Integer) currentValue);
            }
        });
        stats.set("player", player.getName());
        writePlayerStats(player, stats);
    }

    public void savePlayerStats(GamePlayer player) throws IOException {
        savePlayerStats(player.getPlayer(), player.getPlayerStats());
    }


}
