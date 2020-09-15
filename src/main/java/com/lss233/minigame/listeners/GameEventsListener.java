package com.lss233.minigame.listeners;

import com.lss233.minigame.Game;
import com.lss233.minigame.GamePlayer;
import com.lss233.minigame.PlayerManager;
import com.lss233.minigame.Plugin;
import com.lss233.minigame.events.MiniGamePlayerDefeatedEvent;
import com.lss233.minigame.events.MiniGameStopEvent;
import com.lss233.minigame.events.player.MiniGamePlayerJoinGameEvent;
import com.lss233.minigame.events.player.MiniGamePlayerQuitEvent;
import com.lss233.minigame.utils.CountdownAction;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameEventsListener implements Listener {
    private final Plugin plugin;
    public GameEventsListener(Plugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDefeated(MiniGamePlayerDefeatedEvent event){
        if(!event.getPlugin().equals(plugin))   return;
        event.getGame().broadcast("game.defeat", "玩家 {winner} 打败了 {loser}", new HashMap<String, Object>() {{
            put("winner", event.getWinner().getPlayer().getName());
            put("loser", event.getLoser().getPlayer().getName());
        }});
    }
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoined(MiniGamePlayerJoinGameEvent event){
        if(!event.getPlugin().equals(plugin))   return;
        event.getGame().broadcast("game.player-join", "{player}加入了游戏。 ({current_players}/{max_players})", new HashMap<String, Object>(){{
            put("player", event.getPlayer().getPlayer().getName());
            put("current_players", event.getGame().getCurrentPlayers() + 1);
            put("max_players", event.getGame().getMaxPlayers());
        }});
        Bukkit.getScheduler().scheduleSyncDelayedTask(event.getPlugin().getJavaPlugin(), () -> {
            if(event.getGame().getCurrentPlayers() >= event.getGame().getMinPlayers()){
                event.getPlayer().attemptToStart();
            }
        }, 20);

    }
    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(MiniGamePlayerQuitEvent.Pre event){
        if(!event.getPlugin().equals(plugin))   return;
        if(event.getGame().getGameStatus().equals(Game.Status.GAMING)){
            event.getGame().updateGameStats("total_players", -1, event.getGame().getCurrentPlayers());
        }
        if(!event.getGame().getGameStatus().equals(Game.Status.WAITING)){
            try {
                event.getPlugin().getStatisticManager().savePlayerStats(event.getPlayer());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        PlayerManager.getPlayer(event.getPlayer(), plugin).ifPresent(GamePlayer::leave);
    }

    @EventHandler(ignoreCancelled = true)
    public void cancelCountingTasks(MiniGameStopEvent.Pre event){
        event.getGame().getCountdownActions().forEach(CountdownAction::cancel);
        /*
        List<CountdownAction> actionList = new ArrayList<>();
        plugin.getJavaPlugin().getServer().getScheduler().getPendingTasks().forEach(i -> {
            System.out.println("i = " + i);
            if(i instanceof CountdownAction){
                CountdownAction action = (CountdownAction) i;
                if(action.getGame().equals(event.getGame()))
                actionList.add(action);
            }
        });
        actionList.forEach(CountdownAction::cancel);

         */
    }
/*
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        PlayerManager.getPlayer(event.getEntity(), plugin).ifPresent(gamePlayer -> {
            if(gamePlayer.getGame().getOptions().isOnceLife()) {
                event.setDeathMessage("");
                if(event.getEntity().getKiller() != null)
                    PlayerManager.getPlayer(event.getEntity().getKiller(), plugin).ifPresent(killer -> {
                        MiniGamePlayerDefeatedEvent defeatedEvent = new MiniGamePlayerDefeatedEvent(killer, gamePlayer);
                        Bukkit.getPluginManager().callEvent(defeatedEvent);
                        //if(defeatedEvent.isCancelled()){}
                    });
                gamePlayer.getGame().kick(gamePlayer, Game.QuitReason.DIED);
            }
        });
    }
 */
}
