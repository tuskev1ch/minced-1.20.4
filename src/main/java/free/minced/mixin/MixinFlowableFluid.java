package free.minced.mixin;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import free.minced.Minced;
import free.minced.modules.impl.misc.NoPush;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.fluid.FlowableFluid.FALLING;

@Mixin(FlowableFluid.class)
public abstract class MixinFlowableFluid {

    @Shadow
    protected abstract boolean isFlowBlocked(BlockView world, BlockPos pos, Direction direction);

    @Inject(method = "getVelocity", at = @At(value = "HEAD"), cancellable = true)
    private void getVelocityHook(BlockView world, BlockPos pos, FluidState state, CallbackInfoReturnable<Vec3d> cir) {
        if (Minced.getInstance().getModuleHandler().get(NoPush.class).isEnabled() &&
                Minced.getInstance().getModuleHandler().get(NoPush.class).getRemoveFrom().get("Water").isEnabled()) {
            double d = 0.0;
            double e = 0.0;
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            Vec3d vec3d = new Vec3d(d, 0.0, e);
            if (state.get(FALLING)) {
                for (Direction direction2 : Direction.Type.HORIZONTAL) {
                    mutable.set(pos, direction2);
                    if (!this.isFlowBlocked(world, mutable, direction2) && !this.isFlowBlocked(world, mutable.up(), direction2))
                        continue;
                    vec3d = vec3d.normalize().add(0.0, -6.0, 0.0);
                    break;
                }
            }
            cir.setReturnValue(vec3d.normalize());
        }
    }
}