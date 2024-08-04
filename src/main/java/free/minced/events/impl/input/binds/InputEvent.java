package free.minced.events.impl.input.binds;

import free.minced.events.Event;
import lombok.Getter;

@Getter
public class InputEvent extends Event {
    private final int type; // 0 for mouse, 1 for keyboard
    private final int buttonOrKey; // button code for mouse, key code for keyboard

    public InputEvent(int type, int buttonOrKey) {
        this.type = type;
        this.buttonOrKey = buttonOrKey;
    }

    public boolean isKeyDown(int key) {
        return key == buttonOrKey;
    }

}