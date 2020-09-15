package com.lss233.minigame.gui;

import com.lss233.minigame.Plugin;
import com.lss233.simplestgui.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import com.lss233.simplestgui.Gui;


public abstract class MenuGui extends Gui{

    @Nullable protected Gui previousPage;
    @Nullable protected Gui nextPage;
    protected final Plugin plugin;

    public MenuGui(@Nonnull Plugin plugin, @Nonnull Player player, String title, int size, @Nullable Gui previousPage, @Nullable Gui nextPage) {
        super(plugin.getJavaPlugin(), player, title, size);
        this.plugin = plugin;
        this.previousPage = previousPage;
        this.nextPage = nextPage;
    }

    @Override
    public void draw() {
        if(this.previousPage != null) {
            setComponent(0, new Component.Builder()
                    .icon(Material.WOOD_BUTTON)
                    .title(ChatColor.GOLD + "返回")
                    .lore(Collections.singletonList(
                            ChatColor.WHITE + "页面：" + previousPage.getTitle()
                    ))
                    .click(((player1, clickType) -> {
                        if(clickType.equals(ClickType.LEFT)){
                            if(previousPage instanceof MenuGui)
                                ((MenuGui) previousPage).open(this);
                            else
                                previousPage.open();
                        }
                    }))
                    .build());

        }
        if(this.nextPage != null) {
            setComponent(8, new Component.Builder()
                    .icon(Material.STONE_BUTTON)
                    .title(ChatColor.GOLD + "下一页")
                    .lore(Collections.singletonList(
                            ChatColor.WHITE + "页面：" + nextPage.getTitle()
                    ))
                    .click(((player1, clickType) -> {
                        if(clickType.equals(ClickType.LEFT)){
                            if(nextPage instanceof MenuGui)
                                ((MenuGui) nextPage).open(this);
                            else
                                nextPage.open();
                        }
                    }))
                    .build());

        }
    }

    protected void setMenuSlots(int slot, Component component){
        setComponent(getSize() - 9 + slot, component);
    }

    public void open(MenuGui previousPage) {
        this.previousPage = previousPage;
        this.open();
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
