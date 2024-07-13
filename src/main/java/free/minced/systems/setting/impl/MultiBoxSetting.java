package free.minced.systems.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import free.minced.modules.Module;
import free.minced.systems.setting.Setting;

import java.util.*;
import java.util.function.BooleanSupplier;

/**
 * @author jbk
 * @since 18.12.2023
 */
@Getter
public class MultiBoxSetting extends Setting {

    // dumb way but ok
    private final List<BooleanSetting> boolSettings;

    public MultiBoxSetting(String name, Module parent, String... values) {
        this.name = name;
        this.parent = parent;
        this.boolSettings = new ArrayList<>();
        // добавляем настройки в список
        Arrays.stream(values).forEach(value -> boolSettings.add(new BooleanSetting(value, false)));

        // добавляем сеттинг
        parent.getSettings().add(this);
    }
    public MultiBoxSetting(String name, Module parent, BooleanSupplier hideCondition, String... values) {
        this.name = name;
        this.parent = parent;
        this.boolSettings = new ArrayList<>();
        this.hideCondition = hideCondition;
        // добавляем настройки в список
        Arrays.stream(values).forEach(value -> boolSettings.add(new BooleanSetting(value, false)));

        // добавляем сеттинг
        parent.getSettings().add(this);
    }
    public boolean isSelectedByIndex(int index) {
        if (index >= 0 && index < boolSettings.size())
            return boolSettings.get(index).isEnabled();

        return false;
    }
    /**
     * gets boolean setting by name
     *
     * @param name name of the setting
     * @return setting
     */
    public BooleanSetting get(String name) {
        return boolSettings.stream().filter(value -> value.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Setting save method
     *
     * @return element
     */
    @Override
    public JsonElement save() {
        JsonObject object = new JsonObject();
        for (BooleanSetting setting : boolSettings) {
            object.addProperty(setting.getName(), setting.isEnabled());
        }
        return object;
    }

    /**
     * Setting load method
     *
     * @param element jsonElement
     */
    @Override
    public void load(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        for (BooleanSetting setting : boolSettings) {
            if (object.has(setting.getName())) {
                setting.setEnabled(object.get(setting.getName()).getAsBoolean());
            }
        }
    }


}