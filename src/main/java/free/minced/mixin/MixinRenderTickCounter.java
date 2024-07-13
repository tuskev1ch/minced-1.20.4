package free.minced.mixin;

import free.minced.Minced;
import free.minced.modules.impl.movement.TimerModule;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.class)
public class MixinRenderTickCounter {

    @Shadow
    public float lastFrameDuration;

    @Shadow private long prevTimeMillis;

    @Shadow @Final
    private float tickTime;

    @Shadow public float tickDelta;

    @Inject(method = "beginRenderTick", at = @At("HEAD"), cancellable = true)
    public void modifyTimerSpeed(long timeMillis, CallbackInfoReturnable<Integer> cir) {

        TimerModule timerModule = Minced.getInstance().getModuleHandler().get(TimerModule.class);

        float speed = 1.0F;
        if (timerModule.isEnabled()) {
            speed = timerModule.speed.getValue().floatValue();
        }

        this.lastFrameDuration = ((float)(timeMillis - this.prevTimeMillis) / this.tickTime) * speed;
        this.prevTimeMillis = timeMillis;
        this.tickDelta += this.lastFrameDuration;
        int i = (int)this.tickDelta;
        this.tickDelta -= (float)i;
        cir.setReturnValue(i);
    }

}
