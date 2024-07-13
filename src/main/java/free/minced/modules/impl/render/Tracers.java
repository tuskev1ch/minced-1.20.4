package free.minced.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.events.Event;
import free.minced.events.impl.render.Render2DEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.game.PlayerHandler;
import free.minced.primary.math.MathHandler;
import free.minced.systems.setting.impl.MultiBoxSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

@ModuleDescriptor(name = "Tracers", category = ModuleCategory.RENDER)
public class Tracers extends Module {

    private final MultiBoxSetting targets = new MultiBoxSetting("Targets", this, "Players", "Mobs", "Animals", "Invisibles");

    @Override
    public void onEvent(Event event) {
        if (event instanceof Render2DEvent e) {
            if (mc.player == null || mc.world == null) return;

            Vec3d playerPos = getPlayerViewPosition(mc.player, ((Render2DEvent) event).getPartialTicks());

            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof LivingEntity livingEntity && shouldRender(livingEntity)) {
                    Vec3d entityPos = MathHandler.getInterpolatedPosition(livingEntity, ((Render2DEvent) event).getPartialTicks());
                    drawLine(playerPos, entityPos);
                }
            }
        }
    }

    private Vec3d getPlayerViewPosition(Entity player, float tickDelta) {
        double x = player.prevX + (player.getX() - player.prevX) * tickDelta;
        double y = player.getEyeHeight(player.getPose()) + player.prevY + (player.getY() - player.prevY) * tickDelta;
        double z = player.prevZ + (player.getZ() - player.prevZ) * tickDelta;
        return new Vec3d(0, 0, 10) // wtf
                .rotateX(- (float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
                .rotateY(- (float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
                .add(x, y, z);
    }

    private void drawLine(Vec3d startPos, Vec3d endPos) {

        // настраиваем рендер
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(2.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.LINES);

        MatrixStack matrices = createMatrixFrom(startPos);
        addVertexLine(matrices, bufferBuilder, new Color(255,255,255), new Vec3d(0, 0, 0), endPos.subtract(startPos));

        // рисуем
        tessellator.draw();

        // заканчиваем рендер
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private @NotNull MatrixStack createMatrixFrom(Vec3d pos) {
        MatrixStack matrices = new MatrixStack();
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
        matrices.translate(pos.x - camera.getPos().x, pos.y - camera.getPos().y, pos.z - camera.getPos().z);
        return matrices;
    }

    private void addVertexLine(@NotNull MatrixStack matrices, @NotNull VertexConsumer buffer, Color color, Vec3d start, Vec3d end) {
        Matrix4f model = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();
        Vector3f normal = MathHandler.calculateNormal(start, end);

        Vec3d mid = start.add(end).multiply(0.5);

        // Start point - fully opaque
        buffer.vertex(model, (float) start.x, (float) start.y, (float) start.z)
                .color(color.getRed(), color.getGreen(), color.getBlue(), 255)
                .normal(normalMatrix, normal.x(), normal.y(), normal.z())
                .next();

        // Mid point - half-transparent
        buffer.vertex(model, (float) mid.x, (float) mid.y, (float) mid.z)
                .color(color.getRed(), color.getGreen(), color.getBlue(), 255)
                .normal(normalMatrix, normal.x(), normal.y(), normal.z())
                .next();

        // End point - fully transparent
        buffer.vertex(model, (float) end.x, (float) end.y, (float) end.z)
                .color(color.getRed(), color.getGreen(), color.getBlue(), 255)
                .normal(normalMatrix, normal.x(), normal.y(), normal.z())
                .next();
    }

    public boolean shouldRender(LivingEntity entity) {
        if (entity.isDead() || entity == mc.player) return false;

        if (entity instanceof PlayerEntity && targets.get("Players").isEnabled()) return true;
        if (entity instanceof MobEntity && targets.get("Mobs").isEnabled()) return true;
        if (entity instanceof AnimalEntity && targets.get("Animals").isEnabled()) return true;
        if (entity instanceof Monster && targets.get("Monsters").isEnabled()) return true;
        return entity.isInvisible() && targets.get("Invisibles").isEnabled();
    }

}
