package free.minced.systems.config.api;


import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import free.minced.modules.impl.misc.UnHook;
import lombok.Getter;
import lombok.Setter;
import free.minced.Minced;
import free.minced.primary.IHolder;
import free.minced.systems.FileHandler;
import free.minced.systems.config.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author jbk
 */

@Getter
@Setter
public class ConfigHandler extends IConfigHandler<Configuration> implements IHolder {

    public static final File DIRECTORY = new File(FileHandler.DIRECTORY, "Minced/configs");
    private static final ArrayList<Configuration> loadedConfigs = new ArrayList<>();

    public ConfigHandler() {
        contents = loadConfigs();
        DIRECTORY.mkdirs();
    }
    // получает все конфиги с папки
    private static ArrayList<Configuration> loadConfigs() {
        File[] files = DIRECTORY.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".mncd")) {
                    loadedConfigs.add(new Configuration(file.getName().replace(".mncd", "")));
                }
            }
        }
        return loadedConfigs;
    }

    // загружает конфиг
    public void load(String fileName) {
        if (fileName == null)
            return;
        Configuration config = find(fileName);
        if (config == null)
            return;
        try {
            FileReader reader = new FileReader(config.getFile());
            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject) parser.parse(reader);

            config.loadConfig(object);
        } catch (Exception e) {
            Minced.LOGGER.error("[CONFIG-MANAGER] Config {} was not found", fileName);
            e.printStackTrace();
        }
    }
    public void saveAutoCfg() {
        if (Minced.getInstance().getModuleHandler().get(UnHook.class).isEnabled()) return;
        Minced.getInstance().getConfigHandler().save("autocfg");
    }
    // сохраняет конфиг
    public void save(String fileName) {
        if (fileName == null) return;

        Configuration config;
        // если мы не можем найти конфиг с нужным именем, мы можем его создать, иначе - высрем ошибку
        if ((config = find(fileName)) == null) {
            Configuration newConfig = config = new Configuration(fileName);
            getContents().add(newConfig);
        }

        String prettyPrintShit = new GsonBuilder().setPrettyPrinting().create().toJson(config.saveConfig());
        try {
            FileWriter writer = new FileWriter(config.getFile());
            writer.write(prettyPrintShit);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ищет конфиг по его названию
    public Configuration find(String fileName) {
        if (fileName == null) return null;

        for (Configuration config : getContents()) {
            if (config.getName().equalsIgnoreCase(fileName))
                return config;
        }

        if (new File(DIRECTORY, fileName + ".mncd").exists()) {
            return new Configuration(fileName);
        }
        return null;
    }
}