package free.minced.events.impl.mobility;

import net.minecraft.util.math.BlockPos;
import free.minced.events.Event;

public class EventBreakBlock extends Event {
    private final BlockPos bp;

    public EventBreakBlock(BlockPos bp) {
        this.bp = bp;
    }

    public BlockPos getPos() {
        return bp;
    }
}
