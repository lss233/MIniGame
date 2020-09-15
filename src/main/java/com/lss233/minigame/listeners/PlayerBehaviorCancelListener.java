package com.lss233.minigame.listeners;

import com.lss233.minigame.Game;
import com.lss233.minigame.PlayerManager;
import com.lss233.minigame.Plugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerBehaviorCancelListener implements Listener {
    private final Plugin plugin;
    public PlayerBehaviorCancelListener(Plugin plugin) {
        this.plugin = plugin;
    }

    private boolean validPlayer(Entity player){
        if(player instanceof Player)
        return
                PlayerManager.getPlayer((Player)player, plugin).isPresent();
        else
            return false;
    }
    @EventHandler(ignoreCancelled = true)
    public void onPlayerPlace(BlockPlaceEvent event){
        if(event.getPlayer().hasPermission(plugin.getJavaPlugin().getName() + ".admin.break-bypass")) return;
        if(validPlayer(event.getPlayer())){
            event.setCancelled(!PlayerManager.getPlayer(event.getPlayer(), plugin).get().getGame().getOptions().isAllowPlayerPlace());
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onPlayerBreak(BlockBreakEvent event){
        if(event.getPlayer().hasPermission(plugin.getJavaPlugin().getName() + ".admin.break-bypass")) return;
        if(validPlayer(event.getPlayer())){
            event.setCancelled(!PlayerManager.getPlayer(event.getPlayer(), plugin).get().getGame().getOptions().isAllowPlayerBreak());
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageEvent event){
        if(event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) return;
        if(validPlayer(event.getEntity())){
            event.setCancelled(!PlayerManager.getPlayer((Player)event.getEntity(), plugin).get().getGame().getOptions().isAllowEntityDamage());
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event){
        if(event.getPlayer().hasPermission(plugin.getJavaPlugin().getName() + ".admin.move-bypass")) return;
        if(validPlayer(event.getPlayer())){
            if(!PlayerManager.getPlayer(event.getPlayer(), plugin).get().getGame().getGameStatus().equals(Game.Status.GAMING)) return;
            event.setCancelled(!PlayerManager.getPlayer(event.getPlayer(), plugin).get().getGame().isInField(event.getTo()));
        }
    }
    @EventHandler
    public void onPlayerHungry(FoodLevelChangeEvent event){
        if(validPlayer(event.getEntity()) && !event.isCancelled()){
            event.setCancelled(!PlayerManager.getPlayer((Player)event.getEntity(), plugin).get().getGame().getOptions().isAllowHungry());
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event){
        if(validPlayer(event.getPlayer()) && !event.isCancelled() && !event.getPlayer().hasPermission(plugin.getJavaPlugin().getName() + ".admin.command-bypass")){
            boolean allowedCommand = PlayerManager.getPlayer(event.getPlayer(), plugin).get().getGame().getOptions().getAllowedCommands().stream().anyMatch(i -> event.getMessage().matches(i));
            if(!allowedCommand){
                event.getPlayer().sendMessage(plugin.L("game.command-forbidden", "该命令禁止在游戏中执行。"));
                event.setCancelled(true);
            }

        }

    }
}
