package free.minced.modules.impl.display.hud;

import net.minecraft.client.util.math.MatrixStack;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.color.CustomColor;
import free.minced.systems.draggable.Draggable;

public interface IHUDElement {

    void render(MatrixStack matrixStack);

    float getDefaultWidth();

    float getHeaderHeight();

    float getCornerRadius();

    Animation getPotionsAnimation();

    Animation getWidthAnimation();

    Animation getHeightAnimation();


    String getHeaderLabel();

    boolean isDraggable();

    Draggable getDraggable();

    boolean isVisible();

    CustomColor getHeaderColor();

    CustomColor getBackgroundColor();

}
