package free.minced.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import free.minced.events.EventCollects;
import free.minced.events.impl.player.PacketEvent;
import free.minced.modules.Module;
import free.minced.systems.SharedClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static free.minced.primary.IHolder.fullNullCheck;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void onHandlePacket(Packet<T> packet, PacketListener listener, CallbackInfo info) {
        if (fullNullCheck()) return;
        PacketEvent.Receive event = new PacketEvent.Receive(packet);
        EventCollects.call(event);
        SharedClass.onPacketReceive(event);
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            Module.setBackTimer.reset();
        }
        if (event.isCancel()) {
            info.cancel();
        }
    }

    @Inject(method = "handlePacket", at = @At("TAIL"), cancellable = true)
    private static <T extends PacketListener> void onHandlePacketPost(Packet<T> packet, PacketListener listener, CallbackInfo info) {
        if (fullNullCheck()) return;
        PacketEvent.ReceivePost event = new PacketEvent.ReceivePost(packet);
        EventCollects.call(event);
        if (event.isCancel()) {
            info.cancel();
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"),cancellable = true)
    private void onSendPacketPre(Packet<?> packet, CallbackInfo info) {
        if (fullNullCheck()) return;

        PacketEvent.Send event = new PacketEvent.Send(packet);
        EventCollects.call(event);
        SharedClass.onSyncWithServer(event);
        if (event.isCancel()) info.cancel();
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("RETURN"),cancellable = true)
    private void onSendPacketPost(Packet<?> packet, CallbackInfo info) {
        if (fullNullCheck()) return;
        PacketEvent.SendPost event = new PacketEvent.SendPost(packet);
        EventCollects.call(event);
        if (event.isCancel()) info.cancel();
    }
}