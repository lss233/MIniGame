package com.lss233.minigame.commands;

import com.lss233.minigame.validators.ValidationErrorException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public interface AdvancedCommandExecutor {
    final Map<String, Method> methodMap = new HashMap<>();


    default boolean handleCommand(CommandSender sender, Command command, String label, String[] args) {
        AdvancedCommandExecutor instance = this;
        if(methodMap.entrySet().stream().noneMatch(entry -> {
            String key = entry.getKey();
            String[] params = key.split(" ");
            if(params.length == args.length){
                Map<String, String> kvMap = new HashMap<>();
                for (int i = 0; i < params.length; i++) {
                    if(params[i].startsWith("<") && params[i].endsWith(">")){
                        kvMap.put(params[i], args[i]);
                    } else {
                        if(!params[i].equalsIgnoreCase(args[i]))
                            return false;
                    }
                }
                try {
                    entry.getValue().invoke(instance, sender, kvMap);
                    return true;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    return false;
                } catch (ValidationErrorException e) {
                    sender.sendMessage(e.getMessage());
                    return true;
                }
            } else
                return false;
        })){
            return false;
        };
        return true;
    }
    default void registerSubCommand(String name, String methodName) throws NoSuchMethodException {
        methodMap.put(name, getClass().getDeclaredMethod(methodName, CommandSender.class, Map.class));
    }
}
