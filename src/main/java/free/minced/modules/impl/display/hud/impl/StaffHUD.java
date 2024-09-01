package free.minced.modules.impl.display.hud.impl;

import com.mojang.authlib.GameProfile;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.events.impl.render.Render2DEvent;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.color.ClientColors;
import free.minced.framework.color.ColorHandler;
import free.minced.framework.font.Fonts;
import free.minced.framework.render.DrawHandler;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.modules.impl.display.hud.AbstractHUDElement;
import free.minced.systems.draggable.Draggable;
import free.minced.systems.theme.PrimaryTheme;
import lombok.Getter;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ModuleDescriptor(name = "StaffHUD", category = ModuleCategory.DISPLAY)
public class StaffHUD extends AbstractHUDElement {
    private final Draggable draggable = registerDraggable(this, "StaffHUD", 100, 250);

    private static final Pattern validUserPattern = Pattern.compile("^\\w{3,16}$");
    private final List<String> staffList = new ArrayList<>();


    @Override
    public void onEvent(Event e) {
        if (e instanceof Render2DEvent event) {
            render(event.getStack());
        }
        if (e instanceof UpdatePlayerEvent) {
            if (mc.player != null && mc.player.age % 10 == 0) {
                staffList.clear();
                staffList.addAll(getOnlinePlayerD());
                staffList.addAll(getVanish());
                staffList.sort(String::compareTo);
            }
        }
    }

    @Override
    public Draggable getDraggable() {
        return draggable;
    }

    @Override
    public String getHeaderLabel() {
        return "Staff";
    }

    public static List<String> getOnlinePlayer() {
        return mc.player.networkHandler.getPlayerList().stream()
                .map(PlayerListEntry::getProfile)
                .map(GameProfile::getName)
                .filter(profileName -> validUserPattern.matcher(profileName).matches())
                .collect(Collectors.toList());
    }

    public static List<String> getOnlinePlayerD() {
        List<String> S = new ArrayList<>();
        for (PlayerListEntry player : mc.player.networkHandler.getPlayerList()) {
            if (mc.isInSingleplayer() || player.getScoreboardTeam() == null) break;
            String prefix = player.getScoreboardTeam().getPrefix().getString();
            if (check(Formatting.strip(prefix).toLowerCase())
                    || Minced.getInstance().getWorkForceHandler().getStaff().toString().toLowerCase().contains(player.getProfile().getName().toLowerCase())
                    || player.getScoreboardTeam().getPrefix().getString().contains("YT")
                    || (player.getScoreboardTeam().getPrefix().getString().contains("Y") && player.getScoreboardTeam().getPrefix().getString().contains("T"))) {
                String name = Arrays.asList(player.getScoreboardTeam().getPlayerList().stream().toArray()).toString().replace("[", "").replace("]", "");

                if (player.getGameMode() == GameMode.SPECTATOR) {
                    S.add(player.getScoreboardTeam().getPrefix().getString() + name + Formatting.GOLD + " GM3");
                    continue;
                }
                S.add(player.getScoreboardTeam().getPrefix().getString() + name + Formatting.GREEN + " ACTIVE");
            }
        }
        return S;
    }

    public List<String> getVanish() {
        List<String> list = new ArrayList<>();
        for (Team s : mc.world.getScoreboard().getTeams()) {
            if (s.getPrefix().getString().isEmpty() || mc.isInSingleplayer()) continue;
            String name = Arrays.asList(s.getPlayerList().stream().toArray()).toString().replace("[", "").replace("]", "");

            if (getOnlinePlayer().contains(name) || name.isEmpty())
                continue;
            if (Minced.getInstance().getWorkForceHandler().getStaff().toString().toLowerCase().contains(name.toLowerCase())
                    && check(s.getPrefix().getString().toLowerCase())
                    || check(s.getPrefix().getString().toLowerCase())
                    || s.getPrefix().getString().contains("YT")
                    || (s.getPrefix().getString().contains("Y") && s.getPrefix().getString().contains("T"))
            )
                list.add(s.getPrefix().getString() + name + Formatting.RED +" VANISH");
        }
        return list;
    }

    public static boolean check(String name) {
        if (mc.getCurrentServerEntry() != null && mc.getCurrentServerEntry().address.contains("mcfunny")) {
            return name.contains("helper") || name.contains("moder") || name.contains("модер") || name.contains("хелпер");
        }
        return name.contains("helper") || name.contains("moder") || name.contains("admin") || name.contains("owner") || name.contains("curator") || name.contains("куратор") || name.contains("модер") || name.contains("админ") || name.contains("хелпер") || name.contains("поддержка") || name.contains("сотрудник") || name.contains("зам") || name.contains("стажёр");
    }


    public void render(MatrixStack poseStack) {
        if (mc.world == null) return;

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
        float staffGap = 0; // пропуск между стаффом
        float maxWidth = 80;
        for (String staffMember : staffList) {
            Animation animation = new Animation(Easing.LINEAR, 300);
            animation.setValue(1);
            float staffHeight = 14;
            float rightMargin = 5;
            // бэкграунд
            if (Minced.getInstance().getThemeHandler().getPrimaryTheme() != PrimaryTheme.LIGHT) {
                DrawHandler.drawRound(poseStack, x, y + offset + (getHeaderHeight() + gapBetweenHeader) * animation.getValue(), width, staffHeight, 3, ColorHandler.applyOpacity(getBackgroundColor().brighter(), 255 * animation.getValue()));
                DrawHandler.drawRect(poseStack, x, (y + offset + (getHeaderHeight() + gapBetweenHeader) * animation.getValue()) - 3, width, staffHeight - 3, ColorHandler.applyOpacity(getBackgroundColor().brighter(), 255 * animation.getValue()));
            } else {
                DrawHandler.drawRound(poseStack, x, y + offset + (getHeaderHeight() + gapBetweenHeader) * animation.getValue(), width, staffHeight, 3, ColorHandler.applyOpacity(getBackgroundColor().darker(0.85F), 255 * animation.getValue()));
                DrawHandler.drawRect(poseStack, x, (y + offset + (getHeaderHeight() + gapBetweenHeader) * animation.getValue()) - 3, width, staffHeight - 3, ColorHandler.applyOpacity(getBackgroundColor().darker(0.85F), 255 * animation.getValue()));
            }

            // имя стаффа
            if (animation.getValue() > 0.05F) {
                Fonts.SEMI_14.drawString(poseStack, staffMember, x + 5, y + offset + (getHeaderHeight() + gapBetweenHeader + middleOfBox) * animation.getValue() + 2, ClientColors.getFontColor().withAlpha(255 * animation.getValue()).getRGB());
            }

            // суффикс


            // обновляем maxWidth только если ширина имени + ширина суффикса больше текущего maxWidth
            float nameAndSuffixWidth = Fonts.SEMI_14.getStringWidth(staffMember) * 1.5f;
            maxWidth = Math.max(maxWidth, nameAndSuffixWidth);
            offset += (staffHeight + staffGap) * animation.getValue();
        }
        getWidthAnimation().run(maxWidth);

        DrawHandler.drawBlurredShadow(poseStack, x, y, width, getHeaderHeight() ,5,  getBackgroundColor().withAlpha(180));

        if(staffList.isEmpty()) {
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

        Fonts.SEMI_16.drawCenteredString(poseStack, getHeaderLabel(), x + width / 2, y + 5.5f, ClientColors.getFontColor().getRGB());
    }

}