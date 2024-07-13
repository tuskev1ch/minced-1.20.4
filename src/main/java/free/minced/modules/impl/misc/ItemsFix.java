package free.minced.modules.impl.misc;

import lombok.Getter;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import free.minced.events.Event;
import free.minced.events.impl.player.PacketEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.primary.chat.ChatHandler;

@Getter
@ModuleDescriptor(name = "ItemsFix", category = ModuleCategory.MISC)
public class ItemsFix extends Module {

    @Override
    public void onEvent(Event event) {
        if (event instanceof PacketEvent.Receive e) {
            if (e.getPacket() instanceof UpdateSelectedSlotS2CPacket) {
                event.setCancel(true);
                IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
            }
        }
    }
}
