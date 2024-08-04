package free.minced.systems.rotations;

import free.minced.primary.IHolder;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.LinkedBlockingQueue;

@Getter
@Setter
public class Rotations implements IHolder {

    public static final LinkedBlockingQueue<RotationRequest> rotationQueue = new LinkedBlockingQueue<>();

    private static float preYaw, prePitch;

    public static void rotate(double yaw, double pitch) {
        rotationQueue.add(new RotationRequest(yaw, pitch));
    }

    public static void onSendMovementPacketsPre() {
        if (mc.cameraEntity!= mc.player) return;

        RotationRequest request = rotationQueue.poll();
        if (request!= null) {
            preYaw = mc.player.getYaw();
            prePitch = mc.player.getPitch();

            mc.player.setYaw((float) request.yaw);
            mc.player.setPitch((float) request.pitch);
        }
    }

    public static void onSendMovementPacketsPost() {
        if (preYaw!= 0 && prePitch!= 0) {
            mc.player.setYaw(preYaw);
            mc.player.setPitch(prePitch);
            preYaw = 0;
            prePitch = 0;
        }
    }

    @Getter
    @Setter
    public static class RotationRequest {
        double yaw;
        double pitch;

        public RotationRequest(double yaw, double pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }
}