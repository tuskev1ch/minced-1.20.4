package free.minced.modules.impl.movement;

import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import net.minecraft.item.Items;

@ModuleDescriptor(name = "ElytraBounce", category = ModuleCategory.MOVEMENT)
public class ElytraBounce extends Module {

    @Override
    public void onEvent(Event event) {
        if (event instanceof UpdatePlayerEvent e) {
            if (mc.player != null && mc.player.getInventory().getStack(38).getItem() == Items.ELYTRA && mc.player.input.jumping) {
                mc.player.input.jumping = false;
            }
        }
    }
}