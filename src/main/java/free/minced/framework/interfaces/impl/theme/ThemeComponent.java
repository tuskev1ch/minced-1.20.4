package free.minced.framework.interfaces.impl.theme;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import free.minced.framework.interfaces.api.MenuComponent;
import free.minced.framework.animation.color.ColorAnimation;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.color.ColorHandler;
import free.minced.framework.font.Fonts;
import free.minced.systems.theme.Theme;

import java.awt.*;


@Getter
public class ThemeComponent extends MenuComponent {

    private final Theme theme;
    private Animation hoverAnimation, selectAnimation;
    private ColorAnimation nameAnimatedColor;

    @Override
    public void init() {
        hoverAnimation = new Animation(Easing.EASE_OUT_SINE, 500);
        selectAnimation = new Animation(Easing.EASE_IN_CIRC, 600);
        nameAnimatedColor = new ColorAnimation(600);
        super.init();
    }

    public ThemeComponent(Theme theme) {
        this.theme = theme;
    }

    public void render(DrawContext DrawContext, float x, float y, float width, float height, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // обновляем анимации
        hoverAnimation.run(isHovered(x, y, width, height, mouseX, mouseY) ? 1 : 0);
        selectAnimation.run(theme.equals(ClientColors.getTheme()) ? 1 : 0);
        selectAnimation.setDuration(300);
        Color nameColor = ClientColors.getTheme().equals(theme) ? ClientColors.getTheme().getFirstColor() : ClientColors.getFontColor();

        nameAnimatedColor.run(nameColor);

        float nameBoxHeight = 10;

        //DrawHandler.drawRoundGradient(DrawContext.getMatrices(), x, y, width, height, 0, startColor, startColor, endColor, endColor);
        // предпросмотр темы
        DrawHandler.horizontalGradient(DrawContext.getMatrices(), x, y, width, height,
                ColorHandler.applyOpacity(theme.getFirstColor(), 255 * getClickGUI().getAlpha().getValue()),
                ColorHandler.applyOpacity(theme.getSecondColor(), 255 * getClickGUI().getAlpha().getValue()));

        // рект который будет отображаться при наводке на тему

        DrawHandler.horizontalGradient(DrawContext.getMatrices(), x, y, width, height,
                ClientColors.WHITE.withAlpha((70 * hoverAnimation.getValue()) * getClickGUI().getAlpha().getValue()).brighter(),
                ClientColors.WHITE.withAlpha((70 * hoverAnimation.getValue()) * getClickGUI().getAlpha().getValue()).brighter());


        // ну рект этот снизу кароче PROBLEMA
        DrawHandler.drawRound(DrawContext.getMatrices(), x - 1, (y + height - nameBoxHeight) - 1, width + 1.5f, nameBoxHeight + 1.5f, 1, ClientColors.getBackgroundColor().withAlpha(255 * getClickGUI().getAlpha().getValue()));

        // название темы
        if (getClickGUI().getAlpha().getValue() > 0.05F) {
            Fonts.SEMI_15.drawCenteredString(DrawContext.getMatrices(), theme.getName(), x + width / 2f, y + height - 7.5f, nameAnimatedColor.getColor().withAlpha(255 * getClickGUI().getAlpha().getValue()).getRGB());

        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {

        if (isHovered(x, y, width, height, mouseX, mouseY) && button == 0) {
            setTheme(theme);
        }

        super.mouseClicked(mouseX, mouseY, button);
    }



}