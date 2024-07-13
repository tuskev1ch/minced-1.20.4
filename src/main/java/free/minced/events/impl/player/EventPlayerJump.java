package free.minced.events.impl.player;

import free.minced.events.Event;

public class EventPlayerJump extends Event {
    private boolean pre;

    public EventPlayerJump(boolean pre) {
        this.pre = pre;
    }

    public boolean isPre() {
        return pre;
    }
}
