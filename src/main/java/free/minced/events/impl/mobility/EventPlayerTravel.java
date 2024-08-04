package free.minced.events.impl.mobility;

import lombok.Getter;
import net.minecraft.util.math.Vec3d;
import free.minced.events.Event;

@Getter
public class EventPlayerTravel extends Event {
    private final Vec3d mVec;
    private final boolean pre;

    public EventPlayerTravel(Vec3d mVec,boolean pre) {
        this.mVec = mVec;
        this.pre = pre;
    }

}