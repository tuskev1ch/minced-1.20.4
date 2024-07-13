package free.minced.modules.impl.misc;


import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
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
import org.lwjgl.glfw.GLFW;

@ModuleDescriptor(name = "ElytraUtils", category = ModuleCategory.MISC)

public class ElytraUtils extends Module {
    private final BindSetting ElytraUtilsBind = new BindSetting("Elytra Swap Bind", this, -1);
    private final BindSetting ElytraFireworkBind = new BindSetting("Elytra FireWork Bind", this, -1);

    @Override
    public void onEvent(Event event) {
        if (event instanceof InputEvent e) {
            if (e.getButtonOrKey() == ElytraUtilsBind.getKey()) {
                if (InventoryHandler.getElytra() == -1) {
                    ChatHandler.display("У вас нет элитры!");
                    return;
                }

                if (InventoryHandler.getChestplate() == -1) {
                    ChatHandler.display("У вас нет нагрудника!");
                    return;
                }

                if (mc.player.getInventory().getStack(38).getItem() == Items.ELYTRA) {
                    int item = InventoryHandler.getChestplate();
                    InventoryHandler.moveItem(item < 46 ? item : 6, 6, true);
                  //  if (sendChatMessage.isEnabled()) {
                        ChatHandler.display("Свапнул на нагрудник");
                   // }
                } else {
                    int item = InventoryHandler.getElytra();
                    InventoryHandler.moveItem(item < 46 ? item : 6, 6, true);
                    // if (sendChatMessage.isEnabled()) {
                        ChatHandler.display("Свапнул на элитру");
                   // }
                }
            }
            if (e.getButtonOrKey() == ElytraFireworkBind.getKey()) {
                if (!mc.player.isFallFlying()) return;

                useFireWork();
            }
        }
    }
    public void useFireWork() {
        SearchInvResult hotbarFireWorkResult = InventoryHandler.findItemInHotBar(Items.FIREWORK_ROCKET);

        if (hotbarFireWorkResult.found()) {
            IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(hotbarFireWorkResult.slot()));

            IHolder.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, id));
            IHolder.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

            IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));

        } else {
            ChatHandler.display("Феерверки не найдены");
            return;
        }

    }
}