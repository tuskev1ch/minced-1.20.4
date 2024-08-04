package free.minced.framework.interfaces.impl.sidebar;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import free.minced.framework.interfaces.api.MenuComponent;
import free.minced.framework.color.ColorHandler;
import free.minced.modules.api.ModuleCategory;
import free.minced.framework.animation.color.ColorAnimation;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.font.Fonts;
import free.minced.systems.theme.PrimaryTheme;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jbk
 * @since 18.10.2023
 */


@Getter
public class Sidebar extends MenuComponent {

    private final float padding = 5;
    private final List<Tab> tabs;
    private float iconTopMargin;
    private final List<PrimaryTheme> themes = Arrays.asList(PrimaryTheme.DARK, PrimaryTheme.BLUE, PrimaryTheme.LIGHT);
    private final int themeIndex = 0;
    private String nameC;

    private ColorAnimation firstAnimatedColor;
    public Sidebar() {
        // собираем категории в список
        tabs = Arrays.stream(ModuleCategory.values()).map(Tab::new).collect(Collectors.toList());
    }
    @Override
    public void init() {
        // размеры
        width = 50;
        height = getClickGUI().getHeight();
        this.firstAnimatedColor = new ColorAnimation(500);
        // позиция
        this.x = getClickGUI().getX();
        this.y = getClickGUI().getY();

        iconTopMargin = 10;

        tabs.forEach(Tab::init);

        super.init();
    }

    @Override
    public void render(DrawContext DrawContext, int mouseX, int mouseY, float partialTicks) {

        float separatorWidth = 25;
        float separatorHeight = 0.5F;
        DrawHandler.drawRound(DrawContext.getMatrices(), x, y, getClickGUI().getSidebar().width, height, 3, ColorHandler.applyOpacity(ClientColors.getBackgroundColor().brighter(),255 * getClickGUI().getAlpha().getValue()));
        DrawHandler.drawRect(DrawContext.getMatrices(), x + 47.5f, y, 2.5f, height, ColorHandler.applyOpacity(ClientColors.getBackgroundColor().brighter(),255 * getClickGUI().getAlpha().getValue()));
        // разделитель
        float iconBottomMargin = 15; // отступ от иконки снизу
        DrawHandler.drawRect(DrawContext.getMatrices(),x + width / 2f - separatorWidth / 2f, y + iconTopMargin + Fonts.ICONFONT_27.getFontHeight() + iconBottomMargin, separatorWidth, separatorHeight, ClientColors.GRAY.withAlpha(200 * getClickGUI().getAlpha().getValue()));
        // рендерим иконку, при клике на которую откроется окно с информацией о пользователе
        if (getClickGUI().getAlpha().getValue() > 0.05) {

            Fonts.ICONFONT_27.drawCenteredString(DrawContext.getMatrices(), "a", x + width / 2f, y + iconTopMargin + 2.5f,
                    ClientColors.getFirstColor().withAlpha(255 * getClickGUI().getAlpha().getValue()).getRGB());


        }


        // табы
        float separatorBottomMargin = 20; // отступ от разделителя снизу
        float offset = iconTopMargin + iconBottomMargin + separatorHeight + separatorBottomMargin;
        for (Tab tab : tabs) {
            float gap = 25; // пропуск между категориями
            tab.render(DrawContext, x, y + 5 + offset, mouseX, mouseY);
            offset += gap;
        }

        super.render(DrawContext, mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        // проверяем, где находится указатель
        if (isHovered(x + width / 2f - Fonts.ICONFONT_27.getStringWidth("a") / 2f, y + iconTopMargin, Fonts.ICONFONT_27.getStringWidth("a"), 15, mouseX, mouseY) && button == 0) {
            getClickGUI().getAboutWindow().setDisplayingElement(!getClickGUI().getAboutWindow().isDisplayingElement());
        }
        // регистрируем клики по табам
        for (Tab tab : tabs) {
            tab.mouseClicked(mouseX, mouseY, button);
        }

        super.mouseClicked(mouseX, mouseY, button);
    }
}
