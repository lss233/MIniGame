package com.lss233.minigame;

import com.lss233.minigame.lang.Lang;

public class LangManager {
    public static Lang load(Plugin plugin) {
        return new Lang(plugin);
    }
}
