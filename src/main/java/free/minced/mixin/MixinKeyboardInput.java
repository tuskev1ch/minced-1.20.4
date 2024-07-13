package free.minced.mixin;

import net.minecraft.client.input.KeyboardInput;
import free.minced.events.EventCollects;
import free.minced.events.impl.input.EventKeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput {
    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/KeyboardInput;sneaking:Z", shift = At.Shift.BEFORE), cancellable = true)
    private void onSneak(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        EventKeyboardInput event = new EventKeyboardInput();
        EventCollects.call(event);
        if (event.isCancel()) ci.cancel();
    }
}
