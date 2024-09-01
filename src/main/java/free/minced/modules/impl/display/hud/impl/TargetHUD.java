package free.minced.modules.impl.display.hud.impl;


import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.modules.impl.misc.NameProtect;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.render.Render2DEvent;
import free.minced.framework.render.GLHandler;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.modules.impl.combat.AttackAura;
import free.minced.modules.impl.display.hud.HUD;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.primary.other.ServerHandler;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.color.ColorHandler;
import free.minced.framework.color.CustomColor;
import free.minced.systems.draggable.Draggable;

import free.minced.framework.font.Fonts;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ModuleDescriptor(name = "TargetHUD", category = ModuleCategory.DISPLAY)

public class TargetHUD extends Module {

    private final Animation alphaAnimation = new Animation(Easing.LINEAR, 750);
    private final Animation heartsWidthAnimation = new Animation(Easing.EASE_OUT_SINE, 400);
    private final Animation goldenHeartsAnimation = new Animation(Easing.EASE_OUT_SINE, 400);
    private final Animation hurtAnimation = new Animation(Easing.EASE_OUT_SINE, 800);
    private final Draggable draggable = registerDraggable(this, "TargetHUD", 155, 86);


    @Override
    public void onEvent(Event event) {
        if (event instanceof Render2DEvent e) {
            float margin = 4; // отступ
            float headScale = 25;
            if (getTarget() == null || !(getTarget() instanceof PlayerEntity))
                return;

            float x = draggable.getX();
            float y = draggable.getY();
            float ycolor = y / Minced.getInstance().getModuleHandler().get(HUD.class).offsetColor.getValue().floatValue();
            draggable.setWidth(115);
            draggable.setHeight(32.5f);
            float width = draggable.getWidth();
            float height = draggable.getHeight();
            // обновляем анимации
            alphaAnimation.run(getTarget() != null || mc.currentScreen instanceof ChatScreen ? 1 : 0);
            alphaAnimation.setDuration(300);

            // если чат открыт, то мы делаем нашего игрока таргетом для киллауры (нужно чтобы таргетхуд рендерился)
            if (mc.currentScreen instanceof ChatScreen && alphaAnimation.getValue() > 0.05 && getTarget() == null) {
                AttackAura.target = mc.player;
            }

            if (alphaAnimation.getValue() > 0.05) {
                DrawHandler.drawBlurredShadow(e.getStack(), x, y, width, height, 5, ClientColors.getBrighterBackgroundColor().withAlpha(255 * alphaAnimation.getValue()));

                DrawHandler.drawStyledRect(e.getStack(), x, y, width, height);

                hurtAnimation.setDuration(100);
                hurtAnimation.run(getTarget().hurtTime > 0 ? 1 : 0);

                DrawHandler.drawRect(e.getStack(), x + margin, y + margin, headScale, headScale, ColorHandler.applyOpacity(ClientColors.getSecondaryBackgroundColor().darker(), 255 * alphaAnimation.getValue()));
                renderHead(e.getStack(), getTarget(), x + margin, y + margin, headScale, hurtAnimation.getValue());
                DrawHandler.drawRect(e.getStack(), x + margin, y + margin, headScale, headScale, ClientColors.RED.withAlpha(75 * (hurtAnimation.getValue() * alphaAnimation.getValue())));

                if (getHealth() == 1000) {
                    Fonts.SEMI_14.drawString(e.getStack(), "HP - ?", x + margin + headScale + margin, y + margin + 11, ClientColors.getFontColor().withAlpha(255 * alphaAnimation.getValue()).getRGB());
                } else {
                    Fonts.SEMI_14.drawString(e.getStack(), "HP - " + String.format("%.1f", getHealth()), x + margin + headScale + margin, y + margin + 11, ClientColors.getFontColor().withAlpha(255 * alphaAnimation.getValue()).getRGB());
                }

                float healthBarWidth = 25;

                float health = getHealth();
                float absorptionHealth = getTarget().getAbsorptionAmount();
                goldenHeartsAnimation.run(ServerHandler.isOnFT() ? 0 : absorptionHealth / Math.max(absorptionHealth, getTarget().getMaxHealth()) * healthBarWidth);
                heartsWidthAnimation.run(health / Math.max(health, getTarget().getMaxHealth()) * healthBarWidth);
                float animatedHealthBarWidth = heartsWidthAnimation.getValue();
                float goldenHealthBarWidth = goldenHeartsAnimation.getValue();

                String username = getTarget() == mc.player ? NameProtect.getCustomName() : getTarget().getName().getString();

                username = username.substring(0, Math.min(username.length(), 13));

                Fonts.SEMI_15.drawString(e.getStack(), username, x + margin + headScale + margin, y + margin + 2.5f,
                        ClientColors.getFontColor().withAlpha(255 * alphaAnimation.getValue()).getRGB());

                DrawHandler.drawBlurredShadow(e.getStack(),x + 80 , y + margin, 25, 10, 8, ClientColors.getBackgroundColor().darker(0.85F).withAlpha(255 * alphaAnimation.getValue()));

                DrawHandler.drawRect(e.getStack(), x + width - margin * 2, y + margin, 2.5f, 25, ClientColors.getSecondaryBackgroundColor().withAlpha(255 * alphaAnimation.getValue()));

                DrawHandler.verticalGradient(e.getStack(), x + width - margin * 2, y + margin + (healthBarWidth - animatedHealthBarWidth), 2.5f, animatedHealthBarWidth,
                        ColorHandler.applyOpacity(ClientColors.getSecondColor(), alphaAnimation.getValue() * 255),
                        ColorHandler.applyOpacity(ClientColors.getFirstColor(), alphaAnimation.getValue() * 255));

                DrawHandler.verticalGradient(e.getStack(), x + width - margin * 2, y + margin + (healthBarWidth - goldenHealthBarWidth), 2.5f, goldenHealthBarWidth,
                        new CustomColor(212, 175, 55).withAlpha(255 * alphaAnimation.getValue()),
                        new CustomColor(63, 52, 13).withAlpha(255 * alphaAnimation.getValue()));

                List<ItemStack> stacks = new ArrayList<>();

                stacks.add(getTarget().getMainHandStack());

                stacks.add(getTarget().getOffHandStack());

                getTarget().getArmorItems().forEach(stacks::add);

                stacks.removeIf(w -> w.getItem() instanceof AirBlockItem);

                float offset = -2;
                Collections.reverse(stacks);
                for (ItemStack stack : stacks) {
                    float xStack = x + margin + headScale + margin + offset - 2.5f;
                    float yStack = y + 17.5f;

                    drawItemStack(e.getContext(), stack, xStack, yStack);
                    offset += 11;
                }
            }
        }
    }
    public static void drawItemStack(DrawContext poseStack, ItemStack stack, double x, double y) {
        drawItemStack(poseStack, stack,x,y,0.6f);
    }

