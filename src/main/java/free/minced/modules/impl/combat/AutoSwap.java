package free.minced.modules.impl.combat;

import free.minced.events.Event;
import free.minced.events.impl.input.binds.InputEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.primary.chat.ChatHandler;
import free.minced.primary.game.PlayerHandler;
import free.minced.systems.setting.impl.BindSetting;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.ModeSetting;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@ModuleDescriptor(name = "AutoSwap", category = ModuleCategory.COMBAT)
public class AutoSwap extends Module {
    private final ModeSetting swapMode = new ModeSetting("Swap Mode", this, "Window", "Window", "ViaVersion 1.17+", "Pick");

    private final BindSetting bindSetting = new BindSetting("Key for swap", this, 0);
    private final BooleanSetting takeIfEmpty = new BooleanSetting("Take If Empty", this, false);

    public final ModeSetting firstItem = new ModeSetting("First item", this, "Firework Star", "Firework Star",
            "Totem of Undying", "Head", "Golden Apple", "Shield");

    public final ModeSetting secondItem = new ModeSetting("Second item", this, "Firework Star", "Firework Star",
            "Totem of Undying", "Head", "Golden Apple", "Shield");

    @Override
    public void onEvent(Event event) {
        if (event instanceof InputEvent inputEvent) {
            if (inputEvent.getButtonOrKey() == bindSetting.getKey()) {
                if (firstItem.getCurrentMode().equals(secondItem.getCurrentMode())) {
                    ChatHandler.display("Один и тот же предмет нельзя свапать =)");
                    return;
                }

                Item item = mc.player.getOffHandStack().getItem();

                if (item == getItem(firstItem.getCurrentMode())) {
                    swapItem(getItem(secondItem.getCurrentMode()));
                } else if (item == getItem(secondItem.getCurrentMode())) {
                    swapItem(getItem(firstItem.getCurrentMode()));
                } else {
                    if (takeIfEmpty.isEnabled()) {
                        swapItem(getItem(firstItem.getCurrentMode()));
                    }
                }
            }
        }
    }

    private Item getItem(String itemName) {
        return switch (itemName) {
            case "Firework Star" -> Items.FIREWORK_STAR;
            case "Totem of Undying" -> Items.TOTEM_OF_UNDYING;
            case "Head" -> Items.PLAYER_HEAD;
            case "Golden Apple" -> Items.GOLDEN_APPLE;
            case "Shield" -> Items.SHIELD;
            default -> Items.AIR;
        };
    }
    private void swapItem(Item item) {
        int itemSlot = PlayerHandler.findItemSlot(item);

        if (itemSlot == -1) {
            return;
        }

        if (mc.player.getOffHandStack().getItem() != item) {
            swapItem(itemSlot);
        }
    }
    public static int findNearestCurrentItem() {
        int i = mc.player.getInventory().selectedSlot;
        if (i == 8) return 7;
        if (i == 0) return 1;
        return i - 1;
    }
    public void swapItem(int slot) {
        if (swapMode.is("Window")) {
            mc.interactionManager.clickSlot(0, slot, 1, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, 45, 1, SlotActionType.PICKUP, mc.player);
        }
        if (swapMode.is("ViaVersion 1.17+")) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 40, SlotActionType.SWAP, mc.player);
            IHolder.sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
        }
        if (swapMode.is("Pick")) {
            int nearestSlot = findNearestCurrentItem();

            int prevCurrentItem = mc.player.getInventory().selectedSlot;

            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, nearestSlot, SlotActionType.SWAP, mc.player);

            IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(nearestSlot));
            IHolder.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
            IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(prevCurrentItem));

        }
    }


}
