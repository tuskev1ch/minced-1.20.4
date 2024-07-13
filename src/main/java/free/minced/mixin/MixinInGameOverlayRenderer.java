package free.minced.mixin;

import free.minced.Minced;
import free.minced.modules.impl.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class MixinInGameOverlayRenderer {

    @Unique private static final NoRender NO_RENDER = Minced.getInstance().getModuleHandler().get(NoRender.class);

    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void removeFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (NO_RENDER.canRemoveFireOverlay()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
    private static void removeUnderwaterOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (NO_RENDER.canRemoveWaterOverlay()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderInWallOverlay", at = @At("HEAD"), cancellable = true)
    private static void removeRenderInWallOverlay(Sprite sprite, MatrixStack matrices, CallbackInfo ci) {
        if (NO_RENDER.canRemoveBlockOverlay()) {
            ci.cancel();
        }
    }

}
