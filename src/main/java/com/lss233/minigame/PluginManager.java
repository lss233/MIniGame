package com.lss233.minigame;

import org.bukkit.plugin.java.JavaPlugin;

public class PluginManager {
    public static Plugin registerPlugin(JavaPlugin plugin, Class<? extends GameOptions> optionsClass){
        Plugin miniPlugin = new Plugin(plugin, optionsClass);
        PlayerManager.registerPlugin(miniPlugin);
        return miniPlugin;
    }
}
