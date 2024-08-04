package free.minced.systems.command.api;

import lombok.Getter;
import free.minced.systems.command.Command;
import free.minced.systems.command.impl.*;


import java.util.ArrayList;
import java.util.List;


@Getter
public class CommandHandler {
    public static boolean isOtmenaCommand;
    public static final String PREFIX = ".";
    public static final List<Command> commands = new ArrayList<>();

    public void initCommands() {
        // allow events to work
        register(new GpsCommand());
        register(new ParseCommand());
        register(new ConfigCommand());
        register(new DropCommand());
        register(new WorkForceCommand());
        register(new VClipCommand());
        register(new EClipCommand());
        register(new HelpCommand());

        register(new MacrosCommand());
        register(new PartnerCommand());


    }

    public void register(Command command) {
        commands.add(command);
    }

   
}
