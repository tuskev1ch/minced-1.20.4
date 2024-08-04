package free.minced.mixin;

import net.minecraft.client.Keyboard;
import free.minced.Minced;
import free.minced.systems.SharedClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static free.minced.primary.IHolder.mc;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        boolean whitelist = mc.currentScreen == null;
        if (!whitelist) return;

        if (action == 1) SharedClass.keyPress(key);
        if (action == 2) action = 1;


    }
}