package free.minced.mixin;

import free.minced.Minced;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;

import static free.minced.primary.IHolder.mc;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void postInitHook(CallbackInfo ci) {

        if (Minced.isOutdated /*&& !FabricLoader.getInstance().isDevelopmentEnvironment()*/) {
            mc.setScreen(new ConfirmScreen(
                    confirm -> {
                        if (confirm) Util.getOperatingSystem().open(URI.create("https://github.com/tuskev1ch/minced-1.20.4/releases/tag/Release"));
                        else mc.stop();
                    },
                    Text.of(Formatting.RED + "You are using an outdated version of our client"), Text.of("Please update to the latest version"), Text.of("Download"), Text.of("Quit Game")));
        }
    }
}
