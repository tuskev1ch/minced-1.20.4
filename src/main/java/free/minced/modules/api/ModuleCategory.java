package free.minced.modules.api;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import free.minced.framework.font.Icons;
import free.minced.framework.interfaces.api.MenuScreen;
import free.minced.framework.interfaces.impl.module.ModuleScreen;
import free.minced.framework.interfaces.impl.theme.ThemeScreen;

/**
 * @author jbk
 * @since 05.10.2023
 * Енум с категориями клиента
 */
@Getter @RequiredArgsConstructor

public enum ModuleCategory {

    COMBAT("Combat", Icons.COMBAT_CATEGORY, new ModuleScreen()),
    MOVEMENT("Movement", Icons.MOVEMENT_CATEGORY, new ModuleScreen()),
    RENDER("Render", Icons.RENDER_CATEGORY, new ModuleScreen()),
    MISC("Misc", Icons.MISC_CATEGORY, new ModuleScreen()),
    DISPLAY("Display", Icons.DISPLAY_CATEGORY, new ModuleScreen()),
    THEME("Theme", Icons.THEME_CATEGORY, new ThemeScreen());

    // аргументы для конструктора
    final String displayName;
    final Icons icon;
    final MenuScreen screen;
}