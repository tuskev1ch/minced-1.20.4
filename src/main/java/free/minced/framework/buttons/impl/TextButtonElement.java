package free.minced.framework.buttons.impl;

import lombok.Getter;
import free.minced.framework.buttons.ButtonElement;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.font.Fonts;
import net.minecraft.client.util.math.MatrixStack;


/**
 * @author jbk
 * @since 10.10.2023
 */
@Getter
public class TextButtonElement extends ButtonElement {

    private final String displayName;

    /**
     * конструктор
     *
     * @param x      координата кнопки по Х
     * @param y      координата кнопки по Y
     * @param width  ширина кнопки
     * @param height высота кнопки
     * @param action действие которое произойдет, когда кнопка будет нажата
     */
    public TextButtonElement(float x, float y, float width, float height, Runnable action, String displayName) {
        super(x, y, width, height, action);
        this.displayName = displayName;
    }

    @Override
    public void draw(MatrixStack pPoseStack, int mouseX, int mouseY) {

        // бекграунд
        DrawHandler.drawRound(pPoseStack, getX() - 0.5f, getY() - 0.5f, getWidth() + 1, getHeight() + 1, 3, ClientColors.getBackgroundColor().withAlpha(getHoverAnimation().getValue()));

     //   DrawUtility.drawRoundedGradientHorizontal(getX() - 0.5f, getY() - 0.5f, getWidth() + 1, getHeight()+ 1,3, ClientColors.getSecondColor().withAlpha(getHoverBGAnimation().getValue()),ClientColors.getFirstColor().withAlpha(getHoverBGAnimation().getValue()));

        DrawHandler.drawRound(pPoseStack, getX(), getY(), getWidth(), getHeight(), 3, ClientColors.getBrighterBackgroundColor().withAlpha(getHoverAnimation().getValue()));

        // рендерим строку XYETA
        if (getHoverAnimation().getValue() / 255 > 0.1F) {
            Fonts.SEMI_16.drawCenteredString(pPoseStack, displayName, getX() + getWidth() / 2f, getY() + 7.5f, ClientColors.getFontColor().withAlpha(getHoverAnimation().getValue()).getRGB());
        }
        super.draw(pPoseStack, mouseX, mouseY);
    }
}