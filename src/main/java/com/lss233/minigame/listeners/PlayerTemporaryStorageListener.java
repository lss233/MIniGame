package com.lss233.minigame.listeners;

import com.lss233.minigame.Plugin;
import com.lss233.minigame.events.player.MiniGamePlayerDataLoadingEvent;
import com.lss233.minigame.events.player.MiniGamePlayerDataSavingEvent;
import com.lss233.minigame.events.player.MiniGamePlayerJoinGameEvent;
import com.lss233.minigame.events.player.MiniGamePlayerQuitEvent;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerTemporaryStorageListener implements Listener {
    private final Plugin plugin;
    private final File dataDir;
    public PlayerTemporaryStorageListener(Plugin plugin) {
        this.plugin = plugin;
        this.dataDir = new File(plugin.getJavaPlugin().getDataFolder(), "playerdata");
    }
    @EventHandler
    public void onLoadIssuer(MiniGamePlayerQuitEvent.Pre event){
        MiniGamePlayerDataLoadingEvent loadingEvent = new MiniGamePlayerDataLoadingEvent(event.getGame(), event.getPlayer());
        plugin.getJavaPlugin().getServer().getPluginManager().callEvent(loadingEvent);
        event.setCancelled(event.isCancelled());
    }
    @EventHandler
    public void onSaveIssuer(MiniGamePlayerJoinGameEvent event){
        MiniGamePlayerDataSavingEvent savingEvent = new MiniGamePlayerDataSavingEvent(event.getGame(), event.getPlayer());
        plugin.getJavaPlugin().getServer().getPluginManager().callEvent(savingEvent);
        event.setCancelled(event.isCancelled());
    }
    @EventHandler
    public void onLoad(MiniGamePlayerDataLoadingEvent event){
        Player player = event.getPlayer().getPlayer();
        try {
            File dataFile = new File(dataDir, player.getUniqueId().toString() + ".yml");
            YamlConfiguration playerData = YamlConfiguration.loadConfiguration(dataFile);
            player.getInventory().clear();
            playerData.getList("inventory").forEach(item -> {
                if(item == null) return;
                player.getInventory().addItem((ItemStack) item);
            });
            player.setTotalExperience(playerData.getInt("total-experience"));
            player.setFoodLevel(playerData.getInt("food-level", 20));
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(playerData.getDouble("maxHp", 20));
            player.setHealth(playerData.getDouble("health", 20));
            player.setLevel(playerData.getInt("level"));
            player.setGameMode(GameMode.valueOf(playerData.getString("gamemode", GameMode.SURVIVAL.name())));
            dataFile.delete();

        } catch (Exception e){
            player.sendMessage("无法读取你的用户数据，操作被取消。请联系管理员查看后台日志。");
            e.printStackTrace();
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onSave(MiniGamePlayerDataSavingEvent event){
        Player player = event.getPlayer().getPlayer();
        try {
            if(!dataDir.exists())
                dataDir.mkdirs();
            File dataFile = new File(dataDir, player.getUniqueId().toString() + ".yml");;

            YamlConfiguration playerData = new YamlConfiguration();

            // Save inventory
            List<String> items = new ArrayList<>();
            /*
            player.getInventory().forEach(i -> {
                NBTTagCompound nbt = new NBTTagCompound();
                CraftItemStack.asNMSCopy(i).save((net.minecraft.server.v1_12_R1.NBTTagCompound) (Object)nbt);
                items.add(nbt.toString());
            });
             */
            playerData.set("inventory", player.getInventory().getContents());

            // HP bar
            playerData.set("total-experience", player.getTotalExperience());
            playerData.set("level", player.getLevel());
            playerData.set("food-level", player.getFoodLevel());
            playerData.set("health", player.getHealth());
            playerData.set("gamemode", player.getGameMode().toString());
            playerData.set("maxHp", player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());


            playerData.save(dataFile);
            player.getInventory().clear();
            player.setTotalExperience(0);
            player.setLevel(0);
            player.setGameMode(GameMode.ADVENTURE);
            player.setFoodLevel(20);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        } catch (IOException e) {
            player.sendMessage("无法保存你的用户数据，操作被取消。请联系管理员查看后台日志。");
            e.printStackTrace();
            event.setCancelled(true);
        }
    }
}
