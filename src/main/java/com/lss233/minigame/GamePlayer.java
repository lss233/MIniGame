package com.lss233.minigame;

import com.lss233.minigame.utils.GameUtils;
import com.lss233.minigame.utils.Messageable;
import dev.wwst.scoreboard.ScoreboardSign;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamePlayer implements Messageable {
    public Location locationBeforeJoin;
    private final Game game;
    private final Player player;
    private final Plugin plugin;
    private final Map<String, Object> playerStats = new HashMap<>();
    private ScoreboardSign scoreboard;

    public GamePlayer(Game game, Player player, Plugin plugin) {
        this.game = game;
        this.player = player;
        this.plugin = plugin;
        this.locationBeforeJoin = player.getLocation();
    }

    /**
     * Leave the current game.
     */
    public void leave() {
        game.kick(this, Game.QuitReason.QUIT);
        sendMessage("game.leave-successful", "您已离开游戏。");
    }

    /**
     * Attempt to start current game.
     */
    public void attemptToStart() {
        if (game.getMinPlayers() > game.getCurrentPlayers()) {
            sendMessage("game.start-players-insufficient", "至少需要{min_players}名玩家才能开始游戏，当前玩家：{current_players}。",
                    new HashMap<String, Object>() {{
                        put("min_players", String.valueOf(game.getMinPlayers()));
                        put("current_players", String.valueOf(game.getCurrentPlayers()));
                    }});
        } else if (game.getGameStatus().equals(Game.Status.WAITING)) {
            game.start();
        } else {
            sendMessage("game.start-already", "游戏已经开始了。");
        }
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the current player's UUID.
     * @return The player's UUID.
     */
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    /**
     * Gets the game the player are currently in.
     * @return The game.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Gets the player's stats in this game.
     * @return The player's stats.
     */
    public Map<String, Object> getPlayerStats() {
        return playerStats;
    }

    /**
     * Sends a ActionBar message.
     * @param key The key on the {@code lang.yml} file.
     * @param def The default message if the {@param key} is not exists.
     * @param variables The variables to be parsed from the message.
     */
    public void sendActionbar(String key, String def, Map<String, Object> variables) {
        GameUtils.sendActionBar(getPlayer(), plugin.L(key, def, variables));
    }

    /**
     * Update a player's stats in this game.
     * <br/>
     * If the stat is exists, it will be add by {@param value}.
     * <br/>
     * If not, the stat will be {@param def} + {@param value}.
     * <br/>
     * You can give the {@code value} a negative value
     * if you want to do subtraction.
     * @param key The key.
     * @param value The value to be add.
     * @param def The default value if the stat is not exists.
     */
    public void updateStats(String key, int value, int def) {
        int currentValue = (int) getPlayerStats().getOrDefault(key, def);
        currentValue += value;
        getPlayerStats().put(key, currentValue);
        updateScoreboard();
    }

    /**
     * Update current statistics to the Scoreboard.
     */
    public void updateScoreboard() {
        if (scoreboard == null)
            scoreboard = new ScoreboardSign(getPlayer(), getPlugin().L("game.scoreboard.title", "统计信息", getPlayerStats()));
        else
            scoreboard.setObjectiveName(getPlugin().L("game.scoreboard.title", "统计信息", getPlayerStats()));
        scoreboard.create();
        scoreboard.setLines(getPlugin().Ls("game.scoreboard.lines", getPlayerStats()));
    }

    /**
     * Remove the Scoreboard from player's interface.
     */
    public void closeScoreboard() {
        if (scoreboard == null) return;
        scoreboard.destroy();
        scoreboard = null;
    }

    @Override
    public int hashCode() {
        return player.hashCode() * 1000 + plugin.hashCode() * 100;
    }
}
