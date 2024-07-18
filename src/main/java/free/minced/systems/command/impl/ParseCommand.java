package free.minced.systems.command.impl;

import free.minced.Minced;
import free.minced.primary.chat.ChatHandler;
import free.minced.systems.FileHandler;
import free.minced.systems.command.Command;
import free.minced.systems.command.api.CommandInfo;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@CommandInfo(name = "Parser", description = "parse a player's list", aliases = {"parse", "parser"})
public class ParseCommand extends Command {
    public final File PARSE_DATA = new File(FileHandler.DIRECTORY, "Minced/");

    @Override
    public void execute(String[] args) {
        String serverIP = "unknown_server";
        if (mc.getNetworkHandler().getServerInfo()!= null && mc.getNetworkHandler().getServerInfo().address!= null)
            serverIP = mc.getNetworkHandler().getServerInfo().address.replace(':', '_');

        String randomSuffix = generateRandomString(5);
        String fileName = "parser_out_" + serverIP + "-" + new SimpleDateFormat("dd.MM.yyyy").format(new Date()) + "-" + randomSuffix + ".txt";

        File file = new File(new File(FileHandler.DIRECTORY, ""), fileName);

        try {
            file.createNewFile();
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write("Server: " + mc.getNetworkHandler().getServerInfo().address + "\n");
            writer.write("Date: " + new SimpleDateFormat("dd.MM.yyyy").format(new Date()) + "\n\n");

            List<PlayerListEntry> sortedPlayers = new ArrayList<>(mc.getNetworkHandler().getPlayerList());
            sortedPlayers.sort((player1, player2) -> {
                String prefix1 = player1.getScoreboardTeam().getPrefix().getString();
                String prefix2 = player2.getScoreboardTeam().getPrefix().getString();
                return prefix2.compareTo(prefix1);
            });

            Map<String, List<String>> ranks = new HashMap<>();
            for (PlayerListEntry entry : sortedPlayers) {
                String prefix = Formatting.strip(entry.getScoreboardTeam().getPrefix().getString());
                String name = Formatting.strip(entry.getProfile().getName());
                if (!ranks.containsKey(prefix)) {
                    ranks.put(prefix, new ArrayList<>());
                }
                ranks.get(prefix).add(name);
            }

            for (Map.Entry<String, List<String>> entry : ranks.entrySet()) {
                writer.write(entry.getKey() + " {\n");
                for (String name : entry.getValue()) {
                    writer.write("   " + name + "\n");
                }
                writer.write("}\n\n");
            }

            writer.close();
            ChatHandler.display(Formatting.GREEN + "Tab was successfully saved in " + Formatting.WHITE + file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateRandomString(int length) {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
}
