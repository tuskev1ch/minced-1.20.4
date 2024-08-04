package free.minced.modules.impl.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.events.impl.render.Render3DEvent;
import free.minced.framework.color.ClientColors;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.math.MathHandler;
import free.minced.systems.SharedClass;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;

import static free.minced.primary.IAccess.BUILDER;

@ModuleDescriptor(name = "BlowParticles", category = ModuleCategory.RENDER)
public class BlowParticles extends Module {
    public final BooleanSetting Minced = new BooleanSetting("Minced", this, true);


    public final BooleanSetting ignoreSelf = new BooleanSetting("Ignore self", this, true);

    public final NumberSetting particlesRadius = new NumberSetting("Particles Radius", this, 0.4, 0.1F, 1, 0.1);
    public final NumberSetting particlesCount = new NumberSetting("Particles Count", this, 4, 4, 200, 1);

    private final ArrayList<ParticleBase> particles = new ArrayList<>();

    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent event) {
            particles.removeIf(ParticleBase::tick);
            for (PlayerEntity entity : mc.world.getPlayers()) {

                if (entity == mc.player && ignoreSelf.isEnabled()) continue;

                if (entity.hurtTime > 0) {
                    for (int j = 0; j < particlesCount.getValue().intValue(); j++) {
                        float posX = (float) (entity.getX());
                        float posY = (float) (entity.getY() + 1.5F);
                        float posZ = (float) (entity.getZ());

                        float motionX = MathHandler.randomize(-particlesRadius.getValue().floatValue(), particlesRadius.getValue().floatValue());
                        float motionY = -MathHandler.randomize(0.1f, 0.3f);
                        float motionZ = MathHandler.randomize(-particlesRadius.getValue().floatValue(), particlesRadius.getValue().floatValue());

                        particles.add(new ParticleBase(posX, posY, posZ, motionX, motionY, motionZ));
                    }

                }
            }
        }
        if (e instanceof Render3DEvent event) {
            event.getStack().push();
            RenderSystem.setShaderTexture(0, Minced.isEnabled() ? SharedClass.LOGO_LOCATION : SharedClass.GLOW_LOCATION);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            BUILDER.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            particles.forEach(p -> p.render(BUILDER));
            BufferRenderer.drawWithGlobalProgram(BUILDER.end());
            RenderSystem.depthMask(true);
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            event.getStack().pop();
        }
    }
    public static double distanceToSqr(double pX, double pY, double pZ) {
        double d0 = mc.player.getX() - pX;
        double d1 = mc.player.getY() - pY;
        double d2 = mc.player.getZ() - pZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }
    public class ParticleBase {

        protected float prevposX, prevposY, prevposZ, posX, posY, posZ, motionX, motionY, motionZ;
        protected int age;
        protected final int maxAge;

        public ParticleBase(float posX, float posY, float posZ, float motionX, float motionY, float motionZ) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            prevposX = posX;
            prevposY = posY;
            prevposZ = posZ;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            age = (int) MathHandler.randomize(100, 300);
            maxAge = age;
        }

        public boolean tick() {
            if (distanceToSqr(posX, posY, posZ) > 4096) age -= 8;
            else --age;

            if (age < 0)
                return true;

            prevposX = posX;
            prevposY = posY;
            prevposZ = posZ;

            posX += motionX;
            posY += motionY;
            posZ += motionZ;

            motionX *= 0.9f;
            motionY *= 0.9f;
            motionZ *= 0.9f;

            motionY -= 0.005f;

            return false;
        }

        public static Vec3d interpolatePos(float prevposX, float prevposY, float prevposZ, float posX, float posY, float posZ) {
            double x = prevposX + ((posX - prevposX) * mc.getTickDelta()) - mc.getEntityRenderDispatcher().camera.getPos().getX();
            double y = prevposY + ((posY - prevposY) * mc.getTickDelta()) - mc.getEntityRenderDispatcher().camera.getPos().getY();
            double z = prevposZ + ((posZ - prevposZ) * mc.getTickDelta()) - mc.getEntityRenderDispatcher().camera.getPos().getZ();
            return new Vec3d(x, y, z);
        }
        public static Color injectAlpha(final Color color, final int alpha) {
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(alpha, 0, 255));
        }
        public void render(BufferBuilder bufferBuilder) {
            RenderSystem.setShaderTexture(0, Minced.isEnabled() ? SharedClass.LOGO_LOCATION : SharedClass.GLOW_LOCATION);

            Camera camera = mc.gameRenderer.getCamera();
            Color color1 = ClientColors.getTheme().getAccentColor(0, 0); // поменять если че
            Color color2 = ClientColors.getTheme().getAccentColorReverse(0, 0); // поменять если че

            Vec3d pos = interpolatePos(prevposX, prevposY, prevposZ, posX, posY, posZ);

            MatrixStack matrices = new MatrixStack();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
            matrices.translate(pos.x, pos.y, pos.z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

            Matrix4f matrix1 = matrices.peek().getPositionMatrix();

            bufferBuilder.vertex(matrix1, 0, -0.25F, 0).texture(0f, 1f).color(injectAlpha(color2, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
            bufferBuilder.vertex(matrix1, -0.25F, -0.25F, 0).texture(1f, 1f).color(injectAlpha(color1, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
            bufferBuilder.vertex(matrix1, -0.25F, 0, 0).texture(1f, 0).color(injectAlpha(color2, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
            bufferBuilder.vertex(matrix1, 0, 0, 0).texture(0, 0).color(injectAlpha(color1, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
        }

    }
}
