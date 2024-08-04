package free.minced.framework.render;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.Minced;
import free.minced.framework.color.ClientColors;
import free.minced.framework.color.ColorHandler;
import free.minced.framework.font.Fonts;
import free.minced.systems.SharedClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import free.minced.framework.font.Texture;
import free.minced.framework.render.shaders.ShaderHandler;
import free.minced.primary.IHolder;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static free.minced.primary.IAccess.BUILDER;
import static free.minced.primary.IAccess.TESSELLATOR;


public class DrawHandler implements IHolder {
    public static final HashMap<Integer, BlurredShadow> shadowCache = new HashMap<>();

    public static final List<FillAction> FILLED_QUEUE = new ArrayList<>();
    public static final List<OutlineAction> OUTLINE_QUEUE = new ArrayList<>();
    public static final List<FadeAction> FADE_QUEUE = new ArrayList<>();
    public static final List<FillSideAction> FILLED_SIDE_QUEUE = new ArrayList<>();
    public static final List<OutlineSideAction> OUTLINE_SIDE_QUEUE = new ArrayList<>();
    public static final List<DebugLineAction> DEBUG_LINE_QUEUE = new ArrayList<>();
    public static final List<LineAction> LINE_QUEUE = new ArrayList<>();

    public static final Matrix4f lastProjMat = new Matrix4f();
    public static final Matrix4f lastModMat = new Matrix4f();
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();
    private static float prevCircleStep;
    private static float circleStep;

    public static void onRender3D(MatrixStack stack) {
        if (!FILLED_QUEUE.isEmpty() || !FADE_QUEUE.isEmpty() || !FILLED_SIDE_QUEUE.isEmpty()) {

            RenderSystem.disableDepthTest();
            setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            BUILDER.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            FILLED_QUEUE.forEach(action -> setFilledBoxVertexes(BUILDER, stack.peek().getPositionMatrix(), action.box(), action.color()));

            FADE_QUEUE.forEach(action -> setFilledFadePoints(action.box(), BUILDER, stack.peek().getPositionMatrix(), action.color(), action.color2()));

            FILLED_SIDE_QUEUE.forEach(action -> setFilledSidePoints(BUILDER, stack.peek().getPositionMatrix(), action.box, action.color(), action.side()));

            TESSELLATOR.draw();
            endRender();
            RenderSystem.enableDepthTest();

            FADE_QUEUE.clear();
            FILLED_SIDE_QUEUE.clear();
            FILLED_QUEUE.clear();
        }

        if (!OUTLINE_QUEUE.isEmpty() || !OUTLINE_SIDE_QUEUE.isEmpty()) {
            setupRender();

            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            BUILDER.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

            RenderSystem.lineWidth(2f);

            OUTLINE_QUEUE.forEach(action -> setOutlinePoints(action.box(), matrixFrom(action.box().minX, action.box().minY, action.box().minZ), BUILDER, action.color()));

            OUTLINE_SIDE_QUEUE.forEach(action -> setSideOutlinePoints(action.box, matrixFrom(action.box().minX, action.box().minY, action.box().minZ), BUILDER, action.color(), action.side()));

            TESSELLATOR.draw();
            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
            endRender();
            OUTLINE_QUEUE.clear();
            OUTLINE_SIDE_QUEUE.clear();
        }

        if (!DEBUG_LINE_QUEUE.isEmpty()) {
            setupRender();
            RenderSystem.disableDepthTest();

            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            BUILDER.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.LINES);
            DEBUG_LINE_QUEUE.forEach(action -> {
                MatrixStack matrices = matrixFrom(action.start.getX(), action.start.getY(), action.start.getZ());
                vertexLine(matrices, BUILDER, 0f, 0f, 0f, (float) (action.end.getX() - action.start.getX()), (float) (action.end.getY() - action.start.getY()), (float) (action.end.getZ() - action.start.getZ()), action.color);
            });
            TESSELLATOR.draw();
            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
            endRender();
            DEBUG_LINE_QUEUE.clear();
        }

