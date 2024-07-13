package free.minced.systems.command.impl;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.Formatting;
import free.minced.Minced;
import free.minced.primary.chat.ChatHandler;
import free.minced.systems.command.Command;
import free.minced.systems.command.api.CommandInfo;
import free.minced.systems.config.Configuration;
import free.minced.systems.config.api.ConfigHandler;

import java.io.FileReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author jbk
 */
@CommandInfo(name = "Config", description = "Config management", aliases = {"cfg", "config"})
public final class ConfigCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 3) sendErrorMessage();

        if (args.length >= 3) {
            switch (args[1].toLowerCase()) {
                case "create", "add" -> createConfig(args[2]);
                case "save" -> saveConfig(args[2]);
                case "load" -> loadConfig(args[2]);
                case "del", "remove", "delete" -> removeConfig(args[2]);
                default -> sendErrorMessage();
            }
        } else if (args.length == 2) {
            switch (args[1].toLowerCase()) {
                case "list" -> displayConfigs();
                case "clear" -> clearConfigs();
            }
        }
    }

    private void removeConfig(String name) {
        Configuration config = Minced.getInstance().getConfigHandler().find(name);

        // если конфиг найден, удаляем его
        if (config != null) {
            Minced.getInstance().getConfigHandler().getContents().remove(config);
            //Minced.getInstance().getConfigHandler().save("default");
            ChatHandler.display("Removed config with name %s".formatted(name));
            // если это не так - отправляем сообщение о том, что конфиг не найден
        } else {
            ChatHandler.display("Config with name %s was not found".formatted(name));
        }
    }

    private void clearConfigs() {
        Minced.getInstance().getConfigHandler().getContents().clear();
        ChatHandler.display("Configs was cleared");
        //Minced.getInstance().getConfigHandler().save("default");
    }

    private void displayConfigs() {
        ChatHandler.display("Configs: ");
        Set<String> uniqueConfigNames = new HashSet<>();
        Minced.getInstance().getConfigHandler().getContents().forEach(configuration -> uniqueConfigNames.add(configuration.getName()));
        uniqueConfigNames.forEach(configName -> ChatHandler.display(Formatting.GRAY + String.valueOf(Formatting.BOLD) + "> " + Formatting.WHITE + configName));
    }

    private void saveConfig(String name) {
        // проверка на наличие конфига с заданным названием
        if (Minced.getInstance().getConfigHandler().find(name) != null) {
            Minced.getInstance().getConfigHandler().save(name);

            // нотификация об успехе
            ChatHandler.display("Config with name %s was saved".formatted(name));
        } else {
            ChatHandler.display("Config with name %s does not exists".formatted(name));

        }
    }

    private void createConfig(String name) {
        // проверка на наличие конфига с заданным названием
        if (Minced.getInstance().getConfigHandler().find(name) == null) {
            Minced.getInstance().getConfigHandler().save(name);

            // нотификация об успехе
            ChatHandler.display("Config with name %s was created".formatted(name));
        } else {
            // нотификация об ошибке
            ChatHandler.display("Config with name %s already exists".formatted(name));
        }
    }

    private void loadConfig(String name) {
        ChatHandler.display("путь - " + Minced.getInstance().getConfigHandler().DIRECTORY.getAbsolutePath());
        if (Minced.getInstance().getConfigHandler().find(name) != null) {
            Minced.getInstance().getConfigHandler().load(name);

            ChatHandler.display("Config with name %s as loaded".formatted(name));
        } else {
            ChatHandler.display("Config with name %s does not exists".formatted(name));
        }
    }

    private void sendErrorMessage() {
        error();
        ChatHandler.display(Formatting.GRAY + ".config add/create " + Formatting.WHITE + "<name>");
        ChatHandler.display(Formatting.GRAY + ".config save " + Formatting.WHITE + "<name>");
        ChatHandler.display(Formatting.GRAY + ".config load " + Formatting.WHITE + "<name>");
        ChatHandler.display(Formatting.GRAY + ".config remove/del/delete " + Formatting.WHITE + "<name>");
        ChatHandler.display(Formatting.GRAY + ".config list");
        ChatHandler.display(Formatting.GRAY + ".config clear");
    }
}