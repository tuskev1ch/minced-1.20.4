package free.minced.mixin;

import free.minced.Minced;
import free.minced.modules.impl.render.SwingAnimations;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinEntityLiving {

    @Inject(method = "getHandSwingDuration", at = {@At("HEAD")}, cancellable = true)
    private void getArmSwingAnimationEnd(final CallbackInfoReturnable<Integer> info) {
        if (Minced.getInstance().getModuleHandler().get(SwingAnimations.class).isEnabled() && Minced.getInstance().getModuleHandler().get(SwingAnimations.class).slowAnimation.isEnabled())
            info.setReturnValue(Minced.getInstance().getModuleHandler().get(SwingAnimations.class).slowAnimationSpeed.getValue().intValue());
    }
}