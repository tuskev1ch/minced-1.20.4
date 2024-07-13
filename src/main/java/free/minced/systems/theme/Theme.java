package free.minced.systems.theme;


import
lombok.Getter;
import free.minced.framework.color.ColorHandler;
import free.minced.framework.color.CustomColor;


import java.awt.*;
/**
 * @author jbk
 * @since 18.10.2023
 */

@Getter
public enum Theme {

    // https://uigradients.com/
    STARFALL("Starfall", decode("#F0C27B"), decode("#4B1248")),
    INFLUENZA("Influenza", decode("#C04848"), decode("#480048")),
    CALMDARYA("Calm-Darya", decode("#5f2c82"), decode("#49a09d")),
    PURPLEPARADISE("Purple-Paradise", decode("#1D2B64"), decode("#F8CDDA")),
    BLOODYMARY("Bloody-Mary", decode("#870000"), decode("#190A05")),
    ANWAR("Anwar", decode("#334d50"), decode("#cbcaa5")),
    VICECITY("Vice-City", decode("#3494E6"), decode("#EC6EAD")),
    AZUREPOP("Azure-Pop", decode("#ef32d9"), decode("#89fffd")),
    SUNSET("Sunset", decode("#0B486B"), decode("#F56217")),
    STRIPE("Stripe", decode("#1FA2FF"), decode("#A6FFCB")),
    AQUAMARINE("Aqua-Marine", decode("#1A2980"), decode("#26D0CE")),
    KYOTO("Kyoto", decode("#2980b9"), decode("#2c3e50")),
    NELSON("Nelson", decode("#f2709c"), decode("#ff9472")),
    EMOKID("Cheer-Up", decode("#556270"), decode("#FF6B6B")),
    SOUNDCLOUD("SoundCloud", decode("#fe8c00"), decode("#f83600")),
    EMERALD("Emerald", decode("#52c234"), decode("#061700")),
    FLACHER("Flacher", decode("#ff0084"), decode("#33001b")),
    BUPE("Bupe", decode("#00416A"), decode("#E4E5E6")),
    ROYALBLUE("Royal-Blue", decode("#536976"), decode("#292E49")),
    ANAMNISAR("Anamnisar", decode("#9796f0"), decode("#fbc7d4")),
    SEL("Sel", decode("#00467F"), decode("#A5CC82")),
    SKYLINE("Skyline", decode("#1488CC"), decode("#2B32B2")),
    DIMIGO("Dimigo", decode("#ec008c"), decode("#fc6767")),
    PURPLELOVE("Purple-Love", decode("#cc2b5e"), decode("#753a88")),
    MONTECARLO("Monte-Carlo", decode("#CC95C0"), decode("#7AA1D2")),
    WINTER("Winter", decode("#8399a2"), decode("#eef2f3")),
    COFFEE("Coffee", decode("#94705b"), decode("#d1b59e")),
    MALENIA("Malenia", decode("#99513b"), decode("#b49252")),
    MOHANED("Mohaned", decode("#131346"), decode("#880707")),
    AUTUMN("Autumn", decode("#869281"), decode("#b1432b")),
    URINE("Urine", decode("#0057B7"), decode("#FFDD00")),
    GHOSTY("Ghosty", decode("#35425b"), decode("#7d8c9d")),
    HOTSOUP("Hotsoup", decode("#000000"), decode("#FFFFFF")),
    CLYDE("Clyde", decode("#9fa7f8"), decode("#5865f2")),
    MEMED("Memed", decode("#240788"), decode("#7705e2")),
    NEPAL("Nepal", decode("#de6161"), decode("#2657eb")),
    PURPINK("Purpink", decode("#7f00ff"), decode("#e100ff")),
    PINKFlAVOUR("Pink-Flavour", decode("#800080"), decode("#ffc0cb")),
    LAWRENCIUM("Lawrencium", decode("#302b63"), decode("#24243e")),
    AUBERGINE("Aubergine", decode("#aa076b"), decode("#61045f")),
    MOJITO("Mojito", decode("#1d976c"), decode("#93f9b9")),
    KASHMIR("Kashmir", decode("#614385"), decode("#516395")),
    PREDAWN("Predawn", decode("#ffa17f"), decode("#00223e")),
    ATLAS("Atlas", decode("#feac5e"), decode("#c779d0")),

    FOREST("Forest", decode("#5A3F37"), decode("#2C7744")),
    MINNESOTA("Minnesota", decode("#5614B0"), decode("#DBD65C")),
    PIZELEX("Pizelex", decode("#F29492"), decode("#bd1c72")),
    BLOSSOMS("Blossoms", decode("#bb377d"), decode("#fbd3e9")),
    VIRGIN("Virgin", decode("#c9ffbf"), decode("#ffafbd")),
    ALMOST("Almost", decode("#faaca8"), decode("#ddd6f3")),
    TAMED("Tamed", decode("#efefbb"), decode("#d4d3dd")),
    BLURRY("Blurry", decode("#d53369"), decode("#cbad6d")),
    TRIPPER("Tripper", decode("#f857a6"), decode("#ff5858")),
    VIOLET("Violet", decode("#7d4bbb"), decode("#000000"));
    private final String name;
    private final CustomColor firstColor, secondColor;

    // Constructor
    Theme(String name, CustomColor firstColor, CustomColor secondColor) {
        this.name = name;
        this.firstColor = firstColor;
        this.secondColor = secondColor;
    }

    public Color getAccentColor(float x, float y) {
        return ColorHandler.mixColors(getFirstColor(), getSecondColor(), getBlendFactor(x, y));
    }
    public Color getAccentColorReverse(float x, float y) {
        return ColorHandler.mixColors(getSecondColor(), getFirstColor(), getBlendFactor(x, y));
    }
    public Color getAccentColor() {
        return getAccentColor(0, 0);
    }

    public double getBlendFactor(float x, float y) {
        return Math.sin(System.currentTimeMillis() / 600.0D
                + x * 0.005D
                + y * 0.06D
        ) * 0.5D + 0.5D;
    }

    // from java.awt.Color
    public static CustomColor decode(String nm) throws NumberFormatException {
        int i = Integer.decode(nm);
        return new CustomColor((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
    }
}