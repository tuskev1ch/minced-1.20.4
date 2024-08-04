package free.minced.mixin;

import net.minecraft.client.Mouse;
import free.minced.events.EventCollects;
import free.minced.events.impl.input.EventMouse;
import free.minced.systems.SharedClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static free.minced.primary.IHolder.mc;

@Mixin(Mouse.class)
public class MixinMouse {
    @Inject(method = "onMouseButton", at = @At("HEAD"))
    public void onMouseButtonHook(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window == mc.getWindow().getHandle()) {
            if (action == 0) SharedClass.onMouseKeyReleased(button);
            if (action == 1) SharedClass.onMouseKeyPressed(button);

            EventCollects.call(new EventMouse(button, action));
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void onMouseScrollHook(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (window == mc.getWindow().getHandle()) {
            EventCollects.call(new EventMouse((int) vertical, 2));
        }
    }
}