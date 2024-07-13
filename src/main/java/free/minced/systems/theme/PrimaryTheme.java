package free.minced.systems.theme;


import lombok.Getter;

import java.awt.*;

/**
 * @author jbk
 * @since 04.03.2024
 */
@Getter

public enum PrimaryTheme {
    DARK(
            new Color(22, 25, 28), // background color
            new Color(20, 24, 26), // secondary background color
            new Color(8, 10, 12), // shadow color
            new Color(30, 32, 34), // brighter background color
            new Color(255, 255, 255) // font color
    ),
    LIGHT(
            new Color(230, 230, 230), // background color
            new Color(230, 230, 230), // secondary background color
            new Color(150, 150, 150), // shadow color
            new Color(255, 255, 255), // brighter background color
            new Color(0, 0, 0) // font color
    ),

    BLUE(
      new Color(27, 28, 44), // background color
      new Color(30, 31, 48), // secondary background color
      new Color(24, 25, 38), // shadow color
      new Color(30, 35, 48), // brighter background color
      new Color(255, 255, 255) // font color
    );

    private final Color
            backgroundColor,
            secondaryBackgroundColor,
            shadowColor,
            brighterBackgroundColor,
            fontColor;

    PrimaryTheme(Color backgroundColor,
                 Color secondaryBackgroundColor,
                 Color shadowColor,
                 Color brighterBackgroundColor, Color fontColor) {
        this.backgroundColor = backgroundColor;
        this.secondaryBackgroundColor = secondaryBackgroundColor;
        this.shadowColor = shadowColor;
        this.brighterBackgroundColor = brighterBackgroundColor;
        this.fontColor = fontColor;
    }

}
