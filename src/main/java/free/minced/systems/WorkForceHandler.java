package free.minced.systems;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WorkForceHandler {

    private final List<String> staff = new ArrayList<>();

    public void addStaff(String name) {
        staff.add(name);
    }

    public void removeStaff(String name) {
        staff.remove(name);
    }

    public boolean contains(String name) {
        return getStaff().stream().anyMatch(staffName -> staffName.equalsIgnoreCase(name));
    }

    public JsonObject save() {
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();

        for (String staffName : getStaff()) {
            array.add(new JsonPrimitive(staffName));
        }

        object.add("ServerStaff", array);
        return object;
    }

    public void load(JsonObject object) {
        staff.clear();

        JsonArray jsonArray = object.getAsJsonArray("ServerStaff");

        // да да я
        for (JsonElement data : jsonArray) {
            if (data.isJsonPrimitive() && data.getAsJsonPrimitive().isString()) {
                String staffName = data.getAsString();
                staff.add(staffName);
            }
        }
    }
}
