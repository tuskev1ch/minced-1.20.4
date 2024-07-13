package free.minced.systems.setting;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import free.minced.modules.Module;
import free.minced.primary.IHolder;

import java.util.function.BooleanSupplier;

/**
 * @author jbk
 * @since 27.10.2023
 */
@Getter @Setter @RequiredArgsConstructor
public abstract class Setting implements IHolder {

    /**
     * name of the setting
     */
    protected String name;

    /**
     * setting hide condition
     */
    protected BooleanSupplier hideCondition;

    /**
     * the module to which the settings will be applied
     */
    protected Module parent;

    /**
     * Setting save method
     *
     * @return element
     */
    public abstract JsonElement save();

    /**
     * Setting load method
     *
     * @param element jsonElement
     */
    public abstract void load(JsonElement element);

}