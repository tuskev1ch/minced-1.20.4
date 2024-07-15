package free.minced.modules.impl.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.modules.impl.display.hud.impl.PotionHUD;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.render.Render2DEvent;
import free.minced.framework.font.CFontRenderer;
import free.minced.framework.font.Fonts;
import free.minced.framework.color.CustomColor;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;

import free.minced.primary.chat.ChatHandler;
import free.minced.primary.other.ServerHandler;
import free.minced.systems.setting.impl.MultiBoxSetting;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4d;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ModuleDescriptor(name = "EntityESP", category = ModuleCategory.RENDER)
public class EntityESP extends Module {

    public final MultiBoxSetting elements = new MultiBoxSetting("Elements", this, "NameTags", "Items");
    public final MultiBoxSetting subElements = new MultiBoxSetting("Sub Elements", this, () -> !elements.get("NameTags").isEnabled(),
            "Armor", "Effects");

    private double posYZ;

    @Override
    public void onEvent(Event event) {
        if (event instanceof Render2DEvent e) {
            if (mc.player == null || mc.world == null) return;


            if (mc.getEntityRenderDispatcher().camera == null) return;


            for (Entity ent : mc.world.getEntities()) {
                if (ent instanceof PlayerEntity entity) {
                    if (elements.get("NameTags").isEnabled()) {
                        renderNameTag(e, entity);
                    }
                }
                if (ent instanceof ItemEntity entity) {
                    if (elements.get("Items").isEnabled()) {
                        renderItemESP(e, entity);
                    }
                }
            }
        }
    }
    private void renderNameTag(Render2DEvent e, PlayerEntity player) {
        if (player == mc.player && mc.options.getPerspective().isFirstPerson()) return;

        double x = interpolate(player.prevX, player.getX());
        double y = interpolate(player.prevY, player.getY());
        double z = interpolate(player.prevZ, player.getZ());

        Vec3d projectedPos = DrawHandler.projectCoordinates(new Vec3d(x, y + getPlayerHeight(player), z));
        if (projectedPos.z <= 0 || projectedPos.z >= 1) return;

        StringBuilder stringbuilder = new StringBuilder();

        String textComponent = "";

        textComponent += (player.getDisplayName().getString()) + " ";
        textComponent += getHealthColor(getHealth(player)) + round2(getHealth(player)) + " ";

        CFontRenderer Font = Fonts.SEMI_14;

        float textWidth = Font.getStringWidth(textComponent) + 6;
        float posX = (float) projectedPos.x - textWidth / 2;
        float posY = ((float) projectedPos.y - getPlayerHeight(player));

        e.getStack().push();
        e.getStack().translate(posX + textWidth / 2, posY + 6.5f, 0);
        e.getStack().scale(0.9f, 0.9f, 1f);
        e.getStack().translate(-posX - textWidth / 2, -posY - 6.5f, 0);

        Color colorRect = Minced.getInstance().getPartnerHandler().isFriend(player) ? new Color(0, 255, 0, 99).darker() : new CustomColor(22, 22, 22).withAlpha(155);
        DrawHandler.drawRect(e.getStack(), posX - 2, posY, textWidth, 11, colorRect);

        Font.drawString(e.getStack(), textComponent, posX + 1, posY + 4, new CustomColor(255, 255, 255).getRGB());

        double maxEnchantY = 0;
        float item_offset = 0;

        ArrayList<ItemStack> stacks = new ArrayList<>();


        stacks.add(player.getInventory().armor.get(0));
        stacks.add(player.getInventory().armor.get(1));
        stacks.add(player.getInventory().armor.get(2));
        stacks.add(player.getInventory().armor.get(3));
        stacks.add(player.getMainHandStack());
        stacks.add(player.getOffHandStack());

        if (subElements.get("Armor").isEnabled()) {
            for (ItemStack armorComponent : stacks) {
                if (!armorComponent.isEmpty()) {
                    e.getContext().getMatrices().push();
                    e.getContext().getMatrices().translate(posX + item_offset, (float) (posY - 20), 0);
                    e.getContext().getMatrices().scale(0.9f, 0.9f, 0.9f);
                    DiffuseLighting.disableGuiDepthLighting();
                    e.getContext().drawItem(armorComponent, 0, 0);
                    e.getContext().drawItemInSlot(mc.textRenderer, armorComponent, 0, 0);
                    e.getContext().getMatrices().pop();


                    float enchantmentY = 0;

                    NbtList enchants = armorComponent.getEnchantments();
                    for (int index = 0; index < enchants.size(); ++index) {
                        String id = enchants.getCompound(index).getString("id");
                        short level = enchants.getCompound(index).getShort("lvl");
                        String encName = " ";

                        switch (id) {
                            case "minecraft:blast_protection", "blast_protection" -> encName = "B" + level;
                            case "minecraft:protection", "protection" -> encName = "P" + level;
                            case "minecraft:thorns", "thorns" -> encName = "T" + level;
                            case "minecraft:sharpness", "sharpness" -> encName = "S" + level;
                            case "minecraft:efficiency", "efficiency" -> encName = "E" + level;
                            case "minecraft:unbreaking", "unbreaking" -> encName = "U" + level;
                            case "minecraft:power", "power" -> encName = "PO" + level;
                            default -> {
                                continue;
                            }
                        }

                        Fonts.SEMI_12.drawString(e.getStack(), encName, posX + 3 + item_offset, (float) posY - 28 + enchantmentY, -1);

                        enchantmentY -= 8;
                        if (maxEnchantY > enchantmentY)
                            maxEnchantY = enchantmentY;
                    }
                }
                item_offset += 18f;
            }
        }
        if (subElements.get("Effects").isEnabled()) {
            renderStatusEffectOverlay(e.getContext(), (float) posX, (float) (posY + maxEnchantY - 60), player);
        }

        e.getStack().pop();
    }
    private void renderStatusEffectOverlay(DrawContext context, float x, float y, PlayerEntity player) {
        ArrayList<StatusEffectInstance> effects = new ArrayList<>(player.getStatusEffects());
        if (effects.isEmpty())
            return;
        x += effects.size() * 12.5f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(effects)) {
            x -= 25;
            String power = "";
            switch (statusEffectInstance.getAmplifier()) {
                case 0 -> power = "I";
                case 1 -> power = "II";
                case 2 -> power = "III";
                case 3 -> power = "IV";
                case 4 -> power = "V";
            }

            context.getMatrices().push();
            context.getMatrices().translate(x, y, 0);
            context.drawSprite(0, 0, 0, 18, 18, mc.getStatusEffectSpriteManager().getSprite(statusEffectInstance.getEffectType()));
            Fonts.SEMI_12.drawCenteredString(context.getMatrices(), PotionHUD.getDuration(statusEffectInstance), 9, -8, -1);
            Fonts.SEMI_12.drawCenteredString(context.getMatrices(), power, 9, -16, -1);
            context.getMatrices().pop();

        }
        RenderSystem.disableBlend();
    }
    private double interpolate(double previous, double current) {
        return previous + (current - previous) * mc.getTickDelta();
    }
    private void renderItemESP(Render2DEvent e, ItemEntity ent) {
        Vec3d[] vectors = getPoints(ent);

        Vector4d position = null;
        for (Vec3d vector : vectors) {
            vector = DrawHandler.projectCoordinates(new Vec3d(vector.x, vector.y, vector.z));
            if (vector.z > 0 && vector.z < 1) {
                if (position == null)
                    position = new Vector4d(vector.x, vector.y, vector.z, 0);
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
                position.w = Math.max(vector.y, position.w);
            }
        }

        if (position != null) {
            float posX = (float) position.x;
            float posY = (float) position.y;
            float endPosX = (float) position.z;


            int count = ent.getStack().getCount();
            String textComponent = "";

            textComponent += (ent.getDisplayName().getString()) + " ";
            textComponent += ent.getStack().getItem().getMaxCount() > 1 ? "x" + count : "";


            float diff = (endPosX - posX) / 2f;
            float textWidth = ent.getStack().getItem().getMaxCount() > 1 ? Fonts.SEMI_14.getStringWidth(textComponent) : Fonts.SEMI_14.getStringWidth(textComponent) - 2;
            float tagX = (posX + diff - textWidth / 2f) * 1;

            Color colorRect = new CustomColor(22,22,22).withAlpha(155);



            DrawHandler.drawRect(e.getStack(), tagX - 2, posY, textWidth + 4, 9, colorRect);

            Fonts.SEMI_14.drawString(e.getStack(), textComponent, tagX, posY + 2.5f, new CustomColor(255,255,255).getRGB());
        }
    }
    private float getPlayerHeight(PlayerEntity player) {
        return player.getPose() == EntityPose.CROUCHING ? 1.5f : 2.0f;
    }
    public static float round2(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value))
            return 1f;
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public float getHealth(PlayerEntity ent) {
        // Первый в комьюнити хп резольвер. Правда, еж?
        if ((mc.getNetworkHandler() != null && mc.getNetworkHandler().getServerInfo() != null && ServerHandler.isOnFT())) {
            ScoreboardObjective scoreBoard = null;
            String resolvedHp = "";
            if ((ent.getScoreboard()).getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME) != null) {
                scoreBoard = (ent.getScoreboard()).getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
                if (scoreBoard != null) {
                    ReadableScoreboardScore readableScoreboardScore = ent.getScoreboard().getScore(ent, scoreBoard);
                    MutableText text2 = ReadableScoreboardScore.getFormattedScore(readableScoreboardScore, scoreBoard.getNumberFormatOr(StyledNumberFormat.EMPTY));
                    resolvedHp = text2.getString();
                }
            }
            float numValue = 0;
            try {
                numValue = Float.parseFloat(resolvedHp);
            } catch (NumberFormatException ignored) {
            }
            return numValue;
        } else return ent.getHealth() + ent.getAbsorptionAmount();
    }

    @NotNull
    private static Vec3d[] getPoints(Entity ent) {
        double x = ent.prevX + (ent.getX() - ent.prevX) * mc.getTickDelta();
        double y = ent.prevY + (ent.getY() - ent.prevY) * mc.getTickDelta();
        double z = ent.prevZ + (ent.getZ() - ent.prevZ) * mc.getTickDelta();
        Box axisAlignedBB2 = ent.getBoundingBox();
        Box axisAlignedBB = new Box(axisAlignedBB2.minX - ent.getX() + x - 0.05, axisAlignedBB2.minY - ent.getY() + y, axisAlignedBB2.minZ - ent.getZ() + z - 0.05, axisAlignedBB2.maxX - ent.getX() + x + 0.05, axisAlignedBB2.maxY - ent.getY() + y + 0.15, axisAlignedBB2.maxZ - ent.getZ() + z + 0.05);
        Vec3d[] vectors = new Vec3d[]{new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)};
        return vectors;
    }
    private @NotNull String getHealthColor(float health) {
        if (health <= 15 && health > 7) return Formatting.YELLOW + "";
        if (health > 15) return Formatting.GREEN + "";
        return Formatting.RED + "";
    }
}
