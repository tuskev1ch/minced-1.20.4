package free.minced.framework.color;



import free.minced.primary.math.MathHandler;
import net.minecraft.util.math.MathHelper;

import java.awt.*;


public class ColorHandler {

    /**
     * <p>Parses a single RGB formatted integer into RGB format</p>
     *
     * @param in The input color integer
     * @return A length 3 array containing the R, G and B component of the color
     */
    public static int[] RGBIntToRGB(int in) {
        int red = in >> 8 * 2 & 0xFF;
        int green = in >> 8 & 0xFF;
        int blue = in & 0xFF;
        return new int[]{red, green, blue};
    }
    public static Color injectAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(alpha, 0, 255));
    }
    public static int getColor(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }
    public static Color getHealthColor(float health, float maxHealth) {
        float[] fractions = {0.0F, 0.5F, 1.0F};
        Color[] colors = { new Color(0xFF3535), new Color(0xFF9E0C), new Color(0x41FF59) };
        float progress = health / maxHealth;
        return blendColors(fractions, colors, progress);
    }
    public static Color interpolateTwoColors(int speed, int index, Color start, Color end) {
        int angle = (int) (((System.currentTimeMillis()) / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return interpolateColor(start, end, angle / 360f);
    }
    public static Color interpolateColor(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(MathHandler.interpolate(color1.getRed(), color2.getRed(), amount),
                MathHandler.interpolate(color1.getGreen(), color2.getGreen(), amount),
                MathHandler.interpolate(color1.getBlue(), color2.getBlue(), amount),
                MathHandler.interpolate(color1.getAlpha(), color2.getAlpha(), amount));
    }
    public CustomColor gradient(int speed, int index, CustomColor... colors) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        int colorIndex = (int) (angle / 360f * colors.length);
        if (colorIndex == colors.length) {
            colorIndex--;
        }
        CustomColor color1 = colors[colorIndex];
        CustomColor color2 = colors[colorIndex == colors.length - 1 ? 0 : colorIndex + 1];
        return interpolateColor(color1, color2, angle / 360f * colors.length - colorIndex);
    }



    public CustomColor interpolateColor(CustomColor color1, CustomColor color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new CustomColor((int) MathHandler.interpolate(color1.getRed(), color2.getRed(), amount),
                (int) MathHandler.interpolate(color1.getGreen(), color2.getGreen(), amount),
                (int) MathHandler.interpolate(color1.getBlue(), color2.getBlue(), amount),
                (int) MathHandler.interpolate(color1.getAlpha(), color2.getAlpha(), amount));
    }
    public static Color blend(Color acolor, Color bcolor, double ratio) {
        float r = (float) ratio;
        float ir = (float) 1.0 - r;

        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];

        acolor.getColorComponents(rgb1);
        bcolor.getColorComponents(rgb2);

        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;

        if (red < 0) {
            red = 0;
        } else if (red > 255) {
            red = 255;
        }
        if (green < 0) {
            green = 0;
        } else if (green > 255) {
            green = 255;
        }
        if (blue < 0) {
            blue = 0;
        } else if (blue > 255) {
            blue = 255;
        }

        Color color = null;
        try {
            color = new Color(red, green, blue);
        } catch (IllegalArgumentException ignored) {
        }
        return color;
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions.length == colors.length) {
            int[] indices = getFractionIndices(fractions, progress);
            float[] range = { fractions[indices[0]], fractions[indices[1]] };
            Color[] colorRange = { colors[indices[0]], colors[indices[1]] };
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            return blend(colorRange[0], colorRange[1], (1.0F - weight));
        }
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
    }



    public static int[] getFractionIndices(float[] fractions, float progress) {
        int[] range = new int[2];
        int startPoint;
        startPoint = 0;
        while (startPoint < fractions.length && fractions[startPoint] <= progress) {
            startPoint++;
        }
        if (startPoint >= fractions.length)
            startPoint = fractions.length - 1;
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    /**
     * @param color   the color to which the transparency will be applied
     * @param opacity from 0-255;
     */
    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }

    /**
     * @param color   the color to which the transparency will be applied
     * @param opacity from 0-255;
     */
    public static Color applyOpacity(final Color color, final float opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) MathHandler.clamp(0, 255, opacity));
    }

    /**
     * Интерполяция цвета
     *
     * @param firstColor  первый цвет
     * @param secondColor второй цвет
     * @param amount      amount
     */
    public static Color interpolateColorC(Color firstColor, Color secondColor, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(MathHandler.lerp(firstColor.getRed(), secondColor.getRed(), amount),
                MathHandler.lerp(firstColor.getGreen(), secondColor.getGreen(), amount),
                MathHandler.lerp(firstColor.getBlue(), secondColor.getBlue(), amount),
                MathHandler.lerp(firstColor.getAlpha(), secondColor.getAlpha(), amount));
    }

    public static float[] getRGBAf(int color) {
        return new float[]{(color >> 16 & 0xFF) / 255.0f, (color >> 8 & 0xFF) / 255.0f, (color & 0xFF) / 255.0f, (color >> 24 & 0xFF) / 255.0f};
    }

    public static Color mixColors(final Color color1, final Color color2, final double percent) {
        final double inverse_percent = 1.0 - percent;
        final int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        final int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        final int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }
    public static Color withAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) MathHandler.clamp(0, 255, alpha));
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

}