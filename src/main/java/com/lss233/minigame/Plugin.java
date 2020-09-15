package com.lss233.minigame;

import com.lss233.minigame.commands.AdminCommandExecutor;
import com.lss233.minigame.commands.MenuCommandExecutor;
import com.lss233.minigame.gui.GameSettingsMenuGui;
import com.lss233.minigame.lang.Lang;
import com.lss233.minigame.listeners.GameEventsListener;
import com.lss233.minigame.listeners.PlayerBehaviorCancelListener;
import com.lss233.minigame.listeners.PlayerTemporaryStorageListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The plugin instance.
 */
public class Plugin {
    private final JavaPlugin plugin;
    private final Logger logger;
    private final Class<? extends GameOptions> optionsClass;
    private Map<String, GameSettingsMenuGui> editingStatus = new HashMap<>();
    private Lang lang;
    private GameManager gameManager;
    private StatisticManager statisticManager;

    Plugin(JavaPlugin plugin, Class<? extends GameOptions> optionsClass){
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.lang = LangManager.load(this);
        this.optionsClass = optionsClass;
        this.gameManager = new GameManager(this);
        this.statisticManager = new StatisticManager(this);
        initialize();
    }

    private void initialize() {
        loadConfig();
    }

    /**
     * Loading configuration files.
     */
    public void loadConfig(){
        if(!this.plugin.getDataFolder().exists())
            this.plugin.getDataFolder().mkdirs();
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();
        this.lang.loadConfig();
        this.gameManager.loadConfig();
        logger.info("配置文件加载完毕。");

    }

    /**
     * Gets the {@code JavaPlugin} related to this plugin.
     * @return The JavaPlugin
     */
    public JavaPlugin getJavaPlugin() {
        return plugin;
    }

    /**
     * Gets the game manager of your plugin.
     * @return The game manager.
     */
    public GameManager getGameManager() {
        return gameManager;
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * Read a message from {@code lang.yml}
     * @param key The key
     * @param def The default message if the key is not exists.
     * @return The message or the {@param def} if not exists.
     */
    public String L(String key, String def){
        return this.lang.L(key, def);
    }

    /**
     * Read a message from {@code lang.yml}
     * and parse all the variables from {@param variables}.
     * @param key The key
     * @param def The default message if the key is not exists.
     * @param variables The variables.
     * @return The message or the {@code def} if not exists.
     */
    public String L(String key, String def, Map<String, Object> variables){
        return this.lang.L(key, def, variables);
    }

    /**
     * Read a list of message from {@code lang.yml}
     * and parse all the variables from {@param variables}.
     * @param key The key
     * @param variables The variables.
     * @return The message or an empty list of not exists.
     */
    public Collection<String> Ls(String key, Map<String, Object> variables){
        return this.lang.Ls(key, variables);
    }

    public Class<? extends GameOptions> getOptionsClass() {
        return optionsClass;
    }

    /**
     * Get the players and GUI who are currently
     * editing an arena.
     * @return A map containing the players and GUI who are currently editing an arena.
     */
    public Map<String, GameSettingsMenuGui> getEditingStatus() {
        return editingStatus;
    }

    /**
     * Enable this plugin.
     * Register all the commands and event listeners.
     * <br/>
     * This method should be called on your plugin's
     * last line of {@code onEnable()} method.
     *
     */
    public void enable() {
        new MenuCommandExecutor(this, this.plugin.getName());
        new AdminCommandExecutor(this, this.plugin.getName()+"admin");

        getJavaPlugin().getServer().getPluginManager().registerEvents(new PlayerBehaviorCancelListener(this), getJavaPlugin());
        getJavaPlugin().getServer().getPluginManager().registerEvents(new PlayerTemporaryStorageListener(this), getJavaPlugin());
        getJavaPlugin().getServer().getPluginManager().registerEvents(new GameEventsListener(this), getJavaPlugin());
    }

    /**
     * Gets the statistic manager of your game.
     * @return The statistic manager.
     */
    public StatisticManager getStatisticManager() {
        return statisticManager;
    }
}
