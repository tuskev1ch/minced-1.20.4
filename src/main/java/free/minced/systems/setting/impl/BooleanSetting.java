package free.minced.systems.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.Setter;
import free.minced.modules.Module;
import free.minced.systems.setting.Setting;

import java.util.function.BooleanSupplier;

/**
 * @author jbk
 * @since 27.10.2023
 */
@Getter @Setter
public class BooleanSetting extends Setting {

    private boolean enabled;

    /**
     * Constructor
     *
     * @param name         name of the setting
     * @param defaultState default state of the setting
     * @param parent       the module to which the settings will be applied
     */
    public BooleanSetting(String name, Module parent, boolean defaultState) {
        this.parent = parent;
        this.name = name;

        this.enabled = defaultState;
        // регестрируем настройку
        parent.getSettings().add(this);
    }

    public BooleanSetting(String name, boolean defaultState) {
        this.parent = null;
        this.name = name;

        this.enabled = defaultState;
    }

        /**
     * Constructor
     *
     * @param name         name of the setting
     * @param defaultState default state of the setting
     * @param parent       the module to which the settings will be applied
     * @param hideCondition hide condition
     */
    public BooleanSetting(String name, Module parent, boolean defaultState, BooleanSupplier hideCondition) {
        this.parent = parent;
        this.name = name;
        this.hideCondition = hideCondition;

        this.enabled = defaultState;
        // регестрируем настройку
        parent.getSettings().add(this);
    }

    public void enable() {
        setEnabled(true);
    }

    public void disable() {
        setEnabled(false);
    }

    public void toggle() {
        setEnabled(! enabled);
    }

    /**
     * Setting save method
     *
     * @return element
     */
    @Override
    public JsonElement save() {
        return new JsonPrimitive(enabled);
    }

    /**
     * Setting load method
     *
     * @param element jsonElement
     */
    @Override
    public void load(JsonElement element) {
        setEnabled(element.getAsBoolean()); // меняем состояние сеттинга
    }
}