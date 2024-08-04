package free.minced.modules.impl.display.hud.impl;



import free.minced.modules.api.ModuleManager;
import net.minecraft.client.util.math.MatrixStack;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.render.Render2DEvent;
import free.minced.framework.color.ColorHandler;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.modules.impl.display.hud.AbstractHUDElement;
import free.minced.primary.other.KeyHandler;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.systems.draggable.Draggable;

import free.minced.framework.font.Fonts;
import free.minced.systems.theme.PrimaryTheme;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.stream.Collectors;

@ModuleDescriptor(name = "KeyBinds", category = ModuleCategory.DISPLAY)

public class KeyBinds extends AbstractHUDElement {

    private final Draggable draggable = registerDraggable(this, "Keybinds", 50, 50);

    public List<Module> getValidModules() {
        return ModuleManager.modules.stream()
                .filter(module -> {
                    module.getEnableAnimation().run(module.isEnabled() ? 1 : 0);
                    return module.getKey() != GLFW.GLFW_KEY_UNKNOWN
                            && module.getKey() != 0
                            && module.getEnableAnimation().getValue() > 0.01F;
                })
                .collect(Collectors.toList());
    }


    @Override
    public void onEvent(Event event) {
        if (event instanceof Render2DEvent e) {
            render(e.getStack());
        }
    }

    @Override
    public void render(MatrixStack poseStack) {
        if (this.mc.player == null || this.mc.world == null) return;


        // обновляем анимации
        getHeightAnimation().run(getHeaderHeight());

        // хеадер
        float width = getWidthAnimation().getValue();
        float height = getHeightAnimation().getValue();
        float x = draggable.getX();
        float y = draggable.getY();

        draggable.setWidth(width);
        draggable.setHeight(height);


        float gapBetweenHeader = 0; // пропуск между хеадером
        float moduleGap = 0; // пропуск между модулями
        float keyNameLeftMargin = 5;
        float offset = 0;
        float middleOfBox = 2.5f;

        float radius = 3;
        float maxWidth = getDefaultWidth();

        for (Module module : getValidModules()) {


            float moduleHeight = 14;
            float leftMargin = 5;
            float rightMargin = 5;

            // бэкграунд
            DrawHandler.drawBlurredShadow(poseStack,  x, y + offset + (getHeaderHeight() + gapBetweenHeader) * module.getEnableAnimation().getValue(), width, moduleHeight,5,  getBackgroundColor().withAlpha(100 * module.getEnableAnimation().getValue()));

            if (Minced.getInstance().getThemeHandler().getPrimaryTheme() != PrimaryTheme.LIGHT) {
                DrawHandler.drawRound(poseStack, x, y + offset + (getHeaderHeight() + gapBetweenHeader) * module.getEnableAnimation().getValue(), width, moduleHeight, radius, ColorHandler.applyOpacity(getBackgroundColor().brighter(), 255 * module.getEnableAnimation().getValue()));
                DrawHandler.drawRect(poseStack, x, (y + offset + (getHeaderHeight() + gapBetweenHeader) * module.getEnableAnimation().getValue()) - 3, width, moduleHeight - 3, ColorHandler.applyOpacity(getBackgroundColor().brighter(), 255 * module.getEnableAnimation().getValue()));
            } else {
                DrawHandler.drawRound(poseStack, x, y + offset + (getHeaderHeight() + gapBetweenHeader) * module.getEnableAnimation().getValue(), width, moduleHeight, radius, ColorHandler.applyOpacity(getBackgroundColor().darker(0.85F), 255 * module.getEnableAnimation().getValue()));
                DrawHandler.drawRect(poseStack, x, (y + offset + (getHeaderHeight() + gapBetweenHeader) * module.getEnableAnimation().getValue()) - 3, width, moduleHeight - 3, ColorHandler.applyOpacity(getBackgroundColor().darker(0.85F), 255 * module.getEnableAnimation().getValue()));
            }

            // название модуля
            if (module.getEnableAnimation().getValue() > 0.05F) {
                Fonts.SEMI_14.drawString(poseStack, module.getName(), x + leftMargin, y + offset + (getHeaderHeight() + gapBetweenHeader + middleOfBox) * module.getEnableAnimation().getValue() + 2, ClientColors.getFontColor().withAlpha(255 * module.getEnableAnimation().getValue()).getRGB());
            }

            String keyName = "[%s]".formatted(KeyHandler.getKeyboardKey(module.getKey()));
            // кнопка
            float keyNameWidth = Fonts.SEMI_14.getStringWidth(keyName);
            if (module.getEnableAnimation().getValue() > 0.05F) {
                Fonts.SEMI_14.drawString(poseStack, keyName, x + width - keyNameWidth - rightMargin, y + offset + (getHeaderHeight() + gapBetweenHeader + middleOfBox) * module.getEnableAnimation().getValue()+ 2, ClientColors.getFontColor().withAlpha(255 * module.getEnableAnimation().getValue()).getRGB());
            }

            maxWidth = Math.max(maxWidth, leftMargin + Fonts.SEMI_14.getStringWidth(module.getName()) + keyNameLeftMargin + Fonts.SEMI_14.getStringWidth(keyName) + rightMargin);

            offset += (moduleHeight + moduleGap) * module.getEnableAnimation().getValue();

        }

        getWidthAnimation().run(maxWidth);

        DrawHandler.drawBlurredShadow(poseStack, x, y, width, getHeaderHeight() ,5,  getBackgroundColor().withAlpha(180));

        if (getValidModules().isEmpty()) {
            if (Minced.getInstance().getThemeHandler().getPrimaryTheme() != PrimaryTheme.LIGHT) {
                DrawHandler.drawRound(poseStack, x, y, width, getHeaderHeight(), radius, getHeaderColor().darker());
            } else {
                DrawHandler.drawRound(poseStack, x, y, width, getHeaderHeight(), radius, getHeaderColor().brighter());
            }
        } else {
            if (Minced.getInstance().getThemeHandler().getPrimaryTheme() != PrimaryTheme.LIGHT) {
                DrawHandler.drawRound(poseStack, x, y, width, getHeaderHeight(), radius, getHeaderColor().darker());
                DrawHandler.drawRect(poseStack, x, y + 3, width, getHeaderHeight() - 3, getHeaderColor().darker());
            } else {
                DrawHandler.drawRound(poseStack, x, y, width, getHeaderHeight(), radius, getHeaderColor().brighter());
                DrawHandler.drawRect(poseStack, x, y + 3, width, getHeaderHeight() - 3, getHeaderColor().brighter());
            }
        }
//        Fonts.ICON_24.drawString(poseStack, Icons.KEYBOARD.getCharacter(), x + 5, y + 4.5f, ClientColors.getFontColor().getRGB());
        Fonts.SEMI_16.drawCenteredString(poseStack, getHeaderLabel(), x + width / 2, y + 5.5f, ClientColors.getFontColor().getRGB());
    }


        @Override
    public Draggable getDraggable() {
        return draggable;
    }

    @Override
    public String getHeaderLabel() {
        return "Keybinds";
    }


}