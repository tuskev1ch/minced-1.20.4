package free.minced.modules.impl.combat;

import free.minced.events.Event;
import free.minced.events.impl.input.binds.InputEvent;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.chat.ChatHandler;
import free.minced.primary.game.InventoryHandler;
import free.minced.primary.game.PlayerHandler;
import free.minced.systems.setting.impl.BindSetting;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.ModeSetting;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

@ModuleDescriptor(name = "AutoSwap", category = ModuleCategory.COMBAT)
public class AutoSwap extends Module {

    private final BindSetting bindSetting = new BindSetting("Key for swap", this, 0);
    private final BooleanSetting takeIfEmpty = new BooleanSetting("Take If Empty", this, false);

    public final ModeSetting firstItem = new ModeSetting("First item", this, "Totem of Undying",
            "Totem of Undying", "Head", "Golden Apple", "Shield");

    public final ModeSetting secondItem = new ModeSetting("Second item", this, "Totem of Undying",
            "Totem of Undying", "Head", "Golden Apple", "Shield");

    @Override
    public void onEvent(Event event) {
        if (event instanceof InputEvent inputEvent) {
            if (inputEvent.getButtonOrKey() == bindSetting.getKey()) {
                if (firstItem.getCurrentMode() == secondItem.getCurrentMode()) {
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
        switch (itemName) {
            case "Totem of Undying":
                return Items.TOTEM_OF_UNDYING;
            case "Head":
                return Items.PLAYER_HEAD;
            case "Golden Apple":
                return Items.GOLDEN_APPLE;
            case "Shield":
                return Items.SHIELD;
            default:
                return Items.AIR;
        }
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

    public static void swapItem(int slot) {
        mc.interactionManager.clickSlot(0, slot, 1, SlotActionType.SWAP, mc.player);
        mc.interactionManager.clickSlot(0, 45, 1, SlotActionType.SWAP, mc.player);

    }


}
