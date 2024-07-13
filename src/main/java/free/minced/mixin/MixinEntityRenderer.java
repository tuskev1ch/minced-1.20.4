package free.minced.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import free.minced.Minced;
import free.minced.modules.impl.render.FullBright;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {
/*
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (entity instanceof ArmorStandEntity && ModuleManager.noRender.isEnabled() && ModuleManager.noRender.noArmorStands.getValue())
            info.cancel();

        if (entity instanceof PlayerEntity && ModuleManager.nameTags.isEnabled())
            info.cancel();
    }
*/

    @Inject(method = "getSkyLight", at = @At("RETURN"), cancellable = true)
    private void onGetSkyLight(CallbackInfoReturnable<Integer> cir) {
        if (Minced.getInstance().getModuleHandler().get(FullBright.class).isEnabled())
            cir.setReturnValue(Minced.getInstance().getModuleHandler().get(FullBright.class).brightness.getValue().intValue());
    }
}
