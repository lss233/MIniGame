package com.lss233.minigame;

import com.lss233.minigame.events.MiniGameStartEvent;
import com.lss233.minigame.events.MiniGameStopEvent;
import com.lss233.minigame.events.player.MiniGamePlayerDataLoadingEvent;
import com.lss233.minigame.events.player.MiniGamePlayerJoinGameEvent;
import com.lss233.minigame.events.player.MiniGamePlayerQuitEvent;
import com.lss233.minigame.utils.CountdownAction;
import com.lss233.minigame.utils.GameTaskRunnable;
import com.lss233.minigame.utils.GameUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A arena instance. <br/>
 * An arena has five status, see {@link Game.Status}.
 */
public class Game {
    private final Plugin plugin;
    private final String name;
    private final Set<GamePlayer> players = new HashSet<>();
    private final Map<GameTaskRunnable, Integer>  ingameTasks = new HashMap<>();
    private final List<CountdownAction> countdownActions = new ArrayList<>();
    private Status gameStatus = Status.DOWN;
    private final GameOptions options;

    public Game(Plugin plugin, String name, GameOptions options, Status status) {
        this.plugin = plugin;
        this.name = name;
        this.options = options;
        this.gameStatus = status;
    }


    /**
     * Gets the maximum player amount this arena could hold.
     * @return The maximum players amount.
     */
    public int getMaxPlayers(){
        return options.getMaxPlayers();
    }

    /**
     * Gets the current player amount.
     * @return The current players amount.
     */
    public int getCurrentPlayers(){
        return players.size();
    }

    /**
     * Get the scheduled countdown actions.
     * @return The scheduled countdown actions.
     */
    public List<CountdownAction> getCountdownActions() {
        return countdownActions;
    }

    /**
     * Start the game.
     */
    public void start() {
        if(!gameStatus.equals(Status.WAITING))
            return;
        Game game = this;
        countdownActions.clear();
        setGameStatus(Status.STARTING);
        AtomicInteger seconds = new AtomicInteger(getOptions().getStartCountdown());
        new CountdownAction(this, seconds.get(), () -> {
            int second = seconds.getAndDecrement();
            broadcast("game.starting-countdown", "游戏还有{second}秒开始，请做好准备。", Collections.singletonMap("second", String.valueOf(second)));
        }){
            @Override
            public void run() {
                ingameTasks.clear();
                getPlayers().forEach(i -> i.getPlayerStats().clear());
                getPlugin().getJavaPlugin().getServer().getPluginManager().callEvent(new MiniGameStartEvent(game));
                ingameTasks.replaceAll((task, id) -> id = getPlugin().getJavaPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(getPlugin().getJavaPlugin(), task, task.getDelay(), task.getPeriod()));
                updateGameStats("total_players", getCurrentPlayers(), 0);
                setGameStatus(Status.GAMING);
            }
        };

    }

