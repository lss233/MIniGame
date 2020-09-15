package com.lss233.minigame.utils;

import com.lss233.minigame.Plugin;
import org.bukkit.entity.Player;

import java.util.Map;

public interface Messageable {

    default void sendMessage(String key, String def){
        getPlayer().sendMessage(getPlugin().L(key, def));
    }
    default void sendMessage(String key, String def, Map<String, Object> variables) {
        getPlayer().sendMessage(getPlugin().L(key, def, variables));
    }
    Plugin getPlugin();
    Player getPlayer();
}
