package free.minced.modules.impl.misc;


import free.minced.events.impl.player.PacketEvent;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.ModeSetting;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import free.minced.events.Event;
import free.minced.events.impl.input.binds.InputEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.primary.chat.ChatHandler;
import free.minced.primary.game.InventoryHandler;
import free.minced.primary.game.SearchInvResult;
import free.minced.systems.setting.impl.BindSetting;

import static free.minced.primary.game.InventoryHandler.switchTo;
import static free.minced.primary.other.KeyHandler.isKeyPressed;

@ModuleDescriptor(name = "ElytraUtils", category = ModuleCategory.MISC)

public class ElytraUtils extends Module {
    private final BindSetting ElytraUtilsBind = new BindSetting("Elytra Swap Bind", this, -1);
    private final BindSetting ElytraFireworkBind = new BindSetting("Elytra FireWork Bind", this, -1);
    public final BooleanSetting take = new BooleanSetting("Take From Inv", this, false);
    public final NumberSetting slotFirework = new NumberSetting("FireWork Slot", this, 0, 0, 9, 1, () -> !take.isEnabled());

    public final BooleanSetting silentStart = new BooleanSetting("Silent Start", this, false);

    public final ModeSetting mode = new ModeSetting("Swing Mode",  this, "Packet",  "Packet", "None");

    @Override
    public void onEvent(Event event) {
        if (event instanceof InputEvent e) {
            if (mc.currentScreen == null) {
                if (e.getButtonOrKey() == ElytraUtilsBind.getKey()) {
                    swapChest(false);
                }

                if (e.getButtonOrKey() == ElytraFireworkBind.getKey() && mc.player.isFallFlying()) {
                    useFireWork();
                }
            }
        }
        if (event instanceof PacketEvent.SendPost e) {
            if(e.getPacket() instanceof ClientCommandC2SPacket command
                    && command.getMode() == ClientCommandC2SPacket.Mode.START_FALL_FLYING
                    && silentStart.isEnabled()) {
                useFireWork();
            }
        }
    }
    public static void clickSlot(int id) {
        if (id == -1 || mc.interactionManager == null || mc.player == null) return;
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, id, 0, SlotActionType.PICKUP, mc.player);
    }

    private void swapChest(boolean disable) {
        SearchInvResult result = InventoryHandler.findItemInInventory(Items.ELYTRA);


        if (mc.player.getInventory().getStack(38).getItem() == Items.ELYTRA) {
            int slot = getChestPlateSlot();
            if (slot != -1) {
                clickSlot(slot);
                clickSlot(6);
                clickSlot(slot);
            } else {
                ChatHandler.display("You don't have a chestplate!");
                return;
            }
        } else if (result.found()) {
                clickSlot(result.slot());
                clickSlot(6);
                clickSlot(result.slot());
                if(silentStart.isEnabled() && mc.player.fallDistance > 0) {
                    IHolder.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                }
        } else {
            ChatHandler.display("You don't have an elytra!");
            return;
        }
    }
    public static int getChestPlateSlot() {
        Item[] items = {Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.LEATHER_CHESTPLATE};
        for (Item item : items) {
            SearchInvResult slot = InventoryHandler.findItemInInventory(item);
            if (slot.found()) {
                return slot.slot();
            }
        }
        return -1;
    }

    public void useFireWork() {
        if (mc.player.getMainHandStack().getItem() == Items.FIREWORK_ROCKET || mc.player.getOffHandStack().getItem() == Items.FIREWORK_ROCKET) return;

        if (mc.player == null) return;

        int fireworkSlotI = InventoryHandler.findItem(Items.FIREWORK_ROCKET, false);
        int fireworkSlotH = InventoryHandler.findItem(Items.FIREWORK_ROCKET, true);

        if (fireworkSlotI == -1 && fireworkSlotH == -1) {
            ChatHandler.display("You've got no fireworks!");
            return;
        }

        if (!mc.player.isFallFlying()) return;

        int bestEmptySlotH = InventoryHandler.findBestEmpySlot(true);

        if (fireworkSlotH == -1) {
            if (take.isEnabled()) {
                mc.interactionManager.clickSlot(0, fireworkSlotI, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(0, bestEmptySlotH + 36, 0, SlotActionType.PICKUP, mc.player);

                IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(bestEmptySlotH));

                IHolder.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, id));
                if (mode.is("Packet")) {
                    IHolder.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                }

                IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));

                mc.interactionManager.clickSlot(0, bestEmptySlotH + 36, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(0, fireworkSlotI, 0, SlotActionType.PICKUP, mc.player);
            }
        } else {
            IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(fireworkSlotH));

            IHolder.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, id));
            if (mode.is("Packet")) {
                IHolder.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }

            IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));

        }

    }


}