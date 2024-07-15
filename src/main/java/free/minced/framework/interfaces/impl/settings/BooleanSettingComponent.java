package free.minced.framework.interfaces.impl.settings;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import free.minced.Minced;
import free.minced.framework.interfaces.api.SettingComponent;
import free.minced.framework.interfaces.impl.module.ModuleComponent;
import free.minced.framework.animation.color.ColorAnimation;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.font.Fonts;
import free.minced.systems.setting.Setting;
import free.minced.systems.setting.impl.BooleanSetting;

@Getter
public class BooleanSettingComponent extends SettingComponent {


    private BooleanSetting booleanSetting;
    // анимации
    private ColorAnimation color;
    private Animation xAnimation, enableAlphaAnimation, glowAlphaAnimation;


    private float circleScale;

    public BooleanSettingComponent(Setting setting, ModuleComponent moduleComponent) {
        super(setting, moduleComponent);
    }

    @Override
    public void init() {
        // сеттинг
        this.booleanSetting = (BooleanSetting) getSetting();

        // размеры
        this.height = 10;
        this.width = 20;
        this.circleScale = 5;

        // анимации
        xAnimation = new Animation(Easing.EASE_IN_OUT_QUINT, 500);
        enableAlphaAnimation = new Animation(Easing.LINEAR, 500);
        glowAlphaAnimation = new Animation(Easing.EASE_OUT_SINE, 500);
        float inPadding = 3;
        xAnimation.setValue(getBooleanSetting().isEnabled() ? width - circleScale - inPadding : inPadding);

        color = new ColorAnimation(1000);
        color.setColor(getBooleanSetting().isEnabled() ? ClientColors.getFirstColor() : ClientColors.getFontColor());


        super.init();
    }

    @Override
    public void render(DrawContext pDrawContext, float x, float y, int mouseX, int mouseY) {
        if (getClickGUI().getAlpha().getValue() < 0.005) return;
        // обновляем анимации
        color.run(getBooleanSetting().isEnabled() ? ClientColors.getFirstColor() : ClientColors.getFontColor());
        float inPadding = 3;
        xAnimation.run(getBooleanSetting().isEnabled() ? width - circleScale - inPadding : inPadding);

        enableAlphaAnimation.run(getBooleanSetting().isEnabled() ? 255 : 100);
        glowAlphaAnimation.run(getBooleanSetting().isEnabled() ? 1 : 0);

        float rightMargin = 10; // на самом деле там отступ в 5 пикселей, но рендер настроек начинается с x + 5, поэтому приходится делать так

        // бекграунд
        DrawHandler.drawRound(pDrawContext.getMatrices(), x + getModuleComponent().getWidth() - rightMargin - width, y, width, height, 2.5f, ClientColors.getSecondaryBackgroundColor().withAlpha(255 * getClickGUI().getAlpha().getValue()));

        // circle glow

        // кружочек ебанный хз как он называется
        DrawHandler.drawRoundGradient(pDrawContext.getMatrices(), x + getModuleComponent().getWidth() - rightMargin - width + xAnimation.getValue(),
                y + getMiddleOfBox(circleScale, height), circleScale, circleScale,2.5f, color.getColor().withAlpha(enableAlphaAnimation.getValue() * getClickGUI().getAlpha().getValue()), color.getColor().withAlpha(enableAlphaAnimation.getValue() * getClickGUI().getAlpha().getValue()));

        // имя сеттинга
        if ((enableAlphaAnimation.getValue() / 255) * getClickGUI().getAlpha().getValue() > 0.05F) {
            Fonts.SEMI_13.drawString(pDrawContext.getMatrices(), getBooleanSetting().getName(), x, y + 3.5f, ClientColors.getFontColor().withAlpha(255 * getClickGUI().getAlpha().getValue()).getRGB());
        }
        super.render(pDrawContext, x, y, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        this.hoveringClickableArea = isHovered(x, y, getModuleComponent().width, height, mouseX, mouseY);

        if (hoveringClickableArea && button == 0) {
            getBooleanSetting().toggle();
            Minced.getInstance().getConfigHandler().save("autocfg");
        }

        super.mouseClicked(mouseX, mouseY, button);
    }
}