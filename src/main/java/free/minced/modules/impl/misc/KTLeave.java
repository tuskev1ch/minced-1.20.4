package free.minced.modules.impl.misc;

import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;

@ModuleDescriptor(name = "KTLeave", category = ModuleCategory.MISC)
public class KTLeave extends Module {


    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent event) {
            if (mc.interactionManager != null) {
                mc.interactionManager.attackEntity(mc.player, mc.player);
            }
            this.disable();
        }
    }
}
