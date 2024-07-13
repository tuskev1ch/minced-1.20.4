package free.minced.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.world.BlockView;
import free.minced.Minced;
import free.minced.modules.impl.misc.FreeCam;
import free.minced.modules.impl.movement.Flight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static free.minced.primary.IHolder.mc;

@Mixin(Camera.class)
public abstract class MixinCamera {
    @Shadow
    private boolean thirdPerson;
    
    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void setPosHook(Args args) {
        if (Minced.getInstance().getModuleHandler().get(FreeCam.class).isEnabled())
            args.setAll(Minced.getInstance().getModuleHandler().get(FreeCam.class).getFakeX(), Minced.getInstance().getModuleHandler().get(FreeCam.class).getFakeY(), Minced.getInstance().getModuleHandler().get(FreeCam.class).getFakeZ());
        
        Flight flight = Minced.getInstance().getModuleHandler().get(Flight.class);
        if (flight.shouldFixElytra()) {
            args.set(1, flight.getFakeY() + mc.player.getEyeHeight(EntityPose.STANDING));
        }
    }
    @Inject(method = "update", at = @At("TAIL"))
    private void updateHook(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (Minced.getInstance().getModuleHandler().get(FreeCam.class).isEnabled()) {
            this.thirdPerson = true;
        }
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void setRotationHook(Args args) {
        if (Minced.getInstance().getModuleHandler().get(FreeCam.class).isEnabled())
            args.setAll(Minced.getInstance().getModuleHandler().get(FreeCam.class).getFakeYaw(), Minced.getInstance().getModuleHandler().get(FreeCam.class).getFakePitch());
    }


}
