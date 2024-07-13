package free.minced.primary.game;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import free.minced.Minced;
import free.minced.events.impl.input.EventKeyboardInput;
import free.minced.events.impl.mobility.EventFixVelocity;
import free.minced.events.impl.mobility.EventPlayerTravel;
import free.minced.events.impl.player.EventPlayerJump;
import free.minced.events.impl.player.EventPostSync;
import free.minced.events.impl.player.EventSync;
import free.minced.mixin.accesors.IClientPlayerEntity;
import free.minced.modules.impl.combat.AttackAura;
import free.minced.primary.IHolder;

import static net.minecraft.util.math.MathHelper.clamp;

public class MobilityFix implements IHolder {
    public static float fixRotation;

    private static float prevRotation;

    public static float yaw;
    public static float pitch;
    public static float bodyYaw;
    public static float prevBodyYaw;

    public static float lastYaw;
    public static float lastPitch;

    private static float clientYaw;
    private static float clientPitch;

    public static void onJump(EventPlayerJump e) {
        if (Float.isNaN(fixRotation) || Minced.getInstance().getModuleHandler().get(AttackAura.class).mobilityFix.is("Off"))
            return;

        if (e.isPre()) {
            prevRotation = mc.player.getYaw();
            
            mc.player.setYaw(fixRotation);
            
        } else {
            mc.player.setYaw(prevRotation);
            
        }
    }

    public static void onPlayerMove(EventFixVelocity event) {
        if (Minced.getInstance().getModuleHandler().get(AttackAura.class).mobilityFix.is("Free")) {
            if (Float.isNaN(fixRotation))
                return;
            event.setVelocity(fix(fixRotation, event.getMovementInput(), event.getSpeed()));
        }
    }

    public static void modifyVelocity(EventPlayerTravel e) {
        if (Minced.getInstance().getModuleHandler().get(AttackAura.class).mobilityFix.is("Focused") && !Float.isNaN(fixRotation)) {
            if (e.isPre()) {
                prevRotation = mc.player.getYaw();
                
                mc.player.setYaw(fixRotation);
                
            } else {
                mc.player.setYaw(prevRotation);
                
            }
        }
    }

    public static void onKeyInput(EventKeyboardInput e) {
        if (Minced.getInstance().getModuleHandler().get(AttackAura.class).mobilityFix.is("Free")) {
            if (Float.isNaN(fixRotation))
                return;

            float mF = mc.player.input.movementForward;
            float mS = mc.player.input.movementSideways;
            float delta = (mc.player.getYaw() - fixRotation) * MathHelper.RADIANS_PER_DEGREE;
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

    /** ROTATIONS SILENT **/


}
