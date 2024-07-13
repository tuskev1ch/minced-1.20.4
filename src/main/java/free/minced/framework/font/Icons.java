package free.minced.framework.font;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Icons {

    // иконки для категорий
    COMBAT_CATEGORY("o"),
    MOVEMENT_CATEGORY("m"),
    RENDER_CATEGORY("l"),
    THEME_CATEGORY("t"),
    MISC_CATEGORY("i"),
    DISPLAY_CATEGORY("y"),

    // другие иконки
    CHECKMARK("C"),
    SETTING("j"),
    CLOSE("c"),
    LOGO("L"),
    ALT("O"),
    ARROW_UP("Y"),
    CLOCK("C"),
    USER("U"),
    WIFI("G"),
    NO_WIFI("п"),
    EXPAND("e"),


    // иконки для худа
    KEYBOARD("K"),
    SHIELD("S"),
    POTION("P"),
    ALARM("A"),

    // иконки погоды
    CLOUDY("W"),
    CLEARLY("g"),
    SNOWY("N"),
    RAINY("R");


    private final String character;

}
