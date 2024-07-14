package free.minced.modules.impl.movement;


import net.minecraft.item.BookItem;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.util.Hand;
import free.minced.events.Event;
import free.minced.events.impl.input.EventKeyboardInput;
import free.minced.events.impl.player.PacketEvent;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.ModeSetting;

@ModuleDescriptor(name = "NoSlowDown", category = ModuleCategory.MOVEMENT)

public class NoSlowDown extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "Grim", "Grim", "Matrix");


    @Override
    public void onEvent(Event event) {
        if (event instanceof EventKeyboardInput e) {
            if (mc.player.isUsingItem() && !mc.player.isRiding() && !mc.player.isFallFlying()) {
                if (mode.is("Matrix")) {
                    mc.player.input.movementForward /= 0.2f;
                    mc.player.input.movementSideways /= 0.2f;
                    if (mc.player.isOnGround()) {
                        if (mc.player.input.movementForward != 0 && mc.player.input.movementSideways != 0) {
                            mc.player.input.movementForward *= 0.35f;
                            mc.player.input.movementSideways *= 0.35f;
                        } else {
                            mc.player.input.movementForward *= 0.5f;
                            mc.player.input.movementSideways *= 0.5f;
                        }
                    } else {
                        if (mc.player.input.movementForward != 0 && mc.player.input.movementSideways != 0) {
                            mc.player.input.movementForward *= 0.47f;
                            mc.player.input.movementSideways *= 0.47f;
                        } else {
                            mc.player.input.movementForward *= 0.67f;
                            mc.player.input.movementSideways *= 0.67f;
                        }
                    }
                }
            }
        }
        if (event instanceof UpdatePlayerEvent e) {
            if (mc.player.isUsingItem() && !mc.player.isRiding() && !mc.player.isFallFlying()) {
                if (mode.is("Grim")) {
                    if (mc.player.getActiveHand() == Hand.OFF_HAND) {
                        IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot % 8 + 1));
                        IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
                    } else if ((mc.player.getItemUseTime() <= 3 || mc.player.age % 2 == 0)) {
                        IHolder.sendSequencedPacket(id -> new PlayerInteractItemC2SPacket(Hand.OFF_HAND, id));
                    }
                }
            }
        }
    }


    public boolean canNoSlow() {
        if (mode.is("Matrix")) return false;

        if ((mc.player.getOffHandStack().isFood() || mc.player.getOffHandStack().getItem() == Items.SHIELD)
                && (mode.is("Grim")) && mc.player.getActiveHand() == Hand.MAIN_HAND)
            return false;

        return true;
    }
}