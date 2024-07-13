package free.minced.systems.command.api;

import lombok.Getter;
import free.minced.Minced;
import free.minced.primary.chat.ChatHandler;
import free.minced.systems.command.Command;
import free.minced.systems.command.impl.*;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@Getter
public class CommandHandler {
    public static boolean isOtmenaCommand;
    public static final String PREFIX = ".";
    public static final List<Command> commands = new ArrayList<>();

    public void initCommands() {
        // allow events to work
        register(new ConfigCommand());
        register(new DropCommand());
        register(new EClipCommand());
        register(new HelpCommand());

        register(new MacrosCommand());
        register(new PartnerCommand());


    }

    public void register(Command command) {
        commands.add(command);
    }

   
}
