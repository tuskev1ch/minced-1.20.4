package free.minced.mixin;
import free.minced.Minced;
import free.minced.modules.impl.misc.Optimization;
import net.minecraft.client.particle.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class MixinParticleManager {
    @Inject(at = @At("HEAD"), method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", cancellable = true)
    public void addParticleHook(Particle p, CallbackInfo e) {
        Optimization optimization = Minced.getInstance().getModuleHandler().get(Optimization.class);

        if (!optimization.isEnabled()) {
            return;
        }

        if (!optimization.getLimits().get("Particles").isEnabled()) {
            return;
        }

        if (!optimization.getAllParticles().isEnabled()) {
            if (p instanceof ElderGuardianAppearanceParticle) {
                e.cancel();
            }

            if (p instanceof RainSplashParticle) {
                e.cancel();
            }

            if (p instanceof ExplosionLargeParticle) {
                e.cancel();
            }

            if (p instanceof CampfireSmokeParticle) {
                e.cancel();
            }

            if (p instanceof BlockDustParticle) {
                e.cancel();
            }

            if ((p instanceof FireworksSparkParticle.FireworkParticle || p instanceof FireworksSparkParticle.Flash)) {
                e.cancel();
            }
        } else {
            e.cancel();
        }
    }
}