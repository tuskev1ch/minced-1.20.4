package free.minced.modules.impl.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.events.Event;
import free.minced.events.impl.input.binds.InputEvent;
import free.minced.events.impl.mobility.EventJumpAxis;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.events.impl.render.Render3DEvent;
import free.minced.framework.color.ClientColors;
import free.minced.framework.particle.CustomParticle;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ModuleDescriptor(name = "Jump Effect", category = ModuleCategory.RENDER)
public class JumpEffect extends Module {

    private final List<CustomParticle> particles = new ArrayList<>();

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventJumpAxis e) {
            if (mc.player == null) return;

            Vec3d particlePos = mc.player.getBlockPos().toCenterPos().add(0, 0.5F, 0);
            Vec3d particleVelocity = new Vec3d(0.03, 0, 0.03);
            Identifier particleTexture = new Identifier("minced","textures/bloom.png");

            CustomParticle particle = new CustomParticle(particlePos, particleVelocity, particleTexture, ClientColors.getTheme().getSecondColor());
            particle.setSize(1);
            particles.add(particle);
        }
        if (event instanceof Render3DEvent e) {
            if (mc.player == null || mc.world == null || particles.isEmpty()) return;

            BufferBuilder builder = Tessellator.getInstance().getBuffer();

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);

            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            for (CustomParticle particle : particles) {
                particle.render(builder, mc.gameRenderer.getCamera());
            }

            BufferRenderer.drawWithGlobalProgram(builder.end());

            RenderSystem.depthMask(true);
            RenderSystem.disableDepthTest();

            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
        if (event instanceof UpdatePlayerEvent e) {
            particles.forEach(CustomParticle::tick);
            particles.removeIf(CustomParticle::shouldRemove);
        }
    }
}
