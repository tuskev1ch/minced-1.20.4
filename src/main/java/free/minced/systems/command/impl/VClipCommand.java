package free.minced.systems.command.impl;


import free.minced.systems.command.Command;
import free.minced.systems.command.api.CommandInfo;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

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
                execute(delay, false);
            } catch (NumberFormatException e) {
                error(".vclip requires a number");
            }
        } else if (args.length == 3) {
            try {
                int delay = Integer.parseInt(args[1]);
                execute(delay, true);
            } catch (NumberFormatException e) {
                error(".vclip requires a number and value");
            }
        }  else {
            error(".vclip y [.vclip 5 false / .vclip -5 true] (extra - false or true)");
        }

    }



    private void execute(float y, boolean extra) {
        if (extra) {
            for (int i = 0; i < 10; ++i)
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false));

            for (int i = 0; i < 10; ++i)
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + y, mc.player.getZ(), false));
        }

        mc.player.setPosition(mc.player.getX(), mc.player.getY() + (double) y, mc.player.getZ());
    }
}