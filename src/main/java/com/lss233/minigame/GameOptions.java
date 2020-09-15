package com.lss233.minigame;

import com.lss233.simplestgui.Component;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("GameOptions")
public abstract class GameOptions implements ConfigurationSerializable {
    protected Location lobbyLocation;

    protected int maxPlayers = 10, minPlayers = 3;

    protected int startCountdown = 3;

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
    }


    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("lobby-location", getLobbyLocation());
        map.put("maxPlayers", getMaxPlayers());
        map.put("minPlayers", getMinPlayers());
        return map;
    }

    public abstract boolean isAllowHungry();

    public abstract boolean isAllowEntityDamage();

    public abstract boolean isAllowPlayerBreak();

    public abstract List<String> getAllowedCommands();

    public abstract Location getFieldLocationA();
    public abstract Location getFieldLocationB();

    public abstract void setFieldLocationA(Location locationA);
    public abstract void setFieldLocationB(Location locationB);

    public abstract boolean shouldCheckY();

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public abstract boolean isAllowPlayerPlace();

    public abstract boolean isOnceLife();

    public int getStartCountdown() {
        return startCountdown;
    }

    public void setStartCountdown(int startCountdown) {
        this.startCountdown = startCountdown;
    }

    public abstract List<Component> getAdminEditMenu();
}
