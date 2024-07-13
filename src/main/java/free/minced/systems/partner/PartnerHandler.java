package free.minced.systems.partner;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import free.minced.Minced;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PartnerHandler {

    private final List<String> friends = new ArrayList<>();

    /**
     * используется для добавления друга по нику
     *
     * @param friend ник друга который будет добавлен
     */
    public void addFriend(String friend) {
        friends.add(friend);
        //Minced.getInstance().getConfigHandler().save("default");
    }

    /**
     * используется для добавления нескольких друзей по нику
     *
     * @param args список друзей которые будут добавлены
     */
    public void addFriends(String... args) {
        friends.addAll(Arrays.asList(args));
        //Minced.getInstance().getConfigHandler().save("default");
    }

    /**
     * используется для удаления друзей
     *
     * @param friend ентити которое будет удалено из друзей
     */
    public void removeFriend(Entity friend) {
        friends.remove(friend.getName().getString());
        //Minced.getInstance().getConfigHandler().save("default");
    }

    /**
     * используется для удаления друга по нику
     *
     * @param friend ник друга который будет удален
     */
    public void removeFriend(String friend) {
        friends.remove(friend);
        //Minced.getInstance().getConfigHandler().save("default");
    }
    /**
     * проверяет, находится ли энтити в спмиске друзей
     *
     * @param entity энтити для проверки
     */
    public boolean isFriend(Entity entity) {
        return friends.contains(entity.getName().getString());
    }

    public boolean isFriend(String entity) {
        return friends.contains(entity);
    }

    /**
     * сохраняет конфиг
     */
    public JsonObject save() {
        JsonObject json = new JsonObject();
        try {
            for (String friend : friends) {
                json.addProperty(friend, "friends");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Minced.LOGGER.error("[{}] хуйня", "FRIEND-MANAGER");
        }
        return json;
    }

    /**
     * загружает конфиг
     */
    public void load(JsonObject data) {
        friends.clear();
        try {
            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if (value.isJsonPrimitive() && value.getAsString().equals("friends")) {
                    // добавляем друзей в список
                    friends.add(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Minced.LOGGER.error("[{}] хуйня", "FRIEND-MANAGER");
        }
    }
}