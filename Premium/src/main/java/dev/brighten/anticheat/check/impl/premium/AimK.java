package dev.brighten.anticheat.check.impl.premium;

import dev.brighten.anticheat.check.api.Check;
import dev.brighten.anticheat.check.api.CheckInfo;
import dev.brighten.api.KauriVersion;
import dev.brighten.api.check.CheckType;
import dev.brighten.api.check.DevStage;

@CheckInfo(name = "Aim (K)", description = "Statistical aim analysis",
        checkType = CheckType.AIM, planVersion = KauriVersion.ARA, devStage = DevStage.CANARY)
public class AimK extends Check {

    private int buffer;

    public void runCheck(double std, double pstd, double[] offset, float[] rot) {
        if(std < 1) {
            if(data.playerInfo.deltaYaw > 0.2 && ++buffer > 8) {
                vl++;
                buffer = 8;
                flag("t=b y=%.2f dy=%.3f s=%.3f", offset[1], data.playerInfo.deltaYaw, std);
            }
        } else buffer = 0;
    }
}