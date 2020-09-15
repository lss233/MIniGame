package com.lss233.minigame;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GameManager {
    private final Plugin plugin;
    private final File configFileDir;
    private Map<String, Game> gameMap;
    public GameManager(Plugin plugin) {
        this.plugin = plugin;
        this.configFileDir  = new File(this.plugin.getJavaPlugin().getDataFolder(), "games");
    }
    public void loadConfig(){
        gameMap = new HashMap<>();

        if(!configFileDir.exists()){
            configFileDir.mkdirs();
        }
        Arrays.stream(Objects.requireNonNull(configFileDir.listFiles((dir, name) -> name.endsWith(".yml")))).forEach(file -> {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String name = config.getString("name");
            GameOptions options = (GameOptions) config.get("options");
            Game.Status status = Game.Status.valueOf(config.getString("status"));

            Game game = new Game(plugin, name, options, status);
            gameMap.put(name, game);

        });

        plugin.getLogger().info(this.plugin.L("system.games-loaded", "竞技场已加载完毕。"));
    }

    public Optional<Game> getGameByName(String name){
        return Optional.ofNullable(gameMap.get(name));
    }
    public Game createGame(String name){
        Game game = null;
        try {
            game = new Game(plugin, name, plugin.getOptionsClass().newInstance(), Game.Status.DOWN);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        gameMap.put(name, game);
        return game;
    }

    public void delete(Game game) {
        File configFile = new File(configFileDir, game.getName() + ".yml");
        if(configFile.exists()){
            configFile.delete();
        }
        gameMap.remove(game.getName());
    }

    public void save(Game game){
        File configFile = new File(configFileDir, game.getName() + ".yml");
        if(configFile.exists()){
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration config = new YamlConfiguration();
        config.set("name", game.getName());
        config.set("options", game.getOptions());
        config.set("status", game.getGameStatus().name());
        gameMap.replace(game.getName(), game);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        gameMap.values().forEach(Game::stop);
        gameMap.values().forEach(this::save);
    }

    public void forceShutdown() {
        gameMap.values().forEach(Game::forceShutdown);
    }
}
