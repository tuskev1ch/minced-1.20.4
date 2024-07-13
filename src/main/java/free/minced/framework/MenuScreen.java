package free.minced.framework;

import lombok.Getter;
import lombok.Setter;
import free.minced.framework.render.GuiHandler;
import free.minced.primary.IHolder;

@Getter
@Setter
public class MenuScreen extends GuiHandler implements IHolder {

    public float x, y, width, height;

    public void init() {
    }



    public void mouseClicked(double mouseX, double mouseY, int button) {
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
    }

    public void mouseScrolled(double mouseX, double mouseY, double delta) {
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
    }

    public void charTyped(char codePoint, int modifiers) {
    }

}