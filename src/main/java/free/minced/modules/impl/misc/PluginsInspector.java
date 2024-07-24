package free.minced.modules.impl.misc;

import free.minced.events.Event;
import free.minced.events.impl.player.PacketEvent;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.primary.chat.ChatHandler;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@ModuleDescriptor(name = "PluginsInspector", category = ModuleCategory.MISC)
public class PluginsInspector extends Module {

    int delay = 0;

    private final String[] knownPlugins = {
            "matrix",
            "aac5",
            "alice",
            "vulcan",
            "kauri",
            "spartan",
            "polar",
            "horizon",
            "intave",
            "prostoac",
            "tesla",
            "buzz",
            "grimac",
            "grim",
            "aac",
            "nocheatplus",
            "anticheatreloaded",
            "negativity",
            "cheatminecore",
            "cmcore",
            "themis"
    };

    @Override
    public void onEvent(Event event) {
        if (event instanceof UpdatePlayerEvent updatePlayerEvent) {
            delay++;
            if (delay > 40) {
                ChatHandler.display("Failed to retrieve plugin list");
                delay = 0;
                this.setEnabled(false);
            }
        } else if (event instanceof PacketEvent packetEvent) {
            if (packetEvent.getPacket() instanceof CommandSuggestionsS2CPacket commandSuggestionsS2CPacket) {
                Set<String> plugins = commandSuggestionsS2CPacket.getSuggestions().getList().stream()
                        .map(cmd -> {
                            String[] command = cmd.getText().split(":");
                            if (command.length > 1) {
                                return command[0].replace("/", "");
                            } else {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .sorted()
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                if (!plugins.isEmpty()) {
                    StringBuilder pluginsString = new StringBuilder();
                    for (String plugin : plugins) {
                        String formattedPlugin;
                        if (Arrays.stream(knownPlugins).map(String::toLowerCase).toList().contains(plugin.toLowerCase())) {
                            formattedPlugin = "§a" + plugin;
                        } else {
                            formattedPlugin = "§c" + plugin;
                        }
                        if (!pluginsString.isEmpty()) {
                            pluginsString.append(", ");
                        }
                        pluginsString.append(formattedPlugin);
                    }
                    String result = pluginsString.toString();

                    ChatHandler.display("Plugins §7(§8%s§7): %s".formatted(plugins.size(), result));
                } else {
                    ChatHandler.display("Failed to retrieve plugin list!");
                }
                this.setEnabled(false);
            }
        }
    }

    @Override
    public void onDisable() {
        delay = 0;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        IHolder.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/"));
        super.onEnable();
    }
}