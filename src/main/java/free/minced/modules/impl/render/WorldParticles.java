package free.minced.modules.impl.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.events.Event;
import free.minced.events.impl.player.PacketEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.math.MathHandler;
import free.minced.framework.color.ClientColors;

import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.events.impl.render.Render3DEvent;
import free.minced.systems.SharedClass;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static free.minced.framework.color.ColorHandler.injectAlpha;
import static free.minced.modules.impl.render.BlowParticles.ParticleBase.interpolatePos;
import static free.minced.primary.IAccess.BUILDER;


@ModuleDescriptor(name = "WorldParticles", category = ModuleCategory.RENDER)
public class WorldParticles extends Module {
    private final BooleanSetting fireFliesBool = new BooleanSetting("Flies", this, true);
    private final BooleanSetting particlesBool = new BooleanSetting("Particles", this, true);

    private final BooleanSetting allowMove = new BooleanSetting("Allow Particles Move", this, false, () -> !particlesBool.isEnabled());

    public final NumberSetting fliesSize = new NumberSetting("Flies Size", this,  1, 0.1F, 6, 0.1, () -> !fireFliesBool.isEnabled());
    public final NumberSetting fliesCount = new NumberSetting("Flies Count", this, 4, 4, 800, 1, () -> !fireFliesBool.isEnabled());


    public final NumberSetting particlesSize = new NumberSetting("Particles Size", this, 1, 0.1F, 6, 0.1, () -> !particlesBool.isEnabled());
    public final NumberSetting particlesCount = new NumberSetting("Particles Count", this, 4, 4, 800, 1, () -> !particlesBool.isEnabled());

    private final ArrayList<ParticleBase> fireFlies = new ArrayList<>();
    private final ArrayList<ParticleBase> particles = new ArrayList<>();



    @Override
    public void onEvent(Event e) {
        if (e instanceof PacketEvent.Receive event) {
            if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
                float prevSizeValue1 = fliesSize.getValue().floatValue();
                fliesCount.setValue(prevSizeValue1 - 0.0001f);
                fliesCount.setValue(prevSizeValue1);

                float prevSizeValue2 = particlesSize.getValue().floatValue();
                particlesSize.setValue(prevSizeValue2 - 0.0001f);
                particlesSize.setValue(prevSizeValue2);
            }
        }

