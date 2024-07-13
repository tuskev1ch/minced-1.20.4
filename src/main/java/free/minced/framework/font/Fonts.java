package free.minced.framework.font;

import free.minced.Minced;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class Fonts {
    public static CFontRenderer CATEGORIES_18 = null;
    public static CFontRenderer SEMI_18 = null;
    public static CFontRenderer SEMI_16 = null;
    public static CFontRenderer SEMI_15 = null;
    public static CFontRenderer SEMI_14 = null;
    public static CFontRenderer SEMI_13 = null;
    public static CFontRenderer SEMI_12 = null;
    public static CFontRenderer UNBOUNDED_BOLD = null;
    public static CFontRenderer UNBOUNDED_BOLD28 = null;
    public static CFontRenderer UNBOUNDED_BOLD16 = null;
    public static CFontRenderer UNBOUNDED_BOLD14 = null;
    public static CFontRenderer ICONFONT_27 = null;
    public static CFontRenderer ICONFONT_16 = null;
    public static CFontRenderer ICON_24 = null;
    public static CFontRenderer ICON_20 = null;
    public static CFontRenderer ICON_16 = null;
    public static CFontRenderer ICON_18 = null;

    public static void initFonts() {
        try {
            CATEGORIES_18 = create(18, "comfortaa");
            SEMI_18 = create(18, "semi");
            SEMI_16 = create(16, "semi");
            SEMI_15 = create(15, "semi");
            SEMI_14 = create(14, "semi");
            SEMI_13 = create(13, "semi");
            SEMI_12 = create(12, "semi");
            UNBOUNDED_BOLD = create(32, "unbounded_bold");
            UNBOUNDED_BOLD28 = create(28, "unbounded_bold");
            UNBOUNDED_BOLD16 = create(16, "unbounded_bold");
            UNBOUNDED_BOLD14 = create(14, "unbounded_bold");
            ICONFONT_27 = create(27, "iconlogo");
            ICONFONT_16 = create(16, "iconlogo");
            ICON_24 = create(24, "icons");
            ICON_20 = create(20, "icons");
            ICON_16 = create(16, "icons");
            ICON_18 = create(18, "icons");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static @NotNull CFontRenderer create(float size, String name) throws IOException, FontFormatException {
        return new CFontRenderer(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Minced.class.getClassLoader().getResourceAsStream("assets/minced/fonts/" + name + ".ttf"))).deriveFont(Font.PLAIN, size / 2f), size / 2f);
    }
}
