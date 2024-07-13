package free.minced.mixin;

import free.minced.events.impl.mobility.EventJumpAxis;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import free.minced.Minced;
import free.minced.events.EventCollects;
import free.minced.events.impl.mobility.EventPlayerTravel;
import free.minced.events.impl.player.EventPlayerJump;
import free.minced.modules.impl.movement.AutoSprint;
import free.minced.primary.IHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerEntity.class, priority = 900)
public class MixinPlayerEntity {

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V", shift = At.Shift.AFTER))
    public void attackAHook(CallbackInfo callbackInfo) {
        if (Minced.getInstance().getModuleHandler().get(AutoSprint.class).isEnabled() && Minced.getInstance().getModuleHandler().get(AutoSprint.class).keepSprint.isEnabled()) {
            final float multiplier = 0.6f + 0.4f * 1;
            IHolder.mc.player.setVelocity(IHolder.mc.player.getVelocity().x / 0.6 * multiplier, IHolder.mc.player.getVelocity().y, IHolder.mc.player.getVelocity().z / 0.6 * multiplier);
            IHolder.mc.player.setSprinting(true);
        }
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravelhookPre(Vec3d movementInput, CallbackInfo ci) {
        if (IHolder.mc.player == null)
            return;

        final EventPlayerTravel event = new EventPlayerTravel(movementInput, true);
        EventCollects.call(event);
        if (event.isCancel()) {
            IHolder.mc.player.move(MovementType.SELF, IHolder.mc.player.getVelocity());
            ci.cancel();
        }
    }


    @Inject(method = "travel", at = @At("RETURN"), cancellable = true)
    private void onTravelhookPost(Vec3d movementInput, CallbackInfo ci) {
        if (IHolder.mc.player == null)
            return;
        final EventPlayerTravel event = new EventPlayerTravel(movementInput, false);
        EventCollects.call(event);
        if (event.isCancel()) {
            IHolder.mc.player.move(MovementType.SELF, IHolder.mc.player.getVelocity());
            ci.cancel();
        }
    }

    @Inject(method = "jump", at = @At("HEAD"))
    private void onJumpPre(CallbackInfo ci) {
        EventCollects.call(new EventPlayerJump(true));
    }

    @Inject(method = "jump", at = @At("TAIL"))
    public void onJump(CallbackInfo ci) {
        EventCollects.call(new EventJumpAxis());
    }

    @Inject(method = "jump", at = @At("RETURN"))
    private void onJumpPost(CallbackInfo ci) {
        EventCollects.call(new EventPlayerJump(false));
    }
}
