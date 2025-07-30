package com.example.examplemod;

import com.example.examplemod.module.ModuleManager;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod {

    public static final String MODID = "examplemod";
    public static final String VERSION = "1.0";

    private static final ModuleManager moduleManager = new ModuleManager();

    public ExampleMod() {
    }

    public static void setup() {
        moduleManager.setup();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("DIRT BLOCK >> " + Blocks.dirt.getUnlocalizedName());
    }
}
