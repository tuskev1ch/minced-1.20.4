package free.minced.modules.impl.display.hud;



import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;
import free.minced.Minced;
import free.minced.addition.ProfileHandler;
import free.minced.events.Event;
import free.minced.events.impl.render.Render2DEvent;
import free.minced.framework.font.CFontRenderer;
import free.minced.framework.font.Icons;
import free.minced.framework.render.DrawHandler;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.framework.color.ClientColors;
import free.minced.primary.other.ServerHandler;



import free.minced.framework.font.Fonts;
import free.minced.systems.setting.impl.MultiBoxSetting;
import free.minced.systems.setting.impl.NumberSetting;
import org.joml.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@ModuleDescriptor(name = "HUD",  category = ModuleCategory.DISPLAY)

public class HUD extends Module {

    public final MultiBoxSetting elements = new MultiBoxSetting("Elements", this, "Watermark", "Coords");

    public final NumberSetting offsetColor = new NumberSetting("Offset Color", this, 4, 4, 7, 1);

    @Override
    public void onEvent(Event event) {
        if (event instanceof Render2DEvent e) {
            if (mc.player == null || mc.world == null) return;

            if (elements.get("Watermark").isEnabled()) {
                drawStringWatermark(e.getStack());
            }
            if (elements.get("Coords").isEnabled()) {
                drawStringCoords(e.getStack());
            }


        }
    }

    public void drawStringCoords(MatrixStack pMatrixStack) {

        String name = "Coords: " + String.format("%s %s %s", (int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ());
        float x = 9;
        float y = sr.getScaledHeight().floatValue() - 22.5f;

        float width = Fonts.SEMI_16.getStringWidth(name) + 6;
        float height = 14;
        DrawHandler.drawBlurredShadow(pMatrixStack,  x, y + 0.5f, width + 0.5f, height,  5, ClientColors.getSecondaryBackgroundColor().withAlpha(255));

        DrawHandler.drawRound(pMatrixStack,  x, y + 0.5f, width + 0.5f, height, 3, ClientColors.getBrighterBackgroundColor());

        Fonts.SEMI_16.drawString(pMatrixStack, name, x + 3, y + 5, ClientColors.getFontColor().withAlpha(255).getRGB());

    }


    public void drawStringWatermark(MatrixStack pMatrixStack) {
        CFontRenderer normalFont = Fonts.SEMI_15;
        CFontRenderer logoIconFont = Fonts.ICONFONT_16;
        CFontRenderer iconFont = Fonts.ICON_18; // по какой-то причине, иконки меньше чем должны были быть, поэтому тут другой размер шрифта
        float cornerRadius = 3;

        float x = 10;
        float y = 10;
        float leftAndRightPadding = 5.0F;
        float iconLeftMargin = 5.0F;
        float textLeftMargin = 5.0F;
        String logoIcon = "a";
        String profileIcon = Icons.USER.getCharacter();
        String clockIcon = Icons.WIFI.getCharacter();
        String username = mc.player.getName().getString(); // TODO REPLACE
        String build = ProfileHandler.getBuild();
        String serverIP = ServerHandler.getServerIP();

        float logoWidth = logoIconFont.getStringWidth(logoIcon);
        float profileIconWidth = iconFont.getStringWidth(profileIcon);
        float clockIconWidth = iconFont.getStringWidth(clockIcon);
        float firstTextWidth = logoWidth + normalFont.getStringWidth(build) + iconLeftMargin;
        float secondTextWidth = profileIconWidth + normalFont.getStringWidth(username) + iconLeftMargin;
        float thirdTextWidth = clockIconWidth + normalFont.getStringWidth(serverIP) + iconLeftMargin;

        float width = leftAndRightPadding * 2 + firstTextWidth + secondTextWidth + thirdTextWidth + leftAndRightPadding * 2;

        // отступ от текста сверху и снизу
        float verticalMargin = 5.0F;
        float height = 7.5f + verticalMargin * 2;

        // бэкграунд
        DrawHandler.drawBlurredShadow(pMatrixStack,x - 0.5f, y- 0.5f, width + 1, height + 1, 5, ClientColors.getBrighterBackgroundColor().withAlpha(225));
        DrawHandler.drawRound(pMatrixStack,x, y, width, height, cornerRadius, ClientColors.getBrighterBackgroundColor().withAlpha(255));

        // рендерим первую иконку
        logoIconFont.drawString(pMatrixStack,logoIcon, x + leftAndRightPadding, y + 7.5f, ClientColors.getFontColor().getRGB());

        // рендерим первый текст
        normalFont.drawGradientString(pMatrixStack,build, x + leftAndRightPadding + logoWidth + iconLeftMargin, y + 6.5f, 10);

        // рендерим вторую иконку
        iconFont.drawString(pMatrixStack,profileIcon, x + leftAndRightPadding + firstTextWidth + textLeftMargin, y + 6.5f, ClientColors.getFontColor().getRGB());

        // рендерим второй текст
        normalFont.drawString(pMatrixStack,username, x + leftAndRightPadding + firstTextWidth + textLeftMargin + profileIconWidth + iconLeftMargin, y + 6.5f, ClientColors.getFontColor().getRGB());

        // рендерим третью иконку
        iconFont.drawString(pMatrixStack,clockIcon, x + leftAndRightPadding + firstTextWidth + secondTextWidth + textLeftMargin * 2, y + 6.5f, ClientColors.getFontColor().getRGB());

        // рендерим третий текст
        normalFont.drawString(pMatrixStack,serverIP, x + leftAndRightPadding + firstTextWidth + secondTextWidth + textLeftMargin * 2 + clockIconWidth + iconLeftMargin, y + 6.5f, ClientColors.getFontColor().getRGB());
    }

}
