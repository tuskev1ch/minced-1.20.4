package free.minced.systems.rotations;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import free.minced.primary.IHolder;

public class Rotations implements IHolder {

    public static boolean rotating;

    public static float serverYaw;
    public static float serverPitch;
    public static int currentPriority;

    private static float preYaw, prePitch;

    public static void rotate(double yaw, double pitch, int priority) {
        if (priority >= currentPriority) {
            serverYaw = (float) yaw;
            serverPitch = (float) pitch;
            currentPriority = priority;
            rotating = true;
        }
    }

    public static void onSendMovementPacketsPre() {
        if (mc.cameraEntity != mc.player) return;

        if (rotating) {
            preYaw = mc.player.getYaw();
            prePitch = mc.player.getPitch();

            mc.player.setYaw(serverYaw);
            mc.player.setPitch(serverPitch);
        }
    }

    public static void onSendMovementPacketsPost() {
        if (rotating) {
            mc.player.setYaw(preYaw);
            mc.player.setPitch(prePitch);
            rotating = false;
            currentPriority = 0;
        }
    }
}
