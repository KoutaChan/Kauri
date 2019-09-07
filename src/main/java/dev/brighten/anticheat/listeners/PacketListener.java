package dev.brighten.anticheat.listeners;

import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.Listen;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.events.impl.PacketSendEvent;
import cc.funkemunky.api.utils.Init;
import dev.brighten.anticheat.Kauri;

@Init
public class PacketListener implements AtlasListener {

    @Listen
    public void onEvent(PacketReceiveEvent event) {
        Kauri.INSTANCE.packetProcessor.processClient(Kauri.INSTANCE.dataManager.getData(event.getPlayer()), event.getPacket(), event.getType());
    }

    @Listen
    public void onEvent(PacketSendEvent event) {
        Kauri.INSTANCE.packetProcessor.processServer(Kauri.INSTANCE.dataManager.getData(event.getPlayer()), event.getPacket(), event.getType());
    }
}