    public static void drawItemStack(DrawContext poseStack, ItemStack stack, double x, double y, float scale) {
        GLHandler.scaleStart(poseStack.getMatrices(), (float) (x + 8), (float) (y + 8), scale);
        poseStack.getMatrices().scale(-0.01f * -200, -0.01f * -200, -0.01f * -200);
        RenderSystem.enableDepthTest();
        poseStack.getMatrices().scale(0.5f, 0.5f, 0.5f);
       // mc.getItemRenderer().renderGuiItem(poseStack, stack, (int) x, (int) y);
        poseStack.drawItem(stack, (int) x, (int) y);


        String count = "";
        if (stack.getCount() > 1) {
            count = stack.getCount() + "";
        }
        poseStack.drawItemInSlot(mc.textRenderer, stack, (int) x, (int) y, count);

        //mc.getItemRenderer().renderGuiItemDecorations(poseStack,mc.font, stack, (int) x, (int) y, count);

        RenderSystem.disableDepthTest();
        GLHandler.scaleEnd(poseStack.getMatrices());
    }
    public void renderHead(MatrixStack poseStack, Entity entity, float x, float y, float scale, float alpha) {

        RenderSystem.enableBlend();

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

         if (entity instanceof PlayerEntity) {
                RenderSystem.setShaderTexture(0, ((AbstractClientPlayerEntity) entity).getSkinTextures().texture());
            } else {
                RenderSystem.setShaderTexture(0, mc.getEntityRenderDispatcher().getRenderer(entity).getTexture(entity));
            }

        DrawHandler.renderTexture(poseStack, (int) x, (int) y, (int) scale, (int) scale, 4, 4, 4, 4, 32, 32);

        RenderSystem.disableBlend();
    }

    public float getHealth() {


        LivingEntity target = getTarget();

        if (target instanceof PlayerEntity ent && (ServerHandler.isOnFT())) {
            ScoreboardObjective scoreBoard;
            String resolvedHp = "";
            if ((ent.getScoreboard()).getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME) != null) {
                scoreBoard = (ent.getScoreboard()).getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
                if (scoreBoard != null) {
                    ReadableScoreboardScore readableScoreboardScore = ent.getScoreboard().getScore(ent, scoreBoard);
                    MutableText text2 = ReadableScoreboardScore.getFormattedScore(readableScoreboardScore, scoreBoard.getNumberFormatOr(StyledNumberFormat.EMPTY));
                    resolvedHp = text2.getString();
                }
            }
            float numValue;
            try {
                numValue = Float.parseFloat(resolvedHp);
            } catch (NumberFormatException e) {
                return target.getHealth();
            }
            return numValue;
        } else return target.getHealth();

    }

    public static LivingEntity getTarget() {
        if (AttackAura.target != null) {
            if (AttackAura.target instanceof LivingEntity e) {
                return e;
            }
        }
        if (mc.currentScreen instanceof ChatScreen) {
            return mc.player;
        }
        return null;
    }

}