        if (!LINE_QUEUE.isEmpty()) {
            setupRender();
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            RenderSystem.lineWidth(2f);
            RenderSystem.disableDepthTest();
            BUILDER.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
            LINE_QUEUE.forEach(action -> {
                MatrixStack matrices = matrixFrom(action.start.getX(), action.start.getY(), action.start.getZ());
                vertexLine(matrices, BUILDER, 0f, 0f, 0f, (float) (action.end.getX() - action.start.getX()), (float) (action.end.getY() - action.start.getY()), (float) (action.end.getZ() - action.start.getZ()), action.color);
            });
            TESSELLATOR.draw();
            RenderSystem.enableCull();
            RenderSystem.lineWidth(1f);
            RenderSystem.enableDepthTest();
            endRender();
            LINE_QUEUE.clear();
        }
    }
    public static void setOutlinePoints(Box box, MatrixStack matrices, BufferBuilder buffer, Color color) {
        box = box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate());

        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        vertexLine(matrices, buffer, x1, y1, z1, x2, y1, z1, color);
        vertexLine(matrices, buffer, x2, y1, z1, x2, y1, z2, color);
        vertexLine(matrices, buffer, x2, y1, z2, x1, y1, z2, color);
        vertexLine(matrices, buffer, x1, y1, z2, x1, y1, z1, color);
        vertexLine(matrices, buffer, x1, y1, z2, x1, y2, z2, color);
        vertexLine(matrices, buffer, x1, y1, z1, x1, y2, z1, color);
        vertexLine(matrices, buffer, x2, y1, z2, x2, y2, z2, color);
        vertexLine(matrices, buffer, x2, y1, z1, x2, y2, z1, color);
        vertexLine(matrices, buffer, x1, y2, z1, x2, y2, z1, color);
        vertexLine(matrices, buffer, x2, y2, z1, x2, y2, z2, color);
        vertexLine(matrices, buffer, x2, y2, z2, x1, y2, z2, color);
        vertexLine(matrices, buffer, x1, y2, z2, x1, y2, z1, color);
    }
    public static void drawLine(@NotNull Vec3d start, @NotNull Vec3d end, @NotNull Color color) {
        LINE_QUEUE.add(new LineAction(start, end, color));
    }

    public static void setSideOutlinePoints(Box box, MatrixStack matrices, BufferBuilder buffer, Color color, Direction dir) {
        box = box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate());

        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        switch (dir) {
            case UP -> {
                vertexLine(matrices, buffer, x1, y2, z1, x2, y2, z1, color);
                vertexLine(matrices, buffer, x2, y2, z1, x2, y2, z2, color);
                vertexLine(matrices, buffer, x2, y2, z2, x1, y2, z2, color);
                vertexLine(matrices, buffer, x1, y2, z2, x1, y2, z1, color);
            }
            case DOWN -> {
                vertexLine(matrices, buffer, x1, y1, z1, x2, y1, z1, color);
                vertexLine(matrices, buffer, x2, y1, z1, x2, y1, z2, color);
                vertexLine(matrices, buffer, x2, y1, z2, x1, y1, z2, color);
                vertexLine(matrices, buffer, x1, y1, z2, x1, y1, z1, color);
            }
            case EAST -> {
                vertexLine(matrices, buffer, x2, y1, z1, x2, y2, z1, color);
                vertexLine(matrices, buffer, x2, y1, z2, x2, y2, z2, color);
                vertexLine(matrices, buffer, x2, y2, z2, x2, y2, z1, color);
                vertexLine(matrices, buffer, x2, y1, z2, x2, y1, z1, color);
            }
            case WEST -> {
                vertexLine(matrices, buffer, x1, y1, z1, x1, y2, z1, color);
                vertexLine(matrices, buffer, x1, y1, z2, x1, y2, z2, color);
                vertexLine(matrices, buffer, x1, y2, z2, x1, y2, z1, color);
                vertexLine(matrices, buffer, x1, y1, z2, x1, y1, z1, color);
            }
            case NORTH -> {
                vertexLine(matrices, buffer, x2, y1, z1, x2, y2, z1, color);
                vertexLine(matrices, buffer, x1, y1, z1, x1, y2, z1, color);
                vertexLine(matrices, buffer, x2, y1, z1, x1, y1, z1, color);
                vertexLine(matrices, buffer, x2, y2, z1, x1, y2, z1, color);
            }
            case SOUTH -> {
                vertexLine(matrices, buffer, x1, y1, z2, x1, y2, z2, color);
                vertexLine(matrices, buffer, x2, y1, z2, x2, y2, z2, color);
                vertexLine(matrices, buffer, x1, y1, z2, x2, y1, z2, color);
                vertexLine(matrices, buffer, x1, y2, z2, x2, y2, z2, color);
            }
        }
    }

    public static void setFilledSidePoints(BufferBuilder buffer, Matrix4f matrix, Box box, Color c, Direction dir) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        if (dir == Direction.DOWN) {
            buffer.vertex(matrix, minX, minY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, minY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, minY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, minY, maxZ).color(c.getRGB()).next();
        }

        if (dir == Direction.NORTH) {
            buffer.vertex(matrix, minX, minY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, maxY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, maxY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, minY, minZ).color(c.getRGB()).next();
        }

        if (dir == Direction.EAST) {
            buffer.vertex(matrix, maxX, minY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, maxY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, maxY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, minY, maxZ).color(c.getRGB()).next();
        }
        if (dir == Direction.SOUTH) {
            buffer.vertex(matrix, minX, minY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, minY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, maxY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, maxY, maxZ).color(c.getRGB()).next();
        }

        if (dir == Direction.WEST) {
            buffer.vertex(matrix, minX, minY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, minY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, maxY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, maxY, minZ).color(c.getRGB()).next();
        }

        if (dir == Direction.UP) {
            buffer.vertex(matrix, minX, maxY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, maxY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, maxY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, maxY, minZ).color(c.getRGB()).next();
        }
    }


    public static void setFilledBoxVertexes(@NotNull BufferBuilder bufferBuilder, Matrix4f m, @NotNull Box box, @NotNull Color c) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        bufferBuilder.vertex(m, minX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, minY, maxZ).color(c.getRGB()).next();

        bufferBuilder.vertex(m, minX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, minY, minZ).color(c.getRGB()).next();

        bufferBuilder.vertex(m, maxX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, minY, maxZ).color(c.getRGB()).next();

        bufferBuilder.vertex(m, minX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, maxY, maxZ).color(c.getRGB()).next();

        bufferBuilder.vertex(m, minX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, maxY, minZ).color(c.getRGB()).next();

        bufferBuilder.vertex(m, minX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, maxY, minZ).color(c.getRGB()).next();
    }

    public static void setFilledFadePoints(Box box, BufferBuilder buffer, Matrix4f posMatrix, Color c, Color c1) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        RenderSystem.enableCull();

        buffer.vertex(posMatrix, minX, minY, minZ).color(c.getRGB()).next();
        buffer.vertex(posMatrix, minX, maxY, minZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, maxX, maxY, minZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, maxX, minY, minZ).color(c.getRGB()).next();

        buffer.vertex(posMatrix, maxX, minY, minZ).color(c.getRGB()).next();
        buffer.vertex(posMatrix, maxX, maxY, minZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, maxX, maxY, maxZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, maxX, minY, maxZ).color(c.getRGB()).next();

        buffer.vertex(posMatrix, minX, minY, maxZ).color(c.getRGB()).next();
        buffer.vertex(posMatrix, maxX, minY, maxZ).color(c.getRGB()).next();
        buffer.vertex(posMatrix, maxX, maxY, maxZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, minX, maxY, maxZ).color(c1.getRGB()).next();

        buffer.vertex(posMatrix, minX, minY, minZ).color(c.getRGB()).next();
        buffer.vertex(posMatrix, minX, minY, maxZ).color(c.getRGB()).next();
        buffer.vertex(posMatrix, minX, maxY, maxZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, minX, maxY, minZ).color(c1.getRGB()).next();

        buffer.vertex(posMatrix, minX, maxY, minZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, minX, maxY, maxZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, maxX, maxY, maxZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, maxX, maxY, minZ).color(c1.getRGB()).next();

        RenderSystem.disableCull();
    }

    public static void vertexLine(@NotNull MatrixStack matrices, @NotNull VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, @NotNull Color lineColor) {
        Matrix4f model = matrices.peek().getPositionMatrix();
        Matrix3f normal = matrices.peek().getNormalMatrix();
        Vector3f normalVec = getNormal(x1, y1, z1, x2, y2, z2);
        buffer.vertex(model, x1, y1, z1).color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha()).normal(normal, normalVec.x(), normalVec.y(), normalVec.z()).next();
        buffer.vertex(model, x2, y2, z2).color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha()).normal(normal, normalVec.x(), normalVec.y(), normalVec.z()).next();
    }

    public static @NotNull Vector3f getNormal(float x1, float y1, float z1, float x2, float y2, float z2) {
        float xNormal = x2 - x1;
        float yNormal = y2 - y1;
        float zNormal = z2 - z1;
        float normalSqrt = MathHelper.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal);

        return new Vector3f(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt);
    }
    public static @NotNull MatrixStack matrixFrom(double x, double y, double z) {
        MatrixStack matrices = new MatrixStack();

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));

        matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

        return matrices;
    }
    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static void endRender() {
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }
    public static double getScaleFactor() {
        return mc.getWindow().getScaleFactor();
    }
    public static double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }
    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return (float) interpolate(oldValue, newValue, (float) interpolationValue);
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, Color c) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BUILDER.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        BUILDER.vertex(matrix, x, y + height, 0.0F).color(c.getRGB()).next();
        BUILDER.vertex(matrix, x + width, y + height, 0.0F).color(c.getRGB()).next();
        BUILDER.vertex(matrix, x + width, y, 0.0F).color(c.getRGB()).next();
        BUILDER.vertex(matrix, x, y, 0.0F).color(c.getRGB()).next();
        TESSELLATOR.draw();
        endRender();
    }

    public static void drawGPS(DrawContext poseStack) {

        if (SharedClass.GPS_POSITION != null) {
            float dst = getDistance(SharedClass.GPS_POSITION);
            float xOffset = mc.getWindow().getScaledWidth() / 2f;
            float yOffset = mc.getWindow().getScaledHeight() / 2f;
            float yaw = getRotations(new Vec2f(SharedClass.GPS_POSITION.getX(), SharedClass.GPS_POSITION.getZ())) - mc.player.getYaw();
            poseStack.getMatrices().translate(xOffset, yOffset, 0.0F);
            poseStack.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(yaw));
            poseStack.getMatrices().translate(-xOffset, -yOffset, 0.0F);
            drawTracerPointer(poseStack, xOffset, yOffset - 50, 0.5F, Minced.getInstance().getThemeHandler().getTheme().getFirstColor().getRGB());
            poseStack.getMatrices().translate(xOffset, yOffset, 0.0F);
            poseStack.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-yaw));
            poseStack.getMatrices().translate(-xOffset, -yOffset, 0.0F);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            Fonts.SEMI_15.drawCenteredString(poseStack.getMatrices(), "GPS (" + (int) dst + " blocks)", (float) (Math.sin(Math.toRadians(yaw)) * 50f) + xOffset, (float) (yOffset - (Math.cos(Math.toRadians(yaw)) * 50f)) - 23, -1);

            if (dst < 10)
                SharedClass.GPS_POSITION = null;
        }
    }
    public static void drawTracerPointer(DrawContext matrices, float x, float y, float size, int color) {
        drawArrow(matrices, x, y, size + 8, new Color(color));
    }
    public static double absSinAnimation(double input) {
        return Math.abs(1 + Math.sin(input)) / 2;
    }
    public static void drawTargetEsp(MatrixStack stack, Entity target) {
        double cs = prevCircleStep + (circleStep - prevCircleStep) * mc.getTickDelta();
        double prevSinAnim = absSinAnimation(cs - 0.45f);
        double sinAnim = absSinAnimation(cs);
        double x = target.prevX + (target.getX() - target.prevX) * mc.getTickDelta() - mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = target.prevY + (target.getY() - target.prevY) * mc.getTickDelta() - mc.getEntityRenderDispatcher().camera.getPos().getY() + prevSinAnim * target.getHeight();
        double z = target.prevZ + (target.getZ() - target.prevZ) * mc.getTickDelta() - mc.getEntityRenderDispatcher().camera.getPos().getZ();
        double nextY = target.prevY + (target.getY() - target.prevY) * mc.getTickDelta() - mc.getEntityRenderDispatcher().camera.getPos().getY() + sinAnim * target.getHeight();
        stack.push();
        setupRender();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BUILDER.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        float cos;
        float sin;
        for (int i = 0; i <= 30; i++) {
            cos = (float) (x + Math.cos(i * 6.28 / 30) * target.getWidth() * 0.8);
            sin = (float) (z + Math.sin(i * 6.28 / 30) * target.getWidth() * 0.8);

            BUILDER.vertex(stack.peek().getPositionMatrix(), cos, (float) nextY, sin).color(ColorHandler.applyOpacity(ClientColors.getTheme().getAccentColor().brighter(), 170).getRGB()).next();
            BUILDER.vertex(stack.peek().getPositionMatrix(), cos, (float) y, sin).color(ColorHandler.applyOpacity(ClientColors.getTheme().getAccentColor().darker(), 0).getRGB()).next();
        }
        TESSELLATOR.draw();
        RenderSystem.enableCull();
        endRender();
        RenderSystem.enableDepthTest();
        stack.pop();
    }
    public static void updateTargetESP() {
        prevCircleStep = circleStep;
        circleStep += 0.15f;
    }
    public static void drawArrow(DrawContext matrices, float x, float y, float size1, Color color) {
        RenderSystem.setShaderTexture(0, SharedClass.ARROW_LOCATION);
        setupRender();
        RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        RenderSystem.disableDepthTest();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(BUILDER);
        Matrix4f matrix = matrices.getMatrices().peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BUILDER.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        float size = size1 + 8;
        BUILDER.vertex(matrix, x - (size / 2f), y + size, 0).texture(0f, 1f).next();
        BUILDER.vertex(matrix, x + size / 2f, y + size, 0).texture(1f, 1f).next();
        BUILDER.vertex(matrix, x + size / 2f, y, 0).texture(1f, 0).next();
        BUILDER.vertex(matrix, x - (size / 2f), y, 0).texture(0, 0).next();
        BufferRenderer.drawWithGlobalProgram(BUILDER.end());
        immediate.draw();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        endRender();
    }
    public static float getRotations(Vec2f vec) {
        if (mc.player == null) return 0;
        double x = vec.x - mc.player.getPos().x;
        double z = vec.y - mc.player.getPos().z;
        return (float) -(Math.atan2(x, z) * (180 / Math.PI));
    }

    public static int getDistance(BlockPos bp) {
        double d0 = mc.player.getX() - bp.getX();
        double d2 = mc.player.getZ() - bp.getZ();
        return (int) (MathHelper.sqrt((float) (d0 * d0 + d2 * d2)));
    }
    public static void drawRound(MatrixStack matrices, float x, float y, float width, float height, float radius, Color color) {
        ShaderHandler.drawRound(matrices, x, y, width, height, radius, color);
    }
    public static void drawRoundGradient(MatrixStack matrices, float x, float y, float width, float height, float radius, Color c1, Color c2) {
        ShaderHandler.drawRoundGradient(matrices, x, y, width, height, radius, c1, c2);
    }

    public static void drawRoundGradient(MatrixStack matrices, float x, float y, float width, float height, float radius, Color c1, Color c2, Color c3, Color c4) {
        ShaderHandler.drawRoundGradient(matrices, x, y, width, height, radius, c1, c2, c3, c4);
    }


    public static void drawBlurredShadow(MatrixStack matrices, float x, float y, float width, float height, int blurRadius, Color color) {
        //if (!HudEditor.glow.getValue()) return;
        width = width + blurRadius * 2;
        height = height + blurRadius * 2;
        x = x - blurRadius;
        y = y - blurRadius;

        int identifier = (int) (width * height + width * blurRadius);
        if (shadowCache.containsKey(identifier)) {
            shadowCache.get(identifier).bind();
        } else {
            BufferedImage original = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = original.getGraphics();
            g.setColor(new Color(-1));
            g.fillRect(blurRadius, blurRadius, (int) (width - blurRadius * 2), (int) (height - blurRadius * 2));
            g.dispose();
            GaussianFilter op = new GaussianFilter(blurRadius);
            BufferedImage blurred = op.filter(original, null);
            shadowCache.put(identifier, new BlurredShadow(blurred));
            return;
        }

        setupRender();
        RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        renderTexture(matrices, x, y, width, height, 0, 0, width, height, width, height);
        endRender();
    }

    public static void renderTexture(MatrixStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight) {
        double x1 = x0 + width;
        double y1 = y0 + height;
        double z = 0;
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BUILDER.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        BUILDER.vertex(matrix, (float) x0, (float) y1, (float) z).texture((u) / (float) textureWidth, (v + (float) regionHeight) / (float) textureHeight).next();
        BUILDER.vertex(matrix, (float) x1, (float) y1, (float) z).texture((u + (float) regionWidth) / (float) textureWidth, (v + (float) regionHeight) / (float) textureHeight).next();
        BUILDER.vertex(matrix, (float) x1, (float) y0, (float) z).texture((u + (float) regionWidth) / (float) textureWidth, (v) / (float) textureHeight).next();
        BUILDER.vertex(matrix, (float) x0, (float) y0, (float) z).texture((u) / (float) textureWidth, (v + 0.0F) / (float) textureHeight).next();
        BufferRenderer.drawWithGlobalProgram(BUILDER.end());
    }

    public static void registerBufferedImageTexture(Texture i, BufferedImage bi) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", baos);
            byte[] bytes = baos.toByteArray();
            registerTexture(i, bytes);
        } catch (Exception ignored) {
        }
    }
    public static void registerTexture(Texture i, byte[] content) {
        try {
            ByteBuffer data = BufferUtils.createByteBuffer(content.length).put(content);
            data.flip();
            NativeImageBackedTexture tex = new NativeImageBackedTexture(NativeImage.read(data));
            mc.execute(() -> mc.getTextureManager().registerTexture(i, tex));
        } catch (Exception ignored) {
        }
    }

    public static void verticalGradient(MatrixStack pPoseStack, float x, float y, float width, float height, Color startColor, Color endColor) {
        drawRoundGradient(pPoseStack, x, y, width, height, 1, endColor, startColor, endColor, startColor);


    }
    public static void horizontalGradient(MatrixStack pPoseStack, float x, float y, float width, float height, Color startColor, Color endColor) {

        drawRoundGradient(pPoseStack, x, y, width, height, 1, startColor, startColor, endColor, endColor);
    }

    public static @NotNull Vec3d projectCoordinates(@NotNull Vec3d pos) {
        Camera camera = mc.getEntityRenderDispatcher().camera;
        if (camera == null) return new Vec3d(0, 0, 0);
        int displayHeight = mc.getWindow().getHeight();
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        Vector3f target = new Vector3f();

        double deltaX = pos.x - camera.getPos().x;
        double deltaY = pos.y - camera.getPos().y;
        double deltaZ = pos.z - camera.getPos().z;

        Vector4f transformedCoordinates = new Vector4f((float) deltaX, (float) deltaY, (float) deltaZ, 1.f).mul(lastWorldSpaceMatrix);
        Matrix4f matrixProj = new Matrix4f(lastProjMat);
        Matrix4f matrixModel = new Matrix4f(lastModMat);
        matrixProj.mul(matrixModel).project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport, target);

        return new Vec3d(target.x / mc.getWindow().getScaleFactor(), (displayHeight - target.y) / mc.getWindow().getScaleFactor(), target.z);
    }
    public static void drawLineDebug(Vec3d start, Vec3d end, Color color) {
        DEBUG_LINE_QUEUE.add(new DebugLineAction(start, end, color));
    }

    public record FillAction(Box box, Color color) {
    }

    public record OutlineAction(Box box, Color color, float lineWidth) {
    }

    public record FadeAction(Box box, Color color, Color color2) {
    }

    public record FillSideAction(Box box, Color color, Direction side) {
    }

    public record OutlineSideAction(Box box, Color color, float lineWidth, Direction side) {
    }

    public record DebugLineAction(Vec3d start, Vec3d end, Color color) {
    }

    public record LineAction(Vec3d start, Vec3d end, Color color) {
    }

    public static class BlurredShadow {
        final Texture id;

        public BlurredShadow(BufferedImage bufferedImage) {
            this.id = new Texture("texture/remote/" + RandomStringUtils.randomAlphanumeric(16));
            registerBufferedImageTexture(id, bufferedImage);
        }

        public void bind() {
            RenderSystem.setShaderTexture(0, id);
        }
    }

}
