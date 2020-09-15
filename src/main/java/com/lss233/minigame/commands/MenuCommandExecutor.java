package com.lss233.minigame.commands;

import com.lss233.minigame.Game;
import com.lss233.minigame.GamePlayer;
import com.lss233.minigame.PlayerManager;
import com.lss233.minigame.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MenuCommandExecutor extends GameCommandExecutor {
    public MenuCommandExecutor(Plugin plugin, String label) {
        super(plugin, label);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length > 0){
                if("help".equalsIgnoreCase(args[0])){
                    return help(player);
                } else if("start".equalsIgnoreCase(args[0])){
                    return start(player, args);
                } else if("leave".equalsIgnoreCase(args[0])){
                    return leave(player, args);
                } else if(args.length == 2) {
                    if("join".equalsIgnoreCase(args[0])){
                        return join(player, args);
                    }
                }

            }
        } else {
            sender.sendMessage(this.plugin.L("command.player-only", "该命令仅允许在服务器内执行。"));
            return true;
        }
        return super.onCommand(sender, command, label, args);
    }

    @Override
    protected void commandNotFound(CommandSender sender) {
        sender.sendMessage(this.plugin.L("command.not-found", "命令不存在， 输入 {prefix} help 获取帮助", Collections.singletonMap("prefix", getPrefix())));
    }

    @Override
    protected List<String> menu() {
        return Arrays.asList(
                String.format("%s%s help            %s-     %s显示帮助菜单", plugin.L("command.prefix", "[玩家菜单] " + ChatColor.WHITE), getPrefix(),ChatColor.GRAY, ChatColor.BLUE),
                String.format("%s%s start           %s-     %s开始当前游戏", plugin.L("command.prefix", "[玩家菜单] " + ChatColor.WHITE), getPrefix(),ChatColor.GRAY, ChatColor.BLUE),
                String.format("%s%s leave           %s-     %s离开游戏", plugin.L("command.prefix", "[玩家菜单] " + ChatColor.WHITE), getPrefix(),ChatColor.GRAY, ChatColor.BLUE),
                String.format("%s%s join <名称>      %s-     %s进入游戏", plugin.L("command.prefix", "[玩家菜单] " + ChatColor.WHITE), getPrefix(),ChatColor.GRAY, ChatColor.BLUE)
        );
    }

    private boolean join(Player player, String[] args) {
        Optional<Game> gameOptional = plugin.getGameManager().getGameByName(args[1]);
        if(gameOptional.isPresent()){
            PlayerManager.joinGame(player, this.plugin, gameOptional.get());
        } else {
            player.sendMessage(this.plugin.L("command.game-not-found", "找不到该竞技场，请检查您的输入是否有误。"));
        }
        return true;
    }

    private boolean leave(Player sender, String[] args) {
        Optional<GamePlayer> playerOptional = PlayerManager.getPlayer(sender, this.plugin);
        if(playerOptional.isPresent()){
            GamePlayer player = playerOptional.get();
            player.leave();
        } else {
            sender.sendMessage(this.plugin.L("command.ingame-only", "您只能在游戏中执行这个命令。"));
        }
        return true;
    }

    private boolean start(Player sender, String[] args) {
        Optional<GamePlayer> playerOptional = PlayerManager.getPlayer(sender, this.plugin);
        if(playerOptional.isPresent()){
            GamePlayer player = playerOptional.get();
            player.attemptToStart();
        } else {
            sender.sendMessage(this.plugin.L("command.ingame-only", "您只能在游戏中执行这个命令。"));
        }
        return true;
    }
}
