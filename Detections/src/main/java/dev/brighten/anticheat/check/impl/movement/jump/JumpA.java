package dev.brighten.anticheat.check.impl.movement.jump;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import dev.brighten.anticheat.check.api.Cancellable;
import dev.brighten.anticheat.check.api.Check;
import dev.brighten.anticheat.check.api.CheckInfo;
import dev.brighten.anticheat.check.api.Packet;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Jump (A)", description = "Checks for invalid jump motion.", punishVL = 10, executable = true)
@Cancellable
public class JumpA extends Check {

    private int airTicks;

    @Packet
    public void onFlying(WrappedInFlyingPacket packet) {
        if (packet.isPos()) {
            //data.playerInfo.airTicks is broken
            airTicks = !packet.isGround() ? airTicks + 1 : 0;

            if (data.playerInfo.generalCancel
                    || data.playerInfo.liquidTimer.isNotPassed(2)
                    || data.playerInfo.canFly
                    || data.playerInfo.climbTimer.isNotPassed(3)
                    || data.playerInfo.blockAboveTimer.isNotPassed(5)
                    || data.playerInfo.lastVelocity.isNotPassed(1)
                    || data.playerInfo.slimeTimer.isNotPassed(3)
                    || data.blockInfo.onHalfBlock
                    || data.blockInfo.inHoney) {
                return;
            }

            final double lastDeltaY = data.playerInfo.lDeltaY;
            final double deltaY = data.playerInfo.deltaY;

            final double jumpBoost = data.potionProcessor.getEffectByType(PotionEffectType.JUMP).map(ef -> ef.getAmplifier() + 1)
                    .orElse(0);

            final double predictionY = data.playerInfo.jumped ? 0.41999998688697815 + (jumpBoost * .1d) : (lastDeltaY - 0.08) * 0.9800000190734863;

            final double diff = Math.abs(deltaY - predictionY);

            debug("diff=%s predictionY=%s deltaY=%s airTicks=%s climb=%s sinceBlockAbove=%s", diff, predictionY, deltaY, airTicks, data.playerInfo.climbTimer.getPassed(), data.playerInfo.blockAboveTimer.getPassed());

            if (deltaY > 0 && diff > 0.0001 && airTicks > 0) {
                vl++;
                flag("diff=%.4f predictionY=%.3f deltaY=%.3f airTicks=%s", diff, predictionY, deltaY, airTicks);
            }

            if (predictionY > 0 && deltaY < 0) {
                vl++;
                flag("predictionY=%.3f deltaY=%.3f airTicks=%s", predictionY, deltaY, airTicks);
            }
        }
    }
}