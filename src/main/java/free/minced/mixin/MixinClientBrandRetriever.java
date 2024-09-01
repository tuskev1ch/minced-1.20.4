package free.minced.mixin;

import free.minced.Minced;
import free.minced.modules.impl.misc.ClientSpoof;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientBrandRetriever.class})
public class MixinClientBrandRetriever {
    @Inject(method = "getClientModName", at = {@At("HEAD")}, cancellable = true, remap = false)
    private static void getClientModNameHook(CallbackInfoReturnable<String> cir) {
        ClientSpoof clientSpoof = Minced.getInstance().getModuleHandler().get(ClientSpoof.class);
        if (clientSpoof.isEnabled()) {
            cir.setReturnValue(clientSpoof.getClientName());
        }
    }
}