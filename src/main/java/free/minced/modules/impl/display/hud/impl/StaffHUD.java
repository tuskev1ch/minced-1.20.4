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
    private final List<StaffMember> allStaffList = new ArrayList<>();

    @Override
    public void onEvent(Event e) {
        if (e instanceof Render2DEvent) {
            render(((Render2DEvent) e).getStack());
        }
        if (e instanceof UpdatePlayerEvent) {
            if (mc.player == null) return;
            List<StaffMember> spectatingStaffList = getSpectatingStaff();
            List<StaffMember> vanishedStaffList = getVanishedStaff();
            List<StaffMember> onlineStaff = getOnlineStaff();
            this.allStaffList.clear();
            this.allStaffList.addAll(vanishedStaffList);
            this.allStaffList.addAll(spectatingStaffList);
            this.allStaffList.addAll(onlineStaff);
        }
    }

    @Override
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
        for (StaffMember staffMember : allStaffList) {
            staffMember.getAnimation().setValue(1);
            float staffHeight = 14;
            float rightMargin = 5;
            // бэкграунд
            if (Minced.getInstance().getThemeHandler().getPrimaryTheme() != PrimaryTheme.LIGHT) {
                DrawHandler.drawRound(poseStack, x, y + offset + (getHeaderHeight() + gapBetweenHeader) * staffMember.getAnimation().getValue(), width, staffHeight, 3, ColorHandler.applyOpacity(getBackgroundColor().brighter(), 255 * staffMember.getAnimation().getValue()));
                DrawHandler.drawRect(poseStack, x, (y + offset + (getHeaderHeight() + gapBetweenHeader) * staffMember.getAnimation().getValue()) - 3, width, staffHeight - 3, ColorHandler.applyOpacity(getBackgroundColor().brighter(), 255 * staffMember.getAnimation().getValue()));
            } else {
                DrawHandler.drawRound(poseStack, x, y + offset + (getHeaderHeight() + gapBetweenHeader) * staffMember.getAnimation().getValue(), width, staffHeight, 3, ColorHandler.applyOpacity(getBackgroundColor().darker(0.85F), 255 * staffMember.getAnimation().getValue()));
                DrawHandler.drawRect(poseStack, x, (y + offset + (getHeaderHeight() + gapBetweenHeader) * staffMember.getAnimation().getValue()) - 3, width, staffHeight - 3, ColorHandler.applyOpacity(getBackgroundColor().darker(0.85F), 255 * staffMember.getAnimation().getValue()));
            }

            // имя стаффа
            if (staffMember.getAnimation().getValue() > 0.05F) {
                Fonts.SEMI_14.drawString(poseStack,staffMember.getPrefix() + staffMember.getName(), x + 5, y + offset + (getHeaderHeight() + gapBetweenHeader + middleOfBox) * staffMember.getAnimation().getValue() + 2, ClientColors.getFontColor().withAlpha(255 * staffMember.getAnimation().getValue()).getRGB());
            }

            // суффикс
            float suffixWidth = Fonts.SEMI_14.getStringWidth(staffMember.getSuffix());
            if (staffMember.getAnimation().getValue() > 0.05F) {
                Fonts.SEMI_14.drawString(poseStack,staffMember.getSuffix(), x + width - suffixWidth - rightMargin, y + offset + (getHeaderHeight() + gapBetweenHeader + middleOfBox) * staffMember.getAnimation().getValue() + 2, ClientColors.getFontColor().withAlpha(255 * staffMember.getAnimation().getValue()).getRGB());
            }

            // обновляем maxWidth только если ширина имени + ширина суффикса больше текущего maxWidth
            float nameAndSuffixWidth = Fonts.SEMI_14.getStringWidth(staffMember.getPrefix() + staffMember.getName()) + Fonts.SEMI_14.getStringWidth(staffMember.getSuffix()) * 2;
            maxWidth = Math.max(maxWidth, nameAndSuffixWidth);
            offset += (staffHeight + staffGap) * staffMember.getAnimation().getValue();
        }
        getWidthAnimation().run(maxWidth);

        DrawHandler.drawBlurredShadow(poseStack, x, y, width, getHeaderHeight() ,5,  getBackgroundColor().withAlpha(180));

        if(allStaffList.isEmpty()) {
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

    @Override
    public Draggable getDraggable() {
        return draggable;
    }

    @Override
    public String getHeaderLabel() {
        return "Staff";
    }

    public static List<String> getAllValidPlayers() {
        return mc.player.networkHandler.getPlayerList().stream()
                .map(PlayerListEntry::getProfile)
                .map(GameProfile::getName)
                .filter(profileName -> validUserPattern.matcher(profileName).matches())
                .collect(Collectors.toList());
    }


    private List<StaffMember> getOnlineStaff() {
        List<StaffMember> staffMembers = new ArrayList<>();

        for (PlayerListEntry playerInfo : mc.player.networkHandler.getPlayerList()) {
            if (mc.isInSingleplayer() || playerInfo.getScoreboardTeam() == null) break;

            String prefix = Formatting.strip(playerInfo.getScoreboardTeam().getPrefix().getString());

            String name = Arrays.asList(playerInfo.getScoreboardTeam().getPlayerList().toArray()).toString().replace("[", "").replace("]", "");

            if (isPrefixValid(Formatting.strip(prefix).toLowerCase()) || Minced.getInstance().getWorkForceHandler().getStaff().contains(playerInfo.getProfile().getName())) {

                staffMembers.add(new StaffMember(name, prefix, Formatting.GREEN + "Active"));
            }
        }

        return staffMembers;
    }

    private List<StaffMember> getVanishedStaff() {
        List<StaffMember> vanishedStaffMembers = new ArrayList<>();
        for (Team team : mc.world.getScoreboard().getTeams()) {
            String name = Arrays.asList(team.getPlayerList().toArray()).toString().replace("[", "").replace("]", "");
            if (mc.player == null || mc.isInSingleplayer() || name.isEmpty()) continue;

            if (getAllValidPlayers().contains(name))
                continue;
            // проверяем на наличие других модераторов в ванише и добавляем их в список
            if (team.getPrefix().getString().toLowerCase().contains("yt")
                    || isPrefixValid(team.getPrefix().getString().toLowerCase())
                    || Minced.getInstance().getWorkForceHandler().getStaff().contains(team.getPrefix().getString())
                    || Minced.getInstance().getWorkForceHandler().getStaff().contains(team.getName()) ) {
                vanishedStaffMembers.add(new StaffMember(name, team.getPrefix().getString(), Formatting.RED + "Vanished"));
            }
        }

        return vanishedStaffMembers;
    }

    private List<StaffMember> getSpectatingStaff() {
        if (mc.player == null || mc.isInSingleplayer()) return Collections.emptyList();

        List<StaffMember> staffMembers = new ArrayList<>();

        for (PlayerListEntry playerInfo : mc.player.networkHandler.getPlayerList()) {
            if (playerInfo.getScoreboardTeam() == null) continue;

            String prefix = Formatting.strip(playerInfo.getScoreboardTeam().getPrefix().getString());

            // prefixes check
            if (isPrefixValid(playerInfo.getScoreboardTeam().getPrefix().getString()) || Minced.getInstance().getWorkForceHandler().getStaff().contains(playerInfo.getProfile().getName()) ) {
                if (playerInfo.getGameMode() == GameMode.SPECTATOR) {
                    staffMembers.add(new StaffMember(playerInfo.getProfile().getName(), prefix, Formatting.GOLD + "GM3"));
                }
            }
        }

        return staffMembers;
    }

    private boolean isPrefixValid(String prefix) {
        prefix = Formatting.strip(prefix);
        return
                prefix.toLowerCase().contains("yt") ||
                        prefix.toLowerCase().contains("developer") ||
                        prefix.toLowerCase().contains("moder") ||
                        prefix.toLowerCase().contains("helper") ||
                        prefix.toLowerCase().contains("admin") ||
                        prefix.toLowerCase().contains("owner") ||
                        prefix.toLowerCase().contains("хелпер") ||
                        prefix.toLowerCase().contains("модер") ||
                        prefix.toLowerCase().contains("админ") ||
                        prefix.toLowerCase().contains("сотрудник") ||
                        prefix.toLowerCase().contains("мл.сотрудник") ||
                        prefix.toLowerCase().contains("поддержка") ||
                        prefix.toLowerCase().contains("модератор");
    }

    @Getter
    public static class StaffMember {
        private final Animation animation = new Animation(Easing.LINEAR, 300);
        private final String name;
        private final String prefix;
        private final String suffix;


        public StaffMember(String name, String prefix, String suffix) {
            this.name = name;
            this.prefix = prefix;
            this.suffix = suffix;
        }
    }
}