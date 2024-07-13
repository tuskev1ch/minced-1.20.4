package free.minced.primary.other;


import lombok.Getter;
import free.minced.primary.IHolder;


public class ServerHandler implements IHolder {

    public static boolean isOnServer() {
        if (mc.player == null || mc.world == null) return false;
        if (mc.isInSingleplayer()) return false;
        if (mc.getCurrentServerEntry() == null) return false;

        return true;
    }

    public static String getServerIP() {
        if (mc.player == null || mc.world == null) return "mainmenu";
        String server;
        if (mc.isInSingleplayer()) {
            server = "local";
        } else {
            if (mc.getCurrentServerEntry() == null) {
                server = "local";
            } else {
                server = mc.getCurrentServerEntry().address.toLowerCase();
            }
        }
        return server;
    }

    public static boolean serverCheck(String ip) {
        if (mc.getCurrentServerEntry() == null)
            return false;

        ip = ip.toLowerCase();
        String server = mc.isInSingleplayer() ? "" : mc.getCurrentServerEntry().address.toLowerCase();
        return server.endsWith("." + ip) || server.equals(ip);
    }

    public static boolean isOnRW() {
        return getServerIP().contains("reallyworld") || getServerIP().contains("playrw");
    }
    public static boolean isOnPT() {
        return getServerIP().contains("mc.prostotrainer.space");
    }
    public static boolean isOnSR() {
        return getServerIP().contains("sunmc");
    }
    public static boolean isOnAM() {
        return getServerIP().contains("aresmine");
    }

    public static boolean isOnFT() {
        return getServerIP().contains("funtime");
    }
}
