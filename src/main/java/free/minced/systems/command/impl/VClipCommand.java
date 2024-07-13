package free.minced.systems.command.impl;


import free.minced.primary.chat.ChatHandler;
import free.minced.primary.game.InventoryHandler;
import free.minced.systems.command.Command;
import free.minced.systems.command.api.CommandInfo;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

/**
 * @author jbk
 */
@CommandInfo(name = "VClip", description = "YClip", aliases = {"vclip", "yclip"})
public final class VClipCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length == 2) {
            try {
                int delay = Integer.parseInt(args[1]);
                execute(delay);
            } catch (NumberFormatException e) {
                error(".vclip requires a number");
            }
        } else {
            error(".vclip y [.vclip 5 / .vclip -5]");
        }

    }



    private void execute(float y) {
        mc.player.setPosition(mc.player.getX(), mc.player.getY() + (double) y, mc.player.getZ());
    }
    private void sendErrorMessage() {

    }
}