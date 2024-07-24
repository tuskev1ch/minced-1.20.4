package free.minced.modules.impl.render;

import free.minced.events.Event;
import free.minced.events.impl.player.PacketEvent;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

@ModuleDescriptor(name = "CustomWorld", category = ModuleCategory.RENDER)
public class CustomWorld extends Module {
    private final BooleanSetting nightMode = new BooleanSetting("Custom Time", this, true);
    private final NumberSetting time = new NumberSetting("Time", this, 12, 1, 24, 1, () -> !nightMode.isEnabled());

    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent event) {
            if (nightMode.isEnabled()) {
                if (mc.world != null) {
                    mc.world.setTimeOfDay(time.getValue().intValue() * 1000);
                }
            }
        }
        if (e instanceof PacketEvent.Receive event) {
            if (nightMode.isEnabled()) {
                if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
                    event.setCancel(true);
                }
            }
        }
    }
}