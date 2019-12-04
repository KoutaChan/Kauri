package dev.brighten.anticheat.check.api;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import dev.brighten.api.check.CheckType;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class CheckSettings {
    public boolean enabled, executable;
    public final String name, description;
    public final CheckType type;
    public final int punishVl;
    public final ProtocolVersion minVersion, maxVersion;
}