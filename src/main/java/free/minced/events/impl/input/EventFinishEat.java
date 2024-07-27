package free.minced.events.impl.input;

import free.minced.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.Item;

@Getter
@AllArgsConstructor
public class EventFinishEat extends Event {

    private final Item item;

}