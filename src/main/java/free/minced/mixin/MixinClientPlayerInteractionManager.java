package free.minced.mixin;

import free.minced.modules.impl.combat.Reach;
import net.minecraft.block.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import free.minced.Minced;
import free.minced.events.EventCollects;
import free.minced.events.impl.mobility.EventBreakBlock;
import free.minced.modules.impl.combat.AttackAura;
import free.minced.modules.impl.misc.NoInteract;
import free.minced.systems.rotations.Rotations;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
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
        Block bs = mc.world.getBlockState(hitResult.getBlockPos()).getBlock();
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
        if (Rotations.rotating) {
            args.set(3, Rotations.serverYaw);
            args.set(4, Rotations.serverPitch);
        }
    }


}