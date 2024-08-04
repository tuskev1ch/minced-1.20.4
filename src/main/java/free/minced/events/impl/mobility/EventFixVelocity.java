package free.minced.events.impl.mobility;


import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.Vec3d;
import free.minced.events.Event;


public class EventFixVelocity extends Event {
    @Getter
    final Vec3d movementInput;
    @Getter
    final float speed;
    final float yaw;
    @Getter
    @Setter
    Vec3d velocity;

    public EventFixVelocity(Vec3d movementInput, float speed, float yaw, Vec3d velocity) {
        this.movementInput = movementInput;
        this.speed = speed;
        this.yaw = yaw;
        this.velocity = velocity;
    }

}
