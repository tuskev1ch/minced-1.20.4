package free.minced.framework.interfaces.impl.settings;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import free.minced.Minced;
import free.minced.framework.font.Fonts;
import free.minced.framework.interfaces.api.SettingComponent;
import free.minced.framework.interfaces.impl.module.ModuleComponent;
import free.minced.primary.chat.ChatHandler;
import free.minced.primary.other.KeyHandler;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.systems.setting.Setting;
import free.minced.systems.setting.impl.BindSetting;
import org.lwjgl.glfw.GLFW;


/**
 * @author jbk
 * @since 27.10.2023
 * Тут пиздец
 */


@Getter
public class BindSettingComponent extends SettingComponent {

    private BindSetting bindSetting;
    private boolean binding;

    // шрифты

    public BindSettingComponent(Setting setting, ModuleComponent moduleComponent) {
        super(setting, moduleComponent);
    }

    @Override
    public void init() {
        // сеттинг
        this.bindSetting = (BindSetting) getSetting();

        // размеры
        this.height = 10;
        this.width = 10;

        // шрифты

        super.init();
    }

    @Override
    public void render(DrawContext pDrawContext, float x, float y, int mouseX, int mouseY) {

        // позиции
        this.x = x;
        this.y = y;

        float rightMargin = 10; // на самом деле там отступ в 5 пикселей, но рендер настроек начинается с x + 5, поэтому приходится делать так

        // бг
        DrawHandler.drawRect(pDrawContext.getMatrices(),x + getModuleComponent().getWidth() - width - rightMargin - Fonts.SEMI_13.getStringWidth(KeyHandler.getKeyboardKey(this.getBindSetting().getKey())), y, width + Fonts.SEMI_13.getStringWidth(KeyHandler.getKeyboardKey(this.getBindSetting().getKey())), height, ClientColors.getSecondaryBackgroundColor().withAlpha(255 * getClickGUI().getAlpha().getValue()));

        Fonts.SEMI_13.drawString(pDrawContext.getMatrices(), KeyHandler.getKeyboardKey(this.getBindSetting().getKey()),x + getModuleComponent().getWidth() - width - rightMargin / 2 - Fonts.SEMI_13.getStringWidth(KeyHandler.getKeyboardKey(this.getBindSetting().getKey())), y + 3.5f, ClientColors.getFontColor().withAlpha(255 * getClickGUI().getAlpha().getValue()).getRGB()); // рендер кнопки бинда

        // название сеттинга
        Fonts.SEMI_13.drawString(pDrawContext.getMatrices(), getBindSetting().getName(), x, y + 3.5f, ClientColors.getFontColor().withAlpha(255 * getClickGUI().getAlpha().getValue()).getRGB());

        super.render(pDrawContext, x, y, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        this.hoveringClickableArea = isHovered(x, y, getModuleComponent().width, height, mouseX, mouseY);
        if (hoveringClickableArea && button == 0) {
            bindSetting.setKey(button);
            binding = true;
        }

        if (binding && (button != 0)) {
            bindSetting.setKey(button);
            binding = false;
        }

        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_GRAVE_ACCENT) {
                // сбрасываем бинд
                bindSetting.setKey(0);
            } else {
                // применяем бинд
                bindSetting.setKey(keyCode);
            }
            binding = false;
            Minced.getInstance().getConfigHandler().save("autocfg");
        }

        super.keyPressed(keyCode, scanCode, modifiers);
    }
}