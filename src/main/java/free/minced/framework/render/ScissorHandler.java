package free.minced.framework.render;

import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.primary.AdjustedDisplay;
import free.minced.primary.IHolder;

public class ScissorHandler implements IHolder {
    public static void doScissor(double x, double y, double width, double height) {
        final double scale = AdjustedDisplay.getScaleFactor().doubleValue();

        y = sr.getScaledHeight().floatValue() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        RenderSystem.enableScissor((int) x, (int) (y - height), (int) width, (int) height);
    }
    public static void end() {
        RenderSystem.disableScissor();
    }
}
