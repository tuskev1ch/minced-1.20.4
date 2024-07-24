package free.minced.framework.buttons;

import free.minced.primary.IHolder;
import lombok.Getter;
import lombok.Setter;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.render.GuiHandler;
import net.minecraft.client.util.math.MatrixStack;


/**
 * @author jbk
 * Просто компонент для упрощения создания и использования будущих кнопок.
 */
@Getter @Setter
public class ButtonElement extends GuiHandler implements IHolder {

    private float x, y, width, height;
    private final Runnable action;

    /**
     * Анимации
     */

    private final Animation hoverAnimation = new Animation(Easing.LINEAR, 200);
    /**
     * конструктор
     *
     * @param x      координата кнопки по Х
     * @param y      координата кнопки по Y
     * @param width  ширина кнопки
     * @param height высота кнопки
     * @param action действие которое произойдет, когда кнопка будет нажата
     */
    public ButtonElement(float x, float y, float width, float height, Runnable action) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.action = action;
    }

    /**
     * Рисует кнопку
     *
     * @param mouseX       координата X мыши
     * @param mouseY       координата Y мыши
     */

    public void draw(MatrixStack pPoseStack, int mouseX, int mouseY) {
        // обновляем анимацию
        hoverAnimation.setEasing(Easing.EASE_IN_OUT_SINE);
        hoverAnimation.setDuration(250);
        hoverAnimation.run(isHovered(this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY) ? 255 : 155);


    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(x, y, width, height, mouseX, mouseY) && button == 0) {
            runAction();
        }
    }

    /**
     * Выполняет действие, связанное с кнопкой, при ее нажатии.
     */
    public void runAction() {
        this.action.run();
    }
}