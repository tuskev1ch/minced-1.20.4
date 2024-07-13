package free.minced.framework.interfaces.impl.sidebar;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import free.minced.framework.interfaces.api.MenuComponent;
import free.minced.modules.api.ModuleCategory;
import free.minced.framework.animation.color.ColorAnimation;
import free.minced.framework.color.ClientColors;
import free.minced.framework.color.CustomColor;
import free.minced.framework.font.Fonts;


/**
 * @author jbk
 * @since 18.10.2023
 */


@Getter
public class Tab extends MenuComponent {

    private final ModuleCategory moduleCategory;
    // анимации
    private ColorAnimation firstAnimatedColor, secondAnimatedColor;

    // конструктор
    public Tab(ModuleCategory moduleCategory) {
        this.moduleCategory = moduleCategory;
    }

    @Override
    public void init() {

        this.width = Fonts.ICON_20.getStringWidth(moduleCategory.getIcon().getCharacter());
        this.height = Fonts.ICON_20.getFontHeight();
        this.firstAnimatedColor = new ColorAnimation(500);
        this.secondAnimatedColor = new ColorAnimation(500);
        super.init();
    }

    public void render(DrawContext DrawContext, float x, float y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;

        CustomColor firstColor = getClickGUI().getCurrentScreen().equals(moduleCategory.getScreen()) ? ClientColors.getTheme().getFirstColor() : ClientColors.getFontColor();
        CustomColor secondColor = getClickGUI().getCurrentScreen().equals(moduleCategory.getScreen()) ? ClientColors.getTheme().getSecondColor() : ClientColors.getFontColor();

        // обновляем анимации
        firstAnimatedColor.run(getClickGUI().getCurrentScreen().equals(moduleCategory.getScreen()) ? firstColor : ClientColors.getFontColor());
        secondAnimatedColor.run(getClickGUI().getCurrentScreen().equals(moduleCategory.getScreen()) ? secondColor : ClientColors.getSecondaryBackgroundColor());

        // глоу

        // рендер иконки
        if (getClickGUI().getAlpha().getValue() > 0.05) {
            // применяем градиент к строке


            Fonts.ICON_20.drawCenteredString(DrawContext.getMatrices(), moduleCategory.getIcon().getCharacter(), x + getClickGUI().getSidebar().getWidth() / 2f, y,
                    firstAnimatedColor.getColor().withAlpha(255 * getClickGUI().getAlpha().getValue()).getRGB());


        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {

        float padding = 10;

        if (isHovered(x + getClickGUI().getSidebar().getWidth() / 2f - padding / 2f, y + 5 - padding / 2f, Fonts.ICON_20.getStringWidth(moduleCategory.getIcon().getCharacter()) + padding, 5 + padding, mouseX, mouseY) && button == 0) {
            getClickGUI().switchScreen(moduleCategory);
        }

        super.mouseClicked(mouseX, mouseY, button);
    }
}
