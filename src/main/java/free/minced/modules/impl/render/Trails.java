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
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;

import static free.minced.framework.color.ColorHandler.injectAlpha;
import static free.minced.modules.impl.render.BlowParticles.ParticleBase.interpolatePos;
import static free.minced.primary.IAccess.BUILDER;

@ModuleDescriptor(name = "Trails", category = ModuleCategory.RENDER)
public class Trails extends Module {
    public final BooleanSetting Glow = new BooleanSetting("Glow", this, true);



    private final ArrayList<ParticleBase> particles = new ArrayList<>();

    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent event) {
            particles.removeIf(ParticleBase::tick);


            for (int j = 0; j < 15; j++) {
                float posX = 0;
                if (mc.player != null) {
                    posX = (float) (mc.player.getX());
                }
                float posY = (float) (mc.player.getY() + 0.19);
                float posZ = (float) (mc.player.getZ());


                particles.add(new ParticleBase(posX, posY, posZ,
                        MathHandler.randomize(-0.05f, 0.05f),
                        0,
                        MathHandler.randomize(-0.05f, 0.05f)));
            }
        }
        if (e instanceof Render3DEvent event) {
            event.getStack().push();
            RenderSystem.setShaderTexture(0, Glow.isEnabled() ? SharedClass.FIRE_FLIES_LOCATION : SharedClass.GLOW_LOCATION);
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

    public class ParticleBase {

        protected float prevposX;
        protected float prevposY;
        protected float prevposZ;
        protected float posX;
        protected float posY;
        protected float posZ;
        protected final float motionX;
        protected final float motionY;
        protected final float motionZ;
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
            age = 25;
            maxAge = 25;
        }

        public boolean tick() {
            age -= 2;

            if (age < 0)
                return true;

            prevposX = posX;
            prevposY = posY;
            prevposZ = posZ;

            posX += motionX;
            posY += motionY;
            posZ += motionZ;



            return false;
        }


        public void render(BufferBuilder bufferBuilder) {
            RenderSystem.setShaderTexture(0, Glow.isEnabled() ? SharedClass.FIRE_FLIES_LOCATION : SharedClass.GLOW_LOCATION);

            Camera camera = mc.gameRenderer.getCamera();
            Color color1 = ClientColors.getTheme().getSecondColor(); // поменять если че
            Color color2 = ClientColors.getTheme().getSecondColor(); // поменять если че

            Vec3d pos = interpolatePos(prevposX, prevposY, prevposZ, posX, posY, posZ);

            MatrixStack matrices = new MatrixStack();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
            matrices.translate(pos.x, pos.y, pos.z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            
            Matrix4f matrix1 = matrices.peek().getPositionMatrix();

            bufferBuilder.vertex(matrix1, 0, -0.25F, 0).texture(0f, 1f).color(injectAlpha(color2,255).getRGB()).next();
            bufferBuilder.vertex(matrix1, -0.25F, -0.25F, 0).texture(1f, 1f).color(injectAlpha(color1,255).getRGB()).next();
            bufferBuilder.vertex(matrix1, -0.25F, 0, 0).texture(1f, 0).color(injectAlpha(color2,255).getRGB()).next();
            bufferBuilder.vertex(matrix1, 0, 0, 0).texture(0, 0).color(injectAlpha(color1,255).getRGB()).next();
        }

    }
}
