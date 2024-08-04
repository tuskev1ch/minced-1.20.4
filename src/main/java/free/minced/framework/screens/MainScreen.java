package free.minced.framework.screens;


import free.minced.framework.screens.elements.AltManagerElement;


import free.minced.framework.buttons.impl.TextButtonElement;
import free.minced.primary.IHolder;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.font.Fonts;
import free.minced.Minced;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;


/**
 * @author tuskevich
 * @since 05.10.2023
 */
public class MainScreen extends Screen implements IHolder {

    public MainScreen() {
        super(Text.of("MainScreen"));
    }

    // анимации
    public static Animation initAnimation;
    private Animation yTranslateAnimation;

    // элементы
    public static AltManagerElement altManagerElement;
    // элементы
    private TextButtonElement singlePlayerButton, multiPlayerButton, settingsButton, exitButton;


    @Override
    protected void init() {

        float elementX = 10;
        float elementY = 7.5F;
        float elementWidth = 100;
        float elementHeight = 200;


        // анимации
        yTranslateAnimation = new Animation(Easing.EASE_IN_OUT_CUBIC, 400);
        initAnimation = new Animation(Easing.EASE_OUT_EXPO, 300);

        // кнопки
        float buttonWidth = 90;
        float buttonHeight = 20;
        float buttonSpacing = 5;
        float buttonY = sr.getScaledHeight().floatValue() / 2f - buttonHeight; // не делим на два потому что кнопки будут в два ряда
        float buttonX = sr.getScaledWidth().floatValue() / 2f - buttonWidth / 2f;

        this.singlePlayerButton = new TextButtonElement(buttonX, buttonY, buttonWidth, buttonHeight, () -> mc.setScreen(new SelectWorldScreen(this)), "Singleplayer");

        this.multiPlayerButton = new TextButtonElement(buttonX, buttonY + buttonHeight + buttonSpacing, buttonWidth, buttonHeight, () -> mc.setScreen(new MultiplayerScreen(this)), "Multiplayer");

        this.settingsButton = new TextButtonElement(buttonX, buttonY + buttonSpacing * 2 + buttonHeight * 2, buttonWidth, buttonHeight, () -> altManagerElement.setDisplayingElement(true), "AltManager");

        this.exitButton = new TextButtonElement(buttonX, buttonY + buttonSpacing * 3 + buttonHeight * 3, buttonWidth, buttonHeight, () ->  mc.setScreen(new OptionsScreen(this, mc.options)), "Settings");

        // элементы
        float altManagerWidth = 200;
        float altManagerHeight = 150;
        altManagerElement = new AltManagerElement(sr.getScaledWidth().floatValue() / 2f - altManagerWidth / 2f, sr.getScaledHeight().floatValue() / 2f - altManagerHeight / 2f, altManagerWidth, altManagerHeight);
        altManagerElement.init();

        super.init();
    }


    @Override
    public void render(DrawContext pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

        // обновляем анимации
        initAnimation.setDuration(2500);
        initAnimation.run(1);
        yTranslateAnimation.run(altManagerElement.isDisplayingElement() ? 1 : 0);

        // бэкграунд
        DrawHandler.drawRect(pPoseStack.getMatrices(), 0, 0, width, height, ClientColors.getBrighterBackgroundColor());


        // затемняем бэкграунд
        DrawHandler.drawRect(pPoseStack.getMatrices(), 0, 0, width, height, ClientColors.BLACK.withAlpha(75));

        Fonts.UNBOUNDED_BOLD.drawCenteredString(pPoseStack.getMatrices(), Minced.NAME.toUpperCase(), width / 2f, height / 2f - 30 - 15 / 2f, ClientColors.getFontColor().getRGB());

        // кнопки
        singlePlayerButton.draw(pPoseStack.getMatrices(), pMouseX, pMouseY);
        multiPlayerButton.draw(pPoseStack.getMatrices(), pMouseX, pMouseY);
        settingsButton.draw(pPoseStack.getMatrices(), pMouseX, pMouseY);
        exitButton.draw(pPoseStack.getMatrices(), pMouseX, pMouseY);

        altManagerElement.render(pPoseStack, pMouseX, pMouseY);

        // super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        altManagerElement.mouseClicked(mouseX, mouseY, button);

        // регистрируем нажатия по кнопкам
        if (!altManagerElement.isDisplayingElement()) {
            singlePlayerButton.mouseClicked(mouseX, mouseY, button);
            multiPlayerButton.mouseClicked(mouseX, mouseY, button);
            settingsButton.mouseClicked(mouseX, mouseY, button);
            exitButton.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override // шорткаты
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {



        altManagerElement.keyPressed(keyCode, scanCode, modifiers);



        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override // для того чтобы при нажатии ESCAPE анимации не сбрасывались
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        altManagerElement.charTyped(codePoint, modifiers);
        return super.charTyped(codePoint, modifiers);
    }
}