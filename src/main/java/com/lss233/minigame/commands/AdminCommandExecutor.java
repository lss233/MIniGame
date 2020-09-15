package com.lss233.minigame.commands;

import com.lss233.minigame.Game;
import com.lss233.minigame.Plugin;
import com.lss233.minigame.gui.GameSettingsMenuGui;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AdminCommandExecutor extends GameCommandExecutor {

    public AdminCommandExecutor(Plugin plugin, String label) {
        super(plugin, label);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
             if(sender.hasPermission(this.label + ".admin")){
                if(args.length > 0){
                    if(args.length == 2){
                        if("edit".equalsIgnoreCase(args[0])){
                            return edit(player, args);
                        } else if("show".equalsIgnoreCase(args[0])){
                            return show(player, args);
                        } else if("create".equalsIgnoreCase(args[0])){
                            return create(player, args);
                        } else if("delete".equalsIgnoreCase(args[0])){
                            return delete(player, args);
                        } else if("down".equalsIgnoreCase(args[0])){
                            return down(player, args, false);
                        } else if("up".equalsIgnoreCase(args[0])){
                            return up(player, args, false);
                        }
                    } else if(args.length == 1){
                        if("reload".equalsIgnoreCase(args[0])){
                            plugin.loadConfig();
                            sender.sendMessage(plugin.L("command.admin.reload-success", "重新载入完毕。"));
                            return true;
                        } else if("save".equalsIgnoreCase(args[0])){
                            //return save(player, args);
                        }
                    } else if(args.length == 3){
                        if("down".equalsIgnoreCase(args[0])&& "confirm".equalsIgnoreCase(args[2])){
                            return down(player, args, true);
                        } else if("up".equalsIgnoreCase(args[0])&& "confirm".equalsIgnoreCase(args[2])){
                            return up(player, args, true);
                        }
                    }
                } else {
                    return help(player);
                }
             } else {
                 sender.sendMessage(plugin.L("command.admin.insufficient-permission", "你没有执行这个命令的权限。"));
                 return true;
             }
        } else {
            sender.sendMessage(plugin.L("command.ingame-only", "你只能在游戏里执行这个命令。"));
            return true;
        }
        return super.onCommand(sender, command, label, args);
    }
/*
    private boolean countdown(Player player, String[] args) {
        if(plugin.getEditingStatus().containsKey(player.getName())){
            Game game = plugin.getEditingStatus().get(player.getName());
            GameOptions options = game.getOptions();
            try{
                int value = Integer.parseInt(args[1]);
                if(value > 0)
                    options.setStartCountdown(value);
                else
                    player.sendMessage(plugin.L("command.admin.edit-value-number-too-small", "输入错误，请输入一个大一些的数字。"));
            } catch (NumberFormatException e){
                player.sendMessage(plugin.L("command.admin.edit-value-not-numbers", "输入错误，请输入一个有效的数字。"));
                return true;
            }
            player.sendMessage(plugin.L("command.admin.edit-value-updated", "修改成功。"));
        } else {
            player.sendMessage(plugin.L("command.admin.edit-only", "你没有在编辑竞技场，此命令无效。"));
        }
        return true;
    }


 */
    @Override
    protected void commandNotFound(CommandSender sender) {
        sender.sendMessage(this.plugin.L("command.admin.not-found", "命令不存在， 输入 {prefix} help 获取帮助", Collections.singletonMap("prefix", getPrefix())));
    }
