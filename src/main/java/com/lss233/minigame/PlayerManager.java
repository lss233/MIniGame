package com.lss233.minigame;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlayerManager {
    private static Map<Plugin, Map<String, GamePlayer>> playerMap = new HashMap<>() ;
    public static Optional<GamePlayer> getPlayer(Player player, Plugin plugin){
        Optional<GamePlayer> gamePlayer = Optional.ofNullable(playerMap.get(plugin).get(player.getName()));
        if(gamePlayer.isPresent()){
            if(gamePlayer.get().getGame() == null)
                return Optional.empty();
            else
                return gamePlayer;
        }
        return Optional.empty();
    }
    static void registerPlugin(Plugin plugin) {
        playerMap.put(plugin, new HashMap<>());
    }

    public static GamePlayer joinGame(Player player, Plugin plugin, Game game) {
        if(playerMap.get(plugin).containsKey(player.getName())){
            player.sendMessage(plugin.L("game.already-ingame", "你已经在游戏中了。"));
            return null;
        }
        GamePlayer gamePlayer = game.join(player);
        if (gamePlayer != null) {
            playerMap.get(plugin).put(player.getName(), gamePlayer);
        }
        return gamePlayer;

    }

    public static void removePlayer(GamePlayer gamePlayer, Plugin plugin) {
        playerMap.computeIfPresent(plugin, (k, v) -> {
            v.remove(gamePlayer.getPlayer().getName());
           return v;
        });
    }
}
