package free.minced.mixin;

import free.minced.events.EventCollects;
import free.minced.events.impl.input.EventFinishEat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import static free.minced.primary.IHolder.mc;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void onFinishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> info) {
        if (user == mc.player) {
            EventFinishEat event = new EventFinishEat(((ItemStack) (Object) this).getItem());
            EventCollects.call(event);
        }
    }
}
