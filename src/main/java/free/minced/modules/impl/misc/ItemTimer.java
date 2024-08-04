package free.minced.modules.impl.misc;

import free.minced.events.Event;
import free.minced.events.impl.input.EventFinishEat;
import free.minced.events.impl.player.PacketEvent;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;

@ModuleDescriptor(name = "ItemTimer", category = ModuleCategory.MISC)
public class ItemTimer extends Module {
    private final NumberSetting gappleCool = new NumberSetting("Cooldown", this, 100, 1, 200, 1);

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventFinishEat event) {
            Item item = event.getItem();

            if (item == Items.GOLDEN_APPLE) {
                mc.player.getItemCooldownManager().set(Items.GOLDEN_APPLE, (int) gappleCool.getValue().floatValue());
            }
        }
        if (e instanceof UpdatePlayerEvent event) {
            Item[] items = {Items.GOLDEN_APPLE};
            for (Item item : items) {
                if (mc.player.getItemCooldownManager().isCoolingDown(item) && mc.player.getActiveItem().getItem() == item) {
                    mc.options.useKey.setPressed(false);
                } else if (mc.options.useKey.isPressed() && !(mc.currentScreen instanceof InventoryScreen)) {
                    mc.options.useKey.setPressed(true);
                }
            }

        }
        if (e instanceof PacketEvent.Send event) {
            if (event.getPacket() instanceof PlayerInteractItemC2SPacket packet) {
                boolean isGappleInHands = mc.player.getMainHandStack().getItem() == Items.GOLDEN_APPLE || mc.player.getOffHandStack().getItem() == Items.GOLDEN_APPLE;
                if (mc.options.useKey.isPressed() && isGappleInHands &&  mc.player.getItemCooldownManager().isCoolingDown(Items.GOLDEN_APPLE) && !(mc.currentScreen instanceof InventoryScreen)) {
                    event.setCancel(true);
                }
            }
        }
    }
}