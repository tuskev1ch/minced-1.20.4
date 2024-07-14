package free.minced.mixin;

import free.minced.modules.impl.combat.HitBox;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import free.minced.Minced;
import free.minced.events.EventCollects;
import free.minced.events.impl.mobility.EventFixVelocity;
import free.minced.modules.impl.misc.NoPush;
import free.minced.primary.IHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static free.minced.primary.IHolder.mc;

@Mixin(Entity.class)
public abstract class MixinEntity  {
    @Shadow
    private Box boundingBox;
    
    @ModifyArgs(method = "pushAwayFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void pushAwayFromHook(Args args) {

        //Condition '...' is always 'false' is a lie!!! do not delete
        if ((Object) this == mc.player &&
                Minced.getInstance().getModuleHandler().get(NoPush.class).isEnabled() &&
                Minced.getInstance().getModuleHandler().get(NoPush.class).getRemoveFrom().get("Entities").isEnabled()) {
            args.set(0, 0.);
            args.set(1, 0.);
            args.set(2, 0.);
        }
    }
    @Inject(method = "getBoundingBox", at = {@At("HEAD")}, cancellable = true)
    public final void getBoundingBox(CallbackInfoReturnable<Box> cir) {
        if (Minced.getInstance().getModuleHandler().get(HitBox.class).isEnabled() && mc != null && mc.player != null && ((Entity) (Object) this).getId() != mc.player.getId()) {
            cir.setReturnValue(new Box(this.boundingBox.minX - Minced.getInstance().getModuleHandler().get(HitBox.class).size.getValue().floatValue() / 2f, this.boundingBox.minY, this.boundingBox.minZ - Minced.getInstance().getModuleHandler().get(HitBox.class).size.getValue().floatValue() / 2f, this.boundingBox.maxX + Minced.getInstance().getModuleHandler().get(HitBox.class).size.getValue().floatValue() / 2f, this.boundingBox.maxY, this.boundingBox.maxZ + Minced.getInstance().getModuleHandler().get(HitBox.class).size.getValue().floatValue() / 2f));
        }
    }
    @Inject(method = "updateVelocity", at = {@At("HEAD")}, cancellable = true)
    public void updateVelocityHook(float speed, Vec3d movementInput, CallbackInfo ci) {
        if ((Object) this == mc.player) {
            ci.cancel();
            EventFixVelocity event = new EventFixVelocity(movementInput, speed, mc.player.getYaw(), movementInputToVelocityC(movementInput, speed, mc.player.getYaw()));
            EventCollects.call(event);
            mc.player.setVelocity(mc.player.getVelocity().add(event.getVelocity()));
        }
    }

    @Unique
    private static Vec3d movementInputToVelocityC(Vec3d movementInput, float speed, float yaw) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        }
        Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);
        float f = MathHelper.sin(yaw * ((float) Math.PI / 180));
        float g = MathHelper.cos(yaw * ((float) Math.PI / 180));
        return new Vec3d(vec3d.x * (double) g - vec3d.z * (double) f, vec3d.y, vec3d.z * (double) g + vec3d.x * (double) f);
    }
}