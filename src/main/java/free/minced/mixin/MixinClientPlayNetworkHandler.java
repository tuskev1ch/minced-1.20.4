package free.minced.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.Formatting;
import free.minced.Minced;
import free.minced.modules.impl.misc.UnHook;
import free.minced.primary.chat.ChatHandler;
import free.minced.systems.command.Command;
import free.minced.systems.command.api.CommandHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

import static free.minced.systems.command.api.CommandHandler.PREFIX;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void sendChatMessageHook(@NotNull String message, CallbackInfo ci) {
        // Check if the message starts with the command prefix
        if (message.startsWith(PREFIX)) {
            if (Minced.getInstance().getModuleHandler().get(UnHook.class).isEnabled()) return;
            // Remove the prefix from the message and split it into command and arguments
            String pMessage = message.substring(PREFIX.length());
            final String[] args = pMessage.split(" ");

            boolean commandFound = false;

            try {
                // Iterate over the registered commands to find a match
                for (Command command : Minced.getInstance().getCommandHandler().commands) {
                    if (Arrays.stream(command.getAliases()).anyMatch(alias -> alias.equalsIgnoreCase(args[0]))) {
                        commandFound = true;
                        command.execute(args);
                        ci.cancel(); // Cancel further processing of the chat message
                        return;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // If no matching command is found, display an error message
            if (!commandFound) {
                ChatHandler.display(Formatting.RED + "Unknown Command. Please check .help" + Formatting.RESET);
                ci.cancel(); // Cancel further processing of the chat message
            }
        }
    }
}
