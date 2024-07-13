package free.minced.framework.interfaces.impl.settings;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import free.minced.framework.interfaces.api.SettingComponent;
import free.minced.framework.interfaces.api.TextField;
import free.minced.framework.interfaces.impl.module.ModuleComponent;
import free.minced.framework.font.Fonts;
import free.minced.systems.setting.Setting;
import free.minced.systems.setting.impl.StringSetting;


@Getter
public class StringSettingComponent extends SettingComponent {

    private TextField textField;
    private StringSetting stringSetting;
    private boolean hoveringParentComponent;

    public StringSettingComponent(Setting setting, ModuleComponent moduleComponent) {
        super(setting, moduleComponent);
    }

    @Override
    public void init() {
        this.stringSetting = (StringSetting) getSetting();
        this.width = 100;
        this.height = 20;

        this.textField = new TextField(x, y, width, height, Fonts.SEMI_14, stringSetting.getName(), true);

        this.textField.setFocused(true);
        this.textField.setText(getStringSetting().getText());
        this.textField.setNormalCursorPosition(textField.getText().length());
        // 300iq bugfix
        this.textField.setTextPosX(textField.getX());

        super.init();
    }

    @Override
    public void render(DrawContext pDrawContext, float x, float y, int mouseX, int mouseY) {

        this.textField.setX(x + getModuleComponent().getWidth() / 2f - width / 2f - 8.5f /*потому что рендер начинается с x + 5*/);
        this.textField.setY(y);


        hoveringParentComponent = isHovered(getModuleComponent().getX(), getModuleComponent().getY(), getModuleComponent().getWidth(), getModuleComponent().getHeight() + textField.getHeight() + 5, mouseX, mouseY);

        textField.setAlpha(getClickGUI().getAlpha().getValue());
        textField.render(pDrawContext, mouseX, mouseY);

        super.render(pDrawContext, x, y, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        hoveringClickableArea = isHovered(textField.getX(), textField.getY(), textField.getWidth(), textField.getHeight(), mouseX, mouseY);

        if (hoveringClickableArea) {
            textField.mouseClicked(mouseX, mouseY, button);
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isHoveringParentComponent()) {
            textField.keyPressed(keyCode, scanCode, modifiers);
            getStringSetting().setText(textField.getText());
        }
        super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        if (isHoveringParentComponent() && textField.getText().length() < getStringSetting().getMaxLength()) {
            textField.charTyped(codePoint, modifiers);
            getStringSetting().setText(textField.getText());
        }
        super.charTyped(codePoint, modifiers);
    }
}