package free.minced.events.impl.player;

import free.minced.events.Event;
import lombok.Getter;

@Getter
public class EventPlayerJump extends Event {
    private final boolean pre;

    public EventPlayerJump(boolean pre) {
        this.pre = pre;
    }

}
