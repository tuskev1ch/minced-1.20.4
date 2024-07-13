package free.minced.modules.impl.movement;

import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.mixin.accesors.ILivingEntity;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.game.MobilityFix;

@ModuleDescriptor(name = "NoDelay", category = ModuleCategory.MISC)
public class NoDelay extends Module {

    @Override
    public void onEvent(Event event) {
        if (event instanceof UpdatePlayerEvent) {
            if (((ILivingEntity)mc.player).getLastJumpCooldown() > 0) { // 0 - задержка
                ((ILivingEntity)mc.player).setLastJumpCooldown(0);
            }
        }
    }
}
