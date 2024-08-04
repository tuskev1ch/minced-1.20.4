package free.minced.systems.command.impl;

import free.minced.systems.command.api.CommandHandler;
import net.minecraft.util.Formatting;
import free.minced.primary.chat.ChatHandler;
import free.minced.systems.command.Command;
import free.minced.systems.command.api.CommandInfo;

@CommandInfo(name = "Help", description = "shows a command list", aliases = {"help", "helpme"})
public class HelpCommand extends Command {
    @Override
    public void execute(String[] args) {
        CommandHandler.commands.forEach(command -> ChatHandler.display(Formatting.GRAY + String.valueOf(Formatting.BOLD) + "> " + Formatting.RESET + command.getDisplayName() + " - " + command.getDescription()));
    }
}
