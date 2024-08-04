package free.minced.framework.render.shaders;


import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import free.minced.framework.render.shaders.impl.RoundedShader;
import org.joml.Matrix4f;

import java.awt.*;

import static free.minced.framework.render.DrawHandler.endRender;
import static free.minced.framework.render.DrawHandler.setupRender;
import static free.minced.primary.IAccess.BUILDER;
import static free.minced.primary.IAccess.TESSELLATOR;



public class ShaderHandler {
    public static RoundedShader ROUNDED_SHADER;

    public static void initShaders() {
        System.out.println("initializating shaders");
        ROUNDED_SHADER = new RoundedShader();
    }
    public static void preShaderDraw(MatrixStack matrices, float x, float y, float width, float height) {
        setupRender();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BUILDER.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        setRectanglePoints(BUILDER, matrix, x, y, x + width, y + height);
    }
    public static void setRectanglePoints(BufferBuilder buffer, Matrix4f matrix, float x, float y, float x1, float y1) {
        buffer.vertex(matrix, x, y, 0).next();
        buffer.vertex(matrix, x, y1, 0).next();
        buffer.vertex(matrix, x1, y1, 0).next();
        buffer.vertex(matrix, x1, y, 0).next();
    }
    public static void drawRoundGradient(MatrixStack matrices, float x, float y, float width, float height, float radius, Color c1, Color c2) {
        preShaderDraw(matrices, x - 10, y - 10, width + 20, height + 20);
        ROUNDED_SHADER.setParameters(x, y, width, height, radius, c1, c2);
        ROUNDED_SHADER.use();
        TESSELLATOR.draw();
        endRender();
    }
    public static void drawRoundGradient(MatrixStack matrices, float x, float y, float width, float height, float radius, Color c1, Color c2, Color c3, Color c4) {
        preShaderDraw(matrices, x - 10, y - 10, width + 20, height + 20);
        ROUNDED_SHADER.setParameters(x, y, width, height, radius, c1, c2, c3, c4);
        ROUNDED_SHADER.use();
        TESSELLATOR.draw();
        endRender();
    }
    public static void drawRound(MatrixStack matrices, float x, float y, float width, float height, float radius, Color c1) {
        preShaderDraw(matrices, x - 10, y - 10, width + 20, height + 20);
        ROUNDED_SHADER.setParameters(x, y, width, height, radius, c1);
        ROUNDED_SHADER.use();
        TESSELLATOR.draw();
        endRender();
    }

}
