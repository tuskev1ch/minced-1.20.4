package free.minced.framework.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.MatrixStack;

import static org.lwjgl.opengl.GL11.*;

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
