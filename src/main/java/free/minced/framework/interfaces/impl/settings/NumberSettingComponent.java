package free.minced.framework.interfaces.impl.settings;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import free.minced.Minced;
import free.minced.framework.interfaces.api.SettingComponent;
import free.minced.framework.interfaces.impl.module.ModuleComponent;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.primary.math.MathHandler;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.color.ColorHandler;
import free.minced.primary.time.TimerHandler;
import free.minced.framework.font.Fonts;
import free.minced.systems.setting.Setting;
import free.minced.systems.setting.impl.NumberSetting;


@Getter
public class NumberSettingComponent extends SettingComponent {

    // другое
    private boolean dragging, hoveringSlider;
    private final TimerHandler timer = new TimerHandler();
    private float percentage, renderPercentage;

    // шрифты

    private float sliderY = 3;

    // размеры
    private float sliderHeight, sliderWidth;
    private float nameWidth;

    // анимации
    private final Animation hoverAnimation = new Animation(Easing.EASE_OUT_CIRC, 200);


    public NumberSettingComponent(Setting setting, ModuleComponent moduleComponent) {
        super(setting, moduleComponent);
        NumberSetting numberSetting = (NumberSetting) setting;
        this.percentage = (- numberSetting.getMin().floatValue() + numberSetting.getValue().floatValue()) / (- numberSetting.getMin().floatValue() + numberSetting.getMax().floatValue());
    }

    @Override
    public void init() {

        // размеры
        this.height = 10;



        super.init();
    }

    @Override
    public void render(DrawContext pDrawContext, float x, float y, final int mouseX, final int mouseY) {

        this.x = x;
        this.y = y;

        // другое
        final NumberSetting numberSetting = (NumberSetting) this.setting;
        String value = String.valueOf(numberSetting.getValue().floatValue());
        float spacing = 5;

        // размеры
        this.nameWidth = Fonts.SEMI_13.getStringWidth(setting.getName()) + spacing;
        this.sliderWidth = getModuleComponent().width - nameWidth - 30 - (Fonts.SEMI_13.getStringWidth(value) >= 25 ? dragging ? 0 : 5 : 0); // dumb wtf
        this.sliderHeight = 2;

        this.hoveringSlider = isHovered(x + nameWidth, y + sliderY, sliderWidth, sliderHeight, mouseX, mouseY);

        // anti retard
        if (value.endsWith(".0")) {
            value = value.replace(".0", "");
        }

        // название сеттинга
        if (getClickGUI().getAlpha().getValue() > 0.05) {
            Fonts.SEMI_13.drawString(pDrawContext.getMatrices(), setting.getName(), x, y + 3.5f, ClientColors.getFontColor().withAlpha(255 * getClickGUI().getAlpha().getValue()).getRGB());

            // слайдер
            DrawHandler.drawRect(pDrawContext.getMatrices(), x + nameWidth, y + (sliderY + 1.5F), sliderWidth, sliderHeight, ClientColors.getSecondaryBackgroundColor().withAlpha(255 * getClickGUI().getAlpha().getValue()));

            // текущее значение
            Fonts.SEMI_13.drawString(pDrawContext.getMatrices(), value, x + getModuleComponent().width - Fonts.SEMI_13.getStringWidth(value) - 10, y + 3.5f, ClientColors.getFontColor().withAlpha(200 * getClickGUI().getAlpha().getValue()).getRGB());
        }
        float selector = x + nameWidth;
        if (dragging) {
            percentage = mouseX - selector;
            percentage /= sliderWidth;
            percentage = Math.max(Math.min(percentage, 1), 0);

            // обновляем текущее значение
            numberSetting.setValue((numberSetting.getMin().floatValue() + (numberSetting.getMax().floatValue() - numberSetting.getMin().floatValue()) * percentage));
            numberSetting.setValue(MathHandler.roundWithSteps(numberSetting.getValue().floatValue(), numberSetting.getStep().floatValue() / 2f));
        }

        // анимации
        final int speed = 50;
        for (int i = 0; i <= timer.getElapsedTime().floatValue(); i++) {
            renderPercentage = (renderPercentage * (speed - 1) + percentage) / speed;
        }

        // анимированная позиция
        final float positionX = selector + renderPercentage * sliderWidth;

        float grabberScale = 5;

        // граббер
        DrawHandler.drawRound(pDrawContext.getMatrices(), positionX - grabberScale / 2.0F, y + sliderY, grabberScale, grabberScale, 2.5f, ColorHandler.applyOpacity(ClientColors.getFirstColor(), (255 + (255 * hoverAnimation.getValue())) * getClickGUI().getAlpha().getValue()));

        timer.reset();
    }


    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        this.hoveringClickableArea = isHovered(x, y + sliderY, nameWidth + sliderWidth, height, mouseX, mouseY);

        if (hoveringClickableArea && button == 0) {
            dragging = true;
        }

        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        // при отпускании мыши мы больше не таскаем эту хуйню
        dragging = false;
        //Minced.getInstance().getConfigHandler().save("default");
        super.mouseReleased(mouseX, mouseY, button);
    }
}