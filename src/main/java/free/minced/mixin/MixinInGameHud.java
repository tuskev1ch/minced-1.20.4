package free.minced.mixin;

import free.minced.framework.render.DrawHandler;
import free.minced.modules.impl.misc.UnHook;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import free.minced.Minced;
import free.minced.events.EventCollects;
import free.minced.events.impl.render.Render2DEvent;
import free.minced.systems.draggable.Draggable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static free.minced.primary.IHolder.fullNullCheck;
import static free.minced.primary.IHolder.mc;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Inject(at = @At(value = "HEAD"), method = "render")
    public void renderHook(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (fullNullCheck() || (mc.options.hudHidden || mc.getDebugHud().shouldShowDebugHud()) || Minced.getInstance().getModuleHandler().get(UnHook.class).isEnabled()) return;
        EventCollects.call(new Render2DEvent(context.getMatrices(), context, tickDelta));
        DrawHandler.drawGPS(context);
        for (Draggable draggable : Minced.getInstance().getDraggableHandler().draggables.values()) {
            draggable.onRender((int) mc.mouse.getX(), (int) mc.mouse.getY());
        }

    }
}
