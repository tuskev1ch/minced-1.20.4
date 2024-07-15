package free.minced.addition;


import lombok.AllArgsConstructor;
import lombok.Getter;
import free.minced.primary.IHolder;


public class ProfileHandler implements IHolder {

    private static String username = "User";

    private static String subscribe = "Never";

    private static String build = "Free";

    private static String version = "1.4";


    public static String getUsername() {
        return username;
    }

    public static String getSubscribe() {
        return subscribe;
    }
    public static String getBuild() {
        return build;
    }

    public static String getVersion() {
        return version;
    }
}