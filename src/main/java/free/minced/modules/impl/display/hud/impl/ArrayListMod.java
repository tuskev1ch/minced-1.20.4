package free.minced.modules.impl.display.hud.impl;


import free.minced.modules.api.ModuleManager;
import free.minced.systems.setting.impl.BooleanSetting;
import net.minecraft.client.util.math.MatrixStack;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.render.Render2DEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.modules.impl.display.hud.AbstractHUDElement;
import free.minced.modules.impl.display.hud.HUD;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.color.ColorHandler;

import free.minced.framework.font.Fonts;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ModuleDescriptor(name = "ArrayList", category = ModuleCategory.DISPLAY)
public class ArrayListMod extends AbstractHUDElement {
    public final BooleanSetting reversed = new BooleanSetting("Reversed", this, false);

    @Override
    public void onEvent(Event event) {
        if (event instanceof Render2DEvent e) {
            if (mc.player == null || mc.world == null) return;
            render(e.getStack());
        }
    }

    public List<Module> getValidModules() {
        return ModuleManager.modules.stream().filter(module -> {
                    module.getEnableAnimation().run(module.isEnabled() ? 1 : 0);
                    return module.getEnableAnimation().getValue() > 0.1f;
                })
                .sorted(Comparator.comparingDouble(module -> - Fonts.SEMI_15.getStringWidth(module.getName()))).collect(Collectors.toList());
    }

    @Override
    public void render(MatrixStack pPoseStack) {
        float offset = 0.0F;
        for (Module module : getValidModules()) {
            if (module.getModuleCategory() == ModuleCategory.RENDER) continue;
            String moduleName = module.getName();

            float ycolor;
            float padding = 4;

            float width = Fonts.SEMI_15.getStringWidth(moduleName) + padding;
            float height = 7f + padding;

            boolean a1 = reversed.isEnabled();
            float y;
            float x;
            if (a1) {
                x = sr.getScaledWidth().floatValue() - 12f; // move to the right side
                y = 35 + offset;

                ycolor = y / Minced.getInstance().getModuleHandler().get(HUD.class).offsetColor.getValue().floatValue();

                DrawHandler.drawBlurredShadow(pPoseStack, x - width * module.getEnableAnimation().getValue(), y + 0.5F, width + 0.5f, height,
                        5, ColorHandler.applyOpacity(ClientColors.getBrighterBackgroundColor(), 255 * module.getEnableAnimation().getValue()));

                DrawHandler.drawBlurredShadow( pPoseStack, x - width * module.getEnableAnimation().getValue() - 3 , y + 0.5F, 2, height, 2,
                        ColorHandler.applyOpacity(getTheme().getAccentColorReverse(x, ycolor), 255 * module.getEnableAnimation().getValue()));

                DrawHandler.drawRect( pPoseStack, x - width * module.getEnableAnimation().getValue() - 3, y + 0.5F, 3, height,
                        ColorHandler.applyOpacity(getTheme().getAccentColorReverse(x, ycolor), 255 * module.getEnableAnimation().getValue()));


                DrawHandler.drawRect(pPoseStack, x - width * module.getEnableAnimation().getValue(), y + 0.5f, width + 0.5f, height, ClientColors.getBrighterBackgroundColor().withAlpha(255 * module.getEnableAnimation().getValue()));

                // полоска справа

                // имя модуля
                if (module.getEnableAnimation().getValue() > 0.05F) {
                    Fonts.SEMI_15.drawString(pPoseStack, moduleName, x - width * module.getEnableAnimation().getValue() + 2.5f, y + 4, ColorHandler.applyOpacity(getTheme().getAccentColorReverse(x, ycolor), 255 * module.getEnableAnimation().getValue()).getRGB());
                }
            } else {
                x = 12f;
                y = 35 + offset;

                ycolor = y / Minced.getInstance().getModuleHandler().get(HUD.class).offsetColor.getValue().floatValue();

                DrawHandler.drawBlurredShadow(pPoseStack, x * module.getEnableAnimation().getValue(), y + 0.5F, width + 0.5f, height,
                        5, ColorHandler.applyOpacity(ClientColors.getBrighterBackgroundColor(), 255 * module.getEnableAnimation().getValue()));

                DrawHandler.drawBlurredShadow( pPoseStack, x * module.getEnableAnimation().getValue() - 3, y + 0.5F, 2, height, 2,
                        ColorHandler.applyOpacity(getTheme().getAccentColorReverse(x, ycolor), 255 * module.getEnableAnimation().getValue()));

                DrawHandler.drawRect( pPoseStack, x * module.getEnableAnimation().getValue() - 3, y + 0.5F, 3, height,
                        ColorHandler.applyOpacity(getTheme().getAccentColorReverse(x, ycolor), 255 * module.getEnableAnimation().getValue()));

                DrawHandler.drawRect(pPoseStack, x * module.getEnableAnimation().getValue(), y + 0.5f, width + 0.5f, height, ClientColors.getBrighterBackgroundColor().withAlpha(255 * module.getEnableAnimation().getValue()));

                // имя модуля
                if (module.getEnableAnimation().getValue() > 0.05F) {
                    Fonts.SEMI_15.drawString(pPoseStack, moduleName, (x * module.getEnableAnimation().getValue()) + 2.5f , y + 4,  ColorHandler.applyOpacity(getTheme().getAccentColorReverse(x, ycolor), 255 * module.getEnableAnimation().getValue()).getRGB());
                }
            }

            offset += height * module.getEnableAnimation().getValue();
        }
    }
    @Override
    public float getCornerRadius() {
        return 2;
    }
}