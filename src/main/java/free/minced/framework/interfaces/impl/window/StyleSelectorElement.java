package free.minced.framework.interfaces.impl.window;


import net.minecraft.client.gui.DrawContext;
import free.minced.Minced;
import free.minced.framework.interfaces.api.CustomElement;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.color.ColorHandler;
import free.minced.systems.theme.PrimaryTheme;
import org.lwjgl.glfw.GLFW;

/**
 * @author jbk
 * @since 04.03.2024
 */


public class StyleSelectorElement extends CustomElement {

    private final PrimaryTheme theme;
    private Animation selectedAnimation;

    public StyleSelectorElement(PrimaryTheme theme) {
        this.theme = theme;
    }

    @Override
    public void init() {
        this.selectedAnimation = new Animation(Easing.LINEAR, 300);
        this.width = 10;
        this.height = 10;
        super.init();
    }

    public void render(DrawContext DrawContext, float x, float y, float alpha) {
        this.x = x;
        this.y = y;

        // обновляем анимации
        selectedAnimation.run(Minced.getInstance().getThemeHandler().getPrimaryTheme().equals(theme) ? 1 : 0);

        DrawHandler.drawRound(DrawContext.getMatrices(), x - 0.5f, y  - 0.5f, width + 1, height + 1,  5,  ClientColors.getFirstColor().withAlpha(((255 * selectedAnimation.getValue()) * alpha) * getClickGUI().getAlpha().getValue()));

        DrawHandler.drawRound(DrawContext.getMatrices(), x, y, width, height, 5, ColorHandler.applyOpacity(theme.getBackgroundColor().brighter(), (255 * alpha) * getClickGUI().getAlpha().getValue()));
    }
    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(x, y, width, height, mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            Minced.getInstance().getThemeHandler().setPrimaryTheme(theme);
            Minced.getInstance().getConfigHandler().save("autocfg");
        }
        super.mouseClicked(mouseX, mouseY, button);
    }
}