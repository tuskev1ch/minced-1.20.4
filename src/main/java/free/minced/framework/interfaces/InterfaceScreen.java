package free.minced.framework.interfaces;


import free.minced.framework.render.shaders.ShaderHandler;
import free.minced.modules.api.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import free.minced.Minced;
import free.minced.framework.interfaces.api.MenuScreen;
import free.minced.framework.interfaces.impl.module.ModuleComponent;
import free.minced.framework.interfaces.impl.sidebar.Sidebar;
import free.minced.framework.interfaces.impl.window.AboutWindow;
import free.minced.framework.render.ScissorHandler;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.impl.display.ClickGUI;
import free.minced.primary.IHolder;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.render.GuiHandler;
import free.minced.framework.color.ClientColors;
import free.minced.primary.time.TimerHandler;
import org.lwjgl.glfw.GLFW;

import java.text.Collator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;



@Getter @Setter
public class InterfaceScreen extends Screen implements IHolder {

    public int dWheel;

    private float x, y, width, height, cornerRadius;
    
    private ClickGUI clickGUIModule;

    private Animation alpha;

    private final TimerHandler timer = new TimerHandler();
    private Sidebar sidebar;

    private AboutWindow aboutWindow;


    private ConcurrentLinkedQueue<ModuleComponent> moduleList = new ConcurrentLinkedQueue<>();

    private boolean closing;

    private MenuScreen currentScreen = ModuleCategory.COMBAT.getScreen(), lastScreen = currentScreen;

    public InterfaceScreen() {
        super(Text.of("ClickScreen"));
    }



    @Override
    protected void init() {
        this.clickGUIModule = Minced.getInstance().getModuleHandler().get(ClickGUI.class);

        rebuildModules();
        this.cornerRadius = 12;

        this.width = 380;
        this.height = 237.5f;

        this.x = (sr.getScaledWidth().floatValue() - width) / 2f;
        this.y = (sr.getScaledHeight().floatValue() - height) / 2f;

        this.alpha = new Animation(Easing.EASE_OUT_SINE, 0);

        this.sidebar = new Sidebar();
        this.sidebar.init();
        this.currentScreen.init();

        closing = false;

        float gap = 10;
        float windowWidth = 125;
        float windowHeight = 155;
        float windowX = this.getX() - windowWidth - gap;
        float windowY = this.getY();
        aboutWindow = new AboutWindow(windowX, windowY, windowWidth, windowHeight);
        aboutWindow.init();


        super.init();
    }

    @Override // рендер
    public void render(DrawContext pDrawContext, int pMouseX, int pMouseY, float pPartialTick) {


        alpha.run(closing ? 0 : 1);
        alpha.setDuration(0);
        alpha.setEasing(Easing.EASE_OUT_SINE);

        pDrawContext.getMatrices().push();
        // бг
        DrawHandler.drawBlurredShadow(pDrawContext.getMatrices(),x - 0.5f, y - 0.5f, width + 1, height + 1, 8, ClientColors.getSecondaryBackgroundColor().withAlpha(180 * alpha.getValue()));

        ShaderHandler.drawRoundedBlur(pDrawContext.getMatrices(), x, y, width, height,3, ClientColors.getBackgroundColor().withAlpha(180 * alpha.getValue()), 20, 0.55f);

        sidebar.render(pDrawContext, pMouseX, pMouseY, pPartialTick);

        aboutWindow.render(pDrawContext, pMouseX, pMouseY);
        ScissorHandler.doScissor(x + 0.5f, y  + 0.5f, width  + 1, height - 0.5f);
        currentScreen.render(pDrawContext, pMouseX, pMouseY, pPartialTick);
        ScissorHandler.end();

        pDrawContext.getMatrices().pop();

        if (closing && alpha.getValue() < 0.05) {
            mc.setScreen(null);
        }

        //super.render(pDrawContext, pMouseX, pMouseY, pPartialTick);
    }

    @Override // клик клик
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        // регистрируем клики в окне
        aboutWindow.mouseClicked(mouseX, mouseY, button);
        if (GuiHandler.isHovered(x, y, width, height, mouseX, mouseY)) {
            sidebar.mouseClicked(mouseX, mouseY, button);
            currentScreen.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override // скролл колесика мыши
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        dWheel = (int) (verticalAmount);

        currentScreen.mouseScrolled(mouseX, mouseY, horizontalAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override // отпускание кнопок мыши
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        currentScreen.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            closing = true;
        }


        currentScreen.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        currentScreen.charTyped(codePoint, modifiers);
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
    public void rebuildModules() {
        moduleList.clear();

        List<Module> sortedModules = ModuleManager.modules;
        sortedModules.sort((o1, o2) -> Collator.getInstance().compare(o1.getName(), o2.getName()));
        sortedModules.forEach(module -> moduleList.add(new ModuleComponent(module)));

    }

    public void switchScreen(ModuleCategory moduleCategory) {
        if (! moduleCategory.getScreen().equals(this.currentScreen)) {
            lastScreen = this.currentScreen;
            System.out.println("Переключился на другую категорию");
            currentScreen = moduleCategory.getScreen();
            timer.reset();
            currentScreen.init();
        }
    }


    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}