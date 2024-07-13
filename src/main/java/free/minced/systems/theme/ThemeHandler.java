package free.minced.systems.theme;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jbk
 * @since 18.10.2023
 */
@Getter @Setter
public class ThemeHandler {

    private Theme theme = Theme.PIZELEX;
    private PrimaryTheme primaryTheme = PrimaryTheme.DARK;

}