        if (e instanceof UpdatePlayerEvent event) {
            fireFlies.removeIf(ParticleBase::tick);

            particles.removeIf(ParticleBase::tick);

            if (fireFliesBool.isEnabled()) {
                for (int i = fireFlies.size(); i < fliesCount.getValue().intValue(); i++) {
                    fireFlies.add(new FireFly(
                            (float) (mc.player.getX() + MathHandler.randomize(-25f, 25f)),
                            (float) (mc.player.getY() + MathHandler.randomize(2f, 15f)),
                            (float) (mc.player.getZ() + MathHandler.randomize(-25f, 25f)),
                            MathHandler.randomize(-0.2f, 0.2f),
                            MathHandler.randomize(-0.1f, 0.1f),
                            MathHandler.randomize(-0.2f, 0.2f)));
                }
            }
            if (particlesBool.isEnabled()) {
                for (int j = particles.size(); j < particlesCount.getValue().intValue(); j++) {
                    boolean drop = allowMove.isEnabled();

                    particles.add(new ParticleBase(
                            (float) (mc.player.getX() + MathHandler.randomize(-48f, 48f)),
                            (float) (mc.player.getY() + MathHandler.randomize(2, 48f)),
                            (float) (mc.player.getZ() + MathHandler.randomize(-48f, 48f)),
                            drop ? 0 : MathHandler.randomize(-0.4f, 0.4f),
                            drop ? MathHandler.randomize(-0.2f, -0.05f) : MathHandler.randomize(-0.1f, 0.1f),
                            drop ? 0 : MathHandler.randomize(-0.4f, 0.4f)));
                }
            }
        }
        if (e instanceof Render3DEvent event) {
            if (fireFliesBool.isEnabled()) {
                event.getStack().push();
                RenderSystem.setShaderTexture(0, SharedClass.FIRE_FLIES_LOCATION);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
                RenderSystem.enableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
                BUILDER.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                fireFlies.forEach(p -> p.render(BUILDER));
                BufferRenderer.drawWithGlobalProgram(BUILDER.end());
                RenderSystem.depthMask(true);
                RenderSystem.disableDepthTest();
                RenderSystem.disableBlend();
                event.getStack().pop();
            }
            if (particlesBool.isEnabled()) {
                event.getStack().push();
                RenderSystem.setShaderTexture(0, SharedClass.STAR_LOCATION);
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
    }



    public static class Trail {
        private final Vec3d from;
        private final Vec3d to;
        private final Color color;
        private int ticks, prevTicks;

        public Trail(Vec3d from, Vec3d to, Color color) {
            this.from = from;
            this.to = to;
            this.ticks = 10;
            this.color = color;
        }

        public Vec3d interpolate(float pt) {
            double x = from.x + ((to.x - from.x) * pt) - mc.getEntityRenderDispatcher().camera.getPos().getX();
            double y = from.y + ((to.y - from.y) * pt) - mc.getEntityRenderDispatcher().camera.getPos().getY();
            double z = from.z + ((to.z - from.z) * pt) - mc.getEntityRenderDispatcher().camera.getPos().getZ();
            return new Vec3d(x, y, z);
        }

        public double animation(float pt) {
            return (this.prevTicks + (this.ticks - this.prevTicks) * pt) / 10.;
        }

        public boolean update() {
            this.prevTicks = this.ticks;
            return this.ticks-- <= 0;
        }

        public Color color() {
            return color;
        }
    }

    public class FireFly extends ParticleBase {
        private final List<Trail> trails = new ArrayList<>();


        public FireFly(float posX, float posY, float posZ, float motionX, float motionY, float motionZ) {
            super(posX, posY, posZ, motionX, motionY, motionZ);
        }

        @Override
        public boolean tick() {

            if (BlowParticles.distanceToSqr(posX, posY, posZ) > 100) age -= 4;
            else if (!mc.world.getBlockState(new BlockPos((int) posX, (int) posY, (int) posZ)).isAir()) age -= 8;
            else age--;

            if (age < 0)
                return true;

            trails.removeIf(Trail::update);

            prevposX = posX;
            prevposY = posY;
            prevposZ = posZ;

            posX += motionX;
            posY += motionY;
            posZ += motionZ;

            trails.add(new Trail(new Vec3d(prevposX, prevposY, prevposZ), new Vec3d(posX, posY, posZ), ClientColors.getSecondColor()));

            motionX *= 0.99f;
            motionY *= 0.99f;
            motionZ *= 0.99f;

            return false;
        }

        @Override
        public void render(BufferBuilder bufferBuilder) {
            RenderSystem.setShaderTexture(0, SharedClass.FIRE_FLIES_LOCATION);
            if (!trails.isEmpty()) {
                Camera camera = mc.gameRenderer.getCamera();
                for (Trail ctx : trails) {
                    Vec3d pos = ctx.interpolate(1f);
                    MatrixStack matrices = new MatrixStack();
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
                    matrices.translate(pos.x, pos.y, pos.z);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                    Matrix4f matrix = matrices.peek().getPositionMatrix();

                    bufferBuilder.vertex(matrix, 0, -fliesSize.getValue().intValue(), 0).texture(0f, 1f).color(injectAlpha(ctx.color(), (int) (255 * ((float) age / (float) maxAge) * ctx.animation(mc.getTickDelta()))).getRGB()).next();
                    bufferBuilder.vertex(matrix, -fliesSize.getValue().intValue(), -fliesSize.getValue().intValue(), 0).texture(1f, 1f).color(injectAlpha(ctx.color(), (int) (255 * ((float) age / (float) maxAge) * ctx.animation(mc.getTickDelta()))).getRGB()).next();
                    bufferBuilder.vertex(matrix, -fliesSize.getValue().intValue(), 0, 0).texture(1f, 0).color(injectAlpha(ctx.color(), (int) (255 * ((float) age / (float) maxAge) * ctx.animation(mc.getTickDelta()))).getRGB()).next();
                    bufferBuilder.vertex(matrix, 0, 0, 0).texture(0, 0).color(injectAlpha(ctx.color(), (int) (255 * ((float) age / (float) maxAge) * ctx.animation(mc.getTickDelta()))).getRGB()).next();
                }
            }
        }
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
            if (BlowParticles.distanceToSqr(posX, posY, posZ) > 4096) age -= 8;
            else age--;

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

            motionY -= 0.001f;

            return false;
        }
        public void render(BufferBuilder bufferBuilder) {
            RenderSystem.setShaderTexture(0, SharedClass.STAR_LOCATION);

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

            bufferBuilder.vertex(matrix1, 0, -particlesSize.getValue().floatValue(), 0).texture(0f, 1f).color(injectAlpha(color1, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
            bufferBuilder.vertex(matrix1, -particlesSize.getValue().floatValue(), -particlesSize.getValue().floatValue(), 0).texture(1f, 1f).color(injectAlpha(color2, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
            bufferBuilder.vertex(matrix1, -particlesSize.getValue().floatValue(), 0, 0).texture(1f, 0).color(injectAlpha(color1, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
            bufferBuilder.vertex(matrix1, 0, 0, 0).texture(0, 0).color(injectAlpha(color2, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
        }

    }
}
