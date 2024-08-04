package free.minced.primary;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;

public interface IAccess {
    Tessellator TESSELLATOR = Tessellator.getInstance();
    BufferBuilder BUILDER = TESSELLATOR.getBuffer();
}
