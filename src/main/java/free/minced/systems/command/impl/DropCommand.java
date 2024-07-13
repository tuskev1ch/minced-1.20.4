package free.minced.systems.command.impl;


import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import free.minced.Minced;
import free.minced.primary.chat.ChatHandler;
import free.minced.systems.SharedClass;
import free.minced.systems.command.Command;
import free.minced.systems.command.api.CommandInfo;


@CommandInfo(name = "Drop", description = "clean's inventory", aliases = {"drop", "dropall", "clean"})

public class DropCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            SharedClass.run(() -> {
                for (int i = 5; i <= 45; i++) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, mc.player);
                }
            });
        } else if (args.length == 2) {
            try {
                int delay = Integer.parseInt(args[1]);
                SharedClass.run(() -> {
                    for (int i = 5; i <= 45; i++) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, mc.player);
                    }
                }, delay);
            } catch (NumberFormatException e) {
                error(".drop delay requires a number");
            }
        } else {
            error(".drop delay [.drop 5 / .drop]");
        }
    }
}