/*
    private boolean save(Player player, String[] args) {
        if(plugin.getEditingStatus().containsKey(player.getName())){
            Game game = plugin.getEditingStatus().get(player.getName());
            plugin.getGameManager().save(game);
            player.sendMessage(plugin.L("command.admin.save-success", "竞技场保存成功！"));
            plugin.getEditingStatus().remove(player.getName());
        } else {
            player.sendMessage(plugin.L("command.admin.edit-only", "你没有在编辑竞技场，此命令无效。"));
        }
        return true;
    }
*/
    private boolean up(Player player, String[] args, boolean force) {
        Optional<Game> gameOptional = plugin.getGameManager().getGameByName(args[1]);
        if(gameOptional.isPresent()) {
            Game game = gameOptional.get();
            if(game.getGameStatus().equals(Game.Status.DOWN) || force){
                game.setGameStatus(Game.Status.WAITING);
                plugin.getGameManager().save(game);
                player.sendMessage(plugin.L("command.admin.game-up-success", "成功开启竞技场。"));
            } else {
                //player.sendMessage(plugin.L("command.admin.game-up-incorrect-status", "竞技场正在运行中，确定要关闭吗？ 输入 " + getPrefix() +" up {name} confirm 以确认。", Collections.singletonMap("name", args[1])));
            }
        } else {
            player.sendMessage(plugin.L("command.admin.game-not-found", "找不到该竞技场，请检查您的输入是否有误。"));
        }
        return true;
    }

    private boolean down(Player player, String[] args, boolean force) {
        Optional<Game> gameOptional = plugin.getGameManager().getGameByName(args[1]);
        if(gameOptional.isPresent()) {
            Game game = gameOptional.get();
            if(game.getGameStatus().equals(Game.Status.WAITING) || game.getGameStatus().equals(Game.Status.DOWN) || force){
                game.stop();
                game.setGameStatus(Game.Status.DOWN);
                player.sendMessage(plugin.L("command.admin.game-down-success", "成功关闭竞技场，现在你可以编辑它了。"));
            } else {
                player.sendMessage(plugin.L("command.admin.game-down-incorrect-status", "竞技场正在游戏中，确定要关闭吗？ 输入 " + getPrefix() +" down {name} confirm 以确认。", Collections.singletonMap("name", args[1])));
            }
        } else {
            player.sendMessage(plugin.L("command.admin.game-not-found", "找不到该竞技场，请检查您的输入是否有误。"));
        }
        return true;
    }

    private boolean delete(Player player, String[] args) {
        Optional<Game> gameOptional = plugin.getGameManager().getGameByName(args[1]);
        if(gameOptional.isPresent()){
            Game game = gameOptional.get();
            if(game.getGameStatus().equals(Game.Status.DOWN)){
                plugin.getGameManager().delete(game);
                player.sendMessage(plugin.L("command.admin.game-delete-success", "竞技场 {name} 场删除成功！", Collections.singletonMap("name", args[1])));
            } else {
                player.sendMessage(plugin.L("command.admin.game-delete-incorrect-status", "竞技场正在运行中，请先使用" + getPrefix() + " down {name} 关闭竞技场。", Collections.singletonMap("name", args[1])));
            }

        } else {
            player.sendMessage(plugin.L("command.admin.game-not-found", "找不到该竞技场，请检查您的输入是否有误。"));
        }
        return true;
    }

    private boolean create(Player player, String[] args) {
        Optional<Game> gameOptional = plugin.getGameManager().getGameByName(args[1]);
        if(gameOptional.isPresent()) {
            player.sendMessage(plugin.L("command.admin.game-create-already-exists", "竞技场已存在。"));
        } else {
            Game game = plugin.getGameManager().createGame(args[1]);
            game.getOptions().setLobbyLocation(player.getLocation());
            player.sendMessage(plugin.L("command.admin.game-create-success", "竞技场创建成功。"));
            edit(player, args);
        }
        return true;
    }

    private boolean show(Player player, String[] args) {
        Optional<Game> gameOptional = plugin.getGameManager().getGameByName(args[1]);
        if(gameOptional.isPresent()) {
            //player.spigot().sendMessage(TextComponent.fromLegacyText(gameOptional.get().toString()));
        } else {
            player.sendMessage(plugin.L("command.admin.game-not-found", "找不到该竞技场，请检查您的输入是否有误。"));
        }
        return true;
    }

    private boolean edit(Player player, String[] args) {
        if(plugin.getEditingStatus().containsKey(player.getName())){
            plugin.getEditingStatus().remove(player.getName());
            //player.sendMessage(plugin.L("command.admin.game-edit-already", "您当前正在编辑竞技场，请先使用 " + getPrefix() +" save 保存竞技场，再重新执行此命令。"));
        }
        Optional<Game> gameOptional = plugin.getGameManager().getGameByName(args[1]);
        if(gameOptional.isPresent()){
            Game game = gameOptional.get();
            if(game.getGameStatus().equals(Game.Status.WAITING) && game.getCurrentPlayers() == 0)
                down(player, args, false);
            if(game.getGameStatus().equals(Game.Status.DOWN)){
                /*
                ItemStack lobbySelector = new ItemStack(Material.STONE_BUTTON);
                ItemStack locationSelector = new ItemStack(Material.STICK);
                ItemMeta lobbySelectorMeta = lobbySelector.getItemMeta(), locationSe = locationSelector.getItemMeta();
                lobbySelectorMeta.setDisplayName(ChatColor.RED + "等待区传送点设置工具");
                lobbySelectorMeta.setLore(
                        Arrays.asList(
                                ChatColor.WHITE + "* 右键地面以设置传送点。",
                                ChatColor.WHITE + "当前位置： " + (game.getOptions().getLobbyLocation() == null ? "未设置" : String.format(
                                        "%d, %d, %d, %s",
                                        game.getOptions().getLobbyLocation().getBlockX(),
                                        game.getOptions().getLobbyLocation().getBlockY(),
                                        game.getOptions().getLobbyLocation().getBlockZ(),
                                        game.getOptions().getLobbyLocation().getWorld().getName()
                                    )
                                )
                        )
                );
                locationSe.setDisplayName(ChatColor.RED + "竞技场选区工具");
                lobbySelector.setItemMeta(lobbySelectorMeta);
                locationSe.setLore(
                        Arrays.asList(
                                ChatColor.WHITE + "* 左键地面以设置起点。",
                                ChatColor.WHITE + "* 右键地面以设置终点。",
                                ChatColor.WHITE + "当前起点： " + (game.getOptions().getFieldLocationA() == null ? "未设置" : String.format(
                                        "%d, %d, %d, %s",
                                        game.getOptions().getFieldLocationA().getBlockX(),
                                        game.getOptions().getFieldLocationA().getBlockY(),
                                        game.getOptions().getFieldLocationA().getBlockZ(),
                                        game.getOptions().getFieldLocationA().getWorld().getName()
                                    )
                                ),
                                ChatColor.WHITE + "当前终点： " + (game.getOptions().getFieldLocationB() == null ? "未设置" : String.format(
                                        "%d, %d, %d, %s",
                                        game.getOptions().getFieldLocationB().getBlockX(),
                                        game.getOptions().getFieldLocationB().getBlockY(),
                                        game.getOptions().getFieldLocationB().getBlockZ(),
                                        game.getOptions().getFieldLocationB().getWorld().getName()
                                    )
                                )
                        )
                );
                locationSelector.setItemMeta(locationSe);


                player.getInventory().addItem(lobbySelector, locationSelector);

                 */
                plugin.getEditingStatus().put(player.getName(), new GameSettingsMenuGui(game, player, null, null));
                plugin.getEditingStatus().get(player.getName()).open();
                player.sendMessage(plugin.L("command.admin.game-edit-enter", "正在编辑 {name}……", Collections.singletonMap("name", args[1])));
            } else {
                player.sendMessage(plugin.L("command.admin.game-edit-incorrect-status", "竞技场正在运行中，请先使用" + getPrefix() + " down {name} 关闭竞技场。", Collections.singletonMap("name", args[1])));
            }

        } else {
            player.sendMessage(plugin.L("command.admin.game-not-found", "找不到该竞技场，请检查您的输入是否有误。"));
        }
        return true;

    }

    @Override
    protected List<String> menu() {
        return Arrays.asList(
                String.format("%s%s %s %s-     %s显示帮助菜单", plugin.L("command.admin.prefix", "[管理员菜单]" + ChatColor.RED),  ChatColor.YELLOW, getPrefix(),ChatColor.GRAY, ChatColor.BLUE),
                String.format("%s%s %s reload %s-     %s重新载入所有配置文件", plugin.L("command.admin.prefix", "[管理员菜单]" + ChatColor.RED),  ChatColor.YELLOW, getPrefix(),ChatColor.GRAY, ChatColor.BLUE),
                String.format("%s%s %s edit <名称> %s-     %s编辑竞技场", plugin.L("command.admin.prefix", "[管理员菜单]" + ChatColor.RED),  ChatColor.YELLOW, getPrefix(),ChatColor.GRAY, ChatColor.BLUE),
                // String.format("%s%s %s save %s-     %s退出编辑竞技场并保存", plugin.L("command.admin.prefix", "[管理员菜单]" + ChatColor.RED),  ChatColor.YELLOW, getPrefix(),ChatColor.GRAY, ChatColor.BLUE),
                String.format("%s%s %s show [名称] %s-     %s显示竞技场状态", plugin.L("command.admin.prefix", "[管理员菜单]" + ChatColor.RED),  ChatColor.YELLOW, getPrefix(),ChatColor.GRAY, ChatColor.BLUE),
                String.format("%s%s %s create <名称> %s-     %s创建竞技场", plugin.L("command.admin.prefix", "[管理员菜单]" + ChatColor.RED),  ChatColor.YELLOW, getPrefix(),ChatColor.GRAY, ChatColor.BLUE),
                String.format("%s%s %s delete <名称> %s-     %s删除竞技场", plugin.L("command.admin.prefix", "[管理员菜单]" + ChatColor.RED),  ChatColor.YELLOW, getPrefix(),ChatColor.GRAY, ChatColor.BLUE),
                String.format("%s%s %s up <名称> %s-     %s开放竞技场", plugin.L("command.admin.prefix", "[管理员菜单]" + ChatColor.RED),  ChatColor.YELLOW, getPrefix(),ChatColor.GRAY, ChatColor.BLUE),
                String.format("%s%s %s down <名称> %s-     %s关闭竞技场(通常用于竞技场维护)", plugin.L("command.admin.prefix", "[管理员菜单]" + ChatColor.RED),  ChatColor.YELLOW, getPrefix(),ChatColor.GRAY, ChatColor.BLUE)
        );
    }
}
