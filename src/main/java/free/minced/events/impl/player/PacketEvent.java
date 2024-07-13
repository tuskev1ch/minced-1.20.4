package free.minced.events.impl.player;

import net.minecraft.network.packet.Packet;
import free.minced.events.Event;

public class PacketEvent extends Event {
    private final Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public <T extends Packet<?>> T getPacket() {
        return (T) this.packet;
    }

    public static class Send extends PacketEvent {
        public Send(Packet<?> packet) {
            super(packet);
        }
    }

    public static class Receive extends PacketEvent {
        public Receive(Packet<?> packet) {
            super(packet);
        }
    }

    public static class SendPost extends PacketEvent {
        public SendPost(Packet<?> packet) {
            super(packet);
        }
    }

    public static class ReceivePost extends PacketEvent {
        public ReceivePost(Packet<?> packet) {
            super(packet);
        }
    }
}