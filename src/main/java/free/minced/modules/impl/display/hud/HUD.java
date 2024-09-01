package free.minced.modules.impl.display.hud;



import free.minced.modules.impl.misc.NameProtect;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.ModeSetting;
import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;
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

@Getter
@ModuleDescriptor(name = "HUD",  category = ModuleCategory.DISPLAY)

public class HUD extends Module {
    public final BooleanSetting customHud = new BooleanSetting("Custom Hud", this, false);

    public final NumberSetting roundness = new NumberSetting("Roundness", this, 3, 1, 7, 1, () -> !customHud.isEnabled());
    public final BooleanSetting enableGlow = new BooleanSetting("Enable Glow", this, false, () -> !customHud.isEnabled());
    public final BooleanSetting glow = new BooleanSetting("Hud Glow", this, false, () -> !enableGlow.isEnabled());

    public final ModeSetting roundSide = new ModeSetting("Style Side", this, "None", () -> !customHud.isEnabled(), "None", "Left", "Right", "Up", "Down", "Full");

    public final MultiBoxSetting elements = new MultiBoxSetting("Elements", this, "Watermark", "Coords");
    public final MultiBoxSetting reversedElements = new MultiBoxSetting("Sub Elements", this, () ->
            (!elements.get("Watermark").isEnabled() ||
            !elements.get("Coords").isEnabled()),

            "Watermark", "Coords");

    public final NumberSetting offsetColor = new NumberSetting("Offset Color", this, 4, 4, 7, 1);

    @Override
    public void onEvent(Event event) {
        if (event instanceof Render2DEvent e) {
            if (mc.player == null || mc.world == null) return;

            if (elements.get("Watermark").isEnabled()) {
                drawStringWatermark(e.getStack(), reversedElements.get("Watermark").isEnabled());
            }
            if (elements.get("Coords").isEnabled()) {
                drawStringCoords(e.getStack(), reversedElements.get("Coords").isEnabled());
            }


        }
    }

    public void drawStringCoords(MatrixStack pMatrixStack, boolean reversed) {
        String name = null;
        if (mc.player != null) {
            name = "Coords: " + String.format("%s %s %s", (int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ());
        }
        float x = reversed ? sr.getScaledWidth().floatValue() - 9 - Fonts.SEMI_16.getStringWidth(name) - 6 : 9;
        float y = sr.getScaledHeight().floatValue() - 22.5f;

        float width = Fonts.SEMI_16.getStringWidth(name) + 6;
        float height = 14;

        DrawHandler.drawStyledRect(pMatrixStack,  x, y + 0.5f, width + 0.5f, height);


        Fonts.SEMI_16.drawString(pMatrixStack, name, x + 3, y + 5, ClientColors.getFontColor().withAlpha(255).getRGB());
    }

    public void drawStringWatermark(MatrixStack pMatrixStack, boolean reversed) {
        CFontRenderer normalFont = Fonts.SEMI_15;
        CFontRenderer logoIconFont = Fonts.ICONFONT_16;
        CFontRenderer iconFont = Fonts.ICON_18;
        float cornerRadius = 3;

        float leftAndRightPadding = 5.0F;
        float iconLeftMargin = 5.0F;
        float textLeftMargin = 5.0F;
        String logoIcon = "a";
        String profileIcon = Icons.USER.getCharacter();
        String clockIcon = Icons.WIFI.getCharacter();
        String username = NameProtect.getCustomName();
        String build = ProfileHandler.getBuild();
        String serverIP = ServerHandler.getServerIP();

        float logoWidth = logoIconFont.getStringWidth(logoIcon);
        float profileIconWidth = iconFont.getStringWidth(profileIcon);
        float clockIconWidth = iconFont.getStringWidth(clockIcon);
        float firstTextWidth = logoWidth + normalFont.getStringWidth(build) + iconLeftMargin;
        float secondTextWidth = profileIconWidth + normalFont.getStringWidth(username) + iconLeftMargin;
        float thirdTextWidth = clockIconWidth + normalFont.getStringWidth(serverIP) + iconLeftMargin;

        float width = leftAndRightPadding * 2 + firstTextWidth + secondTextWidth + thirdTextWidth + leftAndRightPadding * 2;

        float verticalMargin = 5.0F;
        float height = 7.5f + verticalMargin * 2;

        float x = reversed ? sr.getScaledWidth().floatValue() - 10 - width : 10;
        float y = 10;

       // DrawHandler.drawBlurredShadow(pMatrixStack,x - 0.5f, y- 0.5f, width + 1, height + 1, 5, ClientColors.getBrighterBackgroundColor().withAlpha(225));
        DrawHandler.drawStyledRect(pMatrixStack,x, y, width, height);

        logoIconFont.drawString(pMatrixStack,logoIcon, x + leftAndRightPadding, y + 7.5f, ClientColors.getFontColor().getRGB());

        normalFont.drawGradientString(pMatrixStack,build, x + leftAndRightPadding + logoWidth + iconLeftMargin, y + 6.5f, 10);

        iconFont.drawString(pMatrixStack,profileIcon, x + leftAndRightPadding + firstTextWidth + textLeftMargin, y + 6.5f, ClientColors.getFontColor().getRGB());

        normalFont.drawString(pMatrixStack,username, x + leftAndRightPadding + firstTextWidth + textLeftMargin + profileIconWidth + iconLeftMargin, y + 6.5f, ClientColors.getFontColor().getRGB());

        iconFont.drawString(pMatrixStack,clockIcon, x + leftAndRightPadding + firstTextWidth + secondTextWidth + textLeftMargin * 2, y + 6.5f, ClientColors.getFontColor().getRGB());

        normalFont.drawString(pMatrixStack,serverIP, x + leftAndRightPadding + firstTextWidth + secondTextWidth + textLeftMargin * 2 + clockIconWidth + iconLeftMargin, y + 6.5f, ClientColors.getFontColor().getRGB());
    }

}
