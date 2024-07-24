package free.minced.framework.interfaces.impl.module;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import free.minced.framework.interfaces.api.MenuScreen;
import free.minced.modules.api.ModuleCategory;
import free.minced.primary.math.ScrollHandler;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author jbk
 * @since 17.10.2023
 */


@Getter
public class ModuleScreen extends MenuScreen {

    public ArrayList<ModuleComponent> moduleComponents;

    private final ScrollHandler scroll = new ScrollHandler();

    // другое
    private float margin;

    @Override
    public void init() {


        // собираем модули
        moduleComponents = getClickGUI().getModuleList().stream().filter(moduleComponent -> moduleComponent.getModule().getModuleCategory().equals(getCategory())).collect(Collectors.toCollection(ArrayList::new));

        // инициализируем модули
        for (ModuleComponent component : moduleComponents) {
            component.init();
        }

        // другое
        margin = 10;

        // позиции
        this.x = getClickGUI().getX() + 55;
        this.y = getClickGUI().getY();

        super.init();
    }

   @Override
   public void render(DrawContext DrawContext, int mouseX, int mouseY, float partialTicks) {

       float leftSideHeight = 0, rightSideHeight = 0, count = 0, overallHeight = 0;
       float moduleSpacing = 8;

       for (ModuleComponent component : getModuleComponents()) {
           overallHeight += component.getHeight() + moduleSpacing;
       }

       scroll.setMax(-overallHeight / 2f - moduleSpacing * (moduleComponents.size() * 2) + getClickGUI().getHeight());

       // Отрисовываем модули с учетом скролла
       for (ModuleComponent component : getModuleComponents()) {
           boolean rightSide = count % 2 == 1;
           component.render(DrawContext,x + getClickGUI().getWidth() / 2f - 63 / 2f - component.getWidth() + (rightSide ? component.getWidth() + moduleSpacing : -moduleSpacing / 2f),  y + (rightSide ? rightSideHeight : leftSideHeight) + moduleSpacing + scroll.getScroll(),
                   mouseX, mouseY);
           if (rightSide) {
               rightSideHeight += component.getHeight() + moduleSpacing;
           } else {
               leftSideHeight += component.getHeight() + moduleSpacing;
           }
           count++;
       }

       scroll.handle(); // Проверяем скролл
       /*scroll.enableScrolling();*/

       super.render(DrawContext, mouseX, mouseY, partialTicks);
   }


    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        for (ModuleComponent component : moduleComponents) {
            component.mouseClicked(mouseX, mouseY, button);
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ModuleComponent component : moduleComponents) {
            component.keyPressed(keyCode, scanCode, modifiers);
        }
        super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        for (ModuleComponent component : moduleComponents) {
            component.mouseReleased(mouseX, mouseY, button);
        }
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {
        super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        for (ModuleComponent component : moduleComponents) {
            component.charTyped(codePoint, modifiers);
        }
        super.charTyped(codePoint, modifiers);
    }

    private ModuleCategory getCategory() { // gets the category depending on the current screen

        for (final ModuleCategory moduleCategory : ModuleCategory.values()) {
            if (moduleCategory.getScreen() == this.getClickGUI().getCurrentScreen()) {
                return moduleCategory;
            }
        }
        return null;
    }
}
