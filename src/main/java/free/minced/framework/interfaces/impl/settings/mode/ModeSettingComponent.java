package free.minced.framework.interfaces.impl.settings.mode;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import free.minced.framework.interfaces.api.SettingComponent;
import free.minced.framework.interfaces.impl.module.ModuleComponent;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.font.Fonts;
import free.minced.systems.setting.Setting;
import free.minced.systems.setting.impl.ModeSetting;


import java.util.ArrayList;
import java.util.List;

/**
 * @author jbk
 * @since 18.12.2023
 */


@Getter
public class ModeSettingComponent extends SettingComponent {

    private ModeSetting modeSetting;
    private final List<ModeComponent> modeComponents = new ArrayList<>();
    private boolean expanded;
    private float rightMargin, notExpandedHeight;
    private Animation heightAnimation, alphaAnimation, rotateAnimation;


    public ModeSettingComponent(Setting setting, ModuleComponent moduleComponent) {
        super(setting, moduleComponent);
    }

    @Override
    public void init() {

        modeSetting = (ModeSetting) getSetting();

        width = 70;
        rightMargin = 5;
        notExpandedHeight = 15;

        // шрифты


        // инициализируем анимации
        heightAnimation = new Animation(Easing.EASE_OUT_EXPO, 500);
        alphaAnimation = new Animation(Easing.LINEAR, 300);
        rotateAnimation = new Animation(Easing.EASE_IN_EXPO, 300);

        // обновляем список с компонентами
        modeComponents.clear();
        for (String mode : modeSetting.getModes()) {
            ModeComponent component = new ModeComponent(mode, this);
            modeComponents.add(component);

            // инициализируем элемент
            component.init();
        }

        heightAnimation.setValue(notExpandedHeight);

        super.init();
    }

    @Override
    public void render(DrawContext pDrawContext, float x, float y, int mouseX, int mouseY) {

        float leftMargin = 5;

        // обновляем анимации
        alphaAnimation.run(expanded ? 1 : 0);
        rotateAnimation.run(expanded ? 180 : 0);

        // бг
        DrawHandler.drawRound(pDrawContext.getMatrices(), x + getModuleComponent().width - width - 10, y, width, height, 3, ClientColors.getSecondaryBackgroundColor().withAlpha(255 * getClickGUI().getAlpha().getValue()));


        if (getClickGUI().getAlpha().getValue() > 0.05f) {
            Fonts.SEMI_13.drawString(pDrawContext.getMatrices(), modeSetting.getName(), x, y + 5, ClientColors.getFontColor().withAlpha(255 * getClickGUI().getAlpha().getValue()).getRGB());
            Fonts.SEMI_13.drawString(pDrawContext.getMatrices(), modeSetting.getCurrentMode(), x + getModuleComponent().width - width - 10 + leftMargin, y + 6, ClientColors.getFontColor().withAlpha(255 * getClickGUI().getAlpha().getValue()).getRGB());

        }


        // рендерим другие моды если сеттинг открыт
        float offset = 0, gap = 3;
        for (ModeComponent component : modeComponents) { // они рендерятся всегда с нулевой прозрачностью, мы ее увеличиваем когда нужно
            component.render(pDrawContext, x + getModuleComponent().width - width - 10 + leftMargin, y + notExpandedHeight + gap + offset, mouseX, mouseY);
            if (component.getAlphaAnimation().getValue() > 0.01f) {
                offset += component.getHeight() + gap;
            }
        }

        // обновляем анимации
        heightAnimation.run(expanded ? notExpandedHeight + offset + 5 : notExpandedHeight);

        height = heightAnimation.getValue();

        super.render(pDrawContext, x, y, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        hoveringClickableArea = isHovered(x, y, getModuleComponent().getWidth() - rightMargin, heightAnimation.getValue(), mouseX, mouseY);

        if (! hoveringClickableArea) return;

        boolean hoveringHeader = isHovered(x, y, getModuleComponent().getWidth() - rightMargin, notExpandedHeight, mouseX, mouseY);
        if (hoveringHeader && button < 2) { // позволяем открывать настройку нажатием левой и правой кнопкой мыши
            expanded = ! expanded;
        }

        if (expanded) {
            for (ModeComponent component : modeComponents) {
                component.mouseClicked(mouseX, mouseY, button);
            }
        }

        super.mouseClicked(mouseX, mouseY, button);
    }
}