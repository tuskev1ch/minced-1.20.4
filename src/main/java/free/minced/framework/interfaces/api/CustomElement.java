package free.minced.framework.interfaces.api;

import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import lombok.Setter;
import free.minced.primary.IHolder;
import free.minced.framework.render.GuiHandler;

@Getter
@Setter
@SuppressWarnings("all")
public class CustomElement extends GuiHandler implements IHolder {

    public float x, y, width, height;
    private boolean isDisplayingElement;

    public void init() {

    }

    public void render(DrawContext pDrawContextint, int mouseX, int mouseY) {
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
