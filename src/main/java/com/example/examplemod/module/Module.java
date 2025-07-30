package com.example.examplemod.module;

import com.example.examplemod.event.EventManager;
import com.example.examplemod.module.data.SettingsMap;
import com.example.examplemod.utils.MCUtil;

public class Module implements MCUtil {

    protected final SettingsMap settings = new SettingsMap();
    private String name, displayname, suffix;
    private String description;
    private int keybind;
    private boolean enabled, isHidden;
    private Category category;

    public Module(String name, String desc, int keybind, Category category) {
        this.name = name;
        this.description = desc;
        this.keybind = keybind;
        this.category = category;
    }

    public void toggle() {
        enabled = !enabled;

        if (enabled) {
            EventManager.register(this);
            onEnable();
        } else {
            EventManager.unregister(this);
            onDisable();
        }
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public boolean isEnabled() {
        return enabled;
    }

}
