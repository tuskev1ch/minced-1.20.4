package free.minced.events.impl.player;

import free.minced.events.Event;
import lombok.Getter;

@Getter
public class EventSync extends Event {
    public EventSync(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    final float yaw;
    final float pitch;
    Runnable postAction;

    public void addPostAction(Runnable r) {
        postAction = r;
    }

}