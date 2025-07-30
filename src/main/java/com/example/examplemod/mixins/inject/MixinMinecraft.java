package com.example.examplemod.mixins.inject;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.event.events.EventTick;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "startGame", at = @At("TAIL"))
    private void startGame(CallbackInfo ci) {
        System.out.println("Mixin worked!");
        ExampleMod.setup();
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    private void runTickPre(CallbackInfo ci) {
        EventTick eventTick = new EventTick();
        eventTick.call();
    }
}
