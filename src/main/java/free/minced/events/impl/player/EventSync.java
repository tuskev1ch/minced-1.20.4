package free.minced.events.impl.player;

import free.minced.events.Event;

public class EventSync extends Event {
    public EventSync(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    float yaw;
    float pitch;
    Runnable postAction;

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void addPostAction(Runnable r) {
        postAction = r;
    }

    public Runnable getPostAction() {
        return postAction;
    }
}