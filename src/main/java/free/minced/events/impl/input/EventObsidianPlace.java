package free.minced.events.impl.input;

import free.minced.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;

@AllArgsConstructor
@Getter
public class EventObsidianPlace extends Event {

    private final BlockPos pos;

}
