package free.minced.framework.color;



import free.minced.Minced;
import free.minced.framework.animation.color.ColorAnimation;
import free.minced.systems.theme.PrimaryTheme;
import free.minced.systems.theme.Theme;


public abstract class ClientColors {

    // длительность анимации
    public static final long ANIMATION_DURATION = 500;

    // анимированные цвета
    private static final ColorAnimation
            ANIMATED_FIRST_COLOR = new ColorAnimation(ANIMATION_DURATION),
            ANIMATED_SECOND_COLOR = new ColorAnimation(ANIMATION_DURATION),
            ANIMATED_BACKGROUND_COLOR = new ColorAnimation(ANIMATION_DURATION),
            SECONDARY_BACKGROUND_COLOR = new ColorAnimation(ANIMATION_DURATION),
            ANIMATED_SHADOW_COLOR = new ColorAnimation(ANIMATION_DURATION),
            BRIGHTER_BACKGROUND_COLOR = new ColorAnimation(ANIMATION_DURATION),
            ANIMATED_FONT_COLOR = new ColorAnimation(ANIMATION_DURATION);

    public static final CustomColor
            // простые цвета, но с утилкой, чтобы с ними было проще работать
            WHITE = new CustomColor(255, 255, 255),
            RED = new CustomColor(255, 0, 0),
            BLACK = new CustomColor(0, 0, 0),
            YELLOW = new CustomColor(255, 255, 0),
            GRAY = new CustomColor(128, 128, 128),
            CYAN = new CustomColor(0, 255, 255);

    public static PrimaryTheme getPrimaryTheme() {
        return Minced.getInstance().getThemeHandler().getPrimaryTheme();
    }

    public static Theme getTheme() {
        return Minced.getInstance().getThemeHandler().getTheme();
    }

    public static CustomColor getBackgroundColor() {
        ANIMATED_BACKGROUND_COLOR.run(getPrimaryTheme().getBackgroundColor());
        return ANIMATED_BACKGROUND_COLOR.getColor();
    }

    public static CustomColor getSecondaryBackgroundColor() {
        SECONDARY_BACKGROUND_COLOR.run(getPrimaryTheme().getSecondaryBackgroundColor());
        return SECONDARY_BACKGROUND_COLOR.getColor();
    }

    public static CustomColor getShadowColor() {
        ANIMATED_SHADOW_COLOR.run(getPrimaryTheme().getShadowColor());
        return ANIMATED_SHADOW_COLOR.getColor();
    }

    public static CustomColor getBrighterBackgroundColor() {
        BRIGHTER_BACKGROUND_COLOR.run(getPrimaryTheme().getBrighterBackgroundColor());
        return BRIGHTER_BACKGROUND_COLOR.getColor();
    }

    public static CustomColor getFontColor() {
        ANIMATED_FONT_COLOR.run(getPrimaryTheme().getFontColor());
        return ANIMATED_FONT_COLOR.getColor();
    }

    public static CustomColor getFirstColor() {
        ANIMATED_FIRST_COLOR.run(getTheme().getFirstColor());
        return ANIMATED_FIRST_COLOR.getColor();
    }

    public static CustomColor getSecondColor() {
        ANIMATED_SECOND_COLOR.run(getTheme().getSecondColor());
        return ANIMATED_SECOND_COLOR.getColor();
    }
}