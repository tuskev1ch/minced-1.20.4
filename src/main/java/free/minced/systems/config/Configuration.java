package free.minced.systems.config;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import free.minced.Minced;
import free.minced.systems.config.api.ConfigHandler;
import free.minced.systems.config.api.IConfigUpdater;

import java.io.File;

/**
 * @author jbk
 */
@Getter
@Setter
public class Configuration implements IConfigUpdater {

    /**
     * Имя конфига
     */
    private final String name;

    /**
     * Файл конфига
     */

    private final File file;

    public Configuration(String name) {
        this.name = name;
        this.file = new File(ConfigHandler.DIRECTORY, name + ".mncd");
    }

    @Override
    public JsonObject saveConfig() {
        return Minced.saveData();
    }

    @Override
    public void loadConfig(JsonObject object) {
        Minced.loadData(object);
    }
}