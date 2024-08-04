package free.minced.systems.command.impl;

import free.minced.Minced;
import free.minced.primary.chat.ChatHandler;
import free.minced.systems.WorkForceHandler;
import free.minced.systems.command.Command;
import free.minced.systems.command.api.CommandInfo;
import net.minecraft.util.Formatting;

@CommandInfo(name = "staff", aliases = {"staffs", "staff"}, description = "Server staff management")
public class WorkForceCommand extends Command {

    private final WorkForceHandler manager = Minced.getInstance().getWorkForceHandler();

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            sendErrorMessage();
        } else {
            switch (args[1].toLowerCase()) {
                case "add" -> addStaff(args[2]);
                case "remove", "del", "delete" -> removeStaff(args[2]);
                case "list" -> displayStaff();
                case "clear" -> clearStaff();
            }
        }
    }

    public void addStaff(String name) {

        if (manager.contains(name)) {
            ChatHandler.display("Staff with name %s already exists.".formatted(name));
        } else {
            manager.addStaff(name);
            ChatHandler.display("Staff with name %s was added.".formatted(name));
            Minced.getInstance().getConfigHandler().save("default");
        }
    }

    public void removeStaff(String name) {
        if (manager.contains(name)) {
            manager.removeStaff(name);
            ChatHandler.display("Staff with name %s was removed.".formatted(name));
            Minced.getInstance().getConfigHandler().save("default");
        } else {
            ChatHandler.display("Staff with name %s does not exist.".formatted(name));
        }
    }

    public void clearStaff() {
        manager.getStaff().clear();
        ChatHandler.display("Staff list was cleared.");
        Minced.getInstance().getConfigHandler().save("default");
    }

    public void displayStaff() {
        if (manager.getStaff().isEmpty()) {
            ChatHandler.display("Staff list is empty :(");
        } else {
            ChatHandler.display("Staff: ");
            for (String staff : manager.getStaff()) {
                ChatHandler.display(Formatting.GRAY + String.valueOf(Formatting.BOLD) + "> " + Formatting.WHITE + staff);
            }
        }
    }

    public void sendErrorMessage() {
        error();
        ChatHandler.display(Formatting.GRAY + ".staff add " + Formatting.WHITE + "<name or prefix>");
        ChatHandler.display(Formatting.GRAY + ".staff remove/del/delete " + Formatting.WHITE + "<name or prefix>");
        ChatHandler.display(Formatting.GRAY + ".staff list");
        ChatHandler.display(Formatting.GRAY + ".staff clear");
    }
}

