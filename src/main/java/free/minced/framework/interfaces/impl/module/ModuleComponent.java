package free.minced.framework.interfaces.impl.module;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import free.minced.framework.interfaces.api.MenuComponent;
import free.minced.framework.interfaces.api.SettingComponent;
import free.minced.framework.interfaces.impl.settings.BindSettingComponent;
import free.minced.framework.interfaces.impl.settings.BooleanSettingComponent;
import free.minced.framework.interfaces.impl.settings.NumberSettingComponent;
import free.minced.framework.interfaces.impl.settings.StringSettingComponent;
import free.minced.framework.interfaces.impl.settings.mode.ModeSettingComponent;
import free.minced.framework.interfaces.impl.settings.multibox.MultiBoxSettingComponent;
import free.minced.framework.color.ColorHandler;
import free.minced.modules.Module;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.primary.other.KeyHandler;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.font.Fonts;
import free.minced.systems.setting.Setting;
import free.minced.systems.setting.impl.*;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;


@Getter
public class ModuleComponent extends MenuComponent {

    private final Module module;

    // шрифты

    private float headerHeight;

    public final ArrayList<SettingComponent> settings = new ArrayList<>();

    // другое
    private float spacing, margin;
    private Boolean overModule, binding;

    // анимации
    private final Animation toggleAnimation = new Animation(Easing.EASE_IN_OUT_QUINT, 750);
    private final Animation hoverAnimation = new Animation(Easing.EASE_IN_OUT_SINE, 350);
    private final Animation animation = new Animation(Easing.LINEAR, 500);

    public ModuleComponent(Module module) {
        for (Setting setting : module.getSettings()) {
            if (setting instanceof BooleanSetting) {
                settings.add(new BooleanSettingComponent(setting, this));
            } else if (setting instanceof NumberSetting) {
                settings.add(new NumberSettingComponent(setting, this));
            } else if (setting instanceof ModeSetting) {
                settings.add(new ModeSettingComponent(setting, this));
            } else if (setting instanceof MultiBoxSetting) {
                settings.add(new MultiBoxSettingComponent(setting, this));
            } else if (setting instanceof StringSetting) {
                settings.add(new StringSettingComponent(setting, this));
            } else if (setting instanceof BindSetting) {
                settings.add(new BindSettingComponent(setting, this));
            }
        }
        this.module = module;
    }

    @Override
    public void init() {

        this.headerHeight = 20;

        // другое
        this.spacing = 5;
        this.margin = 5;

        // шрифты


        // размеры
        this.height = 0;
        this.width = 131.5f;

        binding = false;

        for (SettingComponent component : settings) {
            component.init();
        }

        super.init();
    }

    public void render(DrawContext pDrawContext, float x, float y, int mouseX, int mouseY) {
        if (getClickGUI().getAlpha().getValue() < 0.05) return;
        float spacing = 5;

        // позиции
        this.x = x;
        this.y = y;

        toggleAnimation.run(module.isEnabled() ? 1 : 0f);

        // другое
        overModule = isHovered(x, y, width, height, mouseX, mouseY);

        // бг
        DrawHandler.drawRound(pDrawContext.getMatrices(), x, y, width, height, 3, ColorHandler.applyOpacity(ClientColors.getBackgroundColor().brighter(),255 * getClickGUI().getAlpha().getValue()));

        // название модуля
        if (toggleAnimation.getValue() + 0.5 > 0.05f && getClickGUI().getAlpha().getValue() > 0.05) {
            Fonts.SEMI_15.drawString(pDrawContext.getMatrices(), binding ? "Bind - " + KeyHandler.getKeyboardKey(module.getKey()) : module.getName(), x + spacing, y + 7.5f, ClientColors.getFontColor().withAlpha(255 * (toggleAnimation.getValue() + 0.5F) * getClickGUI().getAlpha().getValue()).getRGB());
        }

        // дефолтная высота, увеличиваем ее если нам это нужно
        height = headerHeight;
        for (SettingComponent component : settings) {
            if (component.getSetting() != null && component.getSetting().getHideCondition() != null && component.getSetting().getHideCondition().getAsBoolean())
                continue;
            component.render(pDrawContext, x + spacing, y + height, mouseX, mouseY);
            height += component.getHeight() + spacing;
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        for (SettingComponent component : settings) {
            if (component.getSetting() != null && component.getSetting().getHideCondition() != null && component.getSetting().getHideCondition().getAsBoolean())
                continue;
            component.mouseClicked(mouseX, mouseY, button);
        }

        overModule = isHovered(x, y, width, height, mouseX, mouseY);

        // проверяем позицию курсора
        if (overModule) {
            /*
             убираем возможность включения модуля если курсор находится
             в области сеттингов и при нажатии где-то в этой области может
             что-то произойти (включение сеттинга например) p.s плохо объяснил
            */
            for (SettingComponent component : settings) {
                if (component.isHoveringClickableArea()) return;
            }
            switch (button) {
                case 0 -> module.toggle(); // включение/выключение модуля если нажатая кнопка мыши будет левой
                case 2 -> binding = true;  // позволяем биндить модуль если будет нажато колесико мыши
            }
        }

        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override // считываем нажатия для бинда
    public void keyPressed(int keyCode, int scanCode, int modifiers) {

        for (SettingComponent component : settings) {
            if (component.getSetting() != null && component.getSetting().getHideCondition() != null && component.getSetting().getHideCondition().getAsBoolean())
                continue;
            component.keyPressed(keyCode, scanCode, modifiers);
        }

        if (binding) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                // сбрасываем бинд
                module.setKey(0);
            } else {
                // применяем бинд
                module.setKey(keyCode);
            }
            binding = false;
        }

        super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        for (SettingComponent component : settings) {
            if (component.getSetting() != null && component.getSetting().getHideCondition() != null && component.getSetting().getHideCondition().getAsBoolean())
                continue;
            component.mouseReleased(mouseX, mouseY, button);
        }
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        for (SettingComponent component : settings) {
            if (component.getSetting() != null && component.getSetting().getHideCondition() != null && component.getSetting().getHideCondition().getAsBoolean())
                continue;
            component.charTyped(codePoint, modifiers);
        }
        super.charTyped(codePoint, modifiers);
    }
}