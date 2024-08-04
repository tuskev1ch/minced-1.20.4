package free.minced.framework.interfaces.api;

import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import lombok.Setter;
import free.minced.primary.IHolder;
import free.minced.framework.render.GuiHandler;

@Getter @Setter
public class MenuComponent extends GuiHandler implements IHolder {

    public float x, y, width, height;

    public void init() {
    }

    public void render(DrawContext DrawContext, int mouseX, int mouseY, float partialTicks) {
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
