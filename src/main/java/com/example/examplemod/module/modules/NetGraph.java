package com.example.examplemod.module.modules;

import com.example.examplemod.event.EventTarget;
import com.example.examplemod.event.events.EventReceivePacket;
import com.example.examplemod.event.events.EventRender2D;
import com.example.examplemod.event.events.EventTick;
import com.example.examplemod.event.events.EventSendPacket;
import com.example.examplemod.module.Category;
import com.example.examplemod.module.Module;
import com.example.examplemod.utils.render.Colors;
import com.example.examplemod.utils.render.RenderingUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;

public class NetGraph extends Module {

    private final ArrayBlockingQueue<TickPacketData> arrayBlockingQueue = new ArrayBlockingQueue<>(50);
    private List<Packet> incomingPackets = new ArrayList<>();
    private List<Packet> outgoingPackets = new ArrayList<>();
    private int incomingCounter = 0;
    private int outgoingCounter = 0;

    public NetGraph(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onSendPacket(EventSendPacket event) {
        Packet packet = event.getPacket();

        if (event.isCancelled() || mc.getIntegratedServer() != null)
            return;

        outgoingPackets.add(packet);
        outgoingCounter++;
    }

    @EventTarget
    public void onReceivePacket(EventReceivePacket event) {
        Packet packet = event.getPacket();

        if (event.isCancelled() || mc.getIntegratedServer() != null)
            return;

        incomingPackets.add(packet);
        incomingCounter++;
    }

    @EventTarget
    public void onTick(EventTick event) {
        if (arrayBlockingQueue.remainingCapacity() == 0) {
            arrayBlockingQueue.remove();
        }
        arrayBlockingQueue.add(new TickPacketData(incomingPackets, outgoingPackets));
        incomingCounter = outgoingCounter = 0;
        incomingPackets = new ArrayList<>();
        outgoingPackets = new ArrayList<>();
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        GlStateManager.pushMatrix();

        //position
        GlStateManager.translate(-90, -110, 0);
        {
            GlStateManager.pushMatrix();
            RenderingUtil.rectangleBordered(100.5, 124, 201.5, 150.5, 0.5, Colors.getColor(0, 45), Colors.getColor(255, 255));
            RenderingUtil.rectangle(100.5, 129.5, 201.5, 130, -1);
            int highestCountIncoming = 1;

            int totalPackets = 0;

            int validPacketSize = 0;

            HashMap<String, Integer> packetMap = new HashMap<>();

            boolean debug = false;

            for (TickPacketData tickPacketData : arrayBlockingQueue) {
                if (debug) {
                    for (Packet outgoingPacket : tickPacketData.outgoingPackets) {
                        if (outgoingPacket != null) {
                            if (!(outgoingPacket instanceof C00PacketKeepAlive) && !(outgoingPacket instanceof C0FPacketConfirmTransaction))
                                packetMap.put(outgoingPacket.getClass().getSimpleName(), packetMap.getOrDefault(outgoingPacket.getClass().getSimpleName(), 0) + 1);
                        } else {
                            packetMap.put(NetGraph.class.getSimpleName(), 999999);
                        }
                    }
                }

                if (tickPacketData.outgoingPackets.size() > highestCountIncoming) {
                    highestCountIncoming = tickPacketData.outgoingPackets.size();
                }
                if (tickPacketData.outgoingPackets.size() > 0) {
                    validPacketSize++;
                }
                totalPackets += tickPacketData.outgoingPackets.size();
            }

            if (debug) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(100, 200, 0);
                GlStateManager.scale(0.5, 0.5, 0.5);
                int bruhOffset = 0;
                for (Map.Entry<String, Integer> classIntegerEntry : packetMap.entrySet()) {
                    mc.fontRendererObj.drawStringWithShadow(classIntegerEntry.getKey() + " " + classIntegerEntry.getValue(), 0, bruhOffset, -1);
                    bruhOffset += 10;
                }
                GlStateManager.popMatrix();
            }

            float incomingScale = 20F / highestCountIncoming;

            int averagePackets = Math.round((float) totalPackets / validPacketSize);

            float height = 149.5F - (averagePackets * incomingScale);

            GlStateManager.pushMatrix();
            String reallyNigga = averagePackets + " avg";
            GlStateManager.translate(203, height, 0);
            GlStateManager.scale(0.5, 0.5, 0.5);
            mc.fontRendererObj.drawStringWithShadow(reallyNigga, 0, 0, -1);
            GlStateManager.popMatrix();

            float x = 0;

            GlStateManager.pushMatrix();
            GlStateManager.translate(100, 118, 0);
            GlStateManager.scale(0.5, 0.5, 0.5);
            mc.fontRendererObj.drawStringWithShadow("Outgoing Packets | " + (int) (((float) totalPackets / arrayBlockingQueue.size()) * 20) + " p/s | " + validPacketSize + "/" + arrayBlockingQueue.size(), 0, 0, -1);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            String bruh = String.valueOf(highestCountIncoming);
            GlStateManager.translate(99 - mc.fontRendererObj.getStringWidth(bruh) / 2F, 120 + 7, 0);
            GlStateManager.scale(0.5, 0.5, 0.5);
            mc.fontRendererObj.drawStringWithShadow(bruh, 0, 0, -1);
            GlStateManager.popMatrix();

            RenderingUtil.enableGL2D();
            GL11.glLineWidth(1.25F);
            GL11.glBegin(GL_LINE_STRIP);

            for (TickPacketData tickPacketData : arrayBlockingQueue) {
                GL11.glVertex2d(200 + x, 150 - (tickPacketData.outgoingPackets.size() * incomingScale));
                x -= 2;
            }
            GL11.glEnd();
            RenderingUtil.disableGL2D();

            RenderingUtil.rectangle(100.5, height, 201.5, height + 0.5, Colors.getColor(0, 255, 0, 255));

            GlStateManager.popMatrix();
        }

        // Incoming
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(125, 0, 0);
            RenderingUtil.rectangleBordered(100.5, 124, 201.5, 150.5, 0.5, Colors.getColor(0, 45), Colors.getColor(255, 255));
            RenderingUtil.rectangle(100.5, 129.5, 201.5, 130, -1);
            int highestCountIncoming = 0;

            int totalPackets = 0;

            int validPacketSize = 0;

            boolean debug = false;

            HashMap<Class, Integer> packetMap = new HashMap<>();

            for (TickPacketData tickPacketData : arrayBlockingQueue) {
                if (debug)
                    for (Packet incomingPacket : tickPacketData.incomingPackets) {
                        packetMap.put(incomingPacket.getClass(), packetMap.getOrDefault(incomingPacket.getClass(), 0) + 1);
                    }

                if (tickPacketData.incomingPackets.size() > highestCountIncoming) {
                    highestCountIncoming = tickPacketData.incomingPackets.size();
                }
                if (tickPacketData.incomingPackets.size() > 0) {
                    validPacketSize++;
                }
                totalPackets += tickPacketData.incomingPackets.size();
            }

            if (debug) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(100, 200, 0);
                GlStateManager.scale(0.5, 0.5, 0.5);
                int bruhOffset = 0;
                for (Map.Entry<Class, Integer> classIntegerEntry : packetMap.entrySet()) {
                    mc.fontRendererObj.drawStringWithShadow(classIntegerEntry.getKey().getSimpleName() + " " + classIntegerEntry.getValue(), 0, bruhOffset, -1);
                    bruhOffset += 10;
                }
                GlStateManager.popMatrix();
            }


