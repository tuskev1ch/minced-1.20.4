package free.minced.systems.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import lombok.Getter;
import lombok.Setter;
import free.minced.modules.Module;
import free.minced.primary.other.KeyHandler;
import free.minced.systems.setting.Setting;

/**
 * @author jbk
 * @since 27.10.2023
 */
@Getter @Setter
public class BindSetting extends Setting {

    private int key;

    /**
     * Constructor
     *
     * @param name         name of the setting
     * @param parent       the module to which the settings will be applied
     */
    public BindSetting(String name, Module parent, int defaultKey) {
        this.parent = parent;
        this.name = name;

        this.key = defaultKey;
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
        return new JsonPrimitive(key);
    }

    /**
     * Setting load method
     *
     * @param element jsonElement
     */
    @Override
    public void load(JsonElement element) {
        setKey(element.getAsInt()); // меняем кнопку
    }
}