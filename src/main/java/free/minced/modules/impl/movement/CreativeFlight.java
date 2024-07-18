package free.minced.modules.impl.movement;


import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.mobility.EventMove;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.framework.render.DrawHandler;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.primary.chat.ChatHandler;
import free.minced.primary.game.InventoryHandler;
import free.minced.primary.game.MobilityHandler;
import free.minced.primary.game.PlayerHandler;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.ModeSetting;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

import static net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.START_FALL_FLYING;

@ModuleDescriptor(name = "CreativeFlight", category = ModuleCategory.MOVEMENT)
public class CreativeFlight extends Module {

    public final NumberSetting XZspeed = new NumberSetting("Horizontal Speed", this, 0.1, 0.1, 10, 0.1);
    public final NumberSetting Yspeed = new NumberSetting("Vertical Speed", this, 0.1, 0.1, 10, 0.1);

    @Override
    public void onEvent(Event event) {
        if (event instanceof UpdatePlayerEvent) {
            if (!mc.player.getAbilities().flying) return;
            if (MobilityHandler.isMoving()) {
                final double[] dir = MobilityHandler.forward(XZspeed.getValue().doubleValue());
                mc.player.setVelocity(dir[0], 0, dir[1]);
            } else mc.player.setVelocity(0, 0, 0);

            if (mc.options.jumpKey.isPressed()) mc.player.setVelocity(mc.player.getVelocity().add(0, Yspeed.getValue().doubleValue(), 0));
            if (mc.options.sneakKey.isPressed()) mc.player.setVelocity(mc.player.getVelocity().add(0, -Yspeed.getValue().doubleValue(), 0));

        }
    }



}