package free.minced.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.events.impl.render.Render2DEvent;
import free.minced.events.impl.render.Render3DEvent;
import free.minced.framework.color.ClientColors;
import free.minced.framework.font.Fonts;
import free.minced.framework.render.DrawHandler;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.MultiBoxSetting;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.math.RotationAxis;

import java.awt.*;

import static free.minced.framework.render.DrawHandler.*;
import static free.minced.modules.impl.display.hud.impl.TargetHUD.getTarget;

@ModuleDescriptor(name = "TargetESP", category = ModuleCategory.RENDER)
public class TargetESP extends Module {

    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent event) {
            if (mc.player == null || mc.world == null)
                return;

            updateTargetESP();
        }
        if (e instanceof Render3DEvent event) {
            if (mc.player == null || mc.world == null)
                return;
            LivingEntity target = getTarget();
            if (target == null)
                return;

            if (target == mc.player) return;

            drawTargetEsp(event.getStack(), target);

        }
    }
}
