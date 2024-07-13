package free.minced.systems.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.Setter;
import free.minced.modules.Module;
import free.minced.systems.setting.Setting;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * @author jbk
 * @since 27.10.2023
 */
@Getter @Setter
public class ModeSetting extends Setting {

    private final List<String> modes;

    private String currentMode, defaultMode;

    /**
     * Constructor
     *
     * @param name        name of the setting
     * @param defaultMode default mode of the setting
     * @param parent      the module to which the settings will be applied
     * @param modes       list of modes
     *                    (for example, "RW", "HolyWorld", etc�)
     */

    public ModeSetting(String name, Module parent, String defaultMode, String... modes) {
        this.name = name;
        this.parent = parent;
        this.defaultMode = defaultMode;
        this.currentMode = defaultMode;
        this.modes = Arrays.asList(modes);
        // ������������ ���������
        parent.getSettings().add(this);
    }

    /**
     * Constructor
     *
     * @param name          name of the setting
     * @param defaultMode   default mode of the setting
     * @param parent        the module to which the settings will be applied
     * @param modes         list of modes
     *                      (for example, "RW", "HolyWorld", etc�)
     * @param hideCondition hide condition
     */


    public ModeSetting(String name, Module parent, String defaultMode, BooleanSupplier hideCondition, String... modes) {
        this.name = name;
        this.parent = parent;
        this.defaultMode = defaultMode;
        this.currentMode = defaultMode;
        this.modes = Arrays.asList(modes);
        this.hideCondition = hideCondition;
        // регестрируем настройку
        parent.getSettings().add(this);
    }

    public boolean is(String mode) {
        return currentMode.equalsIgnoreCase(mode);
    }

    /**
     * Setting save method
     *
     * @return element
     */
    @Override
    public JsonElement save() {
        return new JsonPrimitive(currentMode);
    }

    /**
     * Setting load method
     *
     * @param element jsonElement
     */
    @Override
    public void load(JsonElement element) {
        this.setCurrentMode(element.getAsString());
    }
}