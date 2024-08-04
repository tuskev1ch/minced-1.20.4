package free.minced.mixin;

import com.mojang.authlib.GameProfile;
import free.minced.systems.rotations.Rotations;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.Vec3d;
import free.minced.Minced;
import free.minced.events.EventCollects;
import free.minced.events.impl.mobility.EventMove;
import free.minced.events.impl.player.*;
import free.minced.modules.impl.misc.NoPush;
import free.minced.modules.impl.movement.NoSlowDown;
import free.minced.systems.SharedClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static free.minced.primary.IHolder.fullNullCheck;
import static free.minced.primary.IHolder.mc;

@Mixin(value = ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    @Unique
    boolean pre_sprint_state = false;
    @Unique
    private final boolean updateLock = false;
    @Unique
    private Runnable postAction;

    @Shadow
    public abstract float getPitch(float tickDelta);

    @Shadow
    protected abstract void sendMovementPackets();

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }


    @Inject(method = "tick", at = @At("HEAD"))
    public void tickHook(CallbackInfo info) {
        if (mc.player != null && mc.world != null) {
            EventCollects.call(new UpdatePlayerEvent());
        }
    }



    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), require = 0)
    private boolean tickMovementHook(ClientPlayerEntity player) {
        if (Minced.getInstance().getModuleHandler().get(NoSlowDown.class).isEnabled() && Minced.getInstance().getModuleHandler().get(NoSlowDown.class).canNoSlow())
            return false;
        return player.isUsingItem();
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"), cancellable = true)
    public void onMoveHook(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        EventMove event = new EventMove(movement.x, movement.y, movement.z);
        EventCollects.call(event);
        if (event.isCancel()) {
            super.move(movementType, new Vec3d(event.getX(), event.getY(), event.getZ()));
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.BEFORE))
    public void tick(CallbackInfo callbackInfo) {
        if (mc.player != null && mc.world != null) {
            EventCollects.call(new TickEvent());
        }
    }


    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void onPushOutOfBlocksHook(double x, double d, CallbackInfo info) {
        if (Minced.getInstance().getModuleHandler().get(NoPush.class).isEnabled() &&
                Minced.getInstance().getModuleHandler().get(NoPush.class).getRemoveFrom().get("Blocks").isEnabled()) {
            info.cancel();
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("RETURN"), cancellable = true)
    private void sendMovementPacketsPostHook(CallbackInfo info) {
        if (fullNullCheck()) return;
        mc.player.lastSprinting = pre_sprint_state;
        SharedClass.lockSprint = false;
        EventPostSync event = new EventPostSync();
        EventCollects.call(event);

        if (postAction != null) {
            postAction.run();
            postAction = null;
        }
        if (event.isCancel())
            info.cancel();
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void sendMovementPacketsHook(CallbackInfo info) {
        if (fullNullCheck()) return;
        Rotations.onSendMovementPacketsPre();


        EventSync event = new EventSync(getYaw(), getPitch());
        EventCollects.call(event);
        SharedClass.onEventSync(event);
        postAction = event.getPostAction();
        EventSprint e = new EventSprint(isSprinting());
        EventCollects.call(e);
        EventCollects.call(new EventAfterRotate());
        if (e.getSprintState() != mc.player.lastSprinting) {
            if (e.getSprintState())
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_SPRINTING));
            else
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.STOP_SPRINTING));

            mc.player.lastSprinting = e.getSprintState();
        }
        pre_sprint_state = mc.player.lastSprinting;
        SharedClass.lockSprint = true;

        if (event.isCancel()) info.cancel();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0))
    private void onTickHasVehicleBeforeSendPackets(CallbackInfo info) {
        Rotations.onSendMovementPacketsPre();
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    private void onSendMovementPacketsTail(CallbackInfo info) {
        Rotations.onSendMovementPacketsPost();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void onTickHasVehicleAfterSendPackets(CallbackInfo info) {
        Rotations.onSendMovementPacketsPost();
    }
}