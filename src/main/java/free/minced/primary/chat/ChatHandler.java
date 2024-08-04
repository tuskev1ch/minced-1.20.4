package free.minced.primary.chat;


import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import free.minced.Minced;
import free.minced.primary.IHolder;


public class ChatHandler implements IHolder {
    private static final String PREFIX = Formatting.BOLD + "" + Formatting.WHITE + "[" + Formatting.GRAY + Minced.NAME.toUpperCase() + Formatting.WHITE  + "]" + Formatting.BOLD + " -> " + Formatting.RESET;


    public static void display(String message) {
        if (mc.player == null) return;
        if (mc.isOnThread()) {
            mc.player.sendMessage(Text.of(PREFIX + message));
        } else {
            mc.executeSync(() -> mc.player.sendMessage(Text.of(PREFIX + message)));
        }
    }

}
