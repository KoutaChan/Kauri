package dev.brighten.anticheat.check.impl.movement.general;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInTransactionPacket;
import dev.brighten.anticheat.check.api.Check;
import dev.brighten.anticheat.check.api.CheckInfo;
import dev.brighten.anticheat.check.api.Packet;
import dev.brighten.api.check.CheckType;
import dev.brighten.api.check.DevStage;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "Blink", description = "Invalid lag spikes", checkType = CheckType.GENERAL,
        devStage = DevStage.ALPHA, vlToFlag = 3)
public class Blink extends Check {

    private int buffer = 0;

    private long last;
    private final List<Long> lagLists = new ArrayList<>();

    @Packet
    public void onFlying(WrappedInFlyingPacket packet, long now) {
        final long current = now - last;

        if (current <= 49) {

            lagLists.add(current);

            if (lagLists.size() > 5) {
                final long lag = data.lagInfo.transPing - data.lagInfo.lastTransPing;

                final long totals = lagLists.stream().mapToLong(l -> l).sum();

                final long finalLags = totals - lag;

                if ((finalLags > 0 && finalLags < 45) || finalLags > 1000) {
                    if (buffer++ > 4) {
                        flag("finalLags=%s", finalLags);

                        buffer = 0;
                    }
                }

                debug("finalLags=%s current=%s lag=%s", finalLags, current, lag);
            }
        } else {
            lagLists.clear();

            buffer = Math.max(0, buffer - 1);
        }

        this.last = now;
    }
}