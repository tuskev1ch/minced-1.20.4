package free.minced.framework.interfaces.impl.settings.multibox;


import free.minced.framework.render.ScissorHandler;
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
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.MultiBoxSetting;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jbk
 * @since 18.12.2023
 */


@Getter
public class MultiBoxSettingComponent extends SettingComponent {

    private MultiBoxSetting multiBoxSetting;
    private final List<SubComponent> subComponents = new ArrayList<>();
    private boolean expanded;
    private float rightMargin, notExpandedHeight;
    private Animation heightAnimation, alphaAnimation, rotateAnimation;


    public MultiBoxSettingComponent(Setting setting, ModuleComponent moduleComponent) {
        super(setting, moduleComponent);
    }

    @Override
    public void init() {

        multiBoxSetting = (MultiBoxSetting) getSetting();

        width = 70;
        rightMargin = 5;
        notExpandedHeight = 15;

        // шрифты


        // инициализируем анимации
        heightAnimation = new Animation(Easing.EASE_OUT_EXPO, 500);
        alphaAnimation = new Animation(Easing.LINEAR, 300);
        rotateAnimation = new Animation(Easing.EASE_IN_EXPO, 300);

        // обновляем список с компонентами
        subComponents.clear();
        for (BooleanSetting setting : multiBoxSetting.getBoolSettings()) {
            SubComponent component = new SubComponent(setting, this);
            subComponents.add(component);

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
        DrawHandler.drawRound(pDrawContext.getMatrices(), x + getModuleComponent().width - width - 10, y, width, height,  3,ClientColors.getSecondaryBackgroundColor().withAlpha(255 * getClickGUI().getAlpha().getValue()));


        if (getClickGUI().getAlpha().getValue() > 0.05f) {


            // имя сеттинга
            Fonts.SEMI_13.drawString(pDrawContext.getMatrices(), multiBoxSetting.getName(), x, y + 5, ClientColors.getFontColor().withAlpha(255 * getClickGUI().getAlpha().getValue()).getRGB());

            // выбранные элементы
            String enabledSettings = multiBoxSetting.getBoolSettings().stream()
                    .filter(BooleanSetting::isEnabled)
                    .map(BooleanSetting::getName)
                    .collect(Collectors.joining(", "));

            enabledSettings = enabledSettings.substring(0, Math.min(enabledSettings.length(), 19));

            if (enabledSettings.isEmpty()) {
                Fonts.SEMI_13.drawString(pDrawContext.getMatrices(), "None", x + getModuleComponent().width - width - 10 + leftMargin, y + 6, ClientColors.getFontColor().withAlpha(255 * getClickGUI().getAlpha().getValue()).getRGB());
            } else {
                Fonts.SEMI_13.drawString(pDrawContext.getMatrices(), enabledSettings, x + getModuleComponent().width - width - 10 + leftMargin, y + 6, ClientColors.getFontColor().withAlpha(255 * getClickGUI().getAlpha().getValue()).getRGB());
            }
        }

        // рендерим другие моды если сеттинг открыт
        float offset = 0, gap = 3;
        for (SubComponent component : subComponents) { // они рендерятся всегда с нулевой прозрачностью, мы ее увеличиваем когда нужно
            component.render(pDrawContext, x + getModuleComponent().width - width - 10 + leftMargin, y + notExpandedHeight + 3 + offset, mouseX, mouseY);
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
            for (SubComponent component : subComponents) {
                component.mouseClicked(mouseX, mouseY, button);
            }
        }

        super.mouseClicked(mouseX, mouseY, button);
    }

    private String getSelectedItemsAsString() {
        StringBuilder selectedItemsBuilder = new StringBuilder();
        List<BooleanSetting> selectedSettings = subComponents.stream()
                .map(SubComponent::getSetting)
                .filter(BooleanSetting::isEnabled)
                .toList();
        for (int i = 0; i < selectedSettings.size(); i++) {
            selectedItemsBuilder.append(selectedSettings.get(i).getName());
            if (i < selectedSettings.size() - 1) {
                selectedItemsBuilder.append(", ");
            }
        }
        return selectedItemsBuilder.toString();
    }
}