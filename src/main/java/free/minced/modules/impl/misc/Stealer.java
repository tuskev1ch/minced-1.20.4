package free.minced.modules.impl.misc;

import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.time.TimerHandler;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

@ModuleDescriptor(name = "Stealer", category = ModuleCategory.MISC)
public class Stealer extends Module {
    TimerHandler timer = new TimerHandler();

    public NumberSetting delay = new NumberSetting("Delay",this,0,0,1500,0.1f);

    @Override
    public void onEvent(Event e) {
        if(e instanceof UpdatePlayerEvent event) {
            if (mc.player == null || mc.interactionManager == null) return;

            if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler container) {

                for (int index = 0; index < container.slots.size(); ++index) {
                    ItemStack itemStack = container.getSlot(index).getStack();
                    if (!itemStack.isEmpty() && timer.finished((long) delay.getValue().floatValue())) {
                        mc.interactionManager.clickSlot(container.syncId, index, 0, SlotActionType.QUICK_MOVE, mc.player);
                        timer.reset();
                    }
                }

            }

        }
    }
}
