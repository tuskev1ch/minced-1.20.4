package free.minced.events.impl.input;

import free.minced.events.Event;
import lombok.Getter;

@Getter
public class EventMouse extends Event {
    final int button;

    final int action;

    public EventMouse(int b,int action){
        button = b;
        this.action = action;
    }
}
