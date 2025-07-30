package com.example.examplemod.module;

import com.example.examplemod.module.modules.NetGraph;

import java.util.ArrayList;

public class ModuleManager {

    private final ArrayList<Module> modules = new ArrayList<Module>();
    private boolean isSetup;

    public void setup() {

        add(new NetGraph("NetGraph", "", 0, Category.Visual));

        if (!isEnabled(NetGraph.class)) {
            getClazz(NetGraph.class).toggle();
        }

        isSetup = true;
    }

    public void add(Module mod){
        modules.add(mod);
    }

    public boolean isEnabled(Class<?> clazz) {
        Module module = getClazz(clazz);
        return (module != null && module.isEnabled());
    }

    public Module getClazz(Class<?> clazz) {
        try {
            for (Module feature : getModules()) {
                if (feature.getClass() == clazz)
                    return feature;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public ArrayList<Module> getModules(){
        return this.modules;
    }
}
