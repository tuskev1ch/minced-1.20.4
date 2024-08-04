package free.minced.primary;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.packet.Packet;
import free.minced.Minced;
import free.minced.framework.interfaces.InterfaceScreen;
import free.minced.mixin.accesors.IClientWorldMixin;
import free.minced.systems.theme.Theme;

public interface IHolder {

    MinecraftClient mc = MinecraftClient.getInstance();
    AdjustedDisplay sr = new AdjustedDisplay();

    static boolean fullNullCheck() {
        return mc.player == null || mc.world == null;
    }

    default Theme getTheme() {
        return Minced.getInstance().getThemeHandler().getTheme();
    }

    default void setTheme(Theme theme) {
        Minced.getInstance().getThemeHandler().setTheme(theme);
    }

    static void sendPacket(Packet<?> packet) {
        if (mc.getNetworkHandler() == null) return;

        mc.getNetworkHandler().sendPacket(packet);
    }

    static void sendSequencedPacket(SequencedPacketCreator packetCreator) {
        if (mc.getNetworkHandler() == null || mc.world == null) return;
        try (PendingUpdateManager pendingUpdateManager = ((IClientWorldMixin) mc.world).getPendingUpdateManager().incrementSequence()){
            int i = pendingUpdateManager.getSequence();
            mc.getNetworkHandler().sendPacket(packetCreator.predict(i));
        }
    }
    default InterfaceScreen getClickGUI() {
        return Minced.getInstance().getInterfaceScreen();
    }


}