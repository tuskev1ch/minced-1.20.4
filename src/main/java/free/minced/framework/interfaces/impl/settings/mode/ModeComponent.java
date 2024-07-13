package free.minced.framework.interfaces.impl.settings.mode;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import free.minced.framework.interfaces.api.CustomElement;
import free.minced.framework.animation.color.ColorAnimation;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.color.ClientColors;
import free.minced.framework.font.Fonts;


/**
 * @author jbk
 * @since 18.12.2023
 */


@Getter
public class ModeComponent extends CustomElement {

    private final String mode;
    private final ModeSettingComponent parent;
    private Animation alphaAnimation, hoverAnimation;
    private ColorAnimation selectColorAnimation;

    public ModeComponent(String mode, ModeSettingComponent parent) {
        this.mode = mode;
        this.parent = parent;
    }

    @Override
    public void init() {
        this.alphaAnimation = new Animation(Easing.LINEAR, 300);
        this.selectColorAnimation = new ColorAnimation(500);
        this.hoverAnimation = new Animation(Easing.EASE_IN_CUBIC, 200);

        this.width = Fonts.SEMI_13.getStringWidth(mode);
        this.height = 7.5f;
        super.init();
    }

    public void render(DrawContext pDrawContext, float x, float y, int mouseX, int mouseY) {
        this.x = x + 5;
        this.y = y;

        // обновляем анимации
        selectColorAnimation.run(mode.equals(parent.getModeSetting().getCurrentMode()) ? ClientColors.getFirstColor().darker().darker() : ClientColors.getFontColor());
        hoverAnimation.run(isHovered(x, y, width, height, mouseX, mouseY) || mode.equals(parent.getModeSetting().getCurrentMode()) ? 1 : 0);

        alphaAnimation.setDuration(parent.isExpanded() ? 500 : 50);
        if (parent.getHeightAnimation().getValue() >= parent.getHeightAnimation().getDestinationValue() - 10) { // almost done check
            alphaAnimation.run(parent.isExpanded() ? 1 : 0);
        }

        if (alphaAnimation.getValue() > 0.1 && getClickGUI().getAlpha().getValue() > 0.05) {

            // рендерим строку
            Fonts.SEMI_13.drawString(pDrawContext.getMatrices(), mode, x, y + 2.5f, selectColorAnimation.getColor().withAlpha((150 * alphaAnimation.getValue() + 100 * hoverAnimation.getValue()) * getClickGUI().getAlpha().getValue()).getRGB());
        }

        super.render(pDrawContext, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {

        if (isHovered(x, y, width, height, mouseX, mouseY) && button == 0) {
            parent.getModeSetting().setCurrentMode(mode);
        }

        super.mouseClicked(mouseX, mouseY, button);
    }



}