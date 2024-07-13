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
 * @since 30.12.2023
 */
@Getter @Setter
public class StringSetting extends Setting {

    private String text;
    private final int maxLength;

    /**
     * конструктор
     *
     * @param name        имя сеттинга
     * @param parent      модуль к которому будет добавлена настройка
     * @param defaultText текст по умолчанию
     * @param maxLength максимальное количество символов которое можно будет написать в поле
     */
    public StringSetting(String name, Module parent, String defaultText, int maxLength) {
        this.name = name;
        this.parent = parent;

        this.maxLength = maxLength;
        this.text = defaultText;
        // регистрируем настройку
        parent.getSettings().add(this);
    }

    /**
     * конструктор
     *
     * @param name          имя сеттинга
     * @param defaultText   текст по умолчанию
     * @param parent        модуль к которому будет добавлена настройка
     * @param hideCondition условие при котором сеттинг будет скрываться
     * @param maxLength максимальное количество символов которое можно будет написать в поле
     */
    public StringSetting(String name, Module parent, String defaultText, int maxLength, BooleanSupplier hideCondition) {
        this.name = name;
        this.hideCondition = hideCondition;
        this.parent = parent;

        this.text = defaultText;
        this.maxLength = maxLength;
        // регистрируем настройку
        parent.getSettings().add(this);
    }


    /**
     * Setting save method
     *
     * @return element
     */
    @Override
    public JsonElement save() {
        return new JsonPrimitive(text);
    }

    /**
     * Setting load method
     *
     * @param element jsonElement
     */
    @Override
    public void load(JsonElement element) {
        setText(element.getAsString());
    }
}