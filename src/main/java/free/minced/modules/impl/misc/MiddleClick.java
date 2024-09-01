package free.minced.modules.impl.misc;



import lombok.Getter;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.player.EventSync;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.primary.game.InventoryHandler;
import free.minced.primary.time.TimerHandler;

import free.minced.systems.setting.impl.BooleanSetting;

import java.util.function.Consumer;

@ModuleDescriptor(name = "MiddleClick", category = ModuleCategory.MISC)
public class MiddleClick extends Module {
    private final BooleanSetting pearl = new BooleanSetting("Pearl", this, true);

    @Getter
    private final BooleanSetting inventory = new BooleanSetting("Inventory", this, false, () -> !pearl.isEnabled());
    private static final TimerHandler timer = new TimerHandler();

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventSync e) {
            if (pearl.isEnabled()) {
                if (mc.options.pickItemKey.isPressed()) {
                    Action.Pearl.doAction(e);
                }
            }
        }
    }

    public enum Action {


        Pearl((EventSync e) -> {
            if (timer.every(500)) {
                int epSlot1 = InventoryHandler.findItemInHotBar(Items.ENDER_PEARL).slot();
                if (!Minced.getInstance().getModuleHandler().get(MiddleClick.class).getInventory().isEnabled() || (Minced.getInstance().getModuleHandler().get(MiddleClick.class).getInventory().isEnabled() && epSlot1 != -1)) {
                    if (epSlot1 != -1) {
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(epSlot1));
                        IHolder.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, id));
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
                    }
                } else {
                    int bestEmptySlotH = InventoryHandler.findBestEmpySlot(true);
                    int epSlot = InventoryHandler.findItemInInventory(Items.ENDER_PEARL).slot();
                    if (epSlot != -1) {
                        mc.interactionManager.clickSlot(0, epSlot, 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(0, bestEmptySlotH + 36, 0, SlotActionType.PICKUP, mc.player);

                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(bestEmptySlotH));

                        IHolder.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, id));

                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(epSlot1));

                        mc.interactionManager.clickSlot(0, bestEmptySlotH + 36, 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(0, epSlot, 0, SlotActionType.PICKUP, mc.player);

                    }
                }
            }
        }),

        None((EventSync e) -> {});

        private final Consumer<EventSync> r;

        Action(Consumer<EventSync> r) {
            this.r = r;
        }

        public void doAction(EventSync e) {
            r.accept(e);
        }
    }

}