package free.minced.framework.buttons.impl;

import lombok.Getter;
import lombok.Setter;
import free.minced.framework.buttons.ButtonElement;
import free.minced.framework.animation.color.ColorAnimation;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.font.Fonts;
import net.minecraft.client.util.math.MatrixStack;

/**
 * @author jbk
 * @since 23.12.2023
 */
@Getter @Setter
public class ColoredButtonElement extends ButtonElement {

    private final ColorAnimation firstColorAnimation, secondColorAnimation;
    private float alpha;
    private final String text;

    /**
     * конструктор
     *
     * @param x      координата кнопки по Х
     * @param y      координата кнопки по Y
     * @param width  ширина кнопки
     * @param height высота кнопки
     * @param action действие, которое произойдет, когда кнопка будет нажата
     * @param alpha альфа которая будет применена к кнопке.
     * @param text текст который будет написан на кнопке
     */
    public ColoredButtonElement(float x, float y, float width, float height, float alpha, String text, Runnable action) {
        super(x, y, width, height, action);
        this.alpha = alpha;
        this.text = text;
        this.firstColorAnimation = new ColorAnimation(500);
        this.secondColorAnimation = new ColorAnimation(500);
    }

    @Override
    public void draw(MatrixStack pPoseStack, int mouseX, int mouseY) {


        // обновляем анимации
        secondColorAnimation.run(isHovered(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY) ? ClientColors.getSecondColor() : ClientColors.getBackgroundColor().withAlpha(alpha));
        firstColorAnimation.run(isHovered(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY) ? ClientColors.GRAY : ClientColors.getBackgroundColor().withAlpha(alpha));

        // бгs
        DrawHandler.drawRound(pPoseStack, getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 3,ClientColors.getBackgroundColor().withAlpha(alpha));

        DrawHandler.drawRound(pPoseStack, getX(), getY(), getWidth(), getHeight(),3,  firstColorAnimation.getColor().withAlpha(alpha));

        // текст XYETA
        if (alpha > 10) {
            Fonts.SEMI_16.drawCenteredString(pPoseStack, text, getX() + getWidth() / 2f, getY() + 7.5f, ClientColors.getFontColor().withAlpha(alpha).getRGB());
        }

        super.draw(pPoseStack, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
    }
}
