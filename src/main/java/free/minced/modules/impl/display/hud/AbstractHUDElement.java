package free.minced.modules.impl.display.hud;

import free.minced.modules.Module;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.color.ClientColors;
import free.minced.framework.color.CustomColor;
import free.minced.primary.game.MobilityHandler;
import free.minced.systems.draggable.Draggable;

public abstract class AbstractHUDElement extends Module implements IHUDElement {
    private final Animation potionsAnimation = new Animation(Easing.EASE_IN_OUT_CUBIC, 600);
    private final Animation widthAnimation = new Animation(Easing.EASE_IN_OUT_SINE, 400);
    private final Animation heightAnimation = new Animation(Easing.EASE_IN_OUT_SINE, 400);
    private final MobilityHandler mobilityHandler = new MobilityHandler();
    @Override
    public float getDefaultWidth() {
        return 80;
    }

    @Override
    public float getHeaderHeight() {
        return 16;
    }

    @Override
    public float getCornerRadius() {
        return 3;
    }

    @Override
    public Animation getPotionsAnimation() {
        return potionsAnimation;
    }


    @Override
    public Animation getWidthAnimation() {
        return widthAnimation;
    }


    @Override
    public Animation getHeightAnimation() {
        return heightAnimation;
    }

    @Override
    public boolean isDraggable() {
        return getDraggable() != null;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public Draggable getDraggable() {
        return null;
    }

    @Override
    public String getHeaderLabel() {
        return null;
    }

    @Override
    public CustomColor getHeaderColor() {
        return ClientColors.getBrighterBackgroundColor();
    }

    @Override
    public CustomColor getBackgroundColor() {
        return ClientColors.getBackgroundColor();
    }


}
