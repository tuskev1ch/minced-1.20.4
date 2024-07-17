package free.minced.mixin;

import free.minced.Minced;
import free.minced.events.EventCollects;
import free.minced.events.impl.mobility.ElytraFixEvent;
import free.minced.modules.impl.combat.AttackAura;
import free.minced.primary.game.MobilityHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d tick(LivingEntity instance) {

        if (instance != MinecraftClient.getInstance().player ||
                !Minced.getInstance().getModuleHandler().get(AttackAura.class).isEnabled()) {
            return instance.getRotationVector();
        }


        ElytraFixEvent event = new ElytraFixEvent(instance.getYaw(), instance.getPitch());
        EventCollects.call(event);
        Vec3d vec3d = MobilityHandler.getRotationVector(event.getPitch(), event.getYaw());


        return vec3d;
    }
}