package free.minced.framework.interfaces.impl.settings.multibox;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import free.minced.framework.interfaces.api.CustomElement;
import free.minced.framework.animation.color.ColorAnimation;
import free.minced.framework.animation.normal.Animation;

import free.minced.framework.animation.normal.Easing;
import free.minced.framework.color.ClientColors;
import free.minced.framework.font.Fonts;
import free.minced.systems.setting.impl.BooleanSetting;


/**
 * @author jbk
 * @since 18.12.2023
 */


@Getter
public class SubComponent extends CustomElement {

    private final BooleanSetting setting;
    private final MultiBoxSettingComponent parent;
    private Animation alphaAnimation, hoverAnimation;
    private ColorAnimation selectColorAnimation;

    public SubComponent(BooleanSetting setting, MultiBoxSettingComponent parent) {
        this.setting = setting;
        this.parent = parent;
    }

    @Override
    public void init() {
        this.alphaAnimation = new Animation(Easing.LINEAR, 300);
        this.selectColorAnimation = new ColorAnimation(500);
        this.hoverAnimation = new Animation(Easing.EASE_IN_CUBIC, 200);

        this.width = Fonts.SEMI_13.getStringWidth(setting.getName());
        this.height = 7.5f;
        super.init();
    }

    public void render(DrawContext pDrawContext, float x, float y, int mouseX, int mouseY) {
        this.x = x + 5;
        this.y = y;

        // обновляем анимации
        selectColorAnimation.run(setting.isEnabled() ? ClientColors.getFirstColor().darker().darker() : ClientColors.getFontColor());
        hoverAnimation.run(isHovered(x, y, width, height, mouseX, mouseY) || setting.isEnabled() ? 1 : 0);

        alphaAnimation.setDuration(parent.isExpanded() ? 500 : 50);
        if (parent.getHeightAnimation().getValue() >= parent.getHeightAnimation().getDestinationValue() - 10) { // almost done check
            alphaAnimation.run(parent.isExpanded() ? 1 : 0);
        }



        if (alphaAnimation.getValue() > 0.1 && getClickGUI().getAlpha().getValue() > 0.05) {
            // рендерим строку
            Fonts.SEMI_13.drawString(pDrawContext.getMatrices(), setting.getName(), x, y + 2.5f, selectColorAnimation.getColor().withAlpha((150 * alphaAnimation.getValue() + 100 * hoverAnimation.getValue()) * getClickGUI().getAlpha().getValue()).getRGB());

        }

        super.render(pDrawContext, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {

        int enabledSettingsCount = (int) parent.getSubComponents().stream().filter(subComponent -> subComponent.getSetting().isEnabled()).count();

    if (isHovered(x, y, width, height, mouseX, mouseY) && button == 0) {
        if (!setting.isEnabled() || enabledSettingsCount > 1) {

            setting.toggle();
        }
    }/////////////////


        super.mouseClicked(mouseX, mouseY, button);
    }



}