    /**
     * Stop the game, kick all the players, and shutdown all the tasks.
     */
    public void stop(){
        if(gameStatus.equals(Status.STOPPING) || gameStatus.equals(Status.WAITING) || gameStatus.equals(Status.DOWN))   return;
        setGameStatus(Status.STOPPING);
        getPlugin()
                .getJavaPlugin()
                .getServer()
                .getPluginManager()
                .callEvent(new MiniGameStopEvent.Pre(this));
        ingameTasks
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 0)
                .forEach(entry -> getPlugin().getJavaPlugin().getServer().getScheduler().cancelTask(entry.getValue()));
        players.forEach(i -> kick(i, QuitReason.QUIT, false));
        players.clear();
        getPlugin()
                .getJavaPlugin()
                .getServer()
                .getPluginManager()
                .callEvent(new MiniGameStopEvent.Post(this));
        setGameStatus(Status.WAITING);
    }

    /**
     * Broadcast a ActionBar message.
     * @param key The key on the {@code lang.yml} file.
     * @param def The default message if the {@param key} is not exists.
     * @param variables The variables to be parsed from the message.
     */
    public void actionbar(String key, String def, Map<String, Object> variables){
        players.forEach(i -> i.sendActionbar(key, def, variables));
    }

    /**
     * Broadcast a chat message.
     * @param key The key on the {@code lang.yml} file.
     * @param def The default message if the {@param key} is not exists.
     * @param variables The variables to be parsed from the message.
     */
    public void broadcast(String key, String def, Map<String, Object> variables){
        players.forEach(player -> player.sendMessage(key,def, variables));
    }

    /**
     * Broadcast a chat message.
     * @param key The key on the {@code lang.yml} file.
     * @param def The default message if the {@param key} is not exists.
     */
    public void broadcast(String key, String def){
        broadcast(key, def, null);
    }

    /**
     * Gets the current game status.
     * @return The game status.
     */
    public Status getGameStatus(){
        return gameStatus;
    }

    /**
     * Gets the minimum player amount to start this game.
     * @return The minimum player amount.
     */
    public int getMinPlayers() {
        return options.getMinPlayers();
    }

    /**
     * Update all player's stats.
     * See {@link GamePlayer#updateStats(String, int, int)} for usage.
     * @param key The key.
     * @param value The value to be add.
     * @param def The default value if the stat is not exists.
     */
    public void updateGameStats(String key, int value, int def){
        players.forEach(i -> {
            i.updateStats(key, value, def);
        });
    }

    /**
     * Register a repeating task.</br>
     * This method should be called on {@link MiniGameStartEvent}.<br/>
     * The task will be started once the game is started, <br/>
     * and cancelled once the game is {@link Game#stop()}.
     * @param runnable The task.
     */
    public void registerGameRepeatingTask(GameTaskRunnable runnable){
        this.ingameTasks.put(runnable, -1);
    }

    /**
     * Lets a player join this game. <br/>
     * This method will check the {@link Game.Status}and {@link this#getMaxPlayers()}.
     * @param player The player to be joined in.
     * @return The GamePlayer if the player is successfully joined in, or null if failed.
     */
    public GamePlayer join(Player player) {
        GamePlayer gamePlayer = new GamePlayer(this, player, getPlugin());
        if(getCurrentPlayers() <= 1)
            stop();
        if(getGameStatus().equals(Status.DOWN)) {
            gamePlayer.sendMessage("game.join-down", "该竞技场已关闭，无法加入。");
        } else if(getCurrentPlayers() >= getMaxPlayers()){
            gamePlayer.sendMessage("game.join-full", "该游戏已满人，无法加入。");
        } else if(!getGameStatus().equals(Status.WAITING)){
            gamePlayer.sendMessage("game.join-status", "该游戏已经开始，无法加入。");
        } else {
            gamePlayer.locationBeforeJoin = player.getLocation();
            gamePlayer.sendMessage("game.join-successful", "您已成功加入游戏。");

            if(!players.contains(gamePlayer)) {
                MiniGamePlayerJoinGameEvent event = new MiniGamePlayerJoinGameEvent(this, gamePlayer);
                plugin.getJavaPlugin().getServer().getPluginManager().callEvent(event);
                if(!event.isCancelled()) {
                    players.add(gamePlayer);
                    player.getPlayer().teleport(options.getLobbyLocation());
                    return gamePlayer;
                }
            }
        }
        return null;
    }

    /**
     * Kicks a player by because {@param reason.}
     * @param gamePlayer The player to be kicked.
     * @param reason The reason why to be kicked.
     */
    public void kick(GamePlayer gamePlayer, QuitReason reason) {
        kick(gamePlayer, reason, true);
    }

    /**
     * Kicks a player by because {@param reason.}
     * @param gamePlayer The player to be kicked.
     * @param reason The reason why to be kicked.
     * @param remove whether to remove this player from the player list. true if you don't know what you are doing.
     */
    public void kick(GamePlayer gamePlayer, QuitReason reason, boolean remove) {
        MiniGamePlayerQuitEvent.Pre event = new MiniGamePlayerQuitEvent.Pre(this, gamePlayer, reason);
        getPlugin().getJavaPlugin().getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            gamePlayer.getPlayer().teleport(gamePlayer.locationBeforeJoin);
            PlayerManager.removePlayer(gamePlayer, getPlugin());
            gamePlayer.closeScoreboard();
            if(remove)
                players.remove(gamePlayer);
            getPlugin().getJavaPlugin().getServer().getPluginManager().callEvent(new MiniGamePlayerQuitEvent.Post(this, gamePlayer, event.getReason()));
        }

        if(getCurrentPlayers() <= 1) {
            stop();
        }
    }

    /**
     * Check if a location is inside the arena field.
     * @param location The location to be check.
     * @return True if it is.
     */
    public boolean isInField(Location location){
        return GameUtils.isIRegion(getOptions().getFieldLocationA(), getOptions().getFieldLocationB(), location, getOptions().shouldCheckY());
    }

    /**
     * Gets the players.
     * @return The players.
     */
    public Set<GamePlayer> getPlayers() {
        return players;
    }

    public GameOptions getOptions() {
        return options;
    }

    public String getName() {
        return name;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public boolean hasPlayer(GamePlayer player){
        return this.players.contains(player);
    }

    public void setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
    }

    /**
     * Directly shutdown the game.<br/>
     * This will restore all player's data.
     * Should be called on your plugin's {@link JavaPlugin#onDisable()} method.
     */
    public void forceShutdown() {
        Map<RegisteredListener, List<Method>> dataLoadingMethods = new HashMap<>();
        for (RegisteredListener registeredListener : MiniGamePlayerDataLoadingEvent.getHandlerList().getRegisteredListeners()) {
            List<Method> methods = new ArrayList<>();

            for (Method declaredMethod : registeredListener.getListener().getClass().getDeclaredMethods()) {
                if(declaredMethod.getParameterCount() == 1){
                    if(declaredMethod.getParameterTypes()[0].equals(MiniGamePlayerDataLoadingEvent.class)){
                        methods.add(declaredMethod);
                    }
                }
            }
            dataLoadingMethods.put(registeredListener, methods);
        }

        players.forEach(i -> {
            i.getPlayer().teleport(i.locationBeforeJoin);
            MiniGamePlayerDataLoadingEvent dataLoadingEvent = new MiniGamePlayerDataLoadingEvent(this, i);
            dataLoadingMethods.entrySet().forEach(entry -> {
                entry.getValue().forEach(method -> {
                    try {
                        method.invoke(entry.getKey().getListener(), dataLoadingEvent);
                    } catch (Exception ignored) {}
                });
            });
            kick(i, QuitReason.QUIT, false);
        });
        stop();
    }

    /**
     * The full lifecycle of a game should be:</br>
     * DOWN -> WAITING -> STARTING -> GAMING -> STOPPING -> WAITING.
     */
    public enum Status {
        /**
         * The game is in waiting, all the players should be at {@link GameOptions#getLobbyLocation()}.
         */
        WAITING,
        /**
         * The game is starting. All the players receiving a countdown.<br/>
         * Once the countdown is over, a {@link MiniGameStartEvent} is being called.<br/>
         * All of the registered {@link this#registerGameRepeatingTask(GameTaskRunnable)} will be
         * started.
         */
        STARTING,
        /**
         * The players are gaming.
         */
        GAMING,
        /**
         * The game is stopping. <br/>
         * All of the players will be kicked out.
         */
        STOPPING,
        /**
         * The game is under maintenance.
         * Nobody could join in the game.
         */
        DOWN;
    }

    public enum QuitReason {
        QUIT, DIED, BANNED, OVER
    }
}
