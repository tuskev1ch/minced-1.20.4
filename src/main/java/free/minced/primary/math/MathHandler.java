package free.minced.primary.math;

import free.minced.primary.IHolder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

public class MathHandler implements IHolder {
    public static @NotNull Vector3f calculateNormal(Vec3d start, Vec3d end) {
        float x = (float) (end.x - start.x);
        float y = (float) (end.y - start.y);
        float z = (float) (end.z - start.z);
        float length = MathHelper.sqrt(x * x + y * y + z * z);
        return new Vector3f(x / length, y / length, z / length);
    }

    public static float calculateDelta(float a, float b) {
        return a - b;
    }
    public static Vec3d getInterpolatedPosition(Entity entity, float tickDelta) {
        return new Vec3d(MathHelper.lerp(tickDelta, entity.prevX, entity.getX()), MathHelper.lerp(tickDelta, entity.prevY, entity.getY()), MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ()));
    }
    public static double clamp(double min, double max, double number) {
        return Math.max(min, Math.min(max, number));
    }

    public static float random(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }
    public static double getRandom(double min, double max) {
        if (min == max) {
            return min;
        } else if (min > max) {
            final double d = min;
            min = max;
            max = d;
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
    public static float getPow2Value(float value) {
        return (float) value * (float) value;
    }
    public static float getPow2Value(int value) {

        return (int) value * (int) value;

    }
    public static float randomize(float min, float max) {
        return (float) (Math.random() * (double) (max - min)) + min;
    }

    public static float lerp(float min, float max, float delta) {
        return min + (max - min) * delta;
    }

    public static int lerp(int min, float max, float delta) {
        return (int) (min + (max - min) * delta);
    }

    public static double roundWithSteps(final double value, final double steps) {
        double a = ((Math.round(value / steps)) * steps);
        a *= 1000;
        a = (int) a;
        a /= 1000;
        return a;
    }

    public static double round(final double value, final int scale, final double inc) {
        final double halfOfInc = inc / 2.0;
        final double floored = Math.floor(value / inc) * inc;

        if (value >= floored + halfOfInc) {
            return new BigDecimal(Math.ceil(value / inc) * inc).setScale(scale, RoundingMode.HALF_UP).doubleValue();
        } else {
            return new BigDecimal(floored).setScale(scale, RoundingMode.HALF_UP).doubleValue();
        }
    }

    public static double round(float value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String round(String value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.stripTrailingZeros();
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.toString();
    }

    public static float wrapDegrees(float value) {
        value = value % 360.0F;

        if (value >= 180.0F) {
            value -= 360.0F;
        }

        if (value < - 180.0F) {
            value += 360.0F;
        }

        return value;
    }

    public static float normalize(float value, float min, float max) {
        return (value - min) / (max - min);
    }

    public static float denormalize(float value, float min, float max) {
        return min + (max - min) * value;
    }

    public static float interpolate(float oldValue, float newValue, float interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static Vector3d getVectorForRotation(float pitch, float yaw) {
        float yawRadians = -yaw * ((float) Math.PI / 180) - (float) Math.PI;
        float pitchRadians = -pitch * ((float) Math.PI / 180);

        double cosYaw = Math.cos(yawRadians);
        double sinYaw = Math.sin(yawRadians);
        double cosPitch = -Math.cos(pitchRadians);
        double sinPitch = Math.sin(pitchRadians);

        return new Vector3d(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
    }
}