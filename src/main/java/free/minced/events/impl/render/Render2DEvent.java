package free.minced.events.impl.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import free.minced.events.Event;

@Getter
@Setter
@AllArgsConstructor
public class Render2DEvent extends Event {
    private final MatrixStack stack;
    private final DrawContext context;
    private final float partialTicks;
}
