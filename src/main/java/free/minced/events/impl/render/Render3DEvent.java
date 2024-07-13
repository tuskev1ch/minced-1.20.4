package free.minced.events.impl.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.math.MatrixStack;
import free.minced.events.Event;

@Getter
@Setter
@AllArgsConstructor
public class Render3DEvent extends Event {

    private final MatrixStack stack;

    private final float partialTicks;

}