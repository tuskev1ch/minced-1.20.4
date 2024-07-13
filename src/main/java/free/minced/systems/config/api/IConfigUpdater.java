package free.minced.systems.config.api;

import com.google.gson.JsonObject;

public interface IConfigUpdater {

    JsonObject saveConfig();

    void loadConfig(JsonObject object);

}