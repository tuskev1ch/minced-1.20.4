package free.minced.modules.impl.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.render.Render2DEvent;
import free.minced.framework.color.ClientColors;
import free.minced.framework.color.CustomColor;
import free.minced.framework.font.Fonts;
import free.minced.framework.render.DrawHandler;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.modules.impl.combat.AntiBot;
import free.minced.systems.setting.impl.MultiBoxSetting;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

import static free.minced.framework.render.DrawHandler.getRotations;

@ModuleDescriptor(name = "Arrows", category = ModuleCategory.RENDER)
public class Arrows extends Module {
    private final MultiBoxSetting targets = new MultiBoxSetting("Targets", this, "Players", "Ender Pearl");
    private final NumberSetting sizeScale = new NumberSetting("Size", this, 2.28f, 0.1f, 5f, 0.1);

    private final NumberSetting radiusScale = new NumberSetting("Radius", this, 68, 20, 100, 0.1);

    @Override
    public void onEvent(Event event) {
        if (event instanceof Render2DEvent render2DEvent) {
            float middleW = getAnimatedPosX();
            float middleH = getAnimatedPosY();
            for (Entity ent : mc.world.getEntities()) {
                if (ent instanceof EnderPearlEntity pearl && targets.get("Ender Pearl").isEnabled()) {

                    float yaw = (float) (getRotations(pearl) - mc.player.getYaw());
                    render2DEvent.getStack().translate(middleW, middleH, 0.0F);
                    render2DEvent.getStack().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(yaw));
                    render2DEvent.getStack().translate(-middleW, -middleH, 0.0F);

                    Color color = ClientColors.getSecondColor();

                    drawTracerPointer(render2DEvent.getContext(), middleW, middleH - radiusScale.getValue().floatValue(),
                            sizeScale.getValue().floatValue() * 5F, color.getRGB());
                    render2DEvent.getStack().translate(middleW, middleH, 0.0F);
                    render2DEvent.getStack().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-yaw));
                    render2DEvent.getStack().translate(-middleW, -middleH, 0.0F);
                    RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

                    Fonts.SEMI_15.drawCenteredString(render2DEvent.getStack(), String.format("%.1f", mc.player.distanceTo(pearl)) + "m", (float) (Math.sin(Math.toRadians(yaw)) * 50f) + middleW, (float) (middleH - (Math.cos(Math.toRadians(yaw)) * 50f)) - 20, -1);

                }
                if (ent instanceof PlayerEntity player && targets.get("Players").isEnabled()) {
                    if (mc.player == player) continue;
                    if (Minced.getInstance().getModuleHandler().get(AntiBot.class).isEnabled() && AntiBot.isBot(player)) continue;


                    float yaw = (float) (getRotations(player) - mc.player.getYaw());
                    render2DEvent.getStack().translate(middleW, middleH, 0.0F);
                    render2DEvent.getStack().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(yaw));
                    render2DEvent.getStack().translate(-middleW, -middleH, 0.0F);

                    Color color = Minced.getInstance().getPartnerHandler().isFriend(player) ?
                            new Color(0, 255, 0) : ClientColors.getFirstColor();

                    drawTracerPointer(render2DEvent.getContext(), middleW, middleH - radiusScale.getValue().floatValue(),
                            sizeScale.getValue().floatValue() * 5F, color.darker().getRGB());

                    render2DEvent.getStack().translate(middleW, middleH, 0.0F);
                    render2DEvent.getStack().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-yaw));
                    render2DEvent.getStack().translate(-middleW, -middleH, 0.0F);
                    RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

                }
            }
        }
    }
    public static float getRotations(Entity entity) {
        if (mc.player == null) return 0;
        double x = interp(entity.getPos().x, entity.prevX) - interp(mc.player.getPos().x, mc.player.prevX);
        double z = interp(entity.getPos().z, entity.prevZ) - interp(mc.player.getPos().z, mc.player.prevZ);
        return (float) -(Math.atan2(x, z) * (180 / Math.PI));
    }

    public static double interp(double d, double d2) {
        return d2 + (d - d2) * (double) mc.getTickDelta();
    }

    public static void drawTracerPointer(DrawContext matrices, float x, float y, float size, int color) {
        DrawHandler.drawArrow(matrices, x, y, size + 8, new Color(color));
    }

    private float getAnimatedPosX() {
        return mc.getWindow().getScaledWidth() / 2f;
    }

    private float getAnimatedPosY() {
        return mc.getWindow().getScaledHeight() / 2f;
    }
}
