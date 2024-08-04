package free.minced.systems.command.impl;



import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import free.minced.primary.chat.ChatHandler;
import free.minced.primary.game.InventoryHandler;
import free.minced.systems.command.Command;
import free.minced.systems.command.api.CommandInfo;

/**
 * @author jbk
 */
@CommandInfo(name = "EClip", description = "Elytra clip", aliases = {"eclip", "elytraclip"})
public final class EClipCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length == 2) {
            try {
                int delay = Integer.parseInt(args[1]);
                execute(delay);
            } catch (NumberFormatException e) {
                error(".eclip delay requires a number");
            }
        } else {
            error(".eclip y [.eclip 5 / .eclip -5]");
        }

    }

    private void executeDown() {
        int i;
        float y = 0.0f;
        for (i = 1; i < 255; ++i) {
            if (mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos()).add(0, -i, 0)) == Blocks.AIR.getDefaultState()) {
                y = -i - 1;
                break;
            }
            if (mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos()).add(0, -i, 0)) == Blocks.BEDROCK.getDefaultState()) {
                ChatHandler.display(Formatting.RED + "Можно телепортироваться только под бедрок");
                ChatHandler.display(Formatting.RED + ".eclip bedrock");
                return;
            }
        }
        execute(y);
    }

    private void executeDown(float y) {
        int i;
        float tempY = 0.0f;
        for (i = 1; i < 255; ++i) {
            if (mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos()).add(0, -i, 0)) == Blocks.AIR.getDefaultState()) {
                tempY = -i - 1;
                break;
            }
            if (mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos()).add(0, -i, 0)) == Blocks.BEDROCK.getDefaultState()) {
                ChatHandler.display(Formatting.RED + "Можно телепортироваться только под бедрок");
                ChatHandler.display(Formatting.RED + ".eclip bedrock");
                return;
            }
        }
        if (tempY == 0.0f) tempY = y;
        execute(tempY);
    }

    private void executeUp() {
        int i;
        float y = 0.0f;
        for (i = 4; i < 255; ++i) {
            if (mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos()).add(0, i, 0)) != Blocks.AIR.getDefaultState()) {
                continue;
            }
            y = i + 1;
            break;
        }
        execute(y);
    }

    private void executeUp(float y) {
        int i;
        float tempY = 0.0f;
        for (i = 4; i < 255; ++i) {
            if (mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos()).add(0, i, 0)) != Blocks.AIR.getDefaultState()) {
                continue;
            }
            tempY = i + 1;
            break;
        }
        if (tempY == 0.0f) tempY = y;
        execute(tempY);
    }
    private void execute(float y) {
        int elytra;

        if ((elytra = InventoryHandler.findItemInInventory(Items.ELYTRA).slot()) == -1) {
            ChatHandler.display(Formatting.RED + "Вам нужны элитры в инвентаре!");
            return;
        }
        if (elytra != -2) {
            mc.interactionManager.clickSlot(0, elytra, 1, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, 6, 1, SlotActionType.PICKUP, mc.player);
        }

        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false));
        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + (double) y, mc.player.getZ(), false));
        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));

        if (elytra != -2) {
            mc.interactionManager.clickSlot(0, 6, 1, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, elytra, 1, SlotActionType.PICKUP, mc.player);
        }

        mc.player.setPosition(mc.player.getX(), mc.player.getY() + (double) y, mc.player.getZ());
    }
}