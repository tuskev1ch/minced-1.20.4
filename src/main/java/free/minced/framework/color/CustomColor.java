package free.minced.framework.color;


import free.minced.primary.math.MathHandler;

import java.awt.*;

public class CustomColor extends Color {

    public CustomColor(int r, int g, int b) {
        super(r, g, b);
    }

    public CustomColor(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public CustomColor(float r, float g, float b) {
        super(r, g, b);
    }


    public CustomColor(int color) {
        super(color);
    }

    /**
     * Метод чтобы применять прозрачность к цвету
     *
     * @param alpha прозрачность (0-255)
     */

    public Color withAlpha(final float alpha) {
        return new Color(getRed(), getGreen(), getBlue(), (int) MathHandler.clamp(0, 255, alpha));
    }


    /**
     * Метод чтобы делать цвет темнее
     *
     * @param factor фактор затемнения (0-1)
     *               Чем ниже фактор тем цвет темнее типо
     */
    public CustomColor darker(float factor) {
        return new CustomColor(Math.max((int) (getRed() * factor), 0), Math.max((int) (getGreen() * factor), 0), Math.max((int) (getBlue() * factor), 0), getAlpha());
    }

    /**
     * Метод чтобы делать цвет светлее
     *
     * @param factor фактор яркости (???) (0-1)
     *               Чем ниже фактор тем цвет светлее
     */
    public CustomColor brighter(float factor) {
        int red = getRed();
        int green = getGreen();
        int blue = getBlue();
        int alpha = getAlpha();

        int i = (int) (1 / (1 - factor));
        if (red == 0 && green == 0 && blue == 0) {
            return new CustomColor(i, i, i, alpha);
        }

        if (red > 0 && red < i) red = i;
        if (green > 0 && green < i) green = i;
        if (blue > 0 && blue < i) blue = i;

        return new CustomColor(Math.min((int) (red / factor), 255),
                Math.min((int) (green / factor), 255),
                Math.min((int) (blue / factor), 255),
                alpha);
    }
}