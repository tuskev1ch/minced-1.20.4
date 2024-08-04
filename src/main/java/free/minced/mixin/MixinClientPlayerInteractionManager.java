package free.minced.mixin;

import free.minced.events.impl.input.EventClickSlot;
import free.minced.modules.impl.combat.Reach;
import free.minced.systems.rotations.Rotations;
import net.minecraft.block.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import free.minced.Minced;
import free.minced.events.EventCollects;
import free.minced.events.impl.mobility.EventBreakBlock;
import free.minced.modules.impl.misc.NoInteract;

import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static free.minced.primary.IHolder.mc;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Shadow
    private int blockBreakingCooldown;
    @Shadow private GameMode gameMode;

    @Inject(method = "getReachDistance", at = @At("HEAD"), cancellable = true)
    public void getReachDistance(CallbackInfoReturnable<Float> cir) {
        Reach reach = Minced.getInstance().getModuleHandler().get(Reach.class);

        if (reach.isEnabled() && reach.getBlocks()) {
            cir.setReturnValue(reach.getRange());
        } else {
            cir.setReturnValue(this.gameMode.isCreative() ? 5.0F : 4.5F);
        }

    }
    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        Block bs = null;
        if (mc.world != null) {
            bs = mc.world.getBlockState(hitResult.getBlockPos()).getBlock();
        }
        if (Minced.getInstance().getModuleHandler().get(NoInteract.class).isEnabled() && (
                bs == Blocks.CHEST ||
                        bs == Blocks.TRAPPED_CHEST ||
                        bs == Blocks.FURNACE ||
                        bs == Blocks.ANVIL ||
                        bs == Blocks.CRAFTING_TABLE ||
                        bs == Blocks.HOPPER ||
                        bs == Blocks.JUKEBOX ||
                        bs == Blocks.NOTE_BLOCK ||
                        bs == Blocks.ENDER_CHEST ||
                        bs == Blocks.DISPENSER ||
                        bs == Blocks.DROPPER ||
                        bs instanceof ShulkerBoxBlock ||
                        bs instanceof FenceBlock ||
                        bs instanceof FenceGateBlock)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
    @Inject(method = "breakBlock", at = @At("HEAD"), cancellable = true)
    public void breakBlockHook(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        EventBreakBlock event = new EventBreakBlock(pos);
        EventCollects.call(event);
        if (event.isCancel())
            cir.setReturnValue(false);
    }

    @ModifyArgs(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/c2s/play/PlayerMoveC2SPacket$Full;<init>(DDDFFZ)V"))
    private void onInteractItem(Args args) {
        Rotations.RotationRequest request = Rotations.rotationQueue.peek();
        if (request != null) {
            args.set(3, request.getYaw());
            args.set(4, request.getPitch());
        }
    }

    @Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
    public void clickSlotHook(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (mc.player == null || mc.world == null) return;
        EventClickSlot event = new EventClickSlot(actionType, slotId, button, syncId);
        EventCollects.call(event);
        if (event.isCancel()) {
            ci.cancel();
        }
    }

}