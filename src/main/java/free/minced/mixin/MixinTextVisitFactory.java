package free.minced.mixin;

import free.minced.Minced;
import free.minced.modules.impl.misc.NameProtect;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static free.minced.primary.IHolder.mc;

@Mixin(value = {TextVisitFactory.class})
public class MixinTextVisitFactory {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", ordinal = 0), method = {"visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z" }, index = 0)
    private static String adjustText(String text) {
        return protect(text);
    }

    private static String protect(String string) {
        NameProtect m4 = Minced.getInstance().getModuleHandler().get(NameProtect.class);
        if (m4 == null ||  !m4.isEnabled() || mc.player == null)
            return string;

        String me = mc.getSession().getUsername();
        if (string.contains(me) || (Minced.getInstance().getPartnerHandler().getFriends().stream().anyMatch(i -> i.contains(string)) && m4.hideFriends.isEnabled())) {
            return string.replace(me, m4.getCustomName());
        }

        return string;
    }
}
