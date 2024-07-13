package free.minced.framework.interfaces.api;

import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import lombok.Setter;
import free.minced.framework.interfaces.impl.module.ModuleComponent;
import free.minced.primary.IHolder;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.render.GuiHandler;
import free.minced.systems.setting.Setting;

@Getter
@Setter
@SuppressWarnings("unused")
public class SettingComponent extends GuiHandler implements IHolder {

    public float x, y, width, height = 20;
    public boolean hoveringClickableArea; // условие при котором сеттинг будет нажиматся, если условие не будет соблюдено, при нажатии ничего не произойдет
    protected Setting setting;
    private Animation animation = new Animation(Easing.EASE_IN_CIRC, 350);
    private final ModuleComponent moduleComponent;

    public SettingComponent(Setting setting, ModuleComponent moduleComponent) {
        this.setting = setting;
        this.moduleComponent = moduleComponent;
    }

    public void init() {
    }

    public void render(DrawContext pDrawContext, float x, float y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
    }

    public void charTyped(char codePoint, int modifiers) {

    }

}