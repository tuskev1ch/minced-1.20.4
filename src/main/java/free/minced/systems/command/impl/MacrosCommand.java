package free.minced.systems.command.impl;

import net.minecraft.util.Formatting;
import free.minced.Minced;
import free.minced.primary.chat.ChatHandler;
import free.minced.primary.other.KeyHandler;
import free.minced.systems.command.Command;
import free.minced.systems.command.api.CommandInfo;
import free.minced.systems.macros.Macros;

import java.util.Arrays;

@CommandInfo(name = "Macros", description = "Macros management", aliases = {"macro", "macros"})
public class MacrosCommand extends Command {
    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            sendErrorMessage();
        } else {
            switch (args[1].toLowerCase()) {
                case "add" -> addMacros(args);
                case "remove", "del", "delete" -> removeMacros(args);
                case "list" -> displayMacrosList();
                case "clear" -> clearMacrosList();
            }
        }
    }

    public void clearMacrosList() {
        Minced.getInstance().getMacrosHandler().macrosList.clear();
        ChatHandler.display("The list of macros has been cleared!");
        Minced.getInstance().getConfigHandler().saveAutoCfg();
    }

    public void displayMacrosList() {

        if (Minced.getInstance().getMacrosHandler().macrosList.isEmpty()) {
            ChatHandler.display("Macros list is empty :(");
        } else {
            for (Macros macros : Minced.getInstance().getMacrosHandler().macrosList) {
                ChatHandler.display(Formatting.GRAY + String.valueOf(Formatting.BOLD) + "> " + Formatting.WHITE + "%s [%s]: %s".formatted(
                        Formatting.GRAY + macros.getName() + Formatting.RESET,
                        KeyHandler.getKeyboardKey(macros.getKey()),
                        Formatting.GRAY + macros.getMessage() + Formatting.RESET));
            }
        }
    }

    public void removeMacros(String[] args) {


        if (args.length != 3) {
            sendErrorMessage();
        } else {
            String name = args[2];
            Macros macros = Minced.getInstance().getMacrosHandler().get(name);

            if (macros == null) {
                ChatHandler.display("A macro with the name %s was not found!".formatted(name));
            } else {
                Minced.getInstance().getMacrosHandler().macrosList.remove(macros);
                ChatHandler.display("Macro %s was deleted!".formatted(name));
                Minced.getInstance().getConfigHandler().saveAutoCfg();
            }
        }
    }

    public void addMacros(String[] args) {


        if (args.length < 5) {
            sendErrorMessage();
        } else {

            String name = args[2];
            String keyName = args[3];
            String message = String.join(" ", Arrays.copyOfRange(args, 4, args.length));

            int key = KeyHandler.getKeyIndex(keyName);

            // key exists check
            if (key == - 1) {
                ChatHandler.display("Кнопки с именем %s не существует!".formatted(name));
                return;
            }

            Macros macros = new Macros(name, key, message);
            if (Minced.getInstance().getMacrosHandler().get(macros.getName()) != null) {
                ChatHandler.display("Макрос %s уже существует!".formatted(name));
            } else {
                Minced.getInstance().getMacrosHandler().macrosList.add(macros);
                ChatHandler.display("Макрос %s был добавлен!".formatted(name));
                Minced.getInstance().getConfigHandler().saveAutoCfg();
            }
        }
    }

    public void sendErrorMessage() {
        error();
        ChatHandler.display(Formatting.GRAY + ".macros add " + Formatting.WHITE + "<name> <key> <message>");
        ChatHandler.display(Formatting.GRAY + ".macros remove/del/delete " + Formatting.WHITE + "<name>");
        ChatHandler.display(Formatting.GRAY + ".macros list");
        ChatHandler.display(Formatting.GRAY + ".macros clear");
    }
}