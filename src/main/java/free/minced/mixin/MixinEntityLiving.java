package free.minced.mixin;

import free.minced.Minced;
import free.minced.events.EventCollects;
import free.minced.events.impl.mobility.ElytraFixEvent;
import free.minced.modules.impl.combat.AttackAura;
import free.minced.modules.impl.render.SwingAnimations;
import free.minced.primary.game.MobilityHandler;
import free.minced.systems.helpers.IEntityLiving;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinEntityLiving implements IEntityLiving {

    @Shadow
    protected double serverX;
    @Shadow
    protected double serverY;
    @Shadow
    protected double serverZ;

    @Unique
    double prevServerX, prevServerY, prevServerZ;

    @Inject(method = {"updateTrackedPositionAndAngles"}, at = {@At("HEAD")})
    private void updateTrackedPositionAndAnglesHook(double x, double y, double z, float yaw, float pitch, int interpolationSteps, CallbackInfo ci) {
        prevServerX = serverX;
        prevServerY = serverY;
        prevServerZ = serverZ;
    }

    @Inject(method = "getHandSwingDuration", at = {@At("HEAD")}, cancellable = true)
    private void getArmSwingAnimationEnd(final CallbackInfoReturnable<Integer> info) {
        if (Minced.getInstance().getModuleHandler().get(SwingAnimations.class).isEnabled() && Minced.getInstance().getModuleHandler().get(SwingAnimations.class).slowAnimation.isEnabled())
            info.setReturnValue(Minced.getInstance().getModuleHandler().get(SwingAnimations.class).slowAnimationSpeed.getValue().intValue());
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPitch()F"))
    private float hookModifyFallFlyingPitch(LivingEntity instance) {
        if ((Object) this != MinecraftClient.getInstance().player ||
                !Minced.getInstance().getModuleHandler().get(AttackAura.class).isEnabled()) {
            return instance.getPitch();
        }

        ElytraFixEvent event = new ElytraFixEvent(MinecraftClient.getInstance().player.getYaw(), MinecraftClient.getInstance().player.getPitch());
        EventCollects.call(event);


        return event.getPitch();
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d hookModifyFallFlyingRotationVector(LivingEntity original) {
        if ((Object) this != MinecraftClient.getInstance().player ||
                !Minced.getInstance().getModuleHandler().get(AttackAura.class).isEnabled()) {
            return original.getRotationVector();
        }

        ElytraFixEvent event = new ElytraFixEvent(MinecraftClient.getInstance().player.getYaw(), MinecraftClient.getInstance().player.getPitch());
        EventCollects.call(event);

        Vec3d vec31 = MobilityHandler.getRotationVector(event.getPitch(), event.getYaw());

        return vec31;
    }

    @Override
    public double getPrevServerX() {
        return prevServerX;
    }

    @Override
    public double getPrevServerY() {
        return prevServerY;
    }

    @Override
    public double getPrevServerZ() {
        return prevServerZ;
    }
}