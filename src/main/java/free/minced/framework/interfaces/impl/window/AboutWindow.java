package free.minced.framework.interfaces.impl.window;


import free.minced.framework.render.shaders.ShaderHandler;
import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import net.minecraft.util.Formatting;
import free.minced.Minced;
import free.minced.addition.ProfileHandler;
import free.minced.framework.interfaces.api.CustomElement;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.color.ColorHandler;
import free.minced.framework.font.CFontRenderer;
import free.minced.framework.font.Fonts;
import free.minced.framework.font.Icons;
import free.minced.systems.theme.PrimaryTheme;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jbk
 * @since 15.01.2024
 */


@Getter
public class AboutWindow extends CustomElement {

    private Animation alphaAnimation;
    private CFontRenderer largeFont, normalFont, smallFont, iconFont;
    private boolean hoveringCloseIcon;
    private List<StyleSelectorElement> styles;

    public AboutWindow(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void init() {
        this.alphaAnimation = new Animation(Easing.LINEAR, 0);

        this.largeFont = Fonts.UNBOUNDED_BOLD28;
        this.normalFont = Fonts.SEMI_14;
        this.smallFont = Fonts.SEMI_14;
        this.iconFont = Fonts.ICON_16;

        // collecting themes
        styles = Arrays.stream(PrimaryTheme.values()).map(StyleSelectorElement::new).collect(Collectors.toList());
        styles.forEach(StyleSelectorElement::init);

        super.init();
    }

    @Override
    public void render(DrawContext DrawContext, int mouseX, int mouseY) {
        // обновляем анимации

        alphaAnimation.run(isDisplayingElement() ? 1 : 0);

        DrawContext.getMatrices().push();

        // бекграунд
        DrawHandler.drawBlurredShadow(DrawContext.getMatrices(),x - 0.5f, y - 0.5f, width + 1, height + 1, 12, ClientColors.getSecondaryBackgroundColor().withAlpha((180 * alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()));

/*
        DrawHandler.drawRound(DrawContext.getMatrices(),x, y, width, height, 3, ClientColors.getBackgroundColor().withAlpha((180 * alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()));
*/
        if (getClickGUI().getAlpha().getValue() > 0.05F && getAlphaAnimation().getValue() > 0.05F) {
            ShaderHandler.drawRoundedBlur(DrawContext.getMatrices(), x, y, width, height, 3, ClientColors.getBackgroundColor().withAlpha((180 * alphaAnimation.getValue())), 20, 0.55f);
        }
        // хеадер (бг)
        float headerHeight = 15;
        DrawHandler.drawRound(DrawContext.getMatrices(),x, y, width, headerHeight, 3, ColorHandler.applyOpacity(ClientColors.getBackgroundColor().brighter(),255 * alphaAnimation.getValue() * getClickGUI().getAlpha().getValue()));
        DrawHandler.drawRect(DrawContext.getMatrices(),x, y+ 13, width, 2,  ColorHandler.applyOpacity(ClientColors.getBackgroundColor().brighter(),255 * alphaAnimation.getValue() * getClickGUI().getAlpha().getValue()));

        // иконка хеадера
        float iconLeftMargin = 3;
        if (getClickGUI().getAlpha().getValue() > 0.05F && getAlphaAnimation().getValue() > 0.05F) {
            iconFont.drawString(DrawContext.getMatrices(),Icons.SETTING.getCharacter(), x + iconLeftMargin, y + 6.5f, ColorHandler.applyOpacity(ClientColors.getFirstColor(), (255 * alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()).getRGB());

            // текст хеадера
            smallFont.drawString(DrawContext.getMatrices(),"About %s".formatted(Minced.NAME), x + iconFont.getStringWidth(Icons.SETTING.getCharacter()) + iconLeftMargin * 2, y + 5, ClientColors.getFontColor().withAlpha((255 * alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()).getRGB());

            // рендерим крестик
            float rightMargin = 5;
            iconFont.drawString(DrawContext.getMatrices(),Icons.CLOSE.getCharacter(), x + width - iconFont.getStringWidth(Icons.CLOSE.getCharacter()) - rightMargin, y + 5 + 1, ClientColors.getFontColor().withAlpha((255 * alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()).getRGB());

            hoveringCloseIcon = isHovered(x + width - iconFont.getStringWidth(Icons.CLOSE.getCharacter()) - rightMargin, y + 5 + 1, iconFont.getStringWidth(Icons.CLOSE.getCharacter()), iconFont.getFontHeight(), mouseX, mouseY);

        }
        // рендерим название клиента
        float topMargin = 5;
        if (alphaAnimation.getValue() > 0.05 && getClickGUI().getAlpha().getValue() > 0.05) {
            largeFont.drawCenteredString(DrawContext.getMatrices(),Minced.NAME.toUpperCase(), x + width / 2f, y + headerHeight + iconFont.getFontHeight() / 2f + 5, ClientColors.getFontColor().withAlpha((255 * alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()).getRGB());

            // разделитель
            float textBottomMargin = 2.5F;
            float separatorHeight = 1;
            DrawHandler.drawRect(DrawContext.getMatrices(), x, y + topMargin + largeFont.getFontHeight() + textBottomMargin + headerHeight + 2.5f, width, separatorHeight, ClientColors.getFontColor().withAlpha(10 * (alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()));

            Formatting Formatting = Minced.getInstance().getThemeHandler().getPrimaryTheme().equals(PrimaryTheme.LIGHT) ? net.minecraft.util.Formatting.DARK_GRAY : net.minecraft.util.Formatting.GRAY;

            // инфа
            float leftMargin = 5;
            float separatorBottomMargin = 5;
            float infoY = y + 5 + topMargin + largeFont.getFontHeight() + textBottomMargin + separatorHeight + separatorBottomMargin + headerHeight;
            float gap = normalFont.getFontHeight() + 6.5f;

            normalFont.drawString(DrawContext.getMatrices(),"Valid until: %s".formatted(Formatting + ProfileHandler.getSubscribe()), x + leftMargin, infoY, ClientColors.getFontColor().withAlpha((255 * alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()).getRGB());
            normalFont.drawString(DrawContext.getMatrices(),"Username: %s".formatted(Formatting + ProfileHandler.getUsername()), x + leftMargin, infoY + gap, ClientColors.getFontColor().withAlpha((255 * alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()).getRGB());
            normalFont.drawString(DrawContext.getMatrices(),"Version: %s".formatted(Formatting + ProfileHandler.getVersion()), x + leftMargin, infoY + gap  * 2, ClientColors.getFontColor().withAlpha((255 * alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()).getRGB());

            normalFont.drawString(DrawContext.getMatrices(),"Branch: %s".formatted(Formatting + ProfileHandler.getBuild()), x + leftMargin, infoY + gap  * 3, ClientColors.getFontColor().withAlpha((255 * alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()).getRGB());
            // стили
            float styleY = y + topMargin + largeFont.getFontHeight() + textBottomMargin + separatorHeight + separatorBottomMargin + headerHeight + gap * 4;
            float rightMargin = 5;
            float styleX = x + width - rightMargin;
            normalFont.drawString(DrawContext.getMatrices(),"Style", x + leftMargin, styleY + 6, ClientColors.getFontColor().withAlpha((255 * alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()).getRGB());

            float offset = 0;
            for (StyleSelectorElement styleSelectorElement : styles) {
                styleSelectorElement.render(DrawContext,styleX - styleSelectorElement.getWidth() - offset, styleY + 3, alphaAnimation.getValue());
                offset += styleSelectorElement.getWidth() + 5;
            }

            // copyright
            float bottomMargin = 5;
            float copyrightY = y + height - normalFont.getFontHeight() - bottomMargin - bottomMargin;
            DrawHandler.drawRect(DrawContext.getMatrices(),x, copyrightY, width, separatorHeight, ClientColors.getFontColor().withAlpha(10 * (alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()));

            normalFont.drawCenteredString(DrawContext.getMatrices(),Minced.NAME.toLowerCase() + " © " + Formatting + "2021-2024", x + width / 2f, copyrightY + bottomMargin + 1, ClientColors.getFontColor().withAlpha((255 * alphaAnimation.getValue()) * getClickGUI().getAlpha().getValue()).getRGB());

        }

        DrawContext.getMatrices().pop();

        super.render(DrawContext, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (hoveringCloseIcon && button == 0) {
            setDisplayingElement(false);
        }


        for (StyleSelectorElement element : styles) {
            element.mouseClicked(mouseX, mouseY, button);
        }

        super.mouseClicked(mouseX, mouseY, button);
    }
}