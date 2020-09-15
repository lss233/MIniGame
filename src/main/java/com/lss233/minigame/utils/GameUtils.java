package com.lss233.minigame.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.lss233.minigame.GameOptions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GameUtils {
    private static final Random random = new Random();
    public static <T> T randomChoice(List<T> item) {
        return item.get(random.nextInt(item.size()));
    }
    public static Location getRandomLocation(GameOptions options){
        Location loc = getRandomLocation(options.getFieldLocationA(), options.getFieldLocationB());
        return loc.getWorld().getHighestBlockAt(loc.getBlockX(), loc.getBlockZ()).getLocation().clone();
    }
    public static boolean isIRegion(Location loc1, Location loc2, Location check, boolean checkY){
        double minX = Math.min(loc1.getX(), loc2.getX());
        double minY = Math.min(loc1.getY(), loc2.getY());
        double minZ = Math.min(loc1.getZ(), loc2.getZ());

        double maxX = Math.max(loc1.getX(), loc2.getX());
        double maxY = Math.max(loc1.getY(), loc2.getY());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());
        if(loc1.getWorld().equals(check.getWorld())){
            if(minX <= check.getBlockX() && check.getBlockX() <= maxX){
                if(minZ <= check.getBlockZ() && check.getBlockZ() <= maxZ){
                    if(checkY){
                        if(minY <= check.getBlockY() && check.getBlockY() <= maxY){
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static Location getRandomLocation(Location loc1, Location loc2) {
        double minX = Math.min(loc1.getX(), loc2.getX());
        double minY = Math.min(loc1.getY(), loc2.getY());
        double minZ = Math.min(loc1.getZ(), loc2.getZ());

        double maxX = Math.max(loc1.getX(), loc2.getX());
        double maxY = Math.max(loc1.getY(), loc2.getY());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

        return new Location(loc1.getWorld(), randomDouble(minX, maxX), randomDouble(minY, maxY), randomDouble(minZ, maxZ));
    }
    public static double randomDouble(double min, double max) {
        return min + ThreadLocalRandom.current().nextDouble(Math.abs(max - min + 1));
    }
    public static Location getFieldsCenter(GameOptions options) {
        Location loc1 = options.getFieldLocationA();
        Location loc2 = options.getFieldLocationB();
        double minX = Math.min(loc1.getX(), loc2.getX());
        double minY = Math.min(loc1.getY(), loc2.getY());
        double minZ = Math.min(loc1.getZ(), loc2.getZ());

        double maxX = Math.max(loc1.getX(), loc2.getX());
        double maxY = Math.max(loc1.getY(), loc2.getY());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());
        return new Location(loc1.getWorld(), (maxX + minX) / 2, (maxY + minY) / 2 , (maxZ + minZ) / 2 );
    }

    public static String toString(Location location) {
        if(location == null) return "暂无";
        return String.format("世界： %s,  X：%d，  Y：%d，  Z：%d", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static void sendActionBar(Player player, String msg) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer chatPacket = protocolManager.createPacket(PacketType.Play.Server.CHAT);
        chatPacket.getChatComponents().write(0, WrappedChatComponent.fromText(msg.replace('&', ChatColor.COLOR_CHAR)));
        chatPacket.getBytes().write(0, (byte) 2);
        try {
            protocolManager.sendServerPacket(player, chatPacket);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
