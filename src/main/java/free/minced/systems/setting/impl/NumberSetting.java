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
public class NumberSetting extends Setting {

    private Number min, max, step, value;

    /**
     * Constructor
     *
     * @param name         name of the setting
     * @param parent       the module to which the settings will be applied
     * @param defaultValue default slider value
     * @param max          max slider value
     * @param min          min slider value
     * @param step         slider step
     */
    public NumberSetting(String name, Module parent, Number defaultValue, Number min, Number max, Number step) {
        this.name = name;
        this.parent = parent;
        this.step = step;
        this.min = min;
        this.max = max;
        this.value = defaultValue;

        // регестрируем настройку
        parent.getSettings().add(this);
    }
    public NumberSetting(String name, Module parent, Number defaultValue, Number min, Number max, Number step, BooleanSupplier hideCondition) {
        this.name = name;
        this.parent = parent;
        this.step = step;
        this.min = min;
        this.max = max;
        this.value = defaultValue;
        this.hideCondition = hideCondition;
        // регестрируем настройку
        parent.getSettings().add(this);
    }

    /**
     * Setting save method
     *
     * @return element
     */
    @Override
    public JsonElement save() {
        return new JsonPrimitive(value);
    }
    /**
     * Setting load method
     *
     * @param element jsonElement
     */
    @Override
    public void load(JsonElement element) {
        setValue(element.getAsNumber());
    }
}