package com.example.examplemod.mixins.inject;

import com.example.examplemod.event.events.EventReceivePacket;
import com.example.examplemod.event.events.EventSendPacket;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(value = NetworkManager.class, priority = 1001)
public abstract class MixinNetworkManager extends SimpleChannelInboundHandler<Packet<?>> {

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet<?> packet, CallbackInfo ci) {
        EventSendPacket eventSendPacket = new EventSendPacket(packet);
        eventSendPacket.call();

        if (eventSendPacket.isCancelled()) {
            ci.cancel();
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void receivePacket(ChannelHandlerContext p_channelRead0_1_, Packet<?> packet, CallbackInfo ci) {
        EventReceivePacket eventReceivePacket = new EventReceivePacket((Packet<INetHandlerPlayClient>) packet);
        eventReceivePacket.call();

        if (eventReceivePacket.isCancelled()) {
            ci.cancel();
        }
    }
}
