package com.lss233.minigame.gui;

import com.lss233.minigame.Game;
import com.lss233.minigame.utils.GameUtils;
import com.lss233.simplestgui.Component;
import com.lss233.simplestgui.Gui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;


import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;

public class GameSettingsMenuGui extends MenuGui {
    private final Game game;

    public GameSettingsMenuGui(Game game, Player editor, @Nullable Gui previousPage, @Nullable Gui nextPage) {
       super(game.getPlugin(), editor, ChatColor.GOLD + "编辑竞技场：" + ChatColor.BLUE + game.getName(), 54, previousPage, nextPage);
       this.game = game;
    }

    @Override
    protected void onClosed() {
        if(getPlayer().isConversing()){
            return;
        }
        plugin.getGameManager().save(game);
        plugin.getEditingStatus().remove(player.getName());
    }

    @Override
    public void draw() {
        super.draw();
        int pos = 0;
        setComponent(pos++, new Component.Builder()
                .icon(Material.SKULL_ITEM)
                .title(ChatColor.AQUA + "最大玩家数")
                .lore(Collections.singletonList(
                        ChatColor.WHITE + "当前数值： " + ChatColor.RED + game.getOptions().getMaxPlayers()
                ))
                .click((player, type) -> {
                    player.closeInventory();
                    Conversation conversation = new Conversation(getPlugin().getJavaPlugin(), player, new StringPrompt() {
                        @Override
                        public String getPromptText(ConversationContext context) {
                            return ChatColor.RED + "请输入一个数值，或者回复" + ChatColor.GREEN + " cancel " + ChatColor.RED + "取消：";
                        }

                        @Override
                        public Prompt acceptInput(ConversationContext context, String input) {
                            if(input.equals("cancel"))
                                return Prompt.END_OF_CONVERSATION;
                            try {
                                int value = Integer.parseInt(input);
                                game.getOptions().setMaxPlayers(value);
                                open();
                                return Prompt.END_OF_CONVERSATION;
                            } catch (Exception e) {
                                context.getForWhom().sendRawMessage("输入有误。");
                                return this;
                            }
                        }
                    });
                    player.beginConversation(conversation);
                })
                .build()
        );
        setComponent(pos++, new Component.Builder()
                .icon(Material.IRON_SWORD)
                .title(ChatColor.AQUA + "最小玩家数")
                .lore(Arrays.asList(
                        ChatColor.WHITE + "当前数值： " + ChatColor.RED + game.getOptions().getMinPlayers(),
                        ChatColor.WHITE + "当房间人数达到最小玩家数时，游戏将自动开始。"
                ))
                .click((player, type) -> {
                    player.closeInventory();
                    Conversation conversation = new Conversation(getPlugin().getJavaPlugin(), player, new StringPrompt() {
                        @Override
                        public String getPromptText(ConversationContext context) {
                            return "请输入一个数值，或者回复 cancel 取消：";
                        }

                        @Override
                        public Prompt acceptInput(ConversationContext context, String input) {
                            if(input.equals("cancel"))
                                return Prompt.END_OF_CONVERSATION;
                            try {
                                int value = Integer.parseInt(input);
                                if(value > game.getOptions().getMaxPlayers()) {
                                    context.getForWhom().sendRawMessage("输入有误：最小玩家数不能大于最大玩家数。");
                                    return this;
                                }
                                game.getOptions().setMinPlayers(value);
                                open();
                                return Prompt.END_OF_CONVERSATION;
                            } catch (Exception e) {
                                context.getForWhom().sendRawMessage("输入有误，请重新输入，或者回复 cancel 取消。");
                                return this;
                            }
                        }
                    });
                    player.beginConversation(conversation);
                })
                .build()
        );
        setComponent(pos++, new Component.Builder()
                .icon(Material.EXP_BOTTLE)
                .title(ChatColor.AQUA + "启动游戏倒计时")
                .lore(Arrays.asList(
                        ChatColor.WHITE + "当前数值：" + ChatColor.RED + game.getOptions().getStartCountdown(),
                        ChatColor.WHITE + "游戏启动前倒计时的时长。"
                ))
                .click((player, type) -> {
                    player.closeInventory();
                    Conversation conversation = new Conversation(getPlugin().getJavaPlugin(), player, new StringPrompt() {
                        @Override
                        public String getPromptText(ConversationContext context) {
                            return "请输入一个数值，或者回复 cancel 取消：";
                        }

                        @Override
                        public Prompt acceptInput(ConversationContext context, String input) {
                            if(input.equals("cancel"))
                                return Prompt.END_OF_CONVERSATION;
                            try {
                                int value = Integer.parseInt(input);
                                game.getOptions().setStartCountdown(value);
                                open();
                                return Prompt.END_OF_CONVERSATION;
                            } catch (Exception e) {
                                context.getForWhom().sendRawMessage("输入有误。");
                                return this;
                            }
                        }
                    });
                    player.beginConversation(conversation);
                })
                .build()
        );
        setComponent(pos++, new Component.Builder()
                .icon(Material.SNOW_BALL)
                .title(ChatColor.AQUA + "等待大厅传送点")
                .lore(Arrays.asList(
                        ChatColor.WHITE + "当前位置：",
                        ChatColor.RED + GameUtils.toString(game.getOptions().getLobbyLocation()),
                        ChatColor.WHITE + "进入游戏后，玩家将会在这个位置等待其他玩家。"
                ))
                .click((player, type) -> {
                    player.closeInventory();
                    Conversation conversation = new Conversation(getPlugin().getJavaPlugin(), player, new StringPrompt() {
                        @Override
                        public String getPromptText(ConversationContext context) {
                            return ChatColor.RED + "请在你想设置的位置回复" + ChatColor.GOLD + "就这"  + ChatColor.RED + "，或者回复 " + ChatColor.GOLD + "cancel"  + ChatColor.RED + " 取消：";
                        }

                        @Override
                        public Prompt acceptInput(ConversationContext context, String input) {
                            if (context.getForWhom() instanceof Player) {
                                if (input.equals("cancel"))
                                    return Prompt.END_OF_CONVERSATION;
                                else if (input.equals("就这")) {
                                    game.getOptions().setLobbyLocation(player.getLocation());
                                    open();
                                    return Prompt.END_OF_CONVERSATION;
                                }
                                return this;
                            } else {
                                context.getForWhom().sendRawMessage(ChatColor.RED + "本操作仅允许在游戏内执行。");
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }
                    });
                    player.beginConversation(conversation);
                })
                .build()
        );
        setComponent(pos++, new Component.Builder()
                .icon(Material.COMPASS)
                .title(ChatColor.AQUA + "竞技场起点")
                .lore(Arrays.asList(
                        ChatColor.WHITE + "当前位置：",
                        ChatColor.RED + GameUtils.toString(game.getOptions().getFieldLocationA())
                ))
                .click((player, type) -> {
                    player.closeInventory();
                    Conversation conversation = new Conversation(getPlugin().getJavaPlugin(), player, new StringPrompt() {
                        @Override
                        public String getPromptText(ConversationContext context) {
                            return ChatColor.RED + "请在你想设置的位置回复" + ChatColor.GOLD + "就这"  + ChatColor.RED + "，或者回复 " + ChatColor.GOLD + "cancel"  + ChatColor.RED + " 取消：";
                        }

                        @Override
                        public Prompt acceptInput(ConversationContext context, String input) {
                            if (context.getForWhom() instanceof Player) {
                                if (input.equals("cancel"))
                                    return Prompt.END_OF_CONVERSATION;
                                else if (input.equals("就这")) {
                                    game.getOptions().setFieldLocationA(player.getLocation());
                                    open();
                                    return Prompt.END_OF_CONVERSATION;
                                }
                                return this;
                            } else {
                                context.getForWhom().sendRawMessage(ChatColor.RED + "本操作仅允许在游戏内执行。");
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }
                    });
                    player.beginConversation(conversation);
                })
                .build()
        );
        setComponent(pos++, new Component.Builder()
                .icon(Material.COMPASS)
                .title(ChatColor.AQUA + "竞技场终点")
                .lore(Arrays.asList(
                        ChatColor.WHITE + "当前位置：",
                        ChatColor.RED + GameUtils.toString(game.getOptions().getFieldLocationB())
                ))
                .click((player, type) -> {
                    player.closeInventory();
                    Conversation conversation = new Conversation(getPlugin().getJavaPlugin(), player, new StringPrompt() {
                        @Override
                        public String getPromptText(ConversationContext context) {
                            return ChatColor.RED + "请在你想设置的位置回复" + ChatColor.GOLD + "就这"  + ChatColor.RED + "，或者回复 " + ChatColor.GOLD + "cancel"  + ChatColor.RED + " 取消：";
                        }

                        @Override
                        public Prompt acceptInput(ConversationContext context, String input) {
                            if (context.getForWhom() instanceof Player) {
                                if (input.equals("cancel"))
                                    return Prompt.END_OF_CONVERSATION;
                                else if (input.equals("就这")) {
                                    game.getOptions().setFieldLocationB(player.getLocation());
                                    open();
                                    return Prompt.END_OF_CONVERSATION;
                                }
                                return this;
                            } else {
                                context.getForWhom().sendRawMessage(ChatColor.RED + "本操作仅允许在游戏内执行。");
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }
                    });
                    player.beginConversation(conversation);
                })
                .build()
        );
        for (Component component : game.getOptions().getAdminEditMenu()) {
            setComponent(pos++, component);
        }
    }
}
