package free.minced.modules.impl.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.render.Render2DEvent;
import free.minced.events.impl.render.Render3DEvent;
import free.minced.framework.color.CustomColor;
import free.minced.framework.render.DrawHandler;
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

    @Override
    public void onEvent(Event event) {
        if (event instanceof Render3DEvent render3DEvent) {
            boolean prevBob = mc.options.getBobView().getValue();
            mc.options.getBobView().setValue(false);

            for (PlayerEntity player : Lists.newArrayList(mc.world.getPlayers())) {
                if (player == mc.player)
                    continue;
                Color color = Minced.getInstance().getPartnerHandler().isFriend(player) ? new Color(0, 255, 0, 99).darker() : new CustomColor(22, 22, 22).withAlpha(155);




                Vec3d vec2 = new Vec3d(0, 0, 75)
                        .rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
                        .rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
                        .add(mc.cameraEntity.getEyePos());

                double x = player.prevX + (player.getX() - player.prevX) * mc.getTickDelta();
                double y = player.prevY + (player.getY() - player.prevY) * mc.getTickDelta();
                double z = player.prevZ + (player.getZ() - player.prevZ) * mc.getTickDelta();

                DrawHandler.drawLineDebug(vec2, new Vec3d(x, y, z), color);
            }

            mc.options.getBobView().setValue(prevBob);
        }
    }
}