            float incomingScale = 20F / highestCountIncoming;

            int averagePackets = Math.round((float) totalPackets / validPacketSize);

            float height = 149.5F - (averagePackets * incomingScale);

            GlStateManager.pushMatrix();
            String reallyNigga = averagePackets + " avg";
            GlStateManager.translate(203, height, 0);
            GlStateManager.scale(0.5, 0.5, 0.5);
            mc.fontRendererObj.drawStringWithShadow(reallyNigga, 0, 0, -1);
            GlStateManager.popMatrix();

            float x = 0;

            GlStateManager.pushMatrix();
            GlStateManager.translate(100, 118, 0);
            GlStateManager.scale(0.5, 0.5, 0.5);
            mc.fontRendererObj.drawStringWithShadow("Incoming Packets | " + (int) (((float) totalPackets / arrayBlockingQueue.size()) * 20) + " p/s | " + validPacketSize + "/" + arrayBlockingQueue.size(), 0, 0, -1);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            String bruh = String.valueOf(highestCountIncoming);
            GlStateManager.translate(99 - mc.fontRendererObj.getStringWidth(bruh) / 2F, 120 + 7, 0);
            GlStateManager.scale(0.5, 0.5, 0.5);
            mc.fontRendererObj.drawStringWithShadow(bruh, 0, 0, -1);
            GlStateManager.popMatrix();

            RenderingUtil.enableGL2D();
            GL11.glLineWidth(1.25F);
            GL11.glBegin(GL_LINE_STRIP);

            for (TickPacketData tickPacketData : arrayBlockingQueue) {
                GL11.glVertex2d(200 + x, 150 - (tickPacketData.incomingPackets.size() * incomingScale));
                x -= 2;
            }
            GL11.glEnd();
            RenderingUtil.disableGL2D();

            RenderingUtil.rectangle(100.5, height, 201.5, height + 0.5, Colors.getColor(0, 255, 0, 255));

            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    public static class TickPacketData {

        List<Packet> incomingPackets;
        List<Packet> outgoingPackets;

        public TickPacketData(List<Packet> incoming, List<Packet> outgoing) {
            this.incomingPackets = incoming;
            this.outgoingPackets = outgoing;
        }

    }
}