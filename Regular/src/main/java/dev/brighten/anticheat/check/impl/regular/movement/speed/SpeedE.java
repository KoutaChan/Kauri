package dev.brighten.anticheat.check.impl.regular.movement.speed;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.KLocation;
import cc.funkemunky.api.utils.MathUtils;
import dev.brighten.anticheat.check.api.Cancellable;
import dev.brighten.anticheat.check.api.Check;
import dev.brighten.anticheat.check.api.CheckInfo;
import dev.brighten.anticheat.check.api.Packet;
import dev.brighten.api.check.CheckType;

@CheckInfo(name = "Speed (E)", description = "Checks for impossible direction changes.",
        checkType = CheckType.SPEED, punishVL = 8, executable = true)
@Cancellable
public class SpeedE extends Check {

    private double lastX, lastZ;

    @Packet
    public void onFlying(WrappedInFlyingPacket packet) {
        if (packet.isPos()) {

            final KLocation to = data.playerInfo.to;
            final KLocation from = data.playerInfo.from;

            final double deltaY = data.playerInfo.deltaY;
            final double lastDeltaY = data.playerInfo.lDeltaY;

            final double x = to.x > from.x ? to.x - from.x : from.x - to.x;
            final double z = to.z > from.z ? to.z - from.z : from.z - to.z;

            final double accelerationX = x - this.lastX;
            final double accelerationZ = z - this.lastZ;

            final double accelerationXZ = MathUtils.hypot(accelerationX, accelerationZ);

            final boolean ground = packet.isGround();

            final double speedAcceleration = Math.abs(data.playerInfo.deltaXZ - data.playerInfo.lDeltaXZ) * 100;

            debug((accelerationXZ > 0.1f ? Color.Green : "") + "acceleration=%s ground=%s speed=%s", accelerationXZ, ground, speedAcceleration);

            if (!ground
                    && accelerationXZ > 0.1f
                    && lastDeltaY > deltaY
                    && speedAcceleration < 1f
                    && !data.blockInfo.onHalfBlock
                    && !data.playerInfo.generalCancel) {
                vl++;
                flag("accelerationXZ=%.3f deltaY=%.3f lastDeltaY=%.3f", accelerationXZ, deltaY, lastDeltaY);
            }

            this.lastX = x;
            this.lastZ = z;
        }
    }
}
