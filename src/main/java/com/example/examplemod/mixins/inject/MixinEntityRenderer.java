package com.example.examplemod.mixins.inject;

import com.example.examplemod.event.events.EventRender2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V", shift = At.Shift.AFTER))
    public void onRender2D(float partialTicks, long p_updateCameraAndRender_2_, CallbackInfo ci) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        EventRender2D eventRender2D = new EventRender2D(sr, partialTicks);
        eventRender2D.call();
    }
}
