package cc.funkemunky.anticheat.impl.checks.movement;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.checks.CheckType;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.anticheat.api.utils.Setting;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutVelocityPacket;
import cc.funkemunky.api.utils.MathUtils;
import lombok.val;
import org.bukkit.event.Event;

@Packets(packets = {
        Packet.Server.ENTITY_VELOCITY,
        Packet.Client.POSITION_LOOK,
        Packet.Client.POSITION,
        Packet.Client.FLYING,
        Packet.Client.LEGACY_POSITION_LOOK,
        Packet.Client.LEGACY_POSITION})
public class VelocityA extends Check {


    @Setting(name = "thresoldVL")
    private int maxVL = 7;

    private float lastVelocity;
    private int vl;

    public VelocityA(String name, String description, CheckType type, CancelType cancelType, int maxVL, boolean enabled, boolean executable, boolean cancellable) {
        super(name, description, type, cancelType, maxVL, enabled, executable, cancellable);
    }

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        if (packetType.equals(Packet.Server.ENTITY_VELOCITY)) {
            WrappedOutVelocityPacket velocity = new WrappedOutVelocityPacket(packet, getData().getPlayer());

            if (velocity.getId() == velocity.getPlayer().getEntityId()) {
                lastVelocity = (float) velocity.getY();
            }
        }
        if (lastVelocity > 0 && getData().getMovementProcessor().getDeltaY() > 0) {
            val ratio = Math.abs(getData().getMovementProcessor().getDeltaY() / lastVelocity);
            val percentage = MathUtils.round(ratio * 100D, 1);

            if (ratio < 1 && !getData().getMovementProcessor().isBlocksOnTop() && !getData().isAbleToFly()) {
                if (vl++ > maxVL) {
                    flag("velocity: " + percentage + "%", true, true);
                }
            } else {
                vl -= vl > 0 ? 1 : 0;
            }

            debug("RATIO: " + ratio + " VL: " + vl + " DELTAY:" + (getData().getMovementProcessor().getTo().getY() - getData().getMovementProcessor().getFrom().getY()) + "VELOCITY: " + lastVelocity);

            lastVelocity = 0;
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
