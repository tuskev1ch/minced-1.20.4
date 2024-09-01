package free.minced.modules.impl.combat;


import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.player.EventSync;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.events.impl.render.Render3DEvent;
import free.minced.mixin.MixinEntityLiving;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.modules.impl.movement.Flight;
import free.minced.modules.impl.movement.Speed;
import free.minced.primary.chat.ChatHandler;
import free.minced.primary.game.PlayerHandler;
import free.minced.systems.helpers.IEntityLiving;
import free.minced.systems.helpers.IOtherClientPlayerEntity;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.NumberSetting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import static free.minced.framework.render.DrawHandler.endRender;
import static free.minced.framework.render.DrawHandler.setupRender;
import static free.minced.primary.IAccess.BUILDER;
import static free.minced.primary.IAccess.TESSELLATOR;

@ModuleDescriptor(name = "BackTrack", category = ModuleCategory.COMBAT)
public class BackTrack extends Module {
    public final BooleanSetting draw = new BooleanSetting("Draw", this, false);

    public final NumberSetting maxValue = new NumberSetting("Max Value", this, 5, 1, 10, 1);

    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent) {

            for (Entity player1 : mc.world.getEntities()) {
                if (player1 instanceof IEntityLiving player) {
                    if (player == mc.player) continue;
                    if (player.getBackTrack().size() > maxValue.getValue().intValue()) {
                        player.getBackTrack().remove(0);
                    }
                }
            }
        }

        if (e instanceof Render3DEvent event) {
            if (!draw.isEnabled()) return;
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof IEntityLiving player && Math.hypot(entity.getVelocity().x, entity.getVelocity().z) > 0) {
                    if (player == mc.player) continue;

                    for (Position position : ((IEntityLiving) entity).getBackTrack()) {
                        Vec3d pos = position.getPos();

                        double x = pos.x - mc.getEntityRenderDispatcher().camera.getPos().getX();
                        double y = pos.y - mc.getEntityRenderDispatcher().camera.getPos().getY();
                        double z = pos.z - mc.getEntityRenderDispatcher().camera.getPos().getZ();

                        MatrixStack stack = event.getStack();
                        stack.push();
                        setupRender();
                        RenderSystem.disableCull();


                        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

                        float width = entity.getWidth() / 2.0f;
                        float height = entity.getHeight();

                        BUILDER.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                        // Bottom
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x - width), (float) y, (float) (z - width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x + width), (float) y, (float) (z - width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x + width), (float) y, (float) (z + width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x - width), (float) y, (float) (z + width)).color(255, 255, 255, 100).next();

                        // Top
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x - width), (float) (y + height), (float) (z - width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x + width), (float) (y + height), (float) (z - width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x + width), (float) (y + height), (float) (z + width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x - width), (float) (y + height), (float) (z + width)).color(255, 255, 255, 100).next();

                        // Sides
                        // Front
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x - width), (float) y, (float) (z - width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x + width), (float) y, (float) (z - width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x + width), (float) (y + height), (float) (z - width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x - width), (float) (y + height), (float) (z - width)).color(255, 255, 255, 100).next();

                        // Back
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x - width), (float) y, (float) (z + width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x + width), (float) y, (float) (z + width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x + width), (float) (y + height), (float) (z + width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x - width), (float) (y + height), (float) (z + width)).color(255, 255, 255, 100).next();

                        // Left
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x - width), (float) y, (float) (z - width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x - width), (float) y, (float) (z + width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x - width), (float) (y + height), (float) (z + width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x - width), (float) (y + height), (float) (z - width)).color(255, 255, 255, 100).next();

                        // Right
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x + width), (float) y, (float) (z - width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x + width), (float) y, (float) (z + width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x + width), (float) (y + height), (float) (z + width)).color(255, 255, 255, 100).next();
                        BUILDER.vertex(stack.peek().getPositionMatrix(), (float) (x + width), (float) (y + height), (float) (z - width)).color(255, 255, 255, 100).next();

                        TESSELLATOR.draw();
                        RenderSystem.enableCull();
                        endRender();
                        stack.pop();
                    }
                }
            }
        }


    }

    @Getter
    @AllArgsConstructor
    public static class Position {
        private final Vec3d pos;
        private final long time;
    }
}