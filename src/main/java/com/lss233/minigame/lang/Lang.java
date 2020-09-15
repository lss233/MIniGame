package com.lss233.minigame.lang;

import com.lss233.minigame.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Lang {
    private final Plugin plugin;
    private final static Pattern variablePattern = Pattern.compile("\\{(\\w+)}");
    private YamlConfiguration yaml;
    public Lang(Plugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig(){
        File langFile = new File(this.plugin.getJavaPlugin().getDataFolder(), "lang.yml");
        if(!langFile.exists()){
            this.plugin.getJavaPlugin().saveResource("lang.yml", false);
        }
        yaml = YamlConfiguration.loadConfiguration(langFile);
        plugin.getLogger().info(L("system.lang-loaded", "语言已加载完毕。"));
    }

    public String L(String key, String def) {
        if(yaml == null)
            this.loadConfig();
        if(!yaml.contains(key)){
            yaml.set(key,def);
            try {
                yaml.save(new File(this.plugin.getJavaPlugin().getDataFolder(), "lang.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return yaml.getString(key, def).replaceAll("&", String.valueOf(ChatColor.COLOR_CHAR));
    }
    public String L(String key, String def, Map<String, Object> variables) {
        final AtomicReference<String> msg = new AtomicReference<>(L(key, def));
        if (variables != null)
            variables.forEach((k, v) -> msg.set(msg.get().replace("{" + k + "}", v.toString())));
        return msg.get().replaceAll("&", String.valueOf(ChatColor.COLOR_CHAR));
    }

    public Collection<String> Ls(String key, Map<String, Object> variables) {
        return yaml.getStringList(key).stream().map(line -> {
            Matcher matcher = variablePattern.matcher(line);
            StringBuilder builder = new StringBuilder();
            int i = 0;
            while (matcher.find()) {
                String replacement = variables.getOrDefault(matcher.group(1), "{" + key + "}").toString();
                builder.append(line, i, matcher.start());
                if (replacement != null) {
                    builder.append(replacement);
                    i = matcher.end();
                }
            }
            builder.append(line.substring(i));
            return builder.toString().replaceAll("&", String.valueOf(ChatColor.COLOR_CHAR));
        }).collect(Collectors.toList());
    }
}
