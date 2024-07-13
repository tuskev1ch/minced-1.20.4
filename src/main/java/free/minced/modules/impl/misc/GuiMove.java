package free.minced.modules.impl.misc;



import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.other.KeyHandler;

@ModuleDescriptor(name = "GuiMove", category = ModuleCategory.MISC)

public class GuiMove extends Module {
    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent event) {


            final KeyBinding[] keys = {mc.options.forwardKey, mc.options.backKey,
                    mc.options.leftKey, mc.options.rightKey, mc.options.jumpKey, mc.options.sprintKey};

            if (mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof SignEditScreen)
                return;

            for (KeyBinding keyBinding : keys) {
                keyBinding.setPressed(KeyHandler.isKeyDown(InputUtil.fromTranslationKey(keyBinding.getBoundKeyTranslationKey()).getCode()));
            }
        }
    }


}