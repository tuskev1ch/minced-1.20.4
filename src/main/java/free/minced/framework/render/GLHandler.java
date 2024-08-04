package free.minced.framework.render;

import net.minecraft.client.util.math.MatrixStack;

public class GLHandler {
    public static void scaleStart(MatrixStack poseStack, float x, float y, float scale) {
        poseStack.push();
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, 1);
        poseStack.translate(- x, - y, 0);
    }

    public static void scaleEnd(MatrixStack poseStack) {
        poseStack.pop();
    }
}
