package free.minced.events.impl.input.binds;

import free.minced.events.Event;

public class InputEvent extends Event {
    private int type; // 0 for mouse, 1 for keyboard
    private int buttonOrKey; // button code for mouse, key code for keyboard

    public InputEvent(int type, int buttonOrKey) {
        this.type = type;
        this.buttonOrKey = buttonOrKey;
    }

    public int getType() {
        return type;
    }

    public int getButtonOrKey() {
        return buttonOrKey;
    }

    public boolean isKeyDown(int key) {
        return key == buttonOrKey;
    }

}