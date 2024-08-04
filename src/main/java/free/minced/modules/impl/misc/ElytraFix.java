package free.minced.modules.impl.misc;


import lombok.Getter;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import free.minced.events.Event;
import free.minced.events.impl.player.EventSync;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.time.TimerHandler;

@Getter
@ModuleDescriptor(name = "ElytraFix", category = ModuleCategory.MISC)

public class ElytraFix extends Module {

    final TimerHandler fixTimer = new TimerHandler();

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventSync e) {
            if (mc.player.currentScreenHandler.getCursorStack().getItem() instanceof ArmorItem armor) {
                if (armor.getType() == ArmorItem.Type.CHESTPLATE) {
                    if (mc.player.getInventory().getArmorStack(2).getItem() == Items.ELYTRA) {
                        mc.interactionManager.clickSlot(0, 6, 1, SlotActionType.PICKUP, mc.player);
                        int empty = findEmptySlot();
                        boolean needDrop = (empty == 999);
                        if (needDrop) {
                            empty = 9;
                        }
                        mc.interactionManager.clickSlot(0, empty, 1, SlotActionType.PICKUP, mc.player);
                        if (needDrop) {
                            mc.interactionManager.clickSlot(0, -999, 1, SlotActionType.PICKUP, mc.player);
                        }
                        fixTimer.reset();
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        fixTimer.reset();
        super.onDisable();
    }

    public static int findEmptySlot() {
        for (int i = 0; i < 36; i++)
            if (mc.player.getInventory().getStack(i).isEmpty()) return i < 9 ? i + 36 : i;
        return 999;
    }
}