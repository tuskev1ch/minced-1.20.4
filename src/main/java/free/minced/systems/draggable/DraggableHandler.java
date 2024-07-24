package free.minced.systems.draggable;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import free.minced.Minced;
import free.minced.modules.impl.misc.UnHook;
import free.minced.primary.chat.ChatHandler;
import free.minced.systems.FileHandler;
import net.minecraft.network.message.SentMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

/**
 * @author jbk
 */

public class DraggableHandler {
    public HashMap<String, Draggable> draggables = new HashMap<>();

    // директория с данными
    public final File DRAG_DATA = new File(FileHandler.DIRECTORY, "Minced/draggables.mncd");

    // я тебя ненавижу, GСын
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    // сохраняем данные
    public void save() {
        if (!DRAG_DATA.exists()) {
            System.out.println(DRAG_DATA.getAbsolutePath());
            DRAG_DATA.getParentFile().mkdirs();
        }
        try {
            Files.writeString(DRAG_DATA.toPath(), GSON.toJson(draggables.values()));
        } catch (IOException exception) {
            exception.printStackTrace();
            Minced.LOGGER.error("FAILED TO SAVE DRAGGABLES");
        }
    }

    // загружаем данные
    public void load() {
        // создаем директорию
        if (!DRAG_DATA.exists()) {
            DRAG_DATA.getParentFile().mkdirs();
            return;
        }

        Draggable[] draggings;

        try {
            draggings = GSON.fromJson(Files.readString(DRAG_DATA.toPath()), Draggable[].class);

        } catch (IOException exception) {
            exception.printStackTrace();
            Minced.LOGGER.error("FAILED TO LOAD DRAGGABLES");
            return;
        }
        for (Draggable dragging : draggings) {
            if (dragging == null) return;
            Draggable currentDrag = draggables.get(dragging.getName());
            if (currentDrag == null) continue;
            // changing draggable position
            currentDrag.setX(dragging.getX());
            currentDrag.setY(dragging.getY());
            draggables.put(dragging.getName(), currentDrag);
        }
    }
}