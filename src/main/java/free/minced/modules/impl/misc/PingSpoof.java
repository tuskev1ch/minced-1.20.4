package free.minced.modules.impl.misc;

import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.player.PacketEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.modules.impl.movement.Scaffold;
import free.minced.primary.IHolder;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.NumberSetting;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.Getter;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

import java.util.HashSet;

@Getter
@ModuleDescriptor(name = "PingSpoof", category = ModuleCategory.MISC)
public class PingSpoof extends Module {

    public NumberSetting ping = new NumberSetting("Ping",this,500f,1f,1500f,1f);
    public BooleanSetting scaffoldOnly = new BooleanSetting("Scaffold only", this, false);
    private final Object2LongMap<KeepAliveC2SPacket> packets = new Object2LongOpenHashMap<>();

    @Override
    public void onEvent(Event e) {
        if (e instanceof PacketEvent.Send event) {
            if (scaffoldOnly.isEnabled() && !Minced.getInstance().getModuleHandler().get(Scaffold.class).isEnabled()) return;
            if (mc.player == null || mc.world == null) return;

            if (event.getPacket() instanceof KeepAliveC2SPacket packet) {
                if (!this.packets.isEmpty() && new HashSet<>(this.packets.keySet()).contains(packet)) {
                    this.packets.removeLong(packet);
                    return;
                }

                this.packets.put(packet, System.currentTimeMillis());

                event.setCancel(true);
            }
        }

        if (e instanceof PacketEvent.Receive event) {
            if (scaffoldOnly.isEnabled() && !Minced.getInstance().getModuleHandler().get(Scaffold.class).isEnabled()) return;
            if (mc.player == null || mc.world == null) return;

            for (KeepAliveC2SPacket packet : new HashSet<>(this.packets.keySet())) {
                if (this.packets.getLong(packet) + (long) ping.getValue().longValue() <= System.currentTimeMillis()) {
                    mc.getNetworkHandler().sendPacket(packet);
                    break;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (scaffoldOnly.isEnabled() && !Minced.getInstance().getModuleHandler().get(Scaffold.class).isEnabled()) return;
        if (mc.player == null || mc.world == null) return;

        if (!this.packets.isEmpty()) {
            for (KeepAliveC2SPacket packet : new HashSet<>(this.packets.keySet())) {
                if (this.packets.getLong(packet) + (long) this.ping.getValue().longValue() <= System.currentTimeMillis()) {
                    mc.getNetworkHandler().sendPacket(packet);
                }
            }
        }
        super.onDisable();
    }
}
