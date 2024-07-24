package free.minced.framework.screens.elements;

import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.buttons.impl.ColoredButtonElement;
import free.minced.framework.color.ClientColors;
import free.minced.framework.font.Fonts;
import free.minced.framework.interfaces.api.CustomElement;
import free.minced.framework.interfaces.api.TextField;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.render.ScissorHandler;
import free.minced.primary.math.MathHandler;
import free.minced.primary.math.ScrollHandler;
import lombok.Getter;
import free.minced.framework.screens.elements.util.AltManagerConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AltManagerElement extends CustomElement {

    private ColoredButtonElement randomButton;
    private Animation openAndCloseAnimation, iconInitAnimation, rotateAnimation;
    private TextField textField;
    private final ScrollHandler scrollHandler = new ScrollHandler();

    public static final List<AccountElement> ACCOUNTS = new ArrayList<>();

    public AltManagerElement(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void init() {

        // инициализируем анимации
        openAndCloseAnimation = new Animation(Easing.EASE_IN_OUT_CUBIC, 400);
        iconInitAnimation = new Animation(Easing.LINEAR, 1000);
        rotateAnimation = new Animation(Easing.LINEAR, 250);

        // инициализируем шрифты

        // обновляем анимации
        openAndCloseAnimation.setValue(y + height );

        // загружаем и инициализируем аккаунты
        ACCOUNTS.clear();
        AltManagerConfig.loadAccounts();
        ACCOUNTS.forEach(AccountElement::init);

        // инициализируем элементы
        float textFieldHeight = 20;
        float textFieldWidth = 80;
        float bottomMargin = 5;
        textField = new TextField(x, y + height + bottomMargin + bottomMargin / 2, textFieldWidth, textFieldHeight, Fonts.SEMI_14, "username", true);
        textField.init();
        textField.setFocused(true);

        float microsoftButtonWidth = 100;
        float microsoftButtonHeight = 20;
        randomButton = new ColoredButtonElement(x + width - microsoftButtonWidth, y + height + bottomMargin + bottomMargin / 2, microsoftButtonWidth, microsoftButtonHeight, 0, "Random Account", this::generateRandomAccount);

        float buttonRightMargin = 15;
        float buttonWidth = 80;
        float buttonHeight = 20;

        float buttonX = textField.getX() + textFieldWidth + buttonRightMargin;
        float buttonY = 0; // я потом обновляю это значение в методе render


        setDisplayingElement(false);
        super.init();
    }

    @Override
    public void render(DrawContext pPoseStack, int mouseX, int mouseY) {

        // обновляем список с аккаунтами
        ACCOUNTS.removeIf(element -> element.isRemoving() && element.getAlphaAnimation().getValue() == 0);

        // обновляем анимации
        openAndCloseAnimation.run(isDisplayingElement() ? 1 : 0);

        // обновляем прозрачность чтобы она соответствовала анимации
        randomButton.setAlpha(255 * openAndCloseAnimation.getValue());
        textField.setAlpha(openAndCloseAnimation.getValue());

        float cornerRadius = 2;

        // бэкграунд


        DrawHandler.drawRound(pPoseStack.getMatrices(), x, y, width, height + 4,3, ClientColors.getBrighterBackgroundColor().withAlpha(255 * openAndCloseAnimation.getValue()));


        // рендерим само поле
        textField.render(pPoseStack, mouseX, mouseY);

        randomButton.draw(pPoseStack.getMatrices(), mouseX, mouseY);

        ScissorHandler.doScissor(x, y, width, height);
        float offset = 0;
        for (AccountElement element : ACCOUNTS) {
            float leftPadding = 10;
            float rightPadding = 10;
            element.render(pPoseStack.getMatrices(), this, x + width / 2f - element.getWidth() / 2f, y + 5 + offset, width - leftPadding - rightPadding, 20, mouseX, mouseY);
            offset += element.getHeight() + 5;
        }
        ScissorHandler.end();

        //scrollHandler.enableScrolling();
        scrollHandler.handle();
        scrollHandler.setMax(-offset + height - 5);

        super.render(pPoseStack, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (!isDisplayingElement()) return;

        // регистрируем нажатие по элементам
        textField.mouseClicked(mouseX, mouseY, button);

        for (AccountElement element : ACCOUNTS) {
            element.mouseClicked(mouseX, mouseY, button);
        }

        // регистрируем нажатие по кнопкам
        randomButton.mouseClicked(mouseX, mouseY, button);

        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isDisplayingElement()) return;

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            setDisplayingElement(false);
        }

        textField.keyPressed(keyCode, scanCode, modifiers);

        // позволяем добавлять аккаунты нажатием на ENTER
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            if (textField.getText().length() >= 3) {
                AccountElement account = new AccountElement(textField.getText().replace(" ", ""));

                // проверяем условия
                if (isAccountValid(account)) {
                    // инициализируем элемент
                    account.init();

                    // добавляем аккаунт в список
                    ACCOUNTS.add(account);

                    // обновляем файл с аккаунтами
                    AltManagerConfig.saveAccounts();

                    // очищаем текстовое поле
                    textField.setText("");
                }
            }
        }


        super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        if (!isDisplayingElement()) return;
        textField.charTyped(codePoint, modifiers);
        super.charTyped(codePoint, modifiers);
    }

    public void generateRandomAccount() {
        String randomName = RandomStringUtils.randomAlphanumeric((int) MathHandler.randomize(5, 9));

        AccountElement element = new AccountElement(randomName);
        ACCOUNTS.add(element);
        element.init();

        // обновляем файлик с аккаунтами
        AltManagerConfig.saveAccounts();

    }

    public boolean isAccountValid(AccountElement account) {
        return account.getUsername().length() >= 3
                // добавляем аккаунт только если аккаунтов с таким же юзернеймом нет в списке.
                && ACCOUNTS.stream().noneMatch(existingAccount -> existingAccount.getUsername().equals(account.getUsername()))
                && textField.getText().length() >= 3;
    }

}
