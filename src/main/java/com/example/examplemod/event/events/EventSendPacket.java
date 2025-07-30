package com.example.examplemod.event.events;

import com.example.examplemod.event.Event;
import net.minecraft.network.Packet;

public class EventSendPacket extends Event {

    private Packet<?> packet;

    public EventSendPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }
}