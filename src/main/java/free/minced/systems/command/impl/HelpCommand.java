package free.minced.systems.command.impl;

import net.minecraft.util.Formatting;
import free.minced.Minced;
import free.minced.primary.chat.ChatHandler;
import free.minced.systems.command.Command;
import free.minced.systems.command.api.CommandInfo;

@CommandInfo(name = "Help", description = "shows a command list", aliases = {"help", "helpme"})
public class HelpCommand extends Command {
    @Override
    public void execute(String[] args) {
        Minced.getInstance().getCommandHandler().commands.forEach(command -> {

            ChatHandler.display(Formatting.GRAY + String.valueOf(Formatting.BOLD) + "> " + Formatting.RESET + command.getDisplayName() + " - " + command.getDescription());

        });
    }
}
