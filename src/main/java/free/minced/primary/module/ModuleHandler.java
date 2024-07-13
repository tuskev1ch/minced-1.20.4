package free.minced.primary.module;


import free.minced.Minced;
import free.minced.events.Event;
import free.minced.modules.Module;
import free.minced.primary.IHolder;
import free.minced.primary.game.MobilityHandler;
import free.minced.systems.draggable.Draggable;

public abstract class ModuleHandler extends MobilityHandler implements IHolder {


    public Draggable registerDraggable(Module module, String name, float x, float y) {
        Minced.getInstance().getDraggableHandler().draggables.put(name, new Draggable(module, name, x, y));
        return Minced.getInstance().getDraggableHandler().draggables.get(name);
    }

    public abstract void onEvent(final Event event);

}