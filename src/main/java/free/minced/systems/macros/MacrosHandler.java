package free.minced.systems.macros;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import free.minced.events.Event;
import free.minced.primary.IHolder;
import free.minced.primary.module.ModuleHandler;




import java.util.ArrayList;
import java.util.List;

/**
 * @author jbk
 * @since 01.01.2024
 */

@Getter
public class MacrosHandler implements IHolder {

    public static final List<Macros> macrosList = new ArrayList<>();

    public MacrosHandler() {
    }

    public Macros get(String name) {
        return macrosList.stream().filter(macros -> macros.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Macros get(int key) {
        return macrosList.stream().filter(macros -> macros.getKey() == key).findFirst().orElse(null);
    }
    public JsonObject save() {
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();

        for (Macros macros : macrosList) {
            JsonObject dataObject = new JsonObject();
            dataObject.addProperty("name", macros.getName());
            dataObject.addProperty("keyName", macros.getKey());
            dataObject.addProperty("message", macros.getMessage());
            array.add(dataObject);
        }

        object.add("MacrosList", array);
        return object;
    }
    public void load(JsonObject object) {
        macrosList.clear();

        JsonArray jsonArray = object.getAsJsonArray("MacrosList");

        for (JsonElement data : jsonArray) {
            JsonObject dataObject = data.getAsJsonObject();

            String name = dataObject.get("name").getAsString();
            int keyName = dataObject.get("keyName").getAsInt(); // под чем я был когда написал getAsString() ??
            String message = dataObject.get("message").getAsString();

            macrosList.add(new Macros(name, keyName, message));
        }
    }
    public static void onKeyPress(int key) {

        for (Macros macros : macrosList) {
            if (key == macros.getKey()) {
                if (mc.player != null) {
                    if (macros.getMessage().contains("/")) mc.player.networkHandler.sendChatCommand(macros.getMessage().replace("/", ""));
                    else mc.player.networkHandler.sendChatMessage(macros.getMessage());
                }
            }
        }
    }
}