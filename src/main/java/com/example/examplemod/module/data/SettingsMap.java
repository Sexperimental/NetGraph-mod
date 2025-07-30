package com.example.examplemod.module.data;

import java.util.HashMap;

public class SettingsMap extends HashMap<String, Setting> {

    public void update(HashMap<String, Setting> newMap) {
        for (String key : newMap.keySet()) {
            if (containsKey(key)) {
                get(key).update(newMap.get(key));
            }
        }
    }
}
