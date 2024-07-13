package free.minced.events;


import free.minced.Minced;
import free.minced.modules.impl.misc.UnHook;

import java.util.concurrent.CopyOnWriteArrayList;


public class EventCollects {

    private static final CopyOnWriteArrayList<EventLogic> registeredEvents = new CopyOnWriteArrayList<>();

    public static void call(Event event) {
        if (Minced.getInstance().getModuleHandler().get(UnHook.class).isEnabled()) return;
        registeredEvents.forEach(m -> m.onEvent(event));
    }

    public static void registerListener(EventLogic klassListener) {
        registeredEvents.add(klassListener);
    }

    public static void unRegisterListener(EventLogic klassListener) {
        registeredEvents.remove(klassListener);
    }


}