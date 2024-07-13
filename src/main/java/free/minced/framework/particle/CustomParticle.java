package free.minced.framework.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.framework.animation.base.AnimationHue;
import free.minced.framework.animation.base.EasingHue;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.color.CustomColor;
import free.minced.primary.IHolder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;

@Getter
@Setter
public class CustomParticle implements IHolder {

    private Vec3d position, prevPosition;
    private Vec3d velocity;
    private float size = 3.0F;
    private final Identifier texture;
    private int age;
    private int maxAge = 100;
    private double gravityStrength = 0.04;
    private boolean alive;
    private boolean collidesWithWorld;
    private final AnimationHue alphaAnimation;
    private final AnimationHue sizeAnimation;
    private final Color color;

    public CustomParticle(Vec3d position, Vec3d velocity, Identifier texture, Color color) {
        this.position = position;
        this.prevPosition = position;
        this.velocity = velocity;
        this.texture = texture;
        this.color = color;
        this.age = 0;
        long animationDuration = maxAge * 5;
        this.alphaAnimation = new AnimationHue(EasingHue.CUBIC_IN_OUT, animationDuration);
        this.sizeAnimation = new AnimationHue(EasingHue.LINEAR, animationDuration);
        this.alive = true;
    }

    public void tick() {
        // Increase age and check if the particle should die
        this.age++;
        if (this.age >= this.maxAge) {
            this.markDead();
        }

        this.prevPosition = position;

        // Update position based on velocity
        this.position = this.position.add(this.velocity);

        // Apply gravity
//        this.velocity = this.velocity.add(0, -gravityStrength, 0);
    }

    public void render(BufferBuilder builder, Camera camera) {
        alphaAnimation.update(isDead() ? 0 : 1);
        sizeAnimation.update(isDead() ? 0 : 1);

        float currentSize = size;
        Color renderColor = new CustomColor(color.getRGB()).withAlpha(255 * alphaAnimation.getValue());

        RenderSystem.setShaderTexture(0, texture);

        MatrixStack matrices = new MatrixStack();
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));

        Vec3d renderPos = getRenderPos(prevPosition, position);
        matrices.translate(renderPos.getX(), renderPos.getY(), renderPos.getZ());

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

        builder.vertex(matrix, 0, -currentSize, 0).texture(0.0F, 1.0F).color(renderColor.getRGB()).next();
        builder.vertex(matrix, -currentSize, -currentSize, 0).texture(1.0F, 1.0F).color(renderColor.getRGB()).next();
        builder.vertex(matrix, -currentSize, 0, 0).texture(1.0F, 0).color(renderColor.getRGB()).next();
        builder.vertex(matrix, 0, 0, 0).texture(0, 0).color(renderColor.getRGB()).next();
    }

    public void setPosition(double x, double y, double z) {
        this.position = new Vec3d(x, y, z);
    }

    public void setVelocity(double x, double y, double z) {
        this.velocity = new Vec3d(x, y, z);
    }

    private void markDead() {
        this.alive = false;
    }

    public boolean isDead() {
        return !alive;
    }

    public boolean shouldRemove() {
        return isDead() && alphaAnimation.getValue() == 0;
    }

    private Vec3d getRenderPos(Vec3d prevPosition, Vec3d position) {
        double x = prevPosition.getX() + ((position.getX() - prevPosition.getX()) * mc.getTickDelta()) - mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = prevPosition.getY() + ((position.getY() - prevPosition.getY()) * mc.getTickDelta()) - mc.getEntityRenderDispatcher().camera.getPos().getY();
        double z = prevPosition.getZ() + ((position.getZ() - prevPosition.getZ()) * mc.getTickDelta()) - mc.getEntityRenderDispatcher().camera.getPos().getZ();
        return new Vec3d(x, y, z);
    }
}
