package free.minced.mixin;

import net.minecraft.client.render.LightmapTextureManager;
import free.minced.Minced;
import free.minced.modules.impl.render.FullBright;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {
    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    private void update(Args args) {
        if (Minced.getInstance().getModuleHandler().get(FullBright.class).isEnabled()) {
            args.set(2, Color.getHSBColor(0, 0f, (float) Minced.getInstance().getModuleHandler().get(FullBright.class).brightness.getValue().intValue() / 15f).getRGB());
        }
    }
}