package free.minced.systems.command.impl;

import net.minecraft.util.Formatting;
import free.minced.Minced;
import free.minced.primary.chat.ChatHandler;
import free.minced.systems.command.Command;
import free.minced.systems.command.api.CommandInfo;

@CommandInfo(name = "Friend", description = "friend managment", aliases = {"f", "friend", "friends"})
public class PartnerCommand extends Command {

    @Override
    public void execute(String[] args) {

        if (args.length < 2) sendErrorMessage();

        switch (args[1].toLowerCase()) {
            case "remove", "delete", "del" -> removeFriend(args[2]);
            case "add" -> addFriend(args[2]);
            case "list" -> displayFriends();
            case "clear" -> clearFriends();
            default -> sendErrorMessage();
        }
    }

    public void addFriend(String name) {
        Minced.getInstance().getPartnerHandler().addFriend(name);
        ChatHandler.display("Success! " + name + " was added as a friends!");
    }

    public void removeFriend(String name) {
        Minced.getInstance().getPartnerHandler().removeFriend(name);
        ChatHandler.display("Success! " + name + " was removed from friends!");
    }

    public void displayFriends() {
        ChatHandler.display("Friends: ");
        for (String friend : Minced.getInstance().getPartnerHandler().getFriends()) {
            ChatHandler.display(Formatting.GRAY + String.valueOf(Formatting.BOLD) + "> " + Formatting.WHITE + friend);
        }
    }

    public void clearFriends() {
        Minced.getInstance().getPartnerHandler().getFriends().clear();
        ChatHandler.display("Success! The friend list has been cleared!");
        Minced.getInstance().getConfigHandler().save("default");
    }

    public void sendErrorMessage() {
        error();
        ChatHandler.display(Formatting.GRAY + ".friend " + Formatting.WHITE + "<name>");
        ChatHandler.display(Formatting.GRAY + ".friend add " + Formatting.WHITE + "<name>");
        ChatHandler.display(Formatting.GRAY + ".friend remove/del/delete " + Formatting.WHITE + "<name>");
        ChatHandler.display(Formatting.GRAY + ".friend list");
        ChatHandler.display(Formatting.GRAY + ".friend clear");
    }
}