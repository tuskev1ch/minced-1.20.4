package free.minced.modules.impl.display.hud.impl;



import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.render.Render2DEvent;

import free.minced.framework.color.ColorHandler;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.modules.impl.display.hud.AbstractHUDElement;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.systems.draggable.Draggable;

import free.minced.framework.font.Fonts;
import free.minced.systems.theme.PrimaryTheme;

import java.util.Locale;

@ModuleDescriptor(name = "PotionHUD", category = ModuleCategory.DISPLAY)

public class PotionHUD extends AbstractHUDElement {

    private final Draggable draggable = registerDraggable(this, "PotionHUD", 100, 100);

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
        float effectGap = 0; // пропуск между эффектами
        float offset = 0;
        float maxWidth = getDefaultWidth();

        for (StatusEffectInstance effect : mc.player.getStatusEffects()) {

            getPotionsAnimation().run(effect.getDuration() / 20f >= 1 ? 1 : 0);
            float effectBoxHeight = 14;
            float rightMargin = 5;
            float effectIconScale = 12;
            float leftMargin = 5;
            float nameLeftMargin = 5;


            float textY = y + offset + (getHeaderHeight() + gapBetweenHeader + 2.5f * getPotionsAnimation().getValue());

            if (getPotionsAnimation().getValue() > 0.05F) {
                // бэкграунд
                DrawHandler.drawBlurredShadow(poseStack, x, y + offset + (getHeaderHeight() + gapBetweenHeader) * getPotionsAnimation().getValue(), width, effectBoxHeight,5,  getBackgroundColor().withAlpha(100 * getPotionsAnimation().getValue()));

                if (Minced.getInstance().getThemeHandler().getPrimaryTheme() != PrimaryTheme.LIGHT) {
                    DrawHandler.drawRound(poseStack, x, y + offset + (getHeaderHeight() + gapBetweenHeader) * getPotionsAnimation().getValue(), width, effectBoxHeight, 3, ColorHandler.applyOpacity(getBackgroundColor().brighter(), 255 * getPotionsAnimation().getValue()));
                    DrawHandler.drawRect(poseStack, x, (y + offset + (getHeaderHeight() + gapBetweenHeader) * getPotionsAnimation().getValue()) - 3, width, effectBoxHeight - 3, ColorHandler.applyOpacity(getBackgroundColor().brighter(), 255 * getPotionsAnimation().getValue()));
                } else {
                    DrawHandler.drawRound(poseStack, x, y + offset + (getHeaderHeight() + gapBetweenHeader) * getPotionsAnimation().getValue(), width, effectBoxHeight, 3, ColorHandler.applyOpacity(getBackgroundColor().darker(0.85F), 255 * getPotionsAnimation().getValue()));
                    DrawHandler.drawRect(poseStack, x, (y + offset + (getHeaderHeight() + gapBetweenHeader) * getPotionsAnimation().getValue()) - 3, width, effectBoxHeight - 3, ColorHandler.applyOpacity(getBackgroundColor().darker(0.85F), 255 * getPotionsAnimation().getValue()));
                }
                // название эффекта
                Fonts.SEMI_14.drawString(poseStack, getEffectDisplayString(effect), x + leftMargin, textY + 2, ClientColors.getFontColor().withAlpha(255 * getPotionsAnimation().getValue()).getRGB());
                // длительность эффекта
                String effectDuration = getDurationText(effect, 1.0F).getString();

                Fonts.SEMI_14.drawString(poseStack, effectDuration, x + width - Fonts.SEMI_14.getStringWidth(effectDuration) - rightMargin, textY + 2, ClientColors.getFontColor().withAlpha(255 * getPotionsAnimation().getValue()).getRGB());
            }

            maxWidth = Math.max(maxWidth, leftMargin + effectIconScale  + Fonts.SEMI_14.getStringWidth(getEffectDisplayString(effect)) + Fonts.SEMI_14.getStringWidth(getDurationText(effect, 1.0F).getString()));

            offset += (effectBoxHeight + effectGap) * getPotionsAnimation().getValue();
        }
        getWidthAnimation().run(maxWidth);

        DrawHandler.drawBlurredShadow(poseStack, x, y, width, getHeaderHeight(),  5, ClientColors.getBrighterBackgroundColor().withAlpha(180));

        // бэкграунд хеадера
        if (mc.player.getActiveStatusEffects().isEmpty()) {
            if (Minced.getInstance().getThemeHandler().getPrimaryTheme() != PrimaryTheme.LIGHT) {
                DrawHandler.drawRound(poseStack, x, y, width, getHeaderHeight(), 3, getHeaderColor().darker());
            } else {
                DrawHandler.drawRound(poseStack, x, y, width, getHeaderHeight(), 3, getHeaderColor().brighter());
            }
        } else {
            if (Minced.getInstance().getThemeHandler().getPrimaryTheme() != PrimaryTheme.LIGHT) {
                DrawHandler.drawRound(poseStack, x, y, width, getHeaderHeight(), 3, getHeaderColor().darker());
                DrawHandler.drawRect(poseStack, x, y + 3, width, getHeaderHeight() - 3, getHeaderColor().darker());
            } else {
                DrawHandler.drawRound(poseStack, x, y, width, getHeaderHeight(), 3, getHeaderColor().brighter());
                DrawHandler.drawRect(poseStack, x, y + 3, width, getHeaderHeight() - 3, getHeaderColor().brighter());
            }
        }
//        Fonts.ICON_24.drawString(poseStack, Icons.POTION.getCharacter(), x + 5, y + 4.5f, ClientColors.getFontColor().getRGB());

        Fonts.SEMI_16.drawCenteredString(poseStack, getHeaderLabel(), x + width / 2, y + 5.5f, ClientColors.getFontColor().getRGB());
    }

    @Override
    public Draggable getDraggable() {
        return draggable;
    }

    @Override
    public String getHeaderLabel() {
        return "Potions";
    }

    public static Text getDurationText(StatusEffectInstance effect, float multiplier) {
        if (effect.isInfinite()) {
            return Text.translatable("effect.duration.infinite");
        } else {
            int i = MathHelper.floor((float)effect.getDuration() * multiplier);
            return Text.literal(formatTickDuration(i));
        }
    }
    public static String formatTickDuration(int pTicks) {
        int i = pTicks / 20;
        int j = i / 60;
        i %= 60;
        int k = j / 60;
        j %= 60;
        return k > 0 ? String.format(Locale.ROOT, "%02d:%02d:%02d", k, j, i) : String.format(Locale.ROOT, "%02d:%02d", j, i);
    }

    public static String getDuration(StatusEffectInstance pe) {
        if (pe.isInfinite()) {
            return "*:*";
        } else {
            int var1 = pe.getDuration();
            int mins = var1 / 1200;
            String sec = String.format("%02d", (var1 % 1200) / 20);
            return mins + ":" + sec;
        }
    }
    private String getEffectDisplayString(StatusEffectInstance effect) {
        String displayString = effect.getEffectType().getName().getString();

        // Получаем уровень эффекта
        int amplifier = effect.getAmplifier() + 1;
        if (amplifier == 0) {
            return displayString; // возвращаем просто название, без уровня эффекта
        } else {
            return displayString + " " + amplifier;
        }
    }
}