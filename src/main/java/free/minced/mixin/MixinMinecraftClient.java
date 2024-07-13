package free.minced.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import free.minced.framework.font.Fonts;
import free.minced.systems.rotations.Rotations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Inject(method = "<init>", at = @At("TAIL"))
    void postWindowInit(RunArgs args, CallbackInfo ci) {
        try {
            Fonts.initFonts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}