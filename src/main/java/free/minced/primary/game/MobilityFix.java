package free.minced.primary.game;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import free.minced.Minced;
import free.minced.events.impl.input.EventKeyboardInput;
import free.minced.events.impl.mobility.EventFixVelocity;
import free.minced.events.impl.mobility.EventPlayerTravel;
import free.minced.events.impl.player.EventPlayerJump;
import free.minced.modules.impl.combat.AttackAura;
import free.minced.primary.IHolder;

public class MobilityFix implements IHolder {

    public static float rotationYaw;
    public static float rotationPitch;
    
    public static float prevYaw;
    public static float prevPitch;


    public static void onJump(EventPlayerJump e) {
        if (Float.isNaN(rotationYaw) || Minced.getInstance().getModuleHandler().get(AttackAura.class).mobilityFix.is("Off"))
            return;

        if (e.isPre()) {
            prevYaw = mc.player.getYaw();
            prevPitch = mc.player.getPitch();

            mc.player.setYaw(rotationYaw);
            if (mc.player.isFallFlying()) {
                mc.player.setPitch(rotationPitch);
            }
        } else {
            mc.player.setYaw(prevYaw);
            mc.player.setPitch(prevPitch);
        }
    }

    public static void onPlayerMove(EventFixVelocity event) {
        if (Minced.getInstance().getModuleHandler().get(AttackAura.class).mobilityFix.is("Free")) {
            if (Float.isNaN(rotationYaw))
                return;
            event.setVelocity(fix(rotationYaw, event.getMovementInput(), event.getSpeed()));
        }
    }

    public static void modifyVelocity(EventPlayerTravel e) {

        if (Minced.getInstance().getModuleHandler().get(AttackAura.class).mobilityFix.is("Focused") && !Float.isNaN(rotationYaw)) {
            if (e.isPre()) {
                prevYaw = mc.player.getYaw();
                prevPitch = mc.player.getPitch();

                mc.player.setYaw(rotationYaw);
                if (mc.player.isFallFlying()) {
                    mc.player.setPitch(rotationPitch);
                }
            } else {
                mc.player.setYaw(prevYaw);
                mc.player.setPitch(prevPitch);
            }
        }
    }

    public static void onKeyInput(EventKeyboardInput e) {
        if (Minced.getInstance().getModuleHandler().get(AttackAura.class).mobilityFix.is("Free")) {
            if (Float.isNaN(rotationYaw))
                return;

            float mF = mc.player.input.movementForward;
            float mS = mc.player.input.movementSideways;
            float delta = (mc.player.getYaw() - rotationYaw) * MathHelper.RADIANS_PER_DEGREE;
            float cos = MathHelper.cos(delta);
            float sin = MathHelper.sin(delta);
            mc.player.input.movementSideways = Math.round(mS * cos - mF * sin);
            mc.player.input.movementForward = Math.round(mF * cos + mS * sin);
        }
    }

    private static Vec3d fix(float yaw, Vec3d movementInput, float speed) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7)
            return Vec3d.ZERO;
        Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);
        float f = MathHelper.sin(yaw * MathHelper.RADIANS_PER_DEGREE);
        float g = MathHelper.cos(yaw * MathHelper.RADIANS_PER_DEGREE);
        return new Vec3d(vec3d.x * (double) g - vec3d.z * (double) f, vec3d.y, vec3d.z * (double) g + vec3d.x * (double) f);
    }


}
