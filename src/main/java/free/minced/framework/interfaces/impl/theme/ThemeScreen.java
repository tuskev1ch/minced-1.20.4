package free.minced.framework.interfaces.impl.theme;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import free.minced.framework.interfaces.api.MenuScreen;
import free.minced.primary.math.ScrollHandler;
import free.minced.systems.theme.Theme;

import java.util.ArrayList;
import java.util.List;


@Getter
public class ThemeScreen extends MenuScreen {

    private float margin;

    private final List<ThemeComponent> themeComponents = new ArrayList<>();
    private final ScrollHandler scroll = new ScrollHandler();

    @Override
    public void init() {

        scroll.reset();

        // добавляем темы в список
        themeComponents.clear();
        for (Theme theme : Theme.values()) {
            themeComponents.add(new ThemeComponent(theme));
        }

        // инициализируем компоненты
        for (ThemeComponent component : themeComponents) {
            component.init();
        }

        margin = 10;
        // размеры компонента
        width = 80;
        height = 20;

        super.init();
    }

    @Override
    public void render(DrawContext DrawContext, int mouseX, int mouseY, float partialTicks) {

        // тускевич втф
        float gap = 8;
        for (int i = 0; i < themeComponents.size(); i++) {
            ThemeComponent component = this.themeComponents.get(i);
            x = getClickGUI().getX() + getClickGUI().getSidebar().getWidth() + getClickGUI().getWidth() / 2f - getClickGUI().getSidebar().getWidth() / 2f - component.getWidth() - component.getWidth() / 2f - gap / 2f + (gap + width) * (i % 3) - 5;
            y = (float) (getClickGUI().getY() + margin + Math.floor(i / 3D) * (height + gap) + scroll.getScroll());

            component.render(DrawContext,x, y, width, height, mouseX, mouseY);
        }

        float rows = themeComponents.size() / 2;
        scroll.handle();
        scroll.setMax((-height + gap) * Math.max(0, (rows - 3)));

        super.render(DrawContext, mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {

        for (ThemeComponent component : themeComponents) {
            component.mouseClicked(mouseX, mouseY, button);
        }

        super.mouseClicked(mouseX, mouseY, button);
    }
}
