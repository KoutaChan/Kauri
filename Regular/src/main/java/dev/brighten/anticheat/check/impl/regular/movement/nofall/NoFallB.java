package dev.brighten.anticheat.check.impl.regular.movement.nofall;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import dev.brighten.anticheat.check.api.Cancellable;
import dev.brighten.anticheat.check.api.Check;
import dev.brighten.anticheat.check.api.CheckInfo;
import dev.brighten.anticheat.check.api.Packet;
import dev.brighten.api.check.CheckType;

@CheckInfo(name = "NoFall (B)", description = "Looks for ground spoofing",
        checkType = CheckType.NOFALL, punishVL = 12, executable = false, developer = true, vlToFlag = 2)
@Cancellable
public class NoFallB extends Check {

    private static final double divisor = 1 / 64.;

    private int airBuffer, groundBuffer;

    @Packet
    public void onFlying(WrappedInFlyingPacket packet, long timestamp) {
        if(!data.playerInfo.doingTeleport
                || data.playerInfo.canFly
                || data.playerInfo.creative
                || timestamp - data.creation < 2000L) return; // If we are waiting for them to teleport, don't check.

        // If they are saying they are on the ground
        if(data.playerInfo.clientGround
                && data.playerInfo.lastBlockPlace.isPassed(4)
                //And are no where near blocks
                && !data.blockInfo.blocksBelow && !data.blockInfo.blocksNear
                //And didn't collide with ghost blocks recently
                && data.playerInfo.lastGhostCollision.isPassed(20)) {
            groundBuffer+= 15;

            if(groundBuffer > 35) {
                vl++;
                flag("T=SPOOF_GROUND dy=%.2f y=%.1f", data.playerInfo.deltaY, data.playerInfo.to.y);
            }
            fixMovementBugs();
        } else if(groundBuffer > 0) groundBuffer-= 2;


        final boolean dground = data.playerInfo.to.y % divisor < 1E-4;
        // If they are saying they are on the ground
        if(!data.playerInfo.clientGround
                // Their bounding box is on the ground
                && ((data.playerInfo.serverGround || data.blockInfo.blocksBelow) && dground)
                && data.playerInfo.lastTeleportTimer.isPassed(1)) {
            if((airBuffer +=10) > 30) {
                vl++;
                flag("T=SPOOF_AIR dy=%.2f y=%.1f", data.playerInfo.deltaY, data.playerInfo.to.y);
            }
        } else if(airBuffer > 0) airBuffer -= 4;

        debug("c=%s s=%s bbelow=%s dg=%s dy=%.4f", data.playerInfo.clientGround, data.playerInfo.serverGround,
                data.blockInfo.blocksBelow, dground, data.playerInfo.deltaY);
    }
}