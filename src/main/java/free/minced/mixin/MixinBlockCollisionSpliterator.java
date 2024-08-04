package free.minced.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.BlockView;
import free.minced.Minced;
import free.minced.events.EventCollects;
import free.minced.events.impl.mobility.EventCollision;
import free.minced.modules.impl.movement.NoClip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static free.minced.primary.IHolder.mc;

@Mixin(value = BlockCollisionSpliterator.class)
public abstract class MixinBlockCollisionSpliterator {

    @Redirect(method = "computeNext", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState computeNextHook(BlockView instance, BlockPos blockPos) {
        if ((mc.player == null || mc.isInSingleplayer()) || Minced.getInstance().getModuleHandler().get(NoClip.class) == null || !Minced.getInstance().getModuleHandler().get(NoClip.class).isEnabled()) {
            return instance.getBlockState(blockPos);
        }

        EventCollision event = new EventCollision(instance.getBlockState(blockPos), blockPos);
        EventCollects.call(event);
        return event.getState();
    }
}

