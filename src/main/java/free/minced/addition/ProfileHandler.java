package free.minced.addition;


import free.minced.primary.IHolder;
import lombok.Getter;


public class ProfileHandler implements IHolder {

    @Getter
    private static final String username = "User";

    @Getter
    private static final String subscribe = "Never";

    @Getter
    private static final String build = "Free";

    @Getter
    private static final String version = "1.9";


}