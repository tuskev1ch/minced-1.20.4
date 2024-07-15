package free.minced.systems.command.impl;

import free.minced.primary.chat.ChatHandler;
import free.minced.systems.SharedClass;
import free.minced.systems.command.Command;
import free.minced.systems.command.api.CommandInfo;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

@CommandInfo(name = "gps", description = "Gps management", aliases = {"gps", "way"})
public final class GpsCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            sendErrorMessage();
            return;
        }

        switch (args[1].toLowerCase()) {
            case "clear", "off" -> SharedClass.GPS_POSITION = null;
            default -> loadLocation(args);
        }
    }

    private void loadLocation(String[] args) {
        if (args.length != 3) {
            sendErrorMessage();
            return;
        }

        try {
            int x = Integer.parseInt(args[1]);
            int y = 0;
            int z = Integer.parseInt(args[2]);
            SharedClass.GPS_POSITION = new BlockPos(x, y, z);
            ChatHandler.display(Formatting.GRAY + "GPS location set to: " + x + ", " + z);
        } catch (NumberFormatException e) {
            ChatHandler.display(Formatting.RED + "Invalid coordinates. Please enter valid integers.");
        }
    }
    private void sendErrorMessage() {
        error();
        ChatHandler.display(Formatting.GRAY + ".gps x, z");
        ChatHandler.display(Formatting.GRAY + ".gps off");
    }
}