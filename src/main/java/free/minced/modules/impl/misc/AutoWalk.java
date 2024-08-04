package free.minced.modules.impl.misc;

import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;

@ModuleDescriptor(name = "AutoWalk", category = ModuleCategory.MISC)
public class AutoWalk extends Module {


    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent event) {
            mc.options.forwardKey.setPressed(true);
        }
    }
}
