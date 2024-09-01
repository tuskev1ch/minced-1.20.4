package free.minced.mixin;

import free.minced.Minced;
import free.minced.mixin.accesors.ILivingEntity;
import free.minced.modules.impl.combat.BackTrack;
import free.minced.systems.SharedClass;
import free.minced.systems.helpers.IEntityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public class MixinProjectileUtil {

    @Inject(method = "raycast", at = @At("HEAD"), cancellable = true)
    private static void raycast(Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double maxDistance, CallbackInfoReturnable<EntityHitResult> cir) {
        cir.cancel();
        EntityHitResult result = SharedClass.getEntityHitResult(entity, min, max, box, predicate, maxDistance);
        cir.setReturnValue(result);
    }

  /*  @Inject(
            at = @At("HEAD"),
            method = "raycast",
            cancellable = true
    )
    private static void beforeGetEntityHitResult(
            final Entity entity, final Vec3 vec3, final Vec3 vec32, final Box Box, final Predicate<Entity> predicate,
            final double d, final CallbackInfoReturnable<@Nullable EntityHitResult> cir) {

        if (!VSGameUtilsKt.getShipsIntersecting(entity.level, Box).iterator().hasNext()) {
            return;
        }

        cir.setReturnValue(RaycastUtilsKt.raytraceEntities(entity.level, entity, vec3, vec32, Box, predicate, d));
    }
*